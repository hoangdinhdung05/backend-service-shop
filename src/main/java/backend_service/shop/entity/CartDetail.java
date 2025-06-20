package backend_service.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "CartDetail")
@Table(name = "tbl_cart_detail")
public class CartDetail extends AbstractEntity<Long> {

    //Join to table product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // Nếu có biến thể (màu, size…), liên kết đến bảng variant (tuỳ chọn)
    // @ManyToOne
    // private ProductVariant variant;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice; // snapshot giá tại thời điểm thêm vào giỏ

    @Column(name = "total_price", precision = 15, scale = 2)
    private BigDecimal totalPrice; // quantity * unitPrice

}
