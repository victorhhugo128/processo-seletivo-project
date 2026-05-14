package com.github.victorhhugo128.gerenciasalas.repository;

import com.github.victorhhugo128.gerenciasalas.model.Sala;
import com.github.victorhhugo128.gerenciasalas.model.enums.TipoSala;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaRepository extends JpaRepository<Sala, Long> {
    List<Sala> findByTipo(TipoSala tipo);
    List<Sala> findByIdNotIn(List<Long> ids);
}
