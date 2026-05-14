package com.github.victorhhugo128.gerenciasalas.controller;

import com.github.victorhhugo128.gerenciasalas.dto.ReservaRequest;
import com.github.victorhhugo128.gerenciasalas.dto.ReservaResponse;
import com.github.victorhhugo128.gerenciasalas.dto.SalasLivresResponse;
import com.github.victorhhugo128.gerenciasalas.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/reserva")
@RequiredArgsConstructor
public class ReservaController {
    private final ReservaService reservaService;

    @GetMapping
    public ResponseEntity<List<ReservaResponse>> listarTodasReservas(){
        return ResponseEntity.ok(reservaService.listarTodasReservas());
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> criarReserva(@RequestBody @Valid ReservaRequest reservaRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.criarReserva(reservaRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id){
        reservaService.cancelarReserva(id);
        return ResponseEntity.noContent().build();
    }
}
