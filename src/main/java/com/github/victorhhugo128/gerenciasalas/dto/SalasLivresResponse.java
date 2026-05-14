package com.github.victorhhugo128.gerenciasalas.dto;

import java.time.LocalDate;
import java.util.List;

public record SalasLivresResponse(
        LocalDate data,
        List<SalaResponse> salasLivres
){}
