package backend_service.shop.controller;

import backend_service.shop.dto.request.OrderRequestDTO;
import backend_service.shop.dto.response.OrderResponse;
import backend_service.shop.dto.response.system.PageResponse;
import backend_service.shop.dto.response.system.ResponseData;
import backend_service.shop.dto.response.system.ResponseError;
import backend_service.shop.service.OrderService;
import backend_service.shop.util.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Controller")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create a new order", description = "Send a request to create a new order")
    @PostMapping("/create")
    public ResponseData<?> createOrder(@Valid @RequestBody OrderRequestDTO orderRequest) {
        try {

            OrderResponse response = orderService.createOrder(orderRequest);

            return new ResponseData<>(HttpStatus.CREATED.value(), "Create order API successfully", response);

        } catch (Exception e) {
            log.info("Create order fail. Error={}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get a order by order id", description = "Send a request to get a order by orer id")
    @GetMapping("/{orderId}")
    public ResponseData<?> getOrderByOrderId(@PathVariable @Min(value = 1, message = "Order id must be greater than or equal to 1") Long orderId) {
        try {

            OrderResponse response = orderService.getOrderById(orderId);

            return new ResponseData<>(HttpStatus.OK.value(), "Get information order successfully", response);

        } catch (Exception e) {
            log.info("Get information order fail. Error={}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get all orders of a user", description = "Retrieve orders placed by a specific user")
    @GetMapping("/user/{userId}")
    public ResponseData<?> getOrdersByUser(@PathVariable @Min(1) Long userId) {
        try {
            var responses = orderService.getOrdersByUser(userId);
            return new ResponseData<>(HttpStatus.OK.value(), "Get orders by user successfully", responses);
        } catch (Exception e) {
            log.error("Get user orders failed. error={}", e.getMessage(), e);
            return new ResponseError(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @Operation(summary = "Cancel an order", description = "Cancel an order by its ID")
    @PatchMapping("/{orderId}/cancel")
    public ResponseData<?> cancelOrder(@PathVariable @Min(1) Long orderId) {
        try {
            orderService.cancelOrder(orderId);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "Cancel order successfully");
        } catch (Exception e) {
            log.error("Cancel order failed. error={}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Change status of an order", description = "Update the status of an order")
    @PatchMapping("/{orderId}/status")
    public ResponseData<?> changeOrderStatus(@PathVariable @Min(1) Long orderId, @RequestBody OrderStatus newStatus) {
        try {

            OrderResponse response = orderService.changeStatus(orderId, newStatus);

            return new ResponseData<>(HttpStatus.OK.value(), "Change status successfully", response);

        } catch (Exception e) {
            log.error("Change status failed. error={}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get all orders with pagination", description = "Get paginated list of orders")
    @GetMapping("/list")
    public ResponseData<?> getAllOrder(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") @Min(1) int size) {
        try {

            PageResponse<?> response = orderService.getAllOrders(page, size);

            return new ResponseData<>(HttpStatus.OK.value(), "Get paginated orders", response);
        } catch (Exception e) {
            log.info("Get all orders fail. Error={}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

}
