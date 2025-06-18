package backend_service.shop.dto.response;

import backend_service.shop.util.CategoryStatus;
import lombok.*;
import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
public class CategoryResponse implements Serializable {

    private long id;
    private String name;
    private Long parentId;
    private CategoryStatus status;
}