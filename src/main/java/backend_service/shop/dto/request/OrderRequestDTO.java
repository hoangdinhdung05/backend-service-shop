package backend_service.shop.dto.request;

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
public class OrderRequestDTO implements Serializable {
    private Long userId;
    private String shippingAddress;
    private String note;
    private PaymentMethod paymentMethod;
    private BigDecimal discount;
    private List<OrderDetailRequestDTO> orderDetails;
}