package backend_service.shop.entity;

import backend_service.shop.util.OrderStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "OrderHistory")
@Table(name = "tbl_order_history")
public class OrderHistory extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    private OrderStatus fromStatus;
    private OrderStatus toStatus;

    private String updatedBy; // admin or system

}
