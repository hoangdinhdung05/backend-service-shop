package backend_service.shop.controller;

import backend_service.shop.dto.request.ProductRequestDTO;
import backend_service.shop.dto.response.ProductResponse;
import backend_service.shop.dto.response.system.PageResponse;
import backend_service.shop.dto.response.system.ResponseData;
import backend_service.shop.dto.response.system.ResponseError;
import backend_service.shop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/product")
@Validated
@Slf4j
@Tag(name = "Product Controller")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Create product", description = "Send a request to create a new product")
    @PostMapping("/create")
    public ResponseData<?> createProduct(@Valid @ModelAttribute ProductRequestDTO request,
                                         @RequestParam(required = false) MultipartFile thumbnail,
                                         @RequestParam(required = false) List<MultipartFile> images) {
        log.info("Request to create product: {}", request.getName());

        try {
            ProductResponse response = productService.createProduct(request, thumbnail, images);
            return new ResponseData<>(HttpStatus.CREATED.value(), "Create product successfully", response);
        } catch (Exception e) {
            log.error("Create product failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Create product failed");
        }
    }

    @Operation(summary = "Update product", description = "Send a request to update an existing product")
    @PutMapping("/{productId}")
    public ResponseData<?> updateProduct(@PathVariable @Min(1) Long productId,
                                         @Valid @ModelAttribute ProductRequestDTO request,
                                         @RequestParam(required = false) MultipartFile thumbnail,
                                         @RequestParam(required = false) List<MultipartFile> images) {
        log.info("Request to update product: {}", productId);

        try {
            ProductResponse response = productService.updateProduct(productId, request, thumbnail, images);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "Update product successfully", response);
        } catch (Exception e) {
            log.error("Update product failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update product failed");
        }
    }

    @Operation(summary = "Delete product", description = "Send a request to delete product by ID")
    @DeleteMapping("/{productId}")
    public ResponseData<?> deleteProduct(@PathVariable @Min(1) Long productId) {
        log.info("Request to delete product: {}", productId);

        try {
            productService.deleteProduct(productId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete product successfully");
        } catch (Exception e) {
            log.error("Delete product failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete product failed");
        }
    }

    @Operation(summary = "Get product by ID", description = "Send a request to retrieve a product by ID")
    @GetMapping("/{productId}")
    public ResponseData<?> getProductById(@PathVariable @Min(1) Long productId) {
        log.info("Request to get product detail: {}", productId);

        try {
            ProductResponse response = productService.getProductById(productId);
            return new ResponseData<>(HttpStatus.OK.value(), "Get product successfully", response);
        } catch (Exception e) {
            log.error("Get product failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get product failed");
        }
    }

    @Operation(summary = "Get paginated product list", description = "Send a request to get paginated product list")
    @GetMapping("/list")
    public ResponseData<?> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") @Min(1) int size) {
        log.info("Request to get product list: page={}, size={}", page, size);

        try {
            PageResponse<?> response = productService.getListProduct(page, size);
            return new ResponseData<>(HttpStatus.OK.value(), "Get product list successfully", response);
        } catch (Exception e) {
            log.error("Get product list failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Get product list failed");
        }
    }
}
