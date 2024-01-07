package pl.bartlomiej.securecapita.role;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    public Role getRoleByName(String roleName);
}