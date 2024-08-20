package com.example.scbapi2.model.repository;

import com.example.scbapi2.model.entity.ContaPoupanca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContaPoupancaRepository extends JpaRepository<ContaPoupanca, Long> {
    List<ContaPoupanca> findByClienteId(Long clienteId);
}