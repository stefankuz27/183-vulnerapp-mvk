package ch.bbw.m183.vulnerapp.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import ch.bbw.m183.vulnerapp.SecurityConfiguration;
import ch.bbw.m183.vulnerapp.datamodel.UserEntity;
import ch.bbw.m183.vulnerapp.datamodel.RoleEntity;
import ch.bbw.m183.vulnerapp.repository.RoleRepository;
import ch.bbw.m183.vulnerapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	public UserEntity createUser(UserEntity newUser) {
		newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
		return userRepository.save(newUser);
	}

	public Page<UserEntity> getUsers(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	public void deleteUser(String username) {
		userRepository.deleteById(username);
	}

	@EventListener(ContextRefreshedEvent.class)
	public void loadTestUsers() {
		var adminRole = roleRepository.save(new RoleEntity().setId(1).setName("ADMIN"));
		var userRole = roleRepository.save(new RoleEntity().setId(2).setName("USER"));

		Stream.of(new UserEntity().setUsername("admin")
								.setFullname("Super Admin")
								.setPassword("sS1!assab")
								.setRoles(Set.of(adminRole)),
						new UserEntity().setUsername("fuu")
								.setFullname("Johanna Doe")
								.setPassword("bB1!aeknue")
								.setRoles(Set.of(userRole)))
				.forEach(this::createUser);
	}
}
