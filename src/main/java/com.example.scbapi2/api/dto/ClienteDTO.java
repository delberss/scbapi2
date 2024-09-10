package com.example.scbapi2.api.dto;

import com.example.scbapi2.model.entity.Cliente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

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

    public static ClienteDTO create(Cliente cliente) {
        ModelMapper modelMapper = new ModelMapper();
        ClienteDTO dto = modelMapper.map(cliente, ClienteDTO.class);
        if (cliente.getEndereco() != null) {
            dto.setEndereco(EnderecoDTO.create(cliente.getEndereco()));
        }
        return dto;
    }
}


