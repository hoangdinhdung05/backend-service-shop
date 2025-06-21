package backend_service.shop.dto.request;

import backend_service.shop.util.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "Shipping address not blank")
    private String shippingAddress;

    private String note;

    @NotNull(message = "Payment method not null")
    private PaymentMethod paymentMethod;

    private BigDecimal discount;
    private List<OrderDetailRequestDTO> orderDetails;
}