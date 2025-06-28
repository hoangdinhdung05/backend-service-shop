package backend_service.shop.service.impl;

import backend_service.shop.dto.request.OrderDetailRequestDTO;
import backend_service.shop.dto.request.OrderRequestDTO;
import backend_service.shop.dto.response.DiscountResult;
import backend_service.shop.dto.response.OrderDetailResponse;
import backend_service.shop.dto.response.OrderResponse;
import backend_service.shop.dto.response.system.PageResponse;
import backend_service.shop.entity.Discount;
import backend_service.shop.entity.DiscountUsage;
import backend_service.shop.entity.Order;
import backend_service.shop.entity.OrderDetail;
import backend_service.shop.exception.ResourceNotFoundException;
import backend_service.shop.repository.*;
import backend_service.shop.service.DiscountService;
import backend_service.shop.service.InvoiceService;
import backend_service.shop.service.OrderService;
import backend_service.shop.util.DiscountType;
import backend_service.shop.util.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final InvoiceService invoiceService;
    private final DiscountService discountService;
    private final DiscountRepository discountRepository;
    private final DiscountUsageRepository discountUsageRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequestDTO orderRequest) {
        // 1. Validate user
        var user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalQuantity = 0;
        List<OrderDetail> orderDetails = new ArrayList<>();

        // 2. Duyệt qua từng item để tạo OrderDetail
        for (OrderDetailRequestDTO item : orderRequest.getOrderDetails()) {
            var product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            BigDecimal unitPrice = product.getPrice();
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

            totalPrice = totalPrice.add(itemTotal);
            totalQuantity += item.getQuantity();

            OrderDetail detail = OrderDetail.builder()
                    .product(product)
                    .quantity(item.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(itemTotal)
                    .build();

            orderDetails.add(detail);
        }

        //Logic use discount
        Discount discount = null;
        BigDecimal discountAmount = BigDecimal.ZERO;

        if(orderRequest.getDiscountCode() != null && !orderRequest.getDiscountCode().isBlank()) {
            log.info("==============User discount logic=============");

            var discountResult = applyDiscount(orderRequest.getDiscountCode(), user.getId(), totalPrice);
            discount = discountResult.getDiscount();
            discountAmount = discountResult.getDiscountAmount();
        }

        // 3. Create Order
        Order order = Order.builder()
                .user(user)
                .shippingAddress(orderRequest.getShippingAddress())
                .note(orderRequest.getNote())
                .paymentMethod(orderRequest.getPaymentMethod())
                .discount(discount)
                .discountAmount(discountAmount)
                .totalPrice(totalPrice)
                .finalPrice(totalPrice.subtract(discountAmount))
                .status(OrderStatus.PENDING)
                .totalQuantity(totalQuantity)
                .build();

        // 4. Save Order
        order = orderRepository.save(order);

        if (discount != null) {
            DiscountUsage usage = DiscountUsage.builder()
                    .user(user)
                    .discount(discount)
                    .order(order)
                    .usedAt(LocalDateTime.now())
                    .build();
            discountUsageRepository.save(usage);
        }

        // 5. Gán order cho từng orderDetail và lưu
        for (OrderDetail detail : orderDetails) {
            detail.setOrder(order);
        }
        orderDetailRepository.saveAll(orderDetails);
        order.setOrderDetails(orderDetails);

        log.info("Create order successfully. orderId={}", order.getId());

        try {
            invoiceService.generateInvoiceForOrder(order);
        } catch (Exception e) {
            log.error("Failed to generate invoice for order id {}: {}", order.getId(), e.getMessage());
            // Có thể không throw lại lỗi để không rollback đơn hàng, tùy chính sách
        }

        // 6. Trả về response
        return convertToResponse(order, orderDetails);
    }

    @Override
    @Transactional
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return convertToResponse(order, order.getOrderDetails());
    }

    @Override
    @Transactional
    public List<OrderResponse> getOrdersByUser(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(order ->
                    convertToResponse(order, order.getOrderDetails()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        //check order
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if(order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED) {
            log.info("Cannot cancel Order with order Id={}", orderId);
            throw new IllegalStateException("Cannot cancel this order");
        }

        order.setStatus(OrderStatus.CANCELLED);

        log.info("Cancel order successfully");

        orderRepository.save(order);
    }

    @Override
    @Transactional
    public OrderResponse changeStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(newStatus);
        orderRepository.save(order);

        return convertToResponse(order, order.getOrderDetails());
    }

    @Override
    @Transactional
    public PageResponse<?> getAllOrders(int page, int size) {
        Page<Order> orderPage = orderRepository.findAll(PageRequest.of(page, size));

        List<OrderResponse> responses = orderPage.getContent().stream()
                .map(order -> convertToResponse(order, order.getOrderDetails()))
                .toList();

        return PageResponse.builder()
                .page(page)
                .size(size)
                .total(orderPage.getTotalPages())
                .items(responses)
                .build();
    }


    private DiscountResult applyDiscount(String discountCode, Long userId, BigDecimal totalPrice) {
        Discount discount = discountService.getValidDiscountByCode(discountCode);

        // Check ngày hợp lệ
        LocalDateTime now = LocalDateTime.now();
        if (discount.getStartDate() != null && discount.getStartDate().isAfter(now)) {
            throw new IllegalArgumentException("Discount not started yet");
        }
        if (discount.getEndDate() != null && discount.getEndDate().isBefore(now)) {
            throw new IllegalArgumentException("Discount has expired");
        }

        // Kiểm tra số lượt dùng tổng
        if (discount.getMaxUses() != null && discount.getMaxUses() <= 0) {
            throw new IllegalArgumentException("Discount has been fully used");
        }

        // Kiểm tra số lượt dùng bởi user
        int useByUser = discountUsageRepository.countByUserIdAndDiscount_Code(userId, discountCode);
        if (discount.getMaxUsesPerUser() != null && useByUser >= discount.getMaxUsesPerUser()) {
            throw new IllegalArgumentException("You have already used this discount code the maximum number of times");
        }

        // Kiểm tra min order amount
        if (discount.getMinOrderAmount() != null && totalPrice.compareTo(discount.getMinOrderAmount()) < 0) {
            throw new IllegalArgumentException("Order total does not meet the minimum requirement for this discount");
        }

        // Tính discount amount
        BigDecimal discountAmount;
        if (discount.getDiscountType().equals(DiscountType.PERCENTAGE)) {
            discountAmount = totalPrice.multiply(discount.getValue()).divide(BigDecimal.valueOf(100));
        } else {
            discountAmount = discount.getValue();
        }

        // Không được vượt quá tổng giá
        if (discountAmount.compareTo(totalPrice) > 0) {
            discountAmount = totalPrice;
        }

        // Trừ maxUses
        if (discount.getMaxUses() != null) {
            discount.setMaxUses(discount.getMaxUses() - 1);
        }

        discountRepository.save(discount);

        return new DiscountResult(discount, discountAmount);
    }


    /**
     * Convert data to order detail response
     *
     * @param detail
     * @return
     */
    private OrderDetailResponse convertOrderDetailToResponse(OrderDetail detail) {
        return OrderDetailResponse.builder()
                .id(detail.getId())
                .productId(detail.getProduct().getId())
                .productName(detail.getProduct().getName())
                .quantity(detail.getQuantity())
                .unitPrice(detail.getUnitPrice())
                .totalPrice(detail.getTotalPrice())
                .build();
    }

    /**
     * Convert data to order response
     *
     * @param order
     * @param orderDetails
     * @return
     */
    private OrderResponse convertToResponse(Order order, List<OrderDetail> orderDetails) {
        List<OrderDetailResponse> detailResponses = orderDetails.stream()
                .map(this::convertOrderDetailToResponse)
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .shippingAddress(order.getShippingAddress())
                .note(order.getNote())
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus())
                .discount(order.getDiscount())
                .totalPrice(order.getTotalPrice())
                .totalQuantity(order.getTotalQuantity())
                .orderDetails(detailResponses)
                .finalPrice(order.getFinalPrice())
                .build();
    }
}
