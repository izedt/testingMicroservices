package com.minsait.testingMicroservices.models;

import com.minsait.testingMicroservices.exceptions.DineroInsuficienteException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@Entity
@Table(name="cuentas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String persona;
    private BigDecimal saldo;




    public void  retirar( BigDecimal monto){
        BigDecimal saldoAux = this.saldo.subtract(monto);
        if(saldoAux.compareTo(BigDecimal.ZERO)<0)throw new DineroInsuficienteException("Dinero Insuficiente");

        this.saldo=saldoAux;
    }

    public void despositar (BigDecimal monto){
        this.saldo=saldo.add(monto);
    }

}
