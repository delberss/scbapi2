package com.example.scbapi2.api.controller;

import com.example.scbapi2.api.dto.ContaCorrenteDTO;
import com.example.scbapi2.exception.RegraNegocioException;
import com.example.scbapi2.model.entity.Cliente;
import com.example.scbapi2.model.entity.ContaCorrente;
import com.example.scbapi2.service.ClienteService;
import com.example.scbapi2.service.ContaCorrenteService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/contascorrentes")
@RequiredArgsConstructor
public class ContaCorrenteController {

    private final ContaCorrenteService service;
    private final ClienteService clienteService;
    private final ModelMapper modelMapper = new ModelMapper();

    @GetMapping()
    public ResponseEntity get() {
        List<ContaCorrente> contasCorrentes = service.getContasCorrentes();
        return ResponseEntity.ok(contasCorrentes.stream().map(ContaCorrenteDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<ContaCorrente> contaCorrente = service.getContaCorrenteById(id);
        if (!contaCorrente.isPresent()) {
            return new ResponseEntity("Conta Corrente não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(contaCorrente.map(ContaCorrenteDTO::create));
    }

    @PostMapping()
    public ResponseEntity post(@RequestBody ContaCorrenteDTO dto) {
        try {
            ContaCorrente contaCorrente = converter(dto);
            contaCorrente = service.salvar(contaCorrente);
            return new ResponseEntity(contaCorrente, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody ContaCorrenteDTO dto) {
        if (!service.getContaCorrenteById(id).isPresent()) {
            return new ResponseEntity("Conta Corrente não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            ContaCorrente contaCorrente = converter(dto);
            contaCorrente.setId(id);
            service.salvar(contaCorrente);
            return ResponseEntity.ok(contaCorrente);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<ContaCorrente> contaCorrente = service.getContaCorrenteById(id);
        if (!contaCorrente.isPresent()) {
            return new ResponseEntity("Conta Corrente não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(contaCorrente.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/sacar")
    public ResponseEntity sacar(@PathVariable("id") Long id, @RequestParam Double valor) {
        Optional<ContaCorrente> contaCorrente = service.getContaCorrenteById(id);
        if (!contaCorrente.isPresent()) {
            return new ResponseEntity("Conta Corrente não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            contaCorrente.get().sacar(valor);
            service.salvar(contaCorrente.get());
            return ResponseEntity.ok(contaCorrente.get());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/depositar")
    public ResponseEntity depositar(@PathVariable("id") Long id, @RequestParam Double valor) {
        Optional<ContaCorrente> contaCorrente = service.getContaCorrenteById(id);
        if (!contaCorrente.isPresent()) {
            return new ResponseEntity("Conta Corrente não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            contaCorrente.get().depositar(valor);
            service.salvar(contaCorrente.get());
            return ResponseEntity.ok(contaCorrente.get());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private ContaCorrente converter(ContaCorrenteDTO dto) {
        ContaCorrente contaCorrente = modelMapper.map(dto, ContaCorrente.class);
        if (dto.getClienteId() != null) {
            Optional<Cliente> cliente = clienteService.getClienteById(dto.getClienteId());
            if (cliente.isPresent()) {
                contaCorrente.setCliente(cliente.get());
            } else {
                throw new RegraNegocioException("Cliente não encontrado");
            }
        }
        return contaCorrente;
    }
}
