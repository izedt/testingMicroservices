package com.minsait.testingMicroservices.controllers;

import com.minsait.testingMicroservices.exceptions.DineroInsuficienteException;
import com.minsait.testingMicroservices.models.Cuenta;
import com.minsait.testingMicroservices.models.TransferirDTO;
import com.minsait.testingMicroservices.services.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

//no confundir estereotipos ocn anotaciones como entity que pertenece a jpa tampoco restcontroller y response body es combinacion entre ambas

@RestController

@RequestMapping("/api/v1/cuentas")

public class CuentaController {

    @Autowired
    private CuentaService service;


    @GetMapping("/hello")
    String helloWorld(){
        return "Hello World!";
    }


    @GetMapping("/listar")
    @ResponseStatus(HttpStatus.OK)
    public List<Cuenta> findAll(){

        return service.findAll();
    }

    //spring.jpa.show=true hay muchas configs por si sellgan a ocupar como mostrar show sql

    @GetMapping("/listar/{id}")//se puede con requestparam $id
    public ResponseEntity<Cuenta> findById(@PathVariable Long id){

        try {
            Cuenta cuenta=service.findById(id);
            return ResponseEntity.ok(cuenta);
        }catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }



    ///Ejercicios Delete, Update, Create


    @DeleteMapping("/borrarporid/{idCuenta}")
    public ResponseEntity<Cuenta> deleteById(@PathVariable Long idCuenta){

        try {
            if (service.findAll().stream().anyMatch(cuenta -> cuenta.getId().equals(idCuenta))) {
                service.deleteById(idCuenta);
                return ResponseEntity.noContent().build();
            }else throw new NoSuchElementException();
        }catch (NoSuchElementException e) {
                return ResponseEntity.notFound().build();
        }

    }

    @PutMapping("/actualizarporid/{idCuenta}")
    public ResponseEntity<Cuenta> updateById(@PathVariable Long idCuenta, @RequestBody Cuenta cuentaInput){
        Cuenta cuenta =null;
        try{
            cuenta = service.findById(idCuenta);
        }catch (NoSuchElementException e){
            return  ResponseEntity.notFound().build();
        }
        if(cuenta==null)return ResponseEntity.notFound().build();

        cuenta.setId(cuentaInput.getId());
        cuenta.setSaldo(cuentaInput.getSaldo());
        cuenta.setPersona(cuentaInput.getPersona());

        return new ResponseEntity<>( service.save(cuenta), HttpStatus.CREATED);
    }

    @PostMapping("/crearcuenta")
//    @ResponseStatus(HttpStatus.CREATED)
    //public Cuenta
    public ResponseEntity<Cuenta> save(@RequestBody Cuenta cuentaInput){

        try{
            if (cuentaInput != null && service.findAll().stream().noneMatch(cuenta -> cuenta.getId().equals(cuentaInput.getId()))){
                service.save(cuentaInput);
                return new ResponseEntity<>(cuentaInput, HttpStatus.CREATED);//build() solo se incluye cuando no se regresa un cuerpo
            }
            else throw new NoSuchElementException();
        }catch (NoSuchElementException e){
            return  ResponseEntity.badRequest().build();
        }

    }




    @PostMapping
    public ResponseEntity<?> transferir (@RequestBody TransferirDTO dto){
        Map<String, Object> response = new HashMap<>();
        response.put("fecha", LocalDate.now().toString());
        response.put("peticion", dto);

        try{
            service.transferir(dto.getIdCuentaOrigen(), dto.getIdCuentaDestino(), dto.getMonto(),dto.getIdBanco());
            response.put("status","OK");
            response.put("mensaje", "Transferencia realizada con exito");
        }catch (NoSuchElementException e){
            response.put("status", "Not Found");
            response.put("mensaje", "Error " + e.getMessage());
            return  new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }catch (DineroInsuficienteException e){
            response.put("status", "OK");
            response.put("mensaje", "Error " + e.getMessage());
        }
    return ResponseEntity.ok(response);
    }

}
