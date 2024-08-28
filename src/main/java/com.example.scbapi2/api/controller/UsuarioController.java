package com.example.scbapi2.api.controller;

import com.example.scbapi2.api.dto.UsuarioDTO;
import com.example.scbapi2.exception.RegraNegocioException;
import com.example.scbapi2.model.entity.Usuario;
import com.example.scbapi2.service.UsuarioService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Api("API de Usuários")
public class UsuarioController {

    private final UsuarioService service;
    private final ModelMapper modelMapper = new ModelMapper();
    private final PasswordEncoder passwordEncoder;


    @GetMapping()
    @ApiOperation("Obter a lista de usuários")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Usuários encontrados"),
            @ApiResponse(code = 404, message = "Usuários não encontrados"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
    public ResponseEntity get() {
        List<Usuario> usuarios = service.getUsuarios();
        return ResponseEntity.ok(usuarios.stream().map(UsuarioDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @ApiOperation("Obter detalhes de um usuário pelo ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Usuário encontrado"),
            @ApiResponse(code = 404, message = "Usuário não encontrado"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Usuario> usuario = service.getUsuarioById(id);
        if (!usuario.isPresent()) {
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(usuario.map(UsuarioDTO::create));
    }

    @PostMapping()
    @ApiOperation("Cadastrar um novo usuário")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Usuário criado com sucesso"),
            @ApiResponse(code = 400, message = "Erro ao cadastrar usuário"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
    public ResponseEntity post(@RequestBody UsuarioDTO dto) {
        try {
            if (dto.getSenha() == null || dto.getSenha().trim().equals("") ||
                    dto.getSenhaRepeticao() == null || dto.getSenhaRepeticao().trim().equals("")) {
                return ResponseEntity.badRequest().body("Senha inválida");
            }
            if (!dto.getSenha().equals(dto.getSenhaRepeticao())) {
                return ResponseEntity.badRequest().body("Senhas não conferem");
            }
            Usuario usuario = converter(dto);
            String senhaCriptografada = passwordEncoder.encode(dto.getSenha());
            usuario.setSenha(senhaCriptografada);
            usuario = service.salvar(usuario);
            return new ResponseEntity(usuario, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    @ApiOperation("Atualizar um usuário existente")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Usuário atualizado com sucesso"),
            @ApiResponse(code = 404, message = "Usuário não encontrado"),
            @ApiResponse(code = 400, message = "Erro ao atualizar usuário"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody UsuarioDTO dto) {
        if (!service.getUsuarioById(id).isPresent()) {
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            if (dto.getSenha() == null || dto.getSenha().trim().equals("") ||
                    dto.getSenhaRepeticao() == null || dto.getSenhaRepeticao().trim().equals("")) {
                return ResponseEntity.badRequest().body("Senha inválida");
            }
            if (!dto.getSenha().equals(dto.getSenhaRepeticao())) {
                return ResponseEntity.badRequest().body("Senhas não conferem");
            }
            Usuario usuario = converter(dto);
            usuario.setId(id);
            service.salvar(usuario);
            return ResponseEntity.ok(usuario);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Excluir um usuário pelo ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Usuário excluído com sucesso"),
            @ApiResponse(code = 404, message = "Usuário não encontrado"),
            @ApiResponse(code = 400, message = "Erro ao excluir usuário"),
            @ApiResponse(code = 500, message = "Erro interno do servidor")
    })
    public ResponseEntity delete(@PathVariable("id") Long id) {
        Optional<Usuario> usuario = service.getUsuarioById(id);
        if (!usuario.isPresent()) {
            return new ResponseEntity("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(usuario.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Usuario converter(UsuarioDTO dto) {
        return modelMapper.map(dto, Usuario.class);
    }
}
