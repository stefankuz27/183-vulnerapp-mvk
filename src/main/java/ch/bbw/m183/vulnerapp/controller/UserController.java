package ch.bbw.m183.vulnerapp.controller;

import java.util.Base64;

import ch.bbw.m183.vulnerapp.datamodel.UserEntity;
import ch.bbw.m183.vulnerapp.repository.UserRepository;
import ch.bbw.m183.vulnerapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final UserRepository userRepository;

	@GetMapping("/whoami")
	public UserEntity whoami(Authentication authentication) {
		return userRepository.findById(authentication.getName())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
}
