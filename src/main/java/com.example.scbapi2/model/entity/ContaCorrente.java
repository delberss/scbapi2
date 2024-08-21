package com.example.scbapi2.model.entity;

import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaCorrente extends Conta {

    private Double limiteChequeEspecial;

    @Override
    public void depositar(Double valor) {
        this.setSaldo(this.getSaldo() + valor);
    }

    @Override
    public void sacar(Double valor) {
        if (this.getSaldo() + limiteChequeEspecial >= valor) {
            this.setSaldo(this.getSaldo() - valor);
        } else {
            throw new IllegalArgumentException("Saldo insuficiente.");
        }
    }
}