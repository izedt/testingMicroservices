package com.minsait.testingMicroservices.services;

import com.minsait.testingMicroservices.models.Cuenta;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaService {
    List<Cuenta> findAll();

    Cuenta findById(Long idCuenta);
    Integer revisarTotalTransferencias(Long idBanco);
    BigDecimal revisarSaldo(Long idCuenta);

    void transferir(Long idCuentaOrigan, Long idCuentaDestion, BigDecimal monto, Long idBanco);
    Cuenta save(Cuenta cuenta);
    boolean deleteById(Long idCuenta);
}
