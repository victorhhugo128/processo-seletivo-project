package com.github.victorhhugo128.gerenciasalas.model;

import com.github.victorhhugo128.gerenciasalas.model.enums.StatusReserva;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name="reserva")
@Getter
@Setter
@NoArgsConstructor
public class Reserva {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate data;

    @NotNull
    private LocalTime horaInicio;

    @NotNull
    private LocalTime horaFim;

    @NotNull
    private StatusReserva status;

    @Nullable
    private LocalDateTime canceladaEm;

    @ManyToOne
    @JoinColumn(name="sala_id", nullable=false)
    private Sala sala;

    public Reserva(LocalDate data, LocalTime horaInicio, LocalTime horaFim, StatusReserva status, Sala sala) {
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.status = status;
        this.sala = sala;
    }
}
