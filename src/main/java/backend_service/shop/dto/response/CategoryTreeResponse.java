package backend_service.shop.dto.response;

import lombok.*;
import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryTreeResponse implements Serializable {
    private Long id;
    private String name;
    private List<CategoryTreeResponse> children;
}
