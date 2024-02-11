package pl.bartlomiej.securecapita.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserById(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :id")
    void updateUserPasswordById(Long id, String password);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isEnabled = :value WHERE u.id = :id")
    void updateUserIsEnabled(Long id, boolean value);
}
