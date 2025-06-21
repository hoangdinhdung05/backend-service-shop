package backend_service.shop.repository;

import backend_service.shop.dto.response.ProductSalesReport;
import backend_service.shop.entity.OrderDetail;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderDetailRepository extends CustomRepository<OrderDetail, Long> {

    List<OrderDetail> findByOrderId(Long orderId);


    @Query("SELECT new backend_service.shop.dto.response.ProductSalesReport(" +
            "d.product.id, d.product.name, SUM(d.quantity), SUM(d.totalPrice)) " +
            "FROM OrderDetail d " +
            "GROUP BY d.product.id, d.product.name " +
            "ORDER BY SUM(d.quantity) DESC")
    List<ProductSalesReport> getProductSalesReport();

}
