package com.minsait.testingMicroservices.repositories;

import com.minsait.testingMicroservices.models.Cuenta;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CuentaRepositoryTest {
    @Autowired
    CuentaRepository repository;

    //como ya hay datos reales ya no se usa el Given
    @Test
    void findByPersona() {
        //When
        Optional<Cuenta> cuenta=repository.findById(1L);

        //Then
        assertTrue(cuenta.isPresent());
        assertEquals("Ricardo", cuenta.get().getPersona() );
    }

    @Test
    void testFindPorPersona(){
        Optional<Cuenta> cuenta= repository.findByPersona("Ricardo");

        assertFalse(cuenta.isEmpty());
        assertEquals(1L, cuenta.get().getId());
    }

    @Test
    void testSave(){
        Cuenta cuenta = new Cuenta(null, "Daniel", new BigDecimal(10000));

        Cuenta cuentasaved = repository.save(cuenta);

        assertEquals("Daniel", cuentasaved.getPersona());
        assertEquals(10000, cuentasaved.getSaldo().intValue());
        assertEquals(3,cuentasaved.getId());
    }

}