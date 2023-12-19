package com.minsait.testingMicroservices.services;

import com.minsait.testingMicroservices.models.Banco;
import com.minsait.testingMicroservices.models.Cuenta;
import com.minsait.testingMicroservices.repositories.BancoRepository;
import com.minsait.testingMicroservices.repositories.CuentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {

    @InjectMocks
    private CuentaServiceImpl cuentaService;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private BancoRepository bancoRepository;

    @Test
    void testFindAll() {
        when(cuentaRepository.findAll()).thenReturn(Arrays.asList(new Cuenta(), new Cuenta()));
        List<Cuenta> cuentas = cuentaService.findAll();
        assertNotNull(cuentas);
        assertEquals(2, cuentas.size());
        verify(cuentaRepository).findAll();
    }

    @Test
    void testFindById() {
        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        Optional<Cuenta> result = Optional.of(cuentaService.findById(1L));
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testCrearCuenta() {
        Cuenta cuenta = new Cuenta();
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);

        Cuenta result = cuentaService.save(cuenta);
        assertNotNull(result);
        verify(cuentaRepository).save(any(Cuenta.class));
    }

    @Test
    void testActualizarCuenta() {
        Cuenta cuentaExistente = new Cuenta();
        cuentaExistente.setId(1L);  // Asumiendo que el ID es un Long
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuentaExistente);
        //when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaExistente));

        Cuenta actualizada = new Cuenta();
        actualizada.setId(1L);
        Cuenta result = cuentaService.save(actualizada);
        assertEquals(1L, result.getId());
        verify(cuentaRepository).save(any(Cuenta.class));
    }

    @Test
    void testRevisarTotalTransferencias() {
        Banco banco = new Banco();
        banco.setId(1L);
        banco.setTotalTransferencias(5);
        when(bancoRepository.findById(1L)).thenReturn(Optional.of(banco));

        Integer totalTransferencias = cuentaService.revisarTotalTransferencias(1L);

        assertEquals(5, totalTransferencias);
        verify(bancoRepository).findById(1L);
    }

    @Test
    void testRevisarSaldo() {
        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setSaldo(new BigDecimal("1000.00"));
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        BigDecimal saldo = cuentaService.revisarSaldo(1L);
        assertEquals(0, saldo.compareTo(new BigDecimal("1000.00")));
    }

    @Test
    void testTransferir() {
        Cuenta origen = new Cuenta();
        origen.setId(1L);
        origen.setSaldo(new BigDecimal("1000.00"));

        Cuenta destino = new Cuenta();
        destino.setId(2L);
        destino.setSaldo(new BigDecimal("500.00"));

        Banco banco = new Banco();
        banco.setId(1L);
        banco.setTotalTransferencias(5);

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(origen));
        when(cuentaRepository.findById(2L)).thenReturn(Optional.of(destino));
        when(bancoRepository.findById(1L)).thenReturn(Optional.of(banco));

        cuentaService.transferir(1L, 2L, new BigDecimal("100.00"), 1L);

        assertEquals(new BigDecimal("900.00"), origen.getSaldo());
        assertEquals(new BigDecimal("600.00"), destino.getSaldo());
        assertEquals(6, banco.getTotalTransferencias());

        verify(cuentaRepository).save(origen);
        verify(cuentaRepository).save(destino);
        verify(bancoRepository).save(banco);
    }


    @Test
    void testDeleteById() {
        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        doNothing().when(cuentaRepository).deleteById(1L);
        boolean result = cuentaService.deleteById(1L);
        assertTrue(result);
        verify(cuentaRepository).deleteById(1L);
    }


    @Test
    void testDeleteByIdIfNotPresent() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.empty());

        assertFalse(cuentaService.deleteById(1L));

        verify(cuentaRepository, times(0)).deleteById(anyLong());
    }



}
