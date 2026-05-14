package com.github.victorhhugo128.gerenciasalas.service;

import com.github.victorhhugo128.gerenciasalas.dto.SalaRequest;
import com.github.victorhhugo128.gerenciasalas.dto.SalaResponse;
import com.github.victorhhugo128.gerenciasalas.dto.SalasLivresResponse;
import com.github.victorhhugo128.gerenciasalas.model.Sala;
import com.github.victorhhugo128.gerenciasalas.model.enums.TipoSala;
import com.github.victorhhugo128.gerenciasalas.repository.ReservaRepository;
import com.github.victorhhugo128.gerenciasalas.repository.SalaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SalaServiceTest {

    @Mock
    private SalaRepository salaRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private SalaService salaService;

    private SalaRequest salaRequestComNome;
    private SalaRequest salaRequestSemNome;

    @BeforeEach
    void setUp() {
        salaRequestComNome = new SalaRequest("Sala Conferência", TipoSala.COLETIVA);
        salaRequestSemNome = new SalaRequest(null, TipoSala.INDIVIDUAL);
    }

    @Test
    void deveCriarSalaComNomeFornecido() {
        // dado que o save retorna a própria sala com id gerado
        Sala salaSalva = new Sala("Sala Conferência", TipoSala.COLETIVA);
        when(salaRepository.save(any(Sala.class)))
                .thenReturn(salaSalva);

        SalaResponse response = salaService.criarSala(salaRequestComNome);

        // deve retornar a sala criada
        assertNotNull(response);

        // deve ter chamado save apenas uma vez, sem nome padrão necessário
        verify(salaRepository, times(1)).save(any(Sala.class));
    }

    @Test
    void deveCriarSalaComNomePadraoQuandoNomeForNulo() {
        // dado que o save retorna sala com id gerado mas sem nome
        Sala salaSalva = new Sala(null, TipoSala.INDIVIDUAL);
        when(salaRepository.save(any(Sala.class)))
                .thenReturn(salaSalva);

        salaService.criarSala(salaRequestSemNome);

        // deve ter chamado save duas vezes, uma para criar, outra para atualizar o nome
        verify(salaRepository, times(2)).save(any(Sala.class));
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverSalas() {
        // dado que não há salas cadastradas
        when(salaRepository.findAll())
                .thenReturn(List.of());

        List<SalaResponse> response = salaService.retornarTodasSalas();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void deveRetornarTodasSalasQuandoNenhumaEstiverOcupada() {
        // dado que não há salas ocupadas
        when(reservaRepository.findOccupiedRoomIds(any(), any(), any(), any()))
                .thenReturn(List.of());

        // e há salas cadastradas
        when(salaRepository.findAll())
                .thenReturn(List.of(new Sala("Sala 1", TipoSala.INDIVIDUAL)));

        SalasLivresResponse response = salaService.listarSalasLivres(
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(11, 0));

        assertNotNull(response);
        assertFalse(response.salasLivres().isEmpty());
    }

    @Test
    void deveRetornarSalasLivresComFiltroDeHorario() {
        // dado que a sala de id 1 está ocupada no horário
        when(reservaRepository.findOccupiedRoomIds(any(), any(), any(), any()))
                .thenReturn(List.of(1L));

        // e há outra sala livre
        when(salaRepository.findByIdNotIn(any()))
                .thenReturn(List.of(new Sala("Sala 2", TipoSala.COLETIVA)));

        SalasLivresResponse response = salaService.listarSalasLivres(
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(11, 0));

        assertNotNull(response);
        assertFalse(response.salasLivres().isEmpty());
    }

    @Test
    void deveRetornarSalasLivresSemFiltroDeHorario() {
        // dado que não há salas ocupadas no dia
        when(reservaRepository.findOccupiedRoomIds(any(), any()))
                .thenReturn(List.of());

        // e há salas cadastradas
        when(salaRepository.findAll())
                .thenReturn(List.of(new Sala("Sala 1", TipoSala.AUDITORIO)));

        // sem filtro de horário, passa null nos horários
        SalasLivresResponse response = salaService.listarSalasLivres(LocalDate.now(), null, null);

        assertNotNull(response);
        assertFalse(response.salasLivres().isEmpty());
    }
}