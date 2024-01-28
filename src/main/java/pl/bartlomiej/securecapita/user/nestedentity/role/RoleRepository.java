package pl.bartlomiej.securecapita.user.nestedentity.role;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    public Role getRoleByName(String roleName);
}