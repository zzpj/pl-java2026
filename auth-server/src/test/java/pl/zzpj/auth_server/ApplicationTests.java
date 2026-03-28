package pl.zzpj.auth_server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(properties = {
		"app.jwt.secret=TwojTestowySekretKtoryMusiBycWystarczajacoDlugiZebyZadowolicAlgorytmJWT"
})
@Testcontainers
class ApplicationTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine");

	@Test
	void contextLoads() {
	}

}
