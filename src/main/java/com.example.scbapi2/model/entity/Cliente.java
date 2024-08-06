package com.example.scbapi2.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends Pessoa {

    @OneToMany(mappedBy = "cliente")
    @JsonIgnore // Ignora a serialização recursiva
    private List<Conta> contas;
}
