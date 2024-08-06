package com.example.scbapi2.api.dto;

import com.example.scbapi2.model.entity.Cliente;
import com.example.scbapi2.model.entity.Conta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private EnderecoDTO endereco;
    private List<Long> contas; // Apenas IDs das contas

    public static ClienteDTO create(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setCpf(cliente.getCpf());
        dto.setEmail(cliente.getEmail());
        dto.setTelefone(cliente.getTelefone());
        if (cliente.getEndereco() != null) {
            dto.setEndereco(EnderecoDTO.create(cliente.getEndereco()));
        }
        if (cliente.getContas() != null) {
            dto.setContas(cliente.getContas().stream()
                    .map(Conta::getId)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
