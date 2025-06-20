package backend_service.shop.repository;

import backend_service.shop.entity.CartDetail;
import java.util.List;
import java.util.Optional;

public interface CartDetailRepository extends CustomRepository<CartDetail, Long> {

    List<CartDetail> findByCartId(Long cartId);

    Optional<CartDetail> findByCartIdAndProductId(Long cartId, Long productId);

}
