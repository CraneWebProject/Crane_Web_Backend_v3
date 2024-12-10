package crane.userservice.repository;


import crane.userservice.dto.UserResponseDto;
import crane.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<UserResponseDto> findUserByUserId(Long id);

    boolean existsByEmail(String email);
}
