package pe.techservices.api_techservices;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiTechservicesApplicationTests {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired PasswordEncoder encoder;

    @BeforeEach void data() {
        jdbc.update("delete from clientes");
        jdbc.update("delete from usuarios_api");
        jdbc.execute("ALTER TABLE clientes ALTER COLUMN id RESTART WITH 1");
        jdbc.execute("ALTER TABLE usuarios_api ALTER COLUMN id RESTART WITH 1");
        jdbc.update("insert into usuarios_api (nombre, api_key_hash, token_hash, tokens_disponibles, estado) values (?,?,?,?,?)", "test", encoder.encode("key"), encoder.encode("token"), 1, true);
        jdbc.update("insert into clientes (nombres, apellidos, dni, correo, telefono) values (?,?,?,?,?)", "Ana", "Test", "123", "ana@test.com", "999");
    }

    @Test void credencialesIncorrectas() throws Exception {
        mvc.perform(get("/api/clientes/1").header("x-api-key", "bad").header("Authorization", "Bearer token"))
                .andExpect(status().isUnauthorized());
    }
    @Test void consumoExitoso() throws Exception {
        mvc.perform(get("/api/clientes/1").header("x-api-key", "key").header("Authorization", "Bearer token"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("tokensRestantes")));
    }
    @Test void clienteInexistenteNoConsume() throws Exception {
        mvc.perform(get("/api/clientes/99").header("x-api-key", "key").header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
        Assertions.assertEquals(1, jdbc.queryForObject("select tokens_disponibles from usuarios_api", Integer.class));
    }
    @Test void saldoAgotado() throws Exception {
        mvc.perform(get("/api/clientes/1").header("x-api-key", "key").header("Authorization", "Bearer token"));
        mvc.perform(get("/api/clientes/1").header("x-api-key", "key").header("Authorization", "Bearer token"))
                .andExpect(status().isForbidden());
    }
}
