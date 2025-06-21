package backend_service.shop.service.AuthService;

import backend_service.shop.entity.User;
import backend_service.shop.repository.UserRepository;
import backend_service.shop.util.AuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        AuthProvider provider = AuthProvider.valueOf(
                userRequest.getClientRegistration().getRegistrationId().toUpperCase()
        );

        String providerId = String.valueOf(Objects.requireNonNull(oAuth2User.getAttribute("id")));
        String email = oAuth2User.getAttribute("email");
        String username = oAuth2User.getAttribute("login"); // GitHub: "login", Google: "name" hoặc "given_name"

        // Fallback nếu email không tồn tại (thường gặp ở GitHub)
        if (email == null && provider == AuthProvider.GITHUB) {
            email = fetchGithubEmail(userRequest.getAccessToken().getTokenValue());
        }

        if (email == null) {
            email = provider.name() + "_" + providerId + "@noemail.com";
            log.warn("Email not provided by {}, generated dummy email: {}", provider, email);
        }

        log.info("OAuth2 login - provider: {}, providerId: {}, email: {}, username: {}", provider, providerId, email, username);

        // Find or create new user
        String finalEmail = email;
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            log.info("New OAuth2 user, creating: {}", finalEmail);
            return newUser;
        });

        user.setProvider(provider);
        user.setProviderId(providerId);
        user.setUsername(username);
        user.setEmail(email);

        userRepository.save(user);
        return oAuth2User;
    }

    private String fetchGithubEmail(String accessToken) {
        log.info("Fetching GitHub email from API...");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            for (Map<String, Object> emailEntry : response.getBody()) {
                Boolean primary = (Boolean) emailEntry.get("primary");
                Boolean verified = (Boolean) emailEntry.get("verified");
                if (Boolean.TRUE.equals(primary) && Boolean.TRUE.equals(verified)) {
                    String email = (String) emailEntry.get("email");
                    log.info("Primary verified email found: {}", email);
                    return email;
                }
            }
        }

        log.warn("No verified primary email found from GitHub");
        return null;
    }
}
