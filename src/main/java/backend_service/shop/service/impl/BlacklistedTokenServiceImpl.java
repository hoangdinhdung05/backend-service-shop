package backend_service.shop.service.impl;

import backend_service.shop.entity.BlacklistedToken;
import backend_service.shop.repository.BlacklistedTokenRepository;
import backend_service.shop.service.AuthService.BlacklistedTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlacklistedTokenServiceImpl implements BlacklistedTokenService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    /**
     * @param token
     * @param expiryMillis
     */
    @Override
    public void blacklistToken(String token, long expiryMillis) {

        log.info("====Save token in DB=====");

        blacklistedTokenRepository.save(BlacklistedToken.builder()
                        .token(token)
                        .expiredAt(LocalDateTime.now().plusMinutes(expiryMillis))
                        .build());
    }

    /**
     * @param token
     * @return
     */
    @Override
    public boolean isBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }
}
