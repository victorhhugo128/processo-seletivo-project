package com.github.victorhhugo128.gerenciasalas.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record AgendaDiaResponse(
        LocalDate date,
        List<ReservaResponse> reservas
){}
