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
    private Long id;
    private String numero;
    private Double saldo;
    private Double taxaJuros;
    private Long clienteId;  // Deve ser um Long, não um objeto Cliente

    public static ContaPoupancaDTO create(ContaPoupanca contaPoupanca) {
        ModelMapper modelMapper = new ModelMapper();
        ContaPoupancaDTO dto = modelMapper.map(contaPoupanca, ContaPoupancaDTO.class);
        if (contaPoupanca.getCliente() != null) {
            dto.setClienteId(contaPoupanca.getCliente().getId());
        }
        return dto;
    }
}
