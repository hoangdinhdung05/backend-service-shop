package backend_service.shop.entity;

import backend_service.shop.util.DiscountStatus;
import backend_service.shop.util.DiscountType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Discount")
@Table(name = "tbl_discount")
public class Discount extends AbstractEntity<Long> {

    @Column(nullable = false, unique = true)
    private String code;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType; // FIXED, PERCENTAGE

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    private Integer maxUses;
    private Integer maxUsesPerUser;

    private BigDecimal minOrderAmount;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_status", nullable = false)
    private DiscountStatus status;

}
