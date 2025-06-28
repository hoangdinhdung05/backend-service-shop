package backend_service.shop.repository;

import backend_service.shop.entity.BlacklistedToken;
import java.util.Optional;

public interface BlacklistedTokenRepository extends CustomRepository<BlacklistedToken, Long> {

    Optional<BlacklistedToken> findByToken(String token);

    boolean existsByToken(String token);

}
