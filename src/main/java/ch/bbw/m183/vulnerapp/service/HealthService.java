package ch.bbw.m183.vulnerapp.service;

import java.net.URL;
import java.util.Base64;
import java.util.Scanner;

import ch.bbw.m183.vulnerapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class HealthService {

	private final UserRepository userRepository;

	@SneakyThrows
	public String health() {
		var url = new URL("http://localhost:8080/actuator/health");
		var s = new Scanner(url.openStream()).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}
