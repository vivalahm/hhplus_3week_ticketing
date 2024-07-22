package com.hhplus.concertticketing.infrastructure.persistance;

import com.hhplus.concertticketing.business.model.ConcertOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertOptionJpaRepository extends JpaRepository<ConcertOption, Long> {

    @Query("SELECT co FROM ConcertOption co WHERE co.concertId = :concertId AND co.concertDate > :currentDateTime AND co.isAvailable = TRUE" )
    List<ConcertOption> findAvailableDatesByConcertIdAndConcertDate(@Param("concertId")Long concertId,@Param("currentDateTime") LocalDateTime currentDateTime);

    List<ConcertOption> findAllByConcertId(Long concertId);

    List<ConcertOption> findByConcertIdAndIsAvailable(Long concertId, Boolean IsAvailable);
}
