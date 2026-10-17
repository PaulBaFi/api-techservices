package pe.techservices.api_techservices;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SecurityChainHttpTests {
    @LocalServerPort int port;
    @Autowired JdbcTemplate jdbc;
    @Autowired PasswordEncoder encoder;

    @BeforeEach void data() {
        jdbc.update("delete from clientes");
        jdbc.update("delete from usuarios_api");
        jdbc.execute("ALTER TABLE clientes ALTER COLUMN id RESTART WITH 1");
        jdbc.execute("ALTER TABLE usuarios_api ALTER COLUMN id RESTART WITH 1");
        jdbc.update("insert into usuarios_api (nombre, api_key_hash, token_hash, tokens_disponibles, estado) values (?,?,?,?,?)", "http-test", encoder.encode("key"), encoder.encode("token"), 1, true);
        jdbc.update("insert into clientes (nombres, apellidos, dni, correo, telefono) values (?,?,?,?,?)", "HTTP", "Test", "987", "http@test.com", "999");
    }

    @Test void peticionHttpAtraviesaCadenaCompleta() throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/clientes/1"))
                .header("x-api-key", "key")
                .header("Authorization", "Bearer token")
                .GET().build();
        var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), response.body());
        assertTrue(response.body().contains("tokensRestantes"));
        assertEquals(0, jdbc.queryForObject("select tokens_disponibles from usuarios_api", Integer.class));
    }
}
