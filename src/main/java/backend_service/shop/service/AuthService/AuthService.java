package backend_service.shop.service.AuthService;

import backend_service.shop.dto.request.SignInRequest;
import backend_service.shop.dto.response.TokenResponse;
import backend_service.shop.entity.Token;
import backend_service.shop.exception.InvalidDataException;
import backend_service.shop.repository.UserRepository;
import backend_service.shop.service.UserService;
import backend_service.shop.util.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import static backend_service.shop.util.TokenType.ACCESS_TOKEN;
import static backend_service.shop.util.TokenType.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final BlacklistedTokenService blacklistedTokenService;

    public TokenResponse authenticate(SignInRequest signInRequest) {
        log.info("---------- authenticate ----------");

        var user = userService.getByUsername(signInRequest.getUsername());

        List<String> roles = userService.findAllRolesByUserId(user.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        //check exists
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                signInRequest.getUsername(),
                signInRequest.getPassword(),
                authorities));

        // create new access token
        String accessToken = jwtService.generateToken(user);

        // create new refresh token
        String refreshToken = jwtService.generateRefreshToken(user);

//        String accessToken = "HOANGDUNG";
//        String refreshToken = "HOANGDUNG";

        // save token to db
        tokenService.save(Token.builder()
                .username(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    /**
     * Refresh token
     *
     * @param request
     * @return
     */
    public TokenResponse refreshToken(HttpServletRequest request) {
        log.info("---------- refreshToken ----------");

        final String authHeader = request.getHeader("Authorization");
        if (StringUtils.isBlank(authHeader) || !authHeader.startsWith("Bearer ")) {
            throw new InvalidDataException("Refresh token must be provided in Authorization header");
        }

        final String refreshToken = authHeader.substring(7);

        final String userName = jwtService.extractUsername(refreshToken, REFRESH_TOKEN);
        var user = userService.getByUsername(userName);

        if (!jwtService.isValid(refreshToken, REFRESH_TOKEN, user)) {
            throw new InvalidDataException("Invalid refresh token");
        }

//        if (blacklistedTokenService.isBlacklisted(refreshToken)) {
//            throw new InvalidDataException("Refresh token has been invalidated");
//        }

        // Tạo token mới
        String accessToken = jwtService.generateToken(user);
        // Optionally: tạo refresh token mới
        // String newRefreshToken = jwtService.generateRefreshToken(user);

        // Lưu token mới
        tokenService.save(Token.builder()
                .username(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken) // hoặc newRefreshToken
                .build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken) // hoặc newRefreshToken
                .userId(user.getId())
                .build();
    }


    /**
     * Logout
     *
     * @param request
     * @return
     */
    public String logout(HttpServletRequest request) {
        log.info("---------- logout ----------");

        final String authHeader = request.getHeader("Authorization");
        if (StringUtils.isBlank(authHeader) || !authHeader.startsWith("Bearer ")) {
            throw new InvalidDataException("Authorization header is missing or invalid");
        }

        final String token = authHeader.substring(7);
        final String userName = jwtService.extractUsername(token, ACCESS_TOKEN);

        tokenService.delete(userName);

        Date expiry = jwtService.extractExpiration(token, ACCESS_TOKEN);
        long millisUntilExpiry = expiry.getTime() - System.currentTimeMillis();
        blacklistedTokenService.blacklistToken(token, millisUntilExpiry);

        return "Deleted!";
    }

}
