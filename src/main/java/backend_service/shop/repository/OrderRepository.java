package backend_service.shop.repository;

import backend_service.shop.entity.Order;
import java.util.List;

public interface OrderRepository extends CustomRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}
