package com.example.scbapi2.api.controller;

import com.example.scbapi2.api.dto.ContaCorrenteDTO;
import com.example.scbapi2.api.dto.ContaDTO;
import com.example.scbapi2.api.dto.ContaPoupancaDTO;
import com.example.scbapi2.model.entity.Conta;
import com.example.scbapi2.model.entity.ContaCorrente;
import com.example.scbapi2.model.entity.ContaPoupanca;
import com.example.scbapi2.service.ContaCorrenteService;
import com.example.scbapi2.service.ContaPoupancaService;
import com.example.scbapi2.service.ContaService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/contas")
@RequiredArgsConstructor
public class ContaController {

    @Autowired
    private ContaCorrenteService contaCorrenteService;

    @Autowired
    private ContaPoupancaService contaPoupancaService;

    private final ContaService service;
    private final ModelMapper modelMapper = new ModelMapper();

    @GetMapping()
    public ResponseEntity get() {
        List<Conta> contas = service.getContas();
        return ResponseEntity.ok(contas.stream().map(ContaDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Conta> conta = service.getContaById(id);
        if (!conta.isPresent()) {
            return new ResponseEntity("Conta não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(conta.map(ContaDTO::create));
    }

    @PostMapping()
    public ResponseEntity post(@RequestBody ContaDTO dto) {
        try {
            Conta conta = converter(dto);
            conta = service.salvar(conta);
            return new ResponseEntity(conta, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody ContaDTO dto) {
        if (!service.getContaById(id).isPresent()) {
            return new ResponseEntity("Conta não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            Conta conta = converter(dto);
            conta.setId(id);
            service.salvar(conta);
            return ResponseEntity.ok(conta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluir(@PathVariable("id") Long id) {
        Optional<Conta> conta = service.getContaById(id);
        if (!conta.isPresent()) {
            return new ResponseEntity<>("Conta não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(conta.get());
            return ResponseEntity.ok("Conta excluída com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/sacar")
    public ResponseEntity sacar(@PathVariable("id") Long id, @RequestParam Double valor) {
        Optional<Conta> conta = service.getContaById(id);
        if (!conta.isPresent()) {
            return new ResponseEntity("Conta não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            conta.get().sacar(valor);
            service.salvar(conta.get());
            return ResponseEntity.ok(conta.get());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/depositar")
    public ResponseEntity depositar(@PathVariable("id") Long id, @RequestParam Double valor) {
        Optional<Conta> conta = service.getContaById(id);
        if (!conta.isPresent()) {
            return new ResponseEntity("Conta não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            conta.get().depositar(valor);
            service.salvar(conta.get());
            return ResponseEntity.ok(conta.get());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ContaDTO>> getContasByClienteId(@PathVariable("clienteId") Long clienteId) {
        List<ContaCorrente> contasCorrentes = contaCorrenteService.getContasCorrentesByClienteId(clienteId);
        List<ContaPoupanca> contasPoupancas = contaPoupancaService.getContasPoupancasByClienteId(clienteId);

        List<ContaDTO> contas = contasCorrentes.stream()
                .map(ContaCorrenteDTO::create)
                .collect(Collectors.toList());

        contas.addAll(contasPoupancas.stream()
                .map(ContaPoupancaDTO::create)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(contas);
    }

    private Conta converter(ContaDTO dto) {
        return modelMapper.map(dto, Conta.class);
    }
}
