package com.minsait.testingMicroservices.controllers;

import com.minsait.testingMicroservices.models.Cuenta;
import com.minsait.testingMicroservices.services.CuentaService;
import com.minsait.testingMicroservices.services.CuentaServiceImpl;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<Cuenta> updateById(@PathVariable Long idCuenta, @RequestBody Cuenta cuentaInput){//pathvariable? no URI allowed
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


        cuenta=service.save(cuenta);

        return ResponseEntity.ok(cuenta);
    }

    @PostMapping("/crearcuenta")
    public ResponseEntity<Cuenta> save(@RequestBody Cuenta cuentaInput){

        try{
            if (cuentaInput != null && service.findAll().stream().noneMatch(cuenta -> cuenta.getId().equals(cuentaInput.getId()))){
                service.save(cuentaInput);
                return ResponseEntity.ok().build();
            }
            else throw new NoSuchElementException();
        }catch (NoSuchElementException e){
            return  ResponseEntity.badRequest().build();
        }

    }

}
