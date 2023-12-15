package com.minsait.testingMicroservices.controllers;

import com.minsait.testingMicroservices.models.Banco;
import com.minsait.testingMicroservices.models.Cuenta;

import java.math.BigDecimal;
import java.util.Optional;

public class Datos {
/*    INSERT INTO cuentas (persona, saldo) VALUES ('Ricardo', 5000);
    INSERT INTO cuentas (persona, saldo) VALUES ('Eder', 60000);

    INSERT INTO bancos(nombre, total_transferencias) VALUES ('BBVA', 0)*/



    public static Optional<Cuenta> crearCuenta1(){
        return Optional.of( new Cuenta(1L, "Ricardo",new BigDecimal(5000)));
    }
    public static Optional<Cuenta> crearCuenta2(){
        return Optional.of( new Cuenta(2L, "Eder",new BigDecimal(60000)));
    }

    public static Optional<Banco> crearBanco(){
        return Optional.of( new Banco(1L,"BBVA", 0));
    }
}

