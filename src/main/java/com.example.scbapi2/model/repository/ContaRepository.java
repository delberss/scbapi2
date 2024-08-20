package com.example.scbapi2.model.repository;

import com.example.scbapi2.model.entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    List<Conta> findByClienteId(Long clienteId);
}