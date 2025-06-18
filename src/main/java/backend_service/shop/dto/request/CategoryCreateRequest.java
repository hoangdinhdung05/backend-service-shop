package backend_service.shop.dto.request;

import backend_service.shop.util.CategoryStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
public class CategoryCreateRequest implements Serializable {

    @NotBlank(message = "name is not blank")
    private String name;
    private CategoryStatus status;
    private Boolean isHot;
    private Boolean isNew;
    private Long parentId;

}
