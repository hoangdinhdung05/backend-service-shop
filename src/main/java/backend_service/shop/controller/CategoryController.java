package backend_service.shop.controller;

import backend_service.shop.config.Translator;
import backend_service.shop.dto.request.CategoryCreateRequest;
import backend_service.shop.dto.response.CategoryResponse;
import backend_service.shop.dto.response.CategoryTreeResponse;
import backend_service.shop.dto.response.system.PageResponse;
import backend_service.shop.dto.response.system.ResponseData;
import backend_service.shop.dto.response.system.ResponseError;
import backend_service.shop.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/category")
@Validated
@Slf4j
@Tag(name = "Category Controller")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Create category", description = "Send a request to create a new category")
    @PostMapping("/create")
    public ResponseData<?> createCategory(@RequestBody @Valid CategoryCreateRequest request) {
        log.info("Request create category, name={}", request.getName());

        try {

            CategoryResponse response = categoryService.createCategory(request);
            return new ResponseData<>(HttpStatus.CREATED.value(), "Create category successfully", response);

        } catch (Exception e) {
            log.info("Create category fail: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Create category fail");
        }
    }

    @Operation(summary = "Update category", description = "Send a request to update category by category")
    @PutMapping("/{categoryId}")
    public ResponseData<?> updateCategory(@PathVariable @Min(value = 1, message = "CategoryId must greater than one") long categoryId,
                                          @RequestBody @Valid CategoryCreateRequest request) {
        log.info("Request update category by id={}", categoryId);

        try {

            CategoryResponse response = categoryService.update(categoryId, request);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "Update category successfully", response);

        } catch (Exception e) {
            log.info("Request update category fail: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update category fail");
        }
    }

    @Operation(summary = "Delete category", description = "Send a request to delete category by category id")
    @DeleteMapping("/{categoryId}")
    public ResponseData<?> deleteCategory(@PathVariable @Min(1) Long categoryId) {
        log.info("Request to delete categoryId={}", categoryId);

        try {
            categoryService.delete(categoryId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), Translator.toLocale("category.del.success"));
        } catch (Exception e) {
            log.error("Delete category failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete category failed");
        }
    }

    @Operation(summary = "Get category by ID", description = "Send a request to retrieve category by ID")
    @GetMapping("/{categoryId}")
    public ResponseData<?> getCategory(@PathVariable @Min(1) Long categoryId) {
        log.info("Request to get category detail, categoryId={}", categoryId);

        try {
            CategoryResponse response = categoryService.getCategoryById(categoryId);
            return new ResponseData<>(HttpStatus.OK.value(), "Get category detail successfully", response);
        } catch (Exception e) {
            log.error("Get category failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get paginated list of categories", description = "Send a request to get category list by pageNo and pageSize")
    @GetMapping("/list")
    public ResponseData<?> getAllCategories(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") @Min(1) int size) {
        log.info("Request to get category list: page={}, size={}", page, size);

        try {
            PageResponse<?> response = categoryService.getListCategory(page, size);
            return new ResponseData<>(HttpStatus.OK.value(), "Get category list successfully", response);
        } catch (Exception e) {
            log.error("Get category list failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get category tree", description = "Send a request to get category tree (nested parent-child structure)")
    @GetMapping("/tree")
    public ResponseData<?> getCategoryTree() {
        log.info("Request to get category tree");

        try {
            List<CategoryTreeResponse> response = categoryService.getAllCategoryTree();
            return new ResponseData<>(HttpStatus.OK.value(), "Get category tree successfully", response);
        } catch (Exception e) {
            log.error("Get category tree failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get category tree failed");
        }
    }
}
