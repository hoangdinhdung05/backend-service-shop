package backend_service.shop.service.impl;

import backend_service.shop.dto.request.ProductRequestDTO;
import backend_service.shop.dto.response.ProductResponse;
import backend_service.shop.dto.response.system.PageResponse;
import backend_service.shop.entity.Category;
import backend_service.shop.entity.Product;
import backend_service.shop.entity.ProductImage;
import backend_service.shop.exception.ResourceNotFoundException;
import backend_service.shop.repository.CategoryRepository;
import backend_service.shop.repository.ProductImageRepository;
import backend_service.shop.repository.ProductRepository;
import backend_service.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Create product and upload list image
     *
     * @param request
     * @param thumbnail
     * @param images
     * @return
     */
    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequestDTO request, MultipartFile thumbnail, List<MultipartFile> images) {
        Category category = getCategoryById(request.getCategoryId());

        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        product.setCategory(category);

        if (thumbnail != null && !thumbnail.isEmpty()) {
            product.setThumbnail("/uploads/" + saveFile(thumbnail));
        }

        Product savedProduct = productRepository.save(product);

        if (images != null && !images.isEmpty()) {
            List<ProductImage> productImages = images.stream()
                    .map(file -> ProductImage.builder()
                            .imageUrl("/uploads/" + saveFile(file))
                            .product(savedProduct)
                            .build())
                    .collect(Collectors.toList());
            productImageRepository.saveAll(productImages);
            savedProduct.setImages(productImages);
        }

        log.info("Created product successfully. productId={}", savedProduct.getId());
        return toProductResponse(savedProduct);
    }

    /**
     * Update product
     *
     * @param id
     * @param request
     * @param thumbnail
     * @param images
     * @return
     */
    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequestDTO request, MultipartFile thumbnail, List<MultipartFile> images) {
        Product product = getProductByIdFromDb(id);
        Category category = getCategoryById(request.getCategoryId());

        BeanUtils.copyProperties(request, product);
        product.setCategory(category);

        if (thumbnail != null && !thumbnail.isEmpty()) {
            product.setThumbnail("/uploads/" + saveFile(thumbnail));
        }

        // delete old image before update new image
        if (images != null && !images.isEmpty()) {
            productImageRepository.deleteAllByProductId(product.getId());
            List<ProductImage> newImages = images.stream()
                    .map(file -> ProductImage.builder()
                            .imageUrl("/uploads/" + saveFile(file))
                            .product(product)
                            .build())
                    .collect(Collectors.toList());
            productImageRepository.saveAll(newImages);
            product.setImages(newImages);
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Updated product successfully. productId={}", updatedProduct.getId());
        return toProductResponse(updatedProduct);
    }

    /**
     * Delete product by id
     *
     * @param id
     */
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductByIdFromDb(id);
        productImageRepository.deleteAllByProductId(product.getId());
        productRepository.delete(product);
        log.info("Deleted product successfully. productId={}", id);
    }

    /**
     * Get product by id
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = getProductByIdFromDb(id);
        return toProductResponse(product);
    }

    /**
     * Get list product per page and size
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<?> getListProduct(int page, int size) {
        Page<Product> productPage = productRepository.findAll(PageRequest.of(page, size));
        List<ProductResponse> items = productPage.stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());

        return PageResponse.builder()
                .page(page)
                .size(size)
                .total(productPage.getTotalPages())
                .items(items)
                .build();
    }

    private ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .shortDescription(product.getShortDescription())
                .price(product.getPrice())
                .salePrice(product.getSalePrice())
                .sku(product.getSku())
                .thumbnail(product.getThumbnail())
                .status(product.getStatus())
                .tag(product.getTag())
                .categoryId(product.getCategory().getId())
                .imageUrls(product.getImages() != null
                        ? product.getImages().stream().map(ProductImage::getImageUrl).toList()
                        : List.of())
                .build();
    }

    private String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + filename);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }

    private Product getProductByIdFromDb(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id=" + id));
    }

    private Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id=" + id));
    }
}
