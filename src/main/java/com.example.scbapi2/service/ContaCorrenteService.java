package com.example.scbapi2.service;

import com.example.scbapi2.exception.RegraNegocioException;
import com.example.scbapi2.model.entity.ContaCorrente;
import com.example.scbapi2.model.repository.ContaCorrenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ContaCorrenteService {

    private final ContaCorrenteRepository repository;

    public ContaCorrenteService(ContaCorrenteRepository repository) {
        this.repository = repository;
    }

    public List<ContaCorrente> getContasCorrentes() {
        return repository.findAll();
    }

    public Optional<ContaCorrente> getContaCorrenteById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public ContaCorrente salvar(ContaCorrente contaCorrente) {
        validar(contaCorrente);
        return repository.save(contaCorrente);
    }

    @Transactional
    public void excluir(ContaCorrente contaCorrente) {
        Objects.requireNonNull(contaCorrente.getId());
        repository.delete(contaCorrente);
    }

    public void validar(ContaCorrente contaCorrente) {
        if (contaCorrente.getSaldo() == null) {
            throw new RegraNegocioException("Saldo não pode ser nulo");
        }
        if (contaCorrente.getLimiteChequeEspecial() == null) {
            throw new RegraNegocioException("Limite de cheque especial não pode ser nulo");
        }
    }

    public List<ContaCorrente> getContasCorrentesByClienteId(Long clienteId) {
        return repository.findByClienteId(clienteId);
    }

    public List<ContaCorrente> getContasPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId);
    }

}
