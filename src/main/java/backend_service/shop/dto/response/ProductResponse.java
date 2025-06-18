package backend_service.shop.dto.response;

import backend_service.shop.util.ProductStatus;
import backend_service.shop.util.ProductTag;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse implements Serializable {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String shortDescription;
    private BigDecimal price;
    private BigDecimal salePrice;
    private Integer stockQuantity;
    private String sku;
    private String thumbnail;
    private ProductStatus status;
    private ProductTag tag;
    private Long categoryId;
    private List<String> imageUrls;
}