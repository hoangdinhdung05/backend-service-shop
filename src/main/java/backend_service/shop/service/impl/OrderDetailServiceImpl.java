package backend_service.shop.service.impl;

import backend_service.shop.dto.response.OrderDetailResponse;
import backend_service.shop.dto.response.ProductSalesReport;
import backend_service.shop.dto.response.system.PageResponse;
import backend_service.shop.entity.OrderDetail;
import backend_service.shop.repository.OrderDetailRepository;
import backend_service.shop.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    @Override
    public List<OrderDetailResponse> getOrderDetailsByOrderId(Long orderId) {
        List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
        return details.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDetailResponse getOrderDetailById(Long detailId) {
        Optional<OrderDetail> optionalDetail = orderDetailRepository.findById(detailId);
        return optionalDetail.map(this::mapToResponse).orElse(null);
    }

    @Override
    public PageResponse<List<OrderDetailResponse>> getAllOrderDetails(int page, int size) {
        Page<OrderDetail> pagedDetails = orderDetailRepository.findAll(PageRequest.of(page, size));
        List<OrderDetailResponse> content = pagedDetails.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PageResponse.<List<OrderDetailResponse>>builder()
                .page(pagedDetails.getNumber())
                .size(pagedDetails.getSize())
                .total(pagedDetails.getTotalElements())
                .items(content)
                .build();
    }

    @Override
    public List<ProductSalesReport> getTopSellingProducts(int limit) {
        return orderDetailRepository.findTopSellingProducts(PageRequest.of(0, limit));
    }

    /**
     * Convert OrderDetail entity → OrderDetailResponse DTO
     */
    private OrderDetailResponse mapToResponse(OrderDetail detail) {
        return OrderDetailResponse.builder()
                .id(detail.getId())
                .productId(detail.getProduct().getId())
                .productName(detail.getProduct().getName())
                .quantity(detail.getQuantity())
                .unitPrice(detail.getUnitPrice())
                .totalPrice(detail.getTotalPrice())
                .build();
    }

}
