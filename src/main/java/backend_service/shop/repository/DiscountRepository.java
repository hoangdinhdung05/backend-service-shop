package backend_service.shop.repository;

import backend_service.shop.entity.Discount;
import java.util.Optional;

public interface DiscountRepository extends CustomRepository<Discount, Long> {

//    Optional<Discount> findByCodeAndStatus(String code);

    Optional<Discount> findByCodeIgnoreCase(String code);

    boolean existsByCode(String code);

}
