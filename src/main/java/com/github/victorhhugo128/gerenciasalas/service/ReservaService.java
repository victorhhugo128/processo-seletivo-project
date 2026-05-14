package com.github.victorhhugo128.gerenciasalas.service;

import com.github.victorhhugo128.gerenciasalas.dto.ReservaRequest;
import com.github.victorhhugo128.gerenciasalas.dto.ReservaResponse;
import com.github.victorhhugo128.gerenciasalas.dto.SalaResponse;
import com.github.victorhhugo128.gerenciasalas.exception.ConflitoReservaException;
import com.github.victorhhugo128.gerenciasalas.exception.RecursoNaoEncontradoException;
import com.github.victorhhugo128.gerenciasalas.model.Reserva;
import com.github.victorhhugo128.gerenciasalas.model.Sala;
import com.github.victorhhugo128.gerenciasalas.model.enums.StatusReserva;
import com.github.victorhhugo128.gerenciasalas.repository.ReservaRepository;
import com.github.victorhhugo128.gerenciasalas.repository.SalaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final SalaRepository salaRepository;

    public ReservaService(ReservaRepository reservaRepository, SalaRepository salaRepository) {
        this.reservaRepository = reservaRepository;
        this.salaRepository = salaRepository;
    }

    public List<ReservaResponse> listarTodasReservas(){
        return reservaRepository.findAll()
                .stream()
                .map(this::toReservaResponse)
                .toList();
    }

    public ReservaResponse criarReserva(ReservaRequest reservaRequest){
        boolean existeReserva = reservaRepository.existsReserva(
                reservaRequest.data(),
                reservaRequest.horaInicio(),
                reservaRequest.horaFim(),
                reservaRequest.salaId(),
                StatusReserva.ATIVO);

        if(existeReserva){
            throw new ConflitoReservaException("Sala já reservada para o horário solicitado");
        }

        Sala sala = salaRepository.findById(reservaRequest.salaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala não encontrada"));

        Reserva novaReserva = new Reserva(
                reservaRequest.data(),
                reservaRequest.horaInicio(),
                reservaRequest.horaFim(),
                StatusReserva.ATIVO,
                sala);

        reservaRepository.save(novaReserva);

        return toReservaResponse(novaReserva);
    }

    public List<ReservaResponse> listarReservasData(LocalDate data, Long salaId){
        return reservaRepository.findByDataAndSalaId(data, salaId)
                .stream()
                .map(this::toReservaResponse)
                .toList();
    }

    public void cancelarReserva(Long reservaId){
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada."));

        if(reserva.getStatus() == StatusReserva.CANCELADO) {
            throw new ConflitoReservaException("Reserva já está cancelada.");
        }

        reserva.setStatus(StatusReserva.CANCELADO);
        reserva.setCanceladaEm(LocalDateTime.now());
        reservaRepository.save(reserva);
    }

    private ReservaResponse toReservaResponse(Reserva reserva) {
        return new ReservaResponse(reserva.getId(),
                reserva.getSala().getNome(),
                reserva.getSala().getTipo(),
                reserva.getData(),
                reserva.getHoraInicio(),
                reserva.getHoraFim(),
                reserva.getStatus());
    }

}
