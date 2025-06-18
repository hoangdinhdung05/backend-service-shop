package backend_service.shop.service.impl;

import backend_service.shop.dto.request.AddressRequestDTO;
import backend_service.shop.dto.request.UserRequestDTO;
import backend_service.shop.dto.response.system.PageResponse;
import backend_service.shop.dto.response.UserDetailResponse;
import backend_service.shop.entity.Address;
import backend_service.shop.entity.User;
import backend_service.shop.exception.ResourceNotFoundException;
import backend_service.shop.repository.UserRepository;
import backend_service.shop.service.UserService;
import backend_service.shop.util.UserStatus;
import backend_service.shop.util.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public User getByUsername(String userName) {
        return this.userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Save new User to DB
     *
     * @param requestDTO
     * @return
     */
    @Override
    public long saveUser(UserRequestDTO requestDTO) {
        User user = User.builder()
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .username(requestDTO.getUsername())
                .email(requestDTO.getEmail())
                .password(requestDTO.getPassword())
                .gender(requestDTO.getGender())
                .phoneNumber(requestDTO.getPhone())
                .userType(UserType.valueOf(requestDTO.getType().toUpperCase()))
                .userStatus(requestDTO.getStatus())
                .addresses(convertToAddress(requestDTO.getAddresses()))
                .build();
        this.userRepository.save(user);
        log.info("User has added successfully. User id = {}", user.getId());
        return user.getId();
    }

    /**
     * Update user by user id
     *
     * @param userId
     * @param request
     */
    @Override
    public void updateUser(long userId, UserRequestDTO request) {

        User user = getUserById(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setPhoneNumber(request.getPhone());
        //check email from database. If not exists then allow update email otherwise throw exception
        if(!request.getEmail().equals(user.getEmail())) {
            user.setEmail(request.getEmail());
        }
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setUserStatus(request.getStatus());
        user.setUserType(UserType.valueOf(request.getType().toUpperCase()));
        user.setAddresses(convertToAddress(request.getAddresses()));
        userRepository.save(user);

        log.info("User has updated successfully, userId={}", userId);

    }

    /**
     * Change User status by user id
     *
     * @param userId
     * @param status
     */
    @Override
    public void changeStatus(long userId, UserStatus status) {
        User user = getUserById(userId);
        user.setUserStatus(status);
        userRepository.save(user);
        log.info("User status has changed successfully, userId={}", userId);
    }

    /**
     * Delete user by user id
     * @param userId
     */
    @Override
    public void deleteUser(long userId) {
        User user = getUserById(userId);
        if(user != null) {
            userRepository.delete(user);
            log.info("User has deleted permanent successfully, userId={}", userId);
        }
    }

    /**
     * Get User detail by User id
     *
     * @param userId
     * @return
     */
    @Override
    public UserDetailResponse getUser(long userId) {
        User user = getUserById(userId);
        return UserDetailResponse.builder()
                .id(userId)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .phone(user.getPhoneNumber())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getUserStatus())
                .type(user.getUserType().name())
                .build();
    }

    /**
     * Get all user per pageNo and pageSize
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public PageResponse<?> getAllUsers(int pageNo, int pageSize) {
        Page<User> page = userRepository.findAll(PageRequest.of(pageNo, pageSize));

        List<UserDetailResponse> responseList = page.stream().map(user -> UserDetailResponse.builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .dateOfBirth(user.getDateOfBirth())
                    .gender(user.getGender())
                    .phone(user.getPhoneNumber())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .status(user.getUserStatus())
                    .type(user.getUserType().name())
                    .build())
                .toList();

        return PageResponse.builder()
                .page(pageNo)
                .size(pageSize)
                .total(page.getTotalPages())
                .items(responseList)
                .build();
    }

    @Override
    public List<String> findAllRolesByUserId(long userId) {
        return userRepository.findAllRolesByUserId(userId);
    }

    /**
     * Get user by user ID
     *
     * @param userId
     * @return
     */
    private User getUserById(Long userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with userId = " + userId));
    }

    /**
     * Convert Set<AddressRequestDTO to Set<Address>
     *
     * @param addresses
     * @return
     */
    private Set<Address> convertToAddress(Set<AddressRequestDTO> addresses) {
        Set<Address> result = new HashSet<>();
        addresses.forEach(a ->
                result.add(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build())
        );
        return result;
    }
}
