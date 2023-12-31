package pl.bartlomiej.securecapita.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.role.id = (SELECT r.id FROM Role r WHERE r.name = :roleName) WHERE u.id = :userId")
    void updateRole(Long userId, String roleName);
}
