package com.hhplus.concertticketing.domain.repository;

import com.hhplus.concertticketing.domain.model.Concert;

import java.util.List;
import java.util.Optional;

public interface ConcertRepository {
    Concert saveConcert(Concert concert);
    Optional<Concert> getConcertById(Long id);
    List<Concert> getAllConcerts();
    List<Concert> getAvailableConcerts();
    void deleteConcertById(Long id);
}
