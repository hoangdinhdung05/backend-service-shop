package backend_service.shop.repository;

import backend_service.shop.entity.ProductImage;

public interface ProductImageRepository extends CustomRepository<ProductImage, Long> {
    void deleteAllByProductId(Long id);
}
