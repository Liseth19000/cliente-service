package com.denkitronik.clienteservice.integration;

import com.denkitronik.clienteservice.domain.entities.Cliente;
import com.denkitronik.clienteservice.domain.entities.Region;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("Tests E2E deshabilitados localmente sin Docker daemon")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("E2E tests — flujo completo HTTP → PostgreSQL con Testcontainers")

class ClienteIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String BASE = "/api/v1/cliente-service";
    private static final AtomicBoolean seeded = new AtomicBoolean(false);
    private static Long regionId;
    private static Long idCreado;

    @BeforeEach
    void seedDatosIniciales() {
        if (seeded.compareAndSet(false, true)) {
            jdbcTemplate.execute("INSERT INTO regiones(nombre) VALUES ('América del Sur')");
            regionId = jdbcTemplate.queryForObject(
                    "SELECT id FROM regiones WHERE nombre = 'América del Sur'",
                    Long.class);
        }
    }

    @Test
    @Order(1)
    void crearCliente_debeRetornar201() {
        Region region = new Region();
        region.setId(regionId);

        Cliente nuevo = new Cliente();
        nuevo.setNombre("Margaret");
        nuevo.setApellido("Hamilton");
        nuevo.setEmail("margaret@apollo.nasa");
        nuevo.setRegion(region);

        ResponseEntity<Cliente> respuesta = restTemplate.postForEntity(BASE + "/clientes", nuevo, Cliente.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody().getId()).isNotNull();

        idCreado = respuesta.getBody().getId();
    }

    @Test
    @Order(2)
    void buscarClienteCreado_debeRetornar200() {
        ResponseEntity<Cliente> respuesta = restTemplate.getForEntity(BASE + "/clientes/" + idCreado, Cliente.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody().getEmail()).isEqualTo("margaret@apollo.nasa");
    }
}