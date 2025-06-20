package backend_service.shop.dto.request;

import backend_service.shop.entity.CartDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.io.Serializable;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CartRequestDTO implements Serializable {

    private Long userId;
    private List<CartDetail> cartDetails;

}
