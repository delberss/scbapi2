package com.example.scbapi2.api.controller;

import com.example.scbapi2.api.dto.ContaCorrenteDTO;
import com.example.scbapi2.api.dto.ContaDTO;
import com.example.scbapi2.api.dto.ContaPoupancaDTO;
import com.example.scbapi2.model.entity.Cliente;
import com.example.scbapi2.model.entity.Conta;
import com.example.scbapi2.model.entity.ContaCorrente;
import com.example.scbapi2.model.entity.ContaPoupanca;
import com.example.scbapi2.service.ContaPoupancaService;
import com.example.scbapi2.service.ClienteService; // Adicione a importação
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/contaspoupancas")
@RequiredArgsConstructor
public class ContaPoupancaController {

    private final ContaPoupancaService service;
    private final ClienteService clienteService; // Adicione o ClienteService
    private final ModelMapper modelMapper = new ModelMapper();

    @GetMapping()
    public ResponseEntity get() {
        List<ContaPoupanca> contasPoupancas = service.getContasPoupancas();
        return ResponseEntity.ok(contasPoupancas.stream().map(ContaPoupancaDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<ContaPoupanca> contaPoupanca = service.getContaPoupancaById(id);
        if (!contaPoupanca.isPresent()) {
            return new ResponseEntity("Conta Poupança não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(contaPoupanca.map(ContaCorrenteDTO::create));
    }

    @PostMapping()
    public ResponseEntity post(@RequestBody ContaPoupancaDTO dto) {
        try {
            ContaPoupanca contaPoupanca = converter(dto);

            // Encontre o Cliente pelo ID
            Cliente cliente = clienteService.getClienteById(dto.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

            contaPoupanca.setCliente(cliente); // Associe o Cliente à ContaPoupanca

            contaPoupanca = service.salvar(contaPoupanca);
            return new ResponseEntity(contaPoupanca, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody ContaPoupancaDTO dto) {
        if (!service.getContaPoupancaById(id).isPresent()) {
            return new ResponseEntity("Conta Poupança não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            ContaPoupanca contaPoupanca = converter(dto);
            contaPoupanca.setId(id);
            service.salvar(contaPoupanca);
            return ResponseEntity.ok(contaPoupanca);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluir(@PathVariable("id") Long id) {
        Optional<ContaPoupanca> contaPoupanca = service.getContaPoupancaById(id);
        if (!contaPoupanca.isPresent()) {
            return new ResponseEntity<>("Conta Poupança não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(contaPoupanca.get());
            return ResponseEntity.ok("Conta Poupança excluída com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/{id}/depositar")
    public ResponseEntity<?> depositar(@PathVariable("id") Long id, @RequestParam Double valor) {
        Optional<ContaPoupanca> contaPoupancaOpt = service.getContaPoupancaById(id);
        if (!contaPoupancaOpt.isPresent()) {
            return new ResponseEntity<>("Conta Poupança não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            ContaPoupanca contaPoupanca = contaPoupancaOpt.get();
            contaPoupanca.setSaldo(contaPoupanca.getSaldo() + valor); // Atualiza o saldo
            service.salvar(contaPoupanca); // Salva a conta atualizada
            return ResponseEntity.ok(contaPoupanca);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/sacar")
    public ResponseEntity<?> sacar(@PathVariable("id") Long id, @RequestParam Double valor) {
        Optional<ContaPoupanca> contaPoupancaOpt = service.getContaPoupancaById(id);
        if (!contaPoupancaOpt.isPresent()) {
            return new ResponseEntity<>("Conta Poupança não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            ContaPoupanca contaPoupanca = contaPoupancaOpt.get();
            if (contaPoupanca.getSaldo() < valor) {
                return new ResponseEntity<>("Saldo insuficiente", HttpStatus.BAD_REQUEST);
            }
            contaPoupanca.setSaldo(contaPoupanca.getSaldo() - valor); // Atualiza o saldo
            service.salvar(contaPoupanca); // Salva a conta atualizada
            return ResponseEntity.ok(contaPoupanca);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ContaPoupancaDTO>> getContasPorCliente(@PathVariable("clienteId") Long clienteId) {
        List<ContaPoupanca> contas = service.getContasPorCliente(clienteId);
        return ResponseEntity.ok(contas.stream().map(ContaPoupancaDTO::create).collect(Collectors.toList()));
    }


    private ContaPoupanca converter(ContaPoupancaDTO dto) {
        return modelMapper.map(dto, ContaPoupanca.class);
    }
}
