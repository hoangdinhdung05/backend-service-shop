package backend_service.shop.service.AuthService;

public interface BlacklistedTokenService {

    void blacklistToken(String token, long expiryMillis);

    boolean isBlacklisted(String token);

}
