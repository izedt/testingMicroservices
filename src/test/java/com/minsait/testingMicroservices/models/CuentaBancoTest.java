package com.minsait.testingMicroservices.models;

import com.minsait.testingMicroservices.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaBancoTest {


    //No es lo mismo?🤔
    Banco banco;
    Cuenta cuenta;
    BigDecimal saldoAnterior;
    @BeforeEach
    void setUp() {
        banco = new Banco(1L,"Banqueder",0);
        cuenta = new Cuenta(1L,"Eder", new BigDecimal(50));
        saldoAnterior=cuenta.getSaldo();
    }

    @Test
    void testGetId() {
        banco.setId(1L);
        assertEquals(1, banco.getId());
    }

    @Test
    void testSetId() {
        banco.setId(1L);
        assertEquals(1, banco.getId());
    }

    @Test
    void testGetNombre() {
        banco.setNombre("BBVA");
        assertEquals("BBVA", banco.getNombre());
    }

    @Test
    void testSetNombre() {
        banco.setNombre("Banamex");
        assertEquals("Banamex", banco.getNombre());
    }

    @Test
    void testGetTotalTransferencias() {
        banco.setTotalTransferencias(100);
        assertEquals(100, banco.getTotalTransferencias());
    }

    @Test
    void testSetTotalTransferencias() {
        banco.setTotalTransferencias(200);
        assertEquals(200, banco.getTotalTransferencias());
    }

    @Test
    void testRetirar() {
        BigDecimal montoRetirar = new BigDecimal(10);
        cuenta.retirar(montoRetirar);
        assertEquals(saldoAnterior.subtract(montoRetirar), cuenta.getSaldo());
    }

    @Test
    void testRetirarConSaldoInsuficiente() {
        BigDecimal montoRetirar = new BigDecimal(100);
        assertThrows(DineroInsuficienteException.class, () -> cuenta.retirar(montoRetirar));
    }

    @Test
    void testDepositar() {
        BigDecimal montoDepositar = new BigDecimal(23121);
        cuenta.despositar(montoDepositar);
        assertEquals(montoDepositar.add(saldoAnterior), cuenta.getSaldo());
    }

}
