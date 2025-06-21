package backend_service.shop.dto.response;

import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSalesReport {
    private Long productId;
    private String productName;
    private Long totalQuantitySold;
    private BigDecimal totalRevenue;
}
