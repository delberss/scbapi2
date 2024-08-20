package com.example.scbapi2.service;

import com.example.scbapi2.exception.RegraNegocioException;
import com.example.scbapi2.model.entity.Cliente;
import com.example.scbapi2.model.entity.ContaCorrente;
import com.example.scbapi2.model.entity.ContaPoupanca;
import com.example.scbapi2.model.repository.ContaCorrenteRepository;
import com.example.scbapi2.model.repository.ContaPoupancaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ContaPoupancaService {

    private final ContaPoupancaRepository repository;

    public ContaPoupancaService(ContaPoupancaRepository repository) {
        this.repository = repository;
    }

    public List<ContaPoupanca> getContasPoupancas() {
        return repository.findAll();
    }

    public Optional<ContaPoupanca> getContaPoupancaById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public ContaPoupanca salvar(ContaPoupanca contaPoupanca) {
        validar(contaPoupanca);
        return repository.save(contaPoupanca);
    }

    @Transactional
    public void excluir(ContaPoupanca contaPoupanca) {
        Objects.requireNonNull(contaPoupanca.getId());
        repository.delete(contaPoupanca);
    }

    public void validar(ContaPoupanca contaPoupanca) {
        if (contaPoupanca.getSaldo() == null) {
            throw new RegraNegocioException("Saldo não pode ser nulo");
        }
        if (contaPoupanca.getTaxaJuros() == null) {
            throw new RegraNegocioException("Taxa de juros não pode ser nula");
        }
    }

    public List<ContaPoupanca> getContasPoupancasByClienteId(Long clienteId) {
        return repository.findByClienteId(clienteId);
    }

    public List<ContaPoupanca> getContasPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId);
    }
}
