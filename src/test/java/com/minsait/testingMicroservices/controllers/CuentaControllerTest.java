package com.minsait.testingMicroservices.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minsait.testingMicroservices.models.Cuenta;
import com.minsait.testingMicroservices.services.CuentaService;
import org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private CuentaService service;

    ObjectMapper mapper;


    @BeforeEach
    void setUP(){
        mapper= new ObjectMapper();
    }
    @Test
    void testFindAll() throws Exception {
        when(service.findAll()).thenReturn(List.of(Datos.crearCuenta1().get(),Datos.crearCuenta2().get()));
        mvc.perform(get("/api/v1/cuentas/listar").contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].persona").value("Ricardo"))
                .andExpect(jsonPath("$[1].persona").value("Eder"))
        ;

    }

    @Test
    void testFindByID() throws Exception{
        //Given
        when(service.findById(1L)).thenReturn(Datos.crearCuenta1().get());
        //Then
        mvc.perform(get("/api/v1/cuentas/listar/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.persona").value("Ricardo"))
                .andExpect(jsonPath("$.saldo").value("5000"));
    }

    @Test
    void testByIdNotFound() throws Exception {
        //Given
        when(service.findById(1L)).thenThrow(NoSuchElementException.class);
        //When
        mvc.perform( get("/api/v1/cuentas/listar/1").contentType(MediaType.APPLICATION_JSON))
                //Then
                .andExpect(status().isNotFound());
    }


    @Test
    void testSave() throws Exception{
        Cuenta cuenta= new Cuenta(null, "Eli", new BigDecimal("80000"));
        when( service.save(any(Cuenta.class))).then(invocationOnMock -> {
                    Cuenta c=invocationOnMock.getArgument(0);
                    c.setId(3L);
                    return  c;
                });

        mvc.perform(post("/api/v1/cuentas/crearcuenta").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(cuenta)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.persona").value("Eli"))
                .andExpect(jsonPath("$.saldo").value("80000"));
    }

    @Test
    void testDelete() throws Exception {
        when(service.deleteById(1L)).thenReturn(true);
        when(service.findAll()).thenReturn(List.of(Datos.crearCuenta1().get(),Datos.crearCuenta2().get()));

        mvc.perform(delete("/api/v1/cuentas/borrarporid/1"))

                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdate() throws Exception{
        Cuenta cuenta= new Cuenta(2231321L, "Eder", new BigDecimal("23"));
        when(service.findById(1L)).thenReturn(Datos.crearCuenta1().get());
        when(service.save(any(Cuenta.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));


        System.out.println(service.findById(1L).getId().intValue());
        mvc.perform(put("/api/v1/cuentas/actualizarporid/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(cuenta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(service.findById(1L).getId().intValue())))
                .andExpect(jsonPath("$.persona", is("Eder")))
                .andExpect(jsonPath("$.saldo", is(23)));

    }

}