package com.example.scbapi2.api.dto;

import com.example.scbapi2.model.entity.Conta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaDTO {

    private Long id;
    private Double saldo;
    private Long clienteId;

    public static ContaDTO create(Conta conta) {
        ModelMapper modelMapper = new ModelMapper();
        ContaDTO dto = modelMapper.map(conta, ContaDTO.class);
        dto.clienteId = conta.getCliente().getId();
        return dto;
    }
}
