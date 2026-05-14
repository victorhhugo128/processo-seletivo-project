package com.github.victorhhugo128.gerenciasalas.dto;

import com.github.victorhhugo128.gerenciasalas.model.enums.TipoSala;

public record SalaResponse(
        Long id,
        String nome,
        TipoSala tipoSala
){}
