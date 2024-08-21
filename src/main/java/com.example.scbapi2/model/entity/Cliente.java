package com.example.scbapi2.model.entity;

import javax.persistence.Entity;
import lombok.Data;
import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
public class Cliente extends Pessoa {
    // @OneToMany(mappedBy = "cliente")
    //    @JsonIgnore // Ignora a serialização recursiva
    //    private List<Conta> contas;
    //}
}
