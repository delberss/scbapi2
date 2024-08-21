package com.example.scbapi2.api.controller;

import com.example.scbapi2.api.dto.ContaCorrenteDTO;
import com.example.scbapi2.api.dto.ContaPoupancaDTO;
import com.example.scbapi2.model.entity.Cliente;
import com.example.scbapi2.model.entity.ContaPoupanca;
import com.example.scbapi2.service.ContaPoupancaService;
import com.example.scbapi2.service.ClienteService;
import io.swagger.annotations.*;
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
@Api("API de Contas Poupanças")
public class ContaPoupancaController {

    private final ContaPoupancaService service;
    private final ClienteService clienteService; // Adicione o ClienteService
    private final ModelMapper modelMapper = new ModelMapper();

    @GetMapping()
    @ApiOperation("Obter a lista de contas poupança")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Contas poupança encontradas"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
    public ResponseEntity get() {
        List<ContaPoupanca> contasPoupancas = service.getContasPoupancas();
        return ResponseEntity.ok(contasPoupancas.stream().map(ContaPoupancaDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @ApiOperation("Obter detalhes de uma conta poupança pelo ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Conta poupança encontrada"),
            @ApiResponse(code = 404, message = "Conta poupança não encontrada"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<ContaPoupanca> contaPoupanca = service.getContaPoupancaById(id);
        if (!contaPoupanca.isPresent()) {
            return new ResponseEntity("Conta Poupança não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(contaPoupanca.map(ContaCorrenteDTO::create));
    }

    @PostMapping()
    @ApiOperation("Cadastrar uma nova conta poupança")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Conta poupança criada com sucesso"),
            @ApiResponse(code = 400, message = "Erro ao cadastrar conta poupança"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
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
    @ApiOperation("Atualizar uma conta poupança existente")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Conta poupança atualizada com sucesso"),
            @ApiResponse(code = 404, message = "Conta poupança não encontrada"),
            @ApiResponse(code = 400, message = "Erro ao atualizar conta poupança"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
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
    @ApiOperation("Excluir uma conta poupança pelo ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Conta poupança excluída com sucesso"),
            @ApiResponse(code = 404, message = "Conta poupança não encontrada"),
            @ApiResponse(code = 400, message = "Erro ao excluir conta poupança"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
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
    @ApiOperation("Realizar um depósito em uma conta poupança")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Depósito realizado com sucesso"),
            @ApiResponse(code = 404, message = "Conta poupança não encontrada"),
            @ApiResponse(code = 400, message = "Erro ao realizar depósito"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
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
    @ApiOperation("Realizar um saque em uma conta poupança")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Saque realizado com sucesso"),
            @ApiResponse(code = 404, message = "Conta poupança não encontrada"),
            @ApiResponse(code = 400, message = "Erro ao realizar saque"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
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
    @ApiOperation("Obter contas poupança por ID do cliente")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Contas poupança encontradas"),
            @ApiResponse(code = 404, message = "Cliente não encontrado"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
    public ResponseEntity<List<ContaPoupancaDTO>> getContasPorCliente(@PathVariable("clienteId") Long clienteId) {
        List<ContaPoupanca> contas = service.getContasPorCliente(clienteId);
        return ResponseEntity.ok(contas.stream().map(ContaPoupancaDTO::create).collect(Collectors.toList()));
    }


    private ContaPoupanca converter(ContaPoupancaDTO dto) {
        return modelMapper.map(dto, ContaPoupanca.class);
    }
}
