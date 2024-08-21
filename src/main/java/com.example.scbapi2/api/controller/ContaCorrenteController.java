package com.example.scbapi2.api.controller;

import com.example.scbapi2.api.dto.ContaCorrenteDTO;
import com.example.scbapi2.exception.RegraNegocioException;
import com.example.scbapi2.model.entity.Cliente;
import com.example.scbapi2.model.entity.ContaCorrente;
import com.example.scbapi2.service.ClienteService;
import com.example.scbapi2.service.ContaCorrenteService;
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
@RequestMapping("/api/v1/contascorrentes")
@RequiredArgsConstructor
@Api("API de Contas Correntes")
public class ContaCorrenteController {

    private final ContaCorrenteService service;
    private final ClienteService clienteService;
    private final ModelMapper modelMapper = new ModelMapper();

    @GetMapping()
    @ApiOperation("Obter a lista de contas correntes")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Contas correntes encontradas"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
    public ResponseEntity get() {
        List<ContaCorrente> contasCorrentes = service.getContasCorrentes();
        return ResponseEntity.ok(contasCorrentes.stream().map(ContaCorrenteDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @ApiOperation("Obter detalhes de uma conta corrente pelo ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Conta corrente encontrada"),
            @ApiResponse(code = 404, message = "Conta corrente não encontrada"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<ContaCorrente> contaCorrente = service.getContaCorrenteById(id);
        if (!contaCorrente.isPresent()) {
            return new ResponseEntity("Conta Corrente não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(contaCorrente.map(ContaCorrenteDTO::create));
    }

    @PostMapping()
    @ApiOperation("Cadastrar uma nova conta corrente")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Conta corrente criada com sucesso"),
            @ApiResponse(code = 400, message = "Erro ao cadastrar conta corrente"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
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
    @ApiOperation("Atualizar uma conta corrente existente")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Conta corrente atualizada com sucesso"),
            @ApiResponse(code = 404, message = "Conta corrente não encontrada"),
            @ApiResponse(code = 400, message = "Erro ao atualizar conta corrente"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
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

    @DeleteMapping("/{id}")
    @ApiOperation("Excluir uma conta corrente pelo ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Conta corrente excluída com sucesso"),
            @ApiResponse(code = 404, message = "Conta corrente não encontrada"),
            @ApiResponse(code = 400, message = "Erro ao excluir conta corrente"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
    public ResponseEntity<String> excluir(@PathVariable("id") Long id) {
        Optional<ContaCorrente> contaCorrente = service.getContaCorrenteById(id);
        if (!contaCorrente.isPresent()) {
            return new ResponseEntity<>("Conta Corrente não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(contaCorrente.get());
            return ResponseEntity.ok("Conta Corrente excluída com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/sacar")
    @ApiOperation("Realizar um saque em uma conta corrente")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Saque realizado com sucesso"),
            @ApiResponse(code = 404, message = "Conta corrente não encontrada"),
            @ApiResponse(code = 400, message = "Erro ao realizar saque"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
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
    @ApiOperation("Realizar um depósito em uma conta corrente")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Depósito realizado com sucesso"),
            @ApiResponse(code = 404, message = "Conta corrente não encontrada"),
            @ApiResponse(code = 400, message = "Erro ao realizar depósito"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
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

    @GetMapping("/cliente/{clienteId}")
    @ApiOperation("Obter contas correntes por ID do cliente")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Contas correntes encontradas"),
            @ApiResponse(code = 404, message = "Cliente não encontrado"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
    public ResponseEntity<List<ContaCorrenteDTO>> getContasPorCliente(@PathVariable("clienteId") Long clienteId) {
        List<ContaCorrente> contas = service.getContasPorCliente(clienteId);
        return ResponseEntity.ok(contas.stream().map(ContaCorrenteDTO::create).collect(Collectors.toList()));
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
