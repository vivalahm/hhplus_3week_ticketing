package com.hhplus.concertticketing.business.repository;

import com.hhplus.concertticketing.business.model.ConcertOption;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConcertOptionRepository {
    ConcertOption saveConcertOption(ConcertOption concertOption);
    Optional<ConcertOption> getConcertOptionById(Long id);
    List<ConcertOption> getAllAvailableDatesByConcertId(Long concertId, LocalDateTime currentDateTime);
    List<ConcertOption> getAllByConcertId(Long concertId);
    List<ConcertOption> getByConcertIdAndIsAvailable(Long concertId, Boolean isAvailable);
    void deleteConcertOption(Long id);
}
