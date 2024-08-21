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
import io.swagger.annotations.*;
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
@Api("API de Contas")
public class ContaController {

    @Autowired
    private ContaCorrenteService contaCorrenteService;

    @Autowired
    private ContaPoupancaService contaPoupancaService;

    private final ContaService service;
    private final ModelMapper modelMapper = new ModelMapper();

    @GetMapping()
    @ApiOperation("Obter a lista de contas")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Contas encontradas"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
    public ResponseEntity get() {
        List<Conta> contas = service.getContas();
        return ResponseEntity.ok(contas.stream().map(ContaDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @ApiOperation("Obter detalhes de uma conta pelo ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Conta encontrada"),
            @ApiResponse(code = 404, message = "Conta não encontrada"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Conta> conta = service.getContaById(id);
        if (!conta.isPresent()) {
            return new ResponseEntity("Conta não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(conta.map(ContaDTO::create));
    }

    @PostMapping()
    @ApiOperation("Cadastrar uma nova conta")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Conta criada com sucesso"),
            @ApiResponse(code = 400, message = "Erro ao cadastrar conta"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
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
    @ApiOperation("Atualizar uma conta existente")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Conta atualizada com sucesso"),
            @ApiResponse(code = 404, message = "Conta não encontrada"),
            @ApiResponse(code = 400, message = "Erro ao atualizar conta"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
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
    @ApiOperation("Excluir uma conta pelo ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Conta excluída com sucesso"),
            @ApiResponse(code = 404, message = "Conta não encontrada"),
            @ApiResponse(code = 400, message = "Erro ao excluir conta"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
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
    @ApiOperation("Realizar um saque em uma conta")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Saque realizado com sucesso"),
            @ApiResponse(code = 404, message = "Conta não encontrada"),
            @ApiResponse(code = 400, message = "Erro ao realizar saque"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
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
    @ApiOperation("Realizar um depósito em uma conta")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Depósito realizado com sucesso"),
            @ApiResponse(code = 404, message = "Conta não encontrada"),
            @ApiResponse(code = 400, message = "Erro ao realizar depósito"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
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
    @ApiOperation("Obter contas por ID do cliente")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Contas encontradas"),
            @ApiResponse(code = 404, message = "Cliente não encontrado"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
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
