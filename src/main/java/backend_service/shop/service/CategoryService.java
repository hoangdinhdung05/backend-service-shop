package backend_service.shop.service;

import backend_service.shop.dto.request.CategoryCreateRequest;
import backend_service.shop.dto.response.CategoryResponse;
import backend_service.shop.dto.response.CategoryTreeResponse;
import backend_service.shop.dto.response.system.PageResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryCreateRequest request);

    CategoryResponse update(Long id, CategoryCreateRequest request);

    void delete(Long id);

    CategoryResponse getCategoryById(Long categoryId);

    PageResponse<?> getListCategory(int page, int size);

    List<CategoryTreeResponse> getAllCategoryTree();

}
