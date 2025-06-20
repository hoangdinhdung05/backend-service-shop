package backend_service.shop.dto.response;

import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse implements Serializable {
    private Long id;
    private Long userId;
    private List<CartDetailResponse> cartDetails;
    private Integer totalQuantity;
    private BigDecimal totalPrice;
}
