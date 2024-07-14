package com.hhplus.concertticketing.business.repository;

import com.hhplus.concertticketing.business.model.Concert;

import java.util.List;
import java.util.Optional;

public interface ConcertRepository {
    Concert saveConcert(Concert concert);
    Optional<Concert> getConcertById(Long id);
    List<Concert> getAllConcerts();
    void deleteConcertById(Long id);
}
