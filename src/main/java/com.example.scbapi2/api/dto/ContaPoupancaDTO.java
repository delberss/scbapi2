package com.example.scbapi2.api.dto;

import com.example.scbapi2.model.entity.ContaPoupanca;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaPoupancaDTO extends ContaDTO {

    private Double taxaJuros;
    private Long clienteId;  // Adiciona o campo clienteId

    public static ContaPoupancaDTO create(ContaPoupanca contaPoupanca) {
        ModelMapper modelMapper = new ModelMapper();
        ContaPoupancaDTO dto = modelMapper.map(contaPoupanca, ContaPoupancaDTO.class);
        dto.setClienteId(contaPoupanca.getCliente().getId());
        return dto;
    }
}
