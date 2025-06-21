package backend_service.shop.service.impl;

import backend_service.shop.dto.request.OrderDetailRequestDTO;
import backend_service.shop.dto.request.OrderRequestDTO;
import backend_service.shop.dto.response.OrderDetailResponse;
import backend_service.shop.dto.response.OrderResponse;
import backend_service.shop.dto.response.system.PageResponse;
import backend_service.shop.entity.Order;
import backend_service.shop.entity.OrderDetail;
import backend_service.shop.exception.ResourceNotFoundException;
import backend_service.shop.repository.OrderDetailRepository;
import backend_service.shop.repository.OrderRepository;
import backend_service.shop.repository.ProductRepository;
import backend_service.shop.repository.UserRepository;
import backend_service.shop.service.InvoiceService;
import backend_service.shop.service.OrderService;
import backend_service.shop.util.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
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

        // TODO: Apply discount logic if any

        // 3. Create Order
        Order order = Order.builder()
                .user(user)
                .shippingAddress(orderRequest.getShippingAddress())
                .note(orderRequest.getNote())
                .paymentMethod(orderRequest.getPaymentMethod())
                .discount(orderRequest.getDiscount())
                .status(OrderStatus.PENDING)
                .totalPrice(totalPrice)
                .totalQuantity(totalQuantity)
                .build();

        // 4. Save Order
        order = orderRepository.save(order);

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
                .build();
    }
}
