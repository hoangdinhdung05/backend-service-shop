package backend_service.shop.dto.request;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDetailRequestDTO implements Serializable {
    private Long productId;
    private Integer quantity;
}
