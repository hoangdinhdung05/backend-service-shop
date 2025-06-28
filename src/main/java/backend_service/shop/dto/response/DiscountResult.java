package backend_service.shop.dto.response;

import backend_service.shop.entity.Discount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class DiscountResult {
    private final Discount discount;
    private final BigDecimal discountAmount;
}
