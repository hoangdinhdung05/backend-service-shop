package backend_service.shop.service;

import backend_service.shop.dto.request.UserRequestDTO;
import backend_service.shop.dto.response.system.PageResponse;
import backend_service.shop.dto.response.UserDetailResponse;
import backend_service.shop.entity.User;
import backend_service.shop.util.UserStatus;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {

//    UserDetailsService userDetailsService();

    User getByUsername(String userName);

//    User getUserByUsername(String userName);

    long saveUser(UserRequestDTO requestDTO);

    void updateUser(long userId, UserRequestDTO requestDTO);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);

    PageResponse<?> getAllUsers(int pageNo, int pageSize);

    List<String> findAllRolesByUserId(long userId);
}
