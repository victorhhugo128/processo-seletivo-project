package com.github.victorhhugo128.gerenciasalas.repository;

import com.github.victorhhugo128.gerenciasalas.model.Reserva;
import com.github.victorhhugo128.gerenciasalas.model.enums.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    @Query("""
        SELECT r.sala.id FROM Reserva r
        WHERE r.data = :data
          AND r.status = :status
          AND r.horaInicio < :fim
          AND r.horaFim > :inicio
        """)
    List<Long> findOccupiedRoomIds(
            @Param("data") LocalDate data,
            @Param("inicio") LocalTime inicio,
            @Param("fim") LocalTime fim,
            @Param("status") StatusReserva status
    );

    @Query("""
        SELECT r.sala.id FROM Reserva r
        WHERE r.data = :data
          AND r.status = :status
        """)
    List<Long> findOccupiedRoomIds(
            @Param("data") LocalDate data,
            @Param("status") StatusReserva status
    );

    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
        FROM Reserva r
        WHERE r.data = :data
          AND r.sala.id = :salaId
          AND r.status = :status
          AND r.horaInicio < :horaFim
          AND r.horaFim > :horaInicio
    """)
    boolean existsReserva(
            @Param("data") LocalDate data,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFim") LocalTime horaFim,
            @Param("salaId") Long salaId,
            @Param("status") StatusReserva status
    );

    List<Reserva> findByDataAndSalaId(LocalDate data, Long salaId);
}
