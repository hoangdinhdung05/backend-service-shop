package backend_service.shop.repository;

import backend_service.shop.dto.response.ProductSalesReport;
import backend_service.shop.entity.OrderDetail;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderDetailRepository extends CustomRepository<OrderDetail, Long> {

    List<OrderDetail> findByOrderId(Long orderId);

    @Query("SELECT new backend_service.shop.dto.response.ProductSalesReport(od.product.id, od.product.name, SUM(od.quantity)) " +
            "FROM OrderDetail od GROUP BY od.product.id, od.product.name ORDER BY SUM(od.quantity) DESC")
    List<ProductSalesReport> findTopSellingProducts(Pageable pageable);

    default List<ProductSalesReport> findTopSellingProducts(int limit) {
        return findTopSellingProducts(PageRequest.of(0, limit));
    }

}
