package com.minsait.testingMicroservices.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="bancos")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Banco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//checar tipo de base de datos que se usa
    private Long id;
    private String nombre;

    @Column(name= "total_transferencias")
    private  int totalTransferencias;

}
