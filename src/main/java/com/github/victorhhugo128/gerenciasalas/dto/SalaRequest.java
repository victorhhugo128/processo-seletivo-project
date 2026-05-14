package com.github.victorhhugo128.gerenciasalas.dto;

import com.github.victorhhugo128.gerenciasalas.model.enums.TipoSala;
import jakarta.validation.constraints.NotNull;

public record SalaRequest(
        String nome,
        @NotNull TipoSala tipoSala
){}
