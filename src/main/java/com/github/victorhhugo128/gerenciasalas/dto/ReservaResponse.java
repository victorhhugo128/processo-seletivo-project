package com.github.victorhhugo128.gerenciasalas.dto;

import com.github.victorhhugo128.gerenciasalas.model.enums.StatusReserva;
import com.github.victorhhugo128.gerenciasalas.model.enums.TipoSala;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaResponse(
        Long id,
        String nomeSala,
        TipoSala tipoSala,
        LocalDate data,
        LocalTime horaInicio,
        LocalTime horaFim,
        StatusReserva statusReserva
){}
