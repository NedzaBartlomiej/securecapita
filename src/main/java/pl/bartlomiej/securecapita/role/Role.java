package pl.bartlomiej.securecapita.role;

import jakarta.persistence.*;
import lombok.*;
import pl.bartlomiej.securecapita.role.permission.Permission;

import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @ManyToMany
    @JoinTable(name = "RolePermission", //todo: FIX -> hibernate generating this table name like "role_permission" <- produces error
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private List<Permission> permissions;

    private String name;

    public enum RoleType {
        ROLE_USER, ROLE_MANAGER, ROLE_ADMIN, ROLE_SYSADMIN
    }
}
