package com.example.scbapi2.service;

import com.example.scbapi2.exception.RegraNegocioException;
import com.example.scbapi2.model.entity.Conta;
import com.example.scbapi2.model.repository.ContaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ContaService {

    private final ContaRepository repository;

    public ContaService(ContaRepository repository) {
        this.repository = repository;
    }

    public List<Conta> getContas() {
        return repository.findAll();
    }

    public Optional<Conta> getContaById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Conta salvar(Conta conta) {
        validar(conta);
        return repository.save(conta);
    }

    @Transactional
    public void excluir(Conta conta) {
        Objects.requireNonNull(conta.getId());
        repository.delete(conta);
    }

    public void validar(Conta conta) {
        if (conta.getSaldo() == null) {
            throw new RegraNegocioException("Saldo não pode ser nulo");
        }
    }

    public List<Conta> getContasByClienteId(Long clienteId) {
        return repository.findByClienteId(clienteId);
    }
}
