package backend_service.shop.dto.request;

import backend_service.shop.util.ProductStatus;
import backend_service.shop.util.ProductTag;
import jakarta.validation.constraints.*;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO implements Serializable {
    @NotBlank
    @Size(max = 150)
    private String name;

    @Size(max = 255)
    private String slug;

    private String description;

    private String shortDescription;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal salePrice;

    @NotNull
    @Min(0)
    private Integer stockQuantity;

    @Size(max = 50)
    private String sku;

    private String thumbnail;

    private ProductStatus status;

    private ProductTag tag;

    @NotNull
    private Long categoryId;

    private List<String> imageUrls;
}
