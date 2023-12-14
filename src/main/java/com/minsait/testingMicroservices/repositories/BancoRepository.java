package com.minsait.testingMicroservices.repositories;

import com.minsait.testingMicroservices.models.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

//se puede usar Crudrepository y Paginationandsort este ultimo para dividir consultas en bloques
//jpa tiene lo anterior y mas
public interface BancoRepository extends JpaRepository<Banco, Long> {

}
