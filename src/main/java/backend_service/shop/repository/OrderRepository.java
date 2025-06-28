package backend_service.shop.repository;

import backend_service.shop.entity.Order;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderRepository extends CustomRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    @Query("select count(o) from Order o where o.user.id = :userId and o.discount.code = :code")
    int countByUserAndDiscountCode(long userId, String code);
}
