package backend_service.shop.repository;

import backend_service.shop.entity.User;

import java.util.Optional;

public interface UserRepository extends CustomRepository<User, Long> {

    Optional<User> findByUsername(String userName);

    Optional<User> findByEmail(String email);



}
