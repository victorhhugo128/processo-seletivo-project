package com.github.victorhhugo128.gerenciasalas.service;

import com.github.victorhhugo128.gerenciasalas.dto.SalaRequest;
import com.github.victorhhugo128.gerenciasalas.dto.SalaResponse;
import com.github.victorhhugo128.gerenciasalas.dto.SalasLivresResponse;
import com.github.victorhhugo128.gerenciasalas.model.Sala;
import com.github.victorhhugo128.gerenciasalas.model.enums.StatusReserva;
import com.github.victorhhugo128.gerenciasalas.repository.ReservaRepository;
import com.github.victorhhugo128.gerenciasalas.repository.SalaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class SalaService {
    private final SalaRepository salaRepository;
    private final ReservaRepository reservaRepository;

    public SalaService(SalaRepository salaRepository, ReservaRepository reservaRepository) {
        this.salaRepository = salaRepository;
        this.reservaRepository = reservaRepository;
    }

    public SalaResponse criarSala(SalaRequest salaRequest){
        Sala novaSala = new Sala(salaRequest.nome(), salaRequest.tipoSala());

        salaRepository.save(novaSala);

        // Se o nome da sala for nulo ou em branco, atribui um nome padrão com base no ID gerado
        if (novaSala.getNome() == null || novaSala.getNome().isBlank()) {
            novaSala.setNome("Sala " + novaSala.getId());
            salaRepository.save(novaSala);
        }

        return toSalaResponse(novaSala);
    }

    public List<SalaResponse> retornarTodasSalas(){
        return salaRepository.findAll()
                .stream()
                .map(this::toSalaResponse)
                .toList();
    }

    public SalasLivresResponse listarSalasLivres(LocalDate data, LocalTime horaInicio, LocalTime horaFim) {
        List<Long> listaIds = (horaInicio != null && horaFim != null)
                ? reservaRepository.findOccupiedRoomIds(data, horaInicio, horaFim, StatusReserva.ATIVO)
                : reservaRepository.findOccupiedRoomIds(data, StatusReserva.ATIVO);

        List<SalaResponse> salasLivres = listaIds.isEmpty()
                ? salaRepository.findAll().stream().map(this::toSalaResponse).toList()
                : salaRepository.findByIdNotIn(listaIds).stream().map(this::toSalaResponse).toList();

        return new SalasLivresResponse(data, salasLivres);
    }


    private SalaResponse toSalaResponse(Sala sala){
        return new SalaResponse(
                sala.getId(),
                sala.getNome(),
                sala.getTipo()
        );
    }
}
