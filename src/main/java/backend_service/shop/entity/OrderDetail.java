package backend_service.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "OrderDetail")
@Table(name = "tbl_order_detail")
public class OrderDetail extends AbstractEntity<Long> {

    // Mỗi dòng đơn hàng thuộc về 1 đơn hàng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Sản phẩm được đặt
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // (tuỳ chọn) Biến thể sản phẩm nếu có
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "variant_id")
    // private ProductVariant variant;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", precision = 15, scale = 2)
    private BigDecimal totalPrice;

}
