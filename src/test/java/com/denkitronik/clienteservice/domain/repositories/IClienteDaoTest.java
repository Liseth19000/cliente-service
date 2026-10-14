package com.denkitronik.clienteservice.domain.repositories;

import com.denkitronik.clienteservice.domain.entities.Cliente;
import com.denkitronik.clienteservice.domain.entities.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Integration tests (data slice) — IClienteDao con H2")
class IClienteDaoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private IClienteDao clienteDao;

    private Region region;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        region = new Region();
        region.setNombre("Asia");
        em.persist(region);

        cliente = new Cliente();
        cliente.setNombre("Grace");
        cliente.setApellido("Hopper");
        cliente.setEmail("grace_test_setup@navy.mil");
        cliente.setRegion(region);
        em.persist(cliente);

        em.flush();
    }

    @Test
    void findAllRegiones_debeRetornarListaDeRegiones() {
        List<Region> regiones = clienteDao.findAllRegiones();
        assertThat(regiones).hasSize(9);
        assertThat(regiones).extracting(Region::getNombre).contains("Asia");
    }

    @Test
    void deleteById_debeEliminarElCliente() {
        Long id = cliente.getId();
        clienteDao.deleteById(id);
        em.flush();

        assertThat(em.find(Cliente.class, id)).isNull();
    }
}