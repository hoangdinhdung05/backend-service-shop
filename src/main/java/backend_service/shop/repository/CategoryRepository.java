package backend_service.shop.repository;

import backend_service.shop.entity.Category;
import java.util.List;

public interface CategoryRepository extends CustomRepository<Category, Long> {
    List<Category> findByParentIsNull(); // Get list parent category
}
