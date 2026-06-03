package ch.bbw.m183.vulnerapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VulnerApplicationTests {

	@Autowired
	WebTestClient webTestClient;

	// --- Anonymous ---
	@Test
	void blogGetIsPublic() {
		webTestClient.get().uri("/api/blog").exchange().expectStatus().isOk();
	}

	@Test
	void blogPostDeniedAnonymous() {
		webTestClient.post().uri("/api/blog").exchange().expectStatus().isUnauthorized();
	}

	@Test
	void whoamiDeniedAnonymous() {
		webTestClient.get().uri("/api/user/whoami").exchange().expectStatus().isUnauthorized();
	}

	@Test
	void adminDeniedAnonymous() {
		webTestClient.get().uri("/api/admin/users").exchange().expectStatus().isUnauthorized();
	}

	@Test
	void healthIsPublic() {
		webTestClient.get().uri("/actuator/health").exchange().expectStatus().isOk();
	}

	// --- User ---
	@Test
	@WithMockUser(username = "fuu", roles = "USER")
	void blogPostAllowedForUser() {
		webTestClient.mutateWith(csrf()).post().uri("/api/blog")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue("{\"title\":\"Test\",\"body\":\"Test body\"}")
				.exchange().expectStatus().isOk();
	}

	@Test
	@WithMockUser(username = "fuu", roles = "USER")
	void blogPostDeniedForUserWithoutCsrf() {
		webTestClient.post().uri("/api/blog")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue("{\"title\":\"Test\",\"body\":\"Test body\"}")
				.exchange().expectStatus().isForbidden();
	}

	@Test
	@WithMockUser(username = "fuu", roles = "USER")
	void adminDeniedForUser() {
		webTestClient.get().uri("/api/admin/users")
				.exchange().expectStatus().isForbidden();
	}

	// --- Admin ---
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void adminAllowedForAdmin() {
		webTestClient.get().uri("/api/admin/users")
				.exchange().expectStatus().isOk();
	}
}