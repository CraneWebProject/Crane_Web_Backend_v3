package sch.crane.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sch.crane.domain.user.exception.UserNotFoundException;
import sch.crane.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    default User findByEmailOrElseThrow(String email) {
        return findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }


}
