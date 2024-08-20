package com.example.scbapi2.api.dto;

import com.example.scbapi2.model.entity.ContaCorrente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaCorrenteDTO extends ContaDTO{
    private Long id;
    private String numero;
    private Double saldo;
    private Double limiteChequeEspecial;
    private Long clienteId;  // Deve ser um Long, não um objeto Cliente

    public static ContaCorrenteDTO create(ContaCorrente contaCorrente) {
        ModelMapper modelMapper = new ModelMapper();
        ContaCorrenteDTO dto = modelMapper.map(contaCorrente, ContaCorrenteDTO.class);
        if (contaCorrente.getCliente() != null) {
            dto.setClienteId(contaCorrente.getCliente().getId());
        }
        return dto;
    }
}
