package com.sem3bank.sem3bank.repository;

import com.sem3bank.sem3bank.model.Movimentation;
import com.sem3bank.sem3bank.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface MovimentationRepository extends JpaRepository<Movimentation, Long> {

    @Query(value = "select * from movimentacao where data between =:data1 and :data2", nativeQuery = true)
    User findByData(@Param("data1") LocalDate data1, @Param("data2") LocalDate data2);

}
