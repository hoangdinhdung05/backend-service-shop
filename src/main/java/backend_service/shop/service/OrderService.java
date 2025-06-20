package backend_service.shop.service;

import backend_service.shop.dto.request.OrderRequestDTO;
import backend_service.shop.dto.response.OrderResponse;
import backend_service.shop.util.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequestDTO orderRequest);

    OrderResponse getOrderById(Long orderId);

    List<OrderResponse> getOrdersByUser(Long userId);

    void cancelOrder(Long orderId);

    OrderResponse changeStatus(Long orderId, OrderStatus newStatus); // thêm status mới

}
