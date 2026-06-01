package ch.bbw.m183.vulnerapp.datamodel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "roles")
public class RoleEntity implements GrantedAuthority {

    @Id
    private Integer id;

    @Column
    private String name;

    @Override
    public String getAuthority() {
        return "ROLE_" + name;
    }
}