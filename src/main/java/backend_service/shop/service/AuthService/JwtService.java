package backend_service.shop.service.AuthService;

import backend_service.shop.util.TokenType;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtService {

    String generateToken(UserDetails user);

    String generateRefreshToken(UserDetails user);

    String extractUsername(String token, TokenType type);

    boolean isValid(String token, TokenType type, UserDetails user);

    Date extractExpiration(String token, TokenType type);

}
