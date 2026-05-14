package com.github.victorhhugo128.gerenciasalas.service;

import com.github.victorhhugo128.gerenciasalas.dto.ReservaRequest;
import com.github.victorhhugo128.gerenciasalas.dto.ReservaResponse;
import com.github.victorhhugo128.gerenciasalas.exception.ConflitoReservaException;
import com.github.victorhhugo128.gerenciasalas.exception.RecursoNaoEncontradoException;
import com.github.victorhhugo128.gerenciasalas.model.Reserva;
import com.github.victorhhugo128.gerenciasalas.model.Sala;
import com.github.victorhhugo128.gerenciasalas.model.enums.StatusReserva;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private SalaRepository salaRepository;

    @InjectMocks
    private ReservaService reservaService;

    private ReservaRequest reservaRequest;

    @BeforeEach
    void setUp() {
        // executado antes de cada teste
        reservaRequest = new ReservaRequest(
                LocalDate.now(),
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                1L
        );
        // comportamento padrão
        when(salaRepository.findById(any()))
                .thenReturn(Optional.of(new Sala()));

        when(reservaRepository.existsReserva(any(), any(), any(), any(), any()))
                .thenReturn(false);

        when(reservaRepository.findById(1L))
                .thenReturn(Optional.of(new Reserva()));
    }

    @Test
    void deveLancarExcecaoQuandoHouverConflito() {
        // e já existe reserva no horário
        when(reservaRepository.existsReserva(any(), any(), any(), any(), any()))
                .thenReturn(true);

        // então deve lançar conflito
        assertThrows(ConflitoReservaException.class,
                () -> reservaService.criarReserva(reservaRequest));
    }

    @Test
    void deveLancarExcecaoQuandoSalaNaoExistir() {
        // dado que a sala não existe
        when(salaRepository.findById(1L))
                .thenReturn(Optional.empty());

        // deve lançar recurso não encontrado
        assertThrows(RecursoNaoEncontradoException.class,
                () -> reservaService.criarReserva(reservaRequest));
    }

    @Test
    void deveCriarReservaComSucesso() {
        ReservaResponse response = reservaService.criarReserva(reservaRequest);

        // deve criar reserva com sucesso
        assertNotNull(response);
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void deveLancarExcecaoQuandoReservaNaoExistir() {
        // dado que a reserva não existe
        when(reservaRepository.findById(1L))
                .thenReturn(Optional.empty());

        // deve lançar recurso não encontrado
        assertThrows(RecursoNaoEncontradoException.class,
                () -> reservaService.cancelarReserva(1L));
    }

    @Test
    void deveLancarExcecaoQuandoReservaJaCancelada() {
        // dado que a reserva já está cancelada
        Reserva reserva = new Reserva();
        reserva.setStatus(StatusReserva.CANCELADO);
        when(reservaRepository.findById(1L))
                .thenReturn(Optional.of(reserva));

        // deve lançar conflito, pois reserva já está cancelada
        assertThrows(ConflitoReservaException.class,
                () -> reservaService.cancelarReserva(1L));
    }

    @Test
    void deveCancelarReservaComSucesso() {
        Reserva reserva = new Reserva();
        when(reservaRepository.findById(1L))
                .thenReturn(Optional.of(reserva));

        reservaService.cancelarReserva(1L);

        assertEquals(StatusReserva.CANCELADO, reserva.getStatus());
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverReservas() {
        // dado que não há reservas para a sala nessa data
        when(reservaRepository.findByDataAndSalaId(any(), any()))
                .thenReturn(List.of());

        List<ReservaResponse> response = reservaService.listarReservasData(LocalDate.now(), 1L);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

}
