package backend_service.shop.service;

import backend_service.shop.dto.request.OrderRequestDTO;
import backend_service.shop.dto.response.OrderResponse;
import backend_service.shop.dto.response.system.PageResponse;
import backend_service.shop.util.OrderStatus;
import java.util.List;

public interface OrderService {

    /**
     * Create a new order based on cart and user input.
     */
    OrderResponse createOrder(OrderRequestDTO orderRequest);

    /**
     * Retrieve an order by its ID.
     */
    OrderResponse getOrderById(Long orderId);

    /**
     * Retrieve all orders of a specific user.
     */
    List<OrderResponse> getOrdersByUser(Long userId);

    /**
     * Cancel an order by its ID (user or admin).
     */
    void cancelOrder(Long orderId);

    /**
     * Change the status of an order (typically used by admin or system).
     */
    OrderResponse changeStatus(Long orderId, OrderStatus newStatus);

    /**
     * Admin: Get all orders in the system (consider pagination later).
     */
    PageResponse<?> getAllOrders(int page, int size);

}
