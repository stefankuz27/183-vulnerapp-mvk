package ch.bbw.m183.vulnerapp.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "users")
public class UserEntity {

	@Id
	@NotBlank
	@Size(min = 3, max = 50)
	private String username;

	@Column
	@NotBlank
	private String fullname;

	@Column
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotBlank
	@Size(min = 8, message = "Das Passwort muss mindestens 8 Zeichen lang sein.")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
			message = "Das Passwort muss mindestens eine Zahl, einen Gross-, einen Kleinbuchstaben und ein Sonderzeichen enthalten.")
	private String password;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "user_roles",
			joinColumns = @JoinColumn(name = "username"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private Set<RoleEntity> roles;
}