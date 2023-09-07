package com.sem3bank.sem3bank.repository;

import com.sem3bank.sem3bank.model.Movimentation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovimentationRepository extends JpaRepository<Movimentation, Long> {

    @Query(value = "SELECT * FROM movimentacao WHERE usuario = :idUsuario AND DATE(data) BETWEEN :dataInicio AND :dataFim", nativeQuery = true)
    List<Movimentation> findAllByPeriod(@Param("idUsuario") Long idUsuario, @Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
}
