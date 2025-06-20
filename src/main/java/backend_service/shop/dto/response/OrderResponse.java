package backend_service.shop.dto.response;

import backend_service.shop.util.OrderStatus;
import backend_service.shop.util.PaymentMethod;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse implements Serializable {
    private Long id;
    private Long userId;
    private String shippingAddress;
    private String note;
    private PaymentMethod paymentMethod;
    private OrderStatus status;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private Integer totalQuantity;
    private List<OrderDetailResponse> orderDetails;
}
