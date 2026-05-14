package com.github.victorhhugo128.gerenciasalas.model;

import com.github.victorhhugo128.gerenciasalas.model.enums.TipoSala;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="sala")
@Getter
@Setter
@NoArgsConstructor
public class Sala {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Nullable
    private String nome;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TipoSala tipo;


    public Sala(String nome, TipoSala tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }
}
