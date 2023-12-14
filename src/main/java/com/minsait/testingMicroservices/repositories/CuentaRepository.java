package com.minsait.testingMicroservices.repositories;

import com.minsait.testingMicroservices.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

//no se usa el Repo
public interface CuentaRepository extends JpaRepository <Cuenta, Long> {
    Optional<Cuenta> findByPersona(String persona);// Named Query


}
