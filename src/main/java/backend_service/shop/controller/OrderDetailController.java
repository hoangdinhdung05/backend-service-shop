package backend_service.shop.controller;

import backend_service.shop.dto.response.OrderDetailResponse;
import backend_service.shop.dto.response.ProductSalesReport;
import backend_service.shop.dto.response.system.PageResponse;
import backend_service.shop.dto.response.system.ResponseData;
import backend_service.shop.dto.response.system.ResponseError;
import backend_service.shop.service.OrderDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/order-detail")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Detail Controller")
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    @Operation(summary = "Get order details by order ID")
    @GetMapping("/order/{orderId}")
    public ResponseData<?> getDetailsByOrderId(@PathVariable @Min(1) Long orderId) {
        try {
            List<OrderDetailResponse> responses = orderDetailService.getOrderDetailsByOrderId(orderId);
            return new ResponseData<>(HttpStatus.OK.value(), "Get order details successfully", responses);
        } catch (Exception e) {
            log.error("Failed to get order details by orderId. error={}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get order detail by detail ID")
    @GetMapping("/{detailId}")
    public ResponseData<?> getDetailById(@PathVariable @Min(1) Long detailId) {
        try {
            OrderDetailResponse response = orderDetailService.getOrderDetailById(detailId);
            return new ResponseData<>(HttpStatus.OK.value(), "Get order detail successfully", response);
        } catch (Exception e) {
            log.error("Failed to get order detail by ID. error={}", e.getMessage(), e);
            return new ResponseError(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @Operation(summary = "Admin: Get all order details with pagination")
    @GetMapping("/list")
    public ResponseData<?> getAllOrderDetails(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") @Min(1) int size) {
        try {
            PageResponse<?> response = orderDetailService.getAllOrderDetails(page, size);
            return new ResponseData<>(HttpStatus.OK.value(), "Get all order details successfully", response);
        } catch (Exception e) {
            log.error("Failed to get all order details. error={}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get top selling products", description = "Returns a list of top selling products")
    @GetMapping("/top-selling")
    public ResponseData<?> getTopSellingProducts(@RequestParam(defaultValue = "5") @Min(1) int limit) {
        try {
            List<ProductSalesReport> reports = orderDetailService.getTopSellingProducts(limit);
            return new ResponseData<>(HttpStatus.OK.value(), "Get top selling products successfully", reports);
        } catch (Exception e) {
            log.error("Failed to get top selling products. error={}", e.getMessage(), e);
            return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }
}
