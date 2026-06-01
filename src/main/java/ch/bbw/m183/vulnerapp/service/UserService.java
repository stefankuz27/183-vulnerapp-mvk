package ch.bbw.m183.vulnerapp.service;

import jakarta.persistence.EntityManager;

import ch.bbw.m183.vulnerapp.datamodel.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.experimental.StandardException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private final EntityManager entityManager;

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@StandardException
	public static class InvalidPasswordException extends RuntimeException {

	}
}
