package backend_service.shop.service;

import backend_service.shop.dto.request.UserRequestDTO;
import backend_service.shop.dto.response.UserDetailResponse;
import backend_service.shop.util.UserStatus;

public interface UserService {
    long saveUser(UserRequestDTO requestDTO);
    void updateUser(long userId, UserRequestDTO requestDTO);
    void changeStatus(long userId, UserStatus status);
    void deleteUser(long userId);
    UserDetailResponse getUser(long userId);
}
