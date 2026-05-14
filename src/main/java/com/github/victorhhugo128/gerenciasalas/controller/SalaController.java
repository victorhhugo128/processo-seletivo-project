package com.github.victorhhugo128.gerenciasalas.controller;

import com.github.victorhhugo128.gerenciasalas.dto.ReservaResponse;
import com.github.victorhhugo128.gerenciasalas.dto.SalaRequest;
import com.github.victorhhugo128.gerenciasalas.dto.SalaResponse;
import com.github.victorhhugo128.gerenciasalas.dto.SalasLivresResponse;
import com.github.victorhhugo128.gerenciasalas.service.ReservaService;
import com.github.victorhhugo128.gerenciasalas.service.SalaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sala")
@RequiredArgsConstructor
public class SalaController {
    private final SalaService salaService;
    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<SalaResponse> criarSala(@RequestBody @Valid SalaRequest salaRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(salaService.criarSala(salaRequest));
    }

    @GetMapping
    public ResponseEntity<List<SalaResponse>> retornarTodasSalas(){
        return ResponseEntity.ok(salaService.retornarTodasSalas());
    }

    @GetMapping("/{id}/reservas")
    public ResponseEntity<List<ReservaResponse>> listarReservasData(
        @PathVariable Long id,
        @RequestParam LocalDate data
    ){
        return ResponseEntity.ok(reservaService.listarReservasData(data, id));
    }

    @GetMapping("/livres")
    public ResponseEntity<SalasLivresResponse> listarSalasLivres(
            @RequestParam LocalDate data,
            @RequestParam(required = false) LocalTime horaInicio,
            @RequestParam(required = false) LocalTime horaFim
    ){
        return ResponseEntity.ok(salaService.listarSalasLivres(data, horaInicio, horaFim));
    }

}
