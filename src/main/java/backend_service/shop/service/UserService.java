package backend_service.shop.service;

import backend_service.shop.dto.request.UserRequestDTO;
import backend_service.shop.dto.response.PageResponse;
import backend_service.shop.dto.response.UserDetailResponse;
import backend_service.shop.entity.User;
import backend_service.shop.util.UserStatus;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {

    UserDetailsService userDetailsService();

    User getUserByUsername(String userName);

    long saveUser(UserRequestDTO requestDTO);

    void updateUser(long userId, UserRequestDTO requestDTO);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);

    PageResponse<?> getAllUsers(int pageNo, int pageSize);
}
