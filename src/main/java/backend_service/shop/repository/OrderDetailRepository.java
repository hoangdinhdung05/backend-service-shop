package backend_service.shop.repository;

import backend_service.shop.entity.OrderDetail;
import java.util.List;

public interface OrderDetailRepository extends CustomRepository<OrderDetail, Long> {

    List<OrderDetail> findByOrderId(Long orderId);

}
