package backend_service.shop.dto.response;

import backend_service.shop.util.DiscountStatus;
import backend_service.shop.util.DiscountType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountResponse {

    private Long id;
    private String code;
    private String description;
    private DiscountType discountType;
    private BigDecimal value;
    private BigDecimal minOrderAmount;
    private Integer maxUses;
    private Integer maxUsesPerUser;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private DiscountStatus status;

}
