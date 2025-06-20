package backend_service.shop.repository;

import backend_service.shop.entity.Cart;
import backend_service.shop.entity.CartDetail;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends CustomRepository<Cart, Long> {

    Optional<Cart> findByUserId(long userId);

    Optional<CartDetail> findCartDetailById(long id);



}
