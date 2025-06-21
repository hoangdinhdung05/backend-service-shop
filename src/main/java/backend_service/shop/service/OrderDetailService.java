package backend_service.shop.service;

import backend_service.shop.dto.response.OrderDetailResponse;
import backend_service.shop.dto.response.ProductSalesReport;
import backend_service.shop.dto.response.system.PageResponse;
import java.util.List;

public interface OrderDetailService {

    /**
     * Lấy danh sách chi tiết của 1 đơn hàng.
     */
    List<OrderDetailResponse> getOrderDetailsByOrderId(Long orderId);

    /**
     * Lấy chi tiết đơn hàng theo ID (nếu cần).
     */
    OrderDetailResponse getOrderDetailById(Long detailId);

    /**
     * Admin: thống kê tất cả chi tiết đơn hàng theo phân trang.
     */
    PageResponse<?> getAllOrderDetails(int page, int size);

    /**
     * Thống kê: tổng số sản phẩm đã bán, doanh thu, v.v.
     */
    List<ProductSalesReport> getTopSellingProducts(int limit);

}
