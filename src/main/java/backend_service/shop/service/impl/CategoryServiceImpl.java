package backend_service.shop.service.impl;

import backend_service.shop.dto.request.CategoryCreateRequest;
import backend_service.shop.dto.response.CategoryResponse;
import backend_service.shop.dto.response.CategoryTreeResponse;
import backend_service.shop.dto.response.system.PageResponse;
import backend_service.shop.entity.Category;
import backend_service.shop.exception.ResourceNotFoundException;
import backend_service.shop.repository.CategoryRepository;
import backend_service.shop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Save category in database
     *
     * @param request
     * @return
     */
    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        Category parent = null;

        if(request.getParentId() != null) {
//            log.info("Check parentId={}", request.getParentId());
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
        }

        Category category = Category.builder()
                .name(request.getName())
                .status(request.getStatus())
                .isHot(request.getIsHot())
                .isNew(request.getIsNew())
                .parent(parent)
                .build();
        categoryRepository.save(category);

        log.info("Create successfully category with categoryId={}", category.getId());

        return toCategoryResponse(category);
    }

    /**
     * Update category by category id
     *
     * @param id
     * @param request
     * @return
     */
    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryCreateRequest request) {

        //check category from database
        Category category = getCategoryFromDb(id);

        //check parentId
        if (request.getParentId() != null) {
            if (id.equals(request.getParentId())) {
                throw new IllegalArgumentException("Category cannot be its own parent.");
            }
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("ParentId not found"));
            category.setParent(parent);
        }

        category.setName(request.getName());
        category.setStatus(request.getStatus());
        category.setIsNew(request.getIsNew());
        category.setIsHot(request.getIsHot());

        Category categoryNew = categoryRepository.save(category);
        log.info("Update category by category id successfully. id={}", id);
        return toCategoryResponse(categoryNew);
    }

    /**
     * Delete category by id
     *
     * @param id
     */
    @Override
    @Transactional
    public void delete(Long id) {
        Category category = getCategoryFromDb(id);
        categoryRepository.delete(category);

        log.info("Delete category successfully. Id={}", category.getId());
    }

    /**
     * Get detail category by id
     *
     * @param categoryId
     * @return
     */
    @Override
    @Transactional
    public CategoryResponse getCategoryById(Long categoryId) {
        Category category = getCategoryFromDb(categoryId);
        return toCategoryResponse(category);
    }

    /**
     * Get list category per page and size
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    @Transactional
    public PageResponse<?> getListCategory(int page, int size) {

        Page<Category> categoryPage = categoryRepository.findAll(PageRequest.of(page, size));
        List<CategoryResponse> responseList = categoryPage.stream()
                .map(this::toCategoryResponse)
                .toList();

        return PageResponse.builder()
                .page(page)
                .size(size)
                .total(categoryPage.getTotalPages())
                .items(responseList)
                .build();
    }

    @Override
    @Transactional
    public List<CategoryTreeResponse> getAllCategoryTree() {
        List<Category> categories = categoryRepository.findByParentIsNull();
        return categories.stream()
                .map(this::convertToTree)
                .collect(Collectors.toList());
    }

    /**
     * Convert to category tree
     *
     * @param category
     * @return
     */
    private CategoryTreeResponse convertToTree(Category category) {
        return CategoryTreeResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .children(
                        category.getChildren() == null ? List.of() :
                                category.getChildren().stream()
                                        .map(this::convertToTree)
                                        .collect(Collectors.toList())
                )
                .build();
    }

    /**
     * Convert to category response data
     *
     * @param category
     * @return
     */
    private CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .status(category.getStatus())
                .build();
    }

    /**
     * Check category by id
     *
     * @param id
     * @return
     */
    private Category getCategoryFromDb(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
}
