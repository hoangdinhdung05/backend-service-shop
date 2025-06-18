package backend_service.shop.service;

import backend_service.shop.dto.request.ProductRequestDTO;
import backend_service.shop.dto.response.ProductResponse;
import backend_service.shop.dto.response.system.PageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequestDTO request, MultipartFile thumbnail, List<MultipartFile> images);

    ProductResponse updateProduct(Long id, ProductRequestDTO request, MultipartFile thumbnail, List<MultipartFile> images);

    void deleteProduct(Long id);

    ProductResponse getProductById(Long id);

    PageResponse<?> getListProduct(int page, int size);
}