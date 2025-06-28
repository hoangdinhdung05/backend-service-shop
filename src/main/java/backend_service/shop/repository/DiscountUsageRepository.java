package backend_service.shop.repository;

import backend_service.shop.entity.DiscountUsage;

public interface DiscountUsageRepository extends CustomRepository<DiscountUsage, Long> {

    int countByUserIdAndDiscount_Code(Long userId, String discountCode);

}
