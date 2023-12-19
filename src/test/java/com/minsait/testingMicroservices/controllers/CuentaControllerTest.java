package com.minsait.testingMicroservices.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minsait.testingMicroservices.exceptions.DineroInsuficienteException;
import com.minsait.testingMicroservices.models.Cuenta;
import com.minsait.testingMicroservices.models.TransferirDTO;
import com.minsait.testingMicroservices.services.CuentaService;
import org.hamcrest.Matchers;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

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
    void testFindByIdNotFound() throws Exception {
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
    void testSaveIfNotFound() throws Exception {
        //Given
        Cuenta cuentaErronea = new Cuenta();
        cuentaErronea.setPersona("Error");
        cuentaErronea.setSaldo(new BigDecimal("1000"));

        //When
        when(service.save(any(Cuenta.class))).thenThrow(new NoSuchElementException());

        mvc.perform(post("/api/v1/cuentas/crearcuenta")
                        //then
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cuentaErronea)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveWhenDuplicatedAccount() throws Exception {
        Cuenta cuentaDuplicada = new Cuenta(1L, "Eli", new BigDecimal("80000"));

        when(service.findAll()).thenReturn(List.of(cuentaDuplicada));
        when(service.save(any(Cuenta.class))).thenThrow(new NoSuchElementException());

        mvc.perform(post("/api/v1/cuentas/crearcuenta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cuentaDuplicada)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testDelete() throws Exception {
        when(service.findAll()).thenReturn(List.of(Datos.crearCuenta1().get(),Datos.crearCuenta2().get()));
        when(service.deleteById(1L)).thenReturn(true);


        mvc.perform(delete("/api/v1/cuentas/borrarporid/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteIfDoesntExist() throws Exception {
        when(service.deleteById(1L)).thenReturn(false);
        when(service.findAll()).thenReturn(List.of(Datos.crearCuenta1().get(),Datos.crearCuenta2().get()));

        mvc.perform(delete("/api/v1/cuentas/borrarporid/3"))

                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdate() throws Exception{
        Cuenta cuenta= new Cuenta(null, "Eder", new BigDecimal("23"));
        when(service.findById(1L)).thenReturn(Datos.crearCuenta1().get());
        when(service.save(any())).thenAnswer(invocationOnMock -> {
            Cuenta cuenta1 = invocationOnMock.getArgument(0);
            cuenta1.setId(1L);
            return cuenta1;
        });


        //System.out.println(service.findById(1L).getId().intValue());
        mvc.perform(put("/api/v1/cuentas/actualizarporid/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(cuenta)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(service.findById(1L).getId().intValue())))
                .andExpect(jsonPath("$.persona", is(cuenta.getPersona())))
                .andExpect(jsonPath("$.saldo", is(cuenta.getSaldo().intValue())));
    }

    @Test
    void testUpdateIFDoesntExist() throws Exception{
        Cuenta cuenta = new Cuenta(null ,"Ricardo", new BigDecimal("54353"));
        when(service.findById(3L)).thenThrow(NoSuchElementException.class);

        mvc.perform(put("/api/v1/cuentas/actualizarporid/3").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString((cuenta)))).andExpect(status().isNotFound());

        verify(service, only()).findById(3L);
        verify(service, times(0)).save(any());
    }

    @Test
    void testTransferir() throws Exception {

        //Given

        //void?
        //Cuenta cuenta = new Cuenta(3L, "Julian", new BigDecimal("90000"));
//        when(service.findById(1L)).thenReturn(Datos.crearCuenta1().get());
//        when(service.findById(2L)).thenReturn(Datos.crearCuenta2().get());

        TransferirDTO dto = new TransferirDTO();
        dto.setIdBanco(1L);
        dto.setIdCuentaDestino(1L);
        dto.setMonto(new BigDecimal("5000"));
        dto.setIdCuentaOrigen(2L);
        Map<String, Object> response = new HashMap<>();
        response.put("fecha", LocalDate.now().toString());
        response.put("peticion", dto);
        response.put("status","OK");
        response.put("mensaje", "Transferencia realizada con exito");
//        int valor = Integer.parseInt(service.revisarSaldo(1L).toString()) +Integer.parseInt(dto.getMonto().toString());

        //when
        mvc.perform(post("/api/v1/cuentas").contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(dto)))
                //Then
               /* .andExpect(status().isOk())
                .andExpect(jsonPath("$.fecha", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$.mensaje", is("Transferencia realizada con exito")));
             */
            //.andExpect(jsonPath("$.saldo", is(valor)));
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(mapper.writeValueAsString(response))//valida toodo el cuerpo de la respuesta
                );
    }

    @Test
    void testTransferirDineroinsuficienteExeception() throws  Exception{
        BigDecimal monto=new BigDecimal("60001");
        Cuenta cuenta = Datos.crearCuenta1().get();

        Exception exception = assertThrows(
                DineroInsuficienteException.class, ()-> cuenta.retirar(monto));


        doThrow(exception).when(service).transferir(anyLong(), anyLong(), any(), anyLong()
        );

        TransferirDTO dto = new TransferirDTO();
        dto.setIdBanco(1L);
        dto.setIdCuentaDestino(1L);
        dto.setMonto(monto);
        dto.setIdCuentaOrigen(2L);


        Map<String, Object> response = new HashMap<>();
        response.put("fecha", LocalDate.now().toString());
        response.put("peticion", dto);
        response.put("status","OK");
        response.put("mensaje", "Error " + exception.getMessage());



        mvc.perform(post("/api/v1/cuentas").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))

                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.mensaje").value("Error " + exception.getMessage() ),
                        content().json(mapper.writeValueAsString(response))//valida toodo el cuerpo de la respuesta
                );

    }


    @Test
    void testTransferirNoSuchElementException() throws Exception {
        BigDecimal monto=new BigDecimal("60001");

        Exception exception= new NoSuchElementException();

        doThrow( exception).when(service).transferir(anyLong(), anyLong(), any(), anyLong());

        TransferirDTO dto = new TransferirDTO();
        dto.setIdBanco(1L);
        dto.setIdCuentaDestino(1L);
        dto.setMonto(monto);
        dto.setIdCuentaOrigen(2L);

        Map<String, Object> response = new HashMap<>();
        response.put("fecha", LocalDate.now().toString());
        response.put("peticion", dto);
        response.put("status","Not Found");
        response.put("mensaje", "Error " + exception.getMessage());

        mvc.perform(post("/api/v1/cuentas").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))

                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        //jsonPath("$.mensaje").value("Error " + exception.getMessage() ),
                        content().json(mapper.writeValueAsString(response))//valida toodo el cuerpo de la respuesta
                );

    }

    @Test
    void testHelloWorld() throws Exception{
        mvc.perform(get("/api/v1/cuentas/hello").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World!"));
    }

}