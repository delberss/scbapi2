package com.example.scbapi2.service;

import com.example.scbapi2.exception.RegraNegocioException;
import com.example.scbapi2.model.entity.Cliente;
import com.example.scbapi2.model.entity.ContaPoupanca;
import com.example.scbapi2.model.repository.ContaPoupancaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ContaPoupancaService {

    private final ContaPoupancaRepository repository;
    private final ClienteService clienteService; // Adicione o ClienteService

    public ContaPoupancaService(ContaPoupancaRepository repository, ClienteService clienteService) {
        this.repository = repository;
        this.clienteService = clienteService; // Inicialize o ClienteService
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
        // Associa o cliente antes de salvar
        if (contaPoupanca.getCliente() != null && contaPoupanca.getCliente().getId() != null) {
            Cliente cliente = clienteService.getClienteById(contaPoupanca.getCliente().getId())
                    .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado"));
            contaPoupanca.setCliente(cliente);
        }
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
}
