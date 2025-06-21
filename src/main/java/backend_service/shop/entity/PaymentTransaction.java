package backend_service.shop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "PaymentTransaction")
@Table(name = "tbl_paymentTransaction")
public class PaymentTransaction extends AbstractEntity<Long> {

    @ManyToOne
    private Order order;

    private String transactionId;
    private String bankCode;
    private Boolean isSuccess;
    private LocalDateTime paidAt;

}
