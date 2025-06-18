package backend_service.shop.repository;

import backend_service.shop.entity.Token;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public interface TokenRepository extends CustomRepository<Token, Integer> {

    Optional<Token> findByUsername(String username);

}
