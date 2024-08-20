package com.hhplus.concertticketing.infrastructure.persistance;

import com.hhplus.concertticketing.domain.model.Concert;
import com.hhplus.concertticketing.domain.repository.ConcertRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ConcertRepositoryImpl implements ConcertRepository {
    private final ConcertJpaRepository concertJpaRepository;

    public ConcertRepositoryImpl(ConcertJpaRepository concertJpaRepository) {
        this.concertJpaRepository = concertJpaRepository;
    }


    @Override
    public Concert saveConcert(Concert concert) {
        return concertJpaRepository.save(concert);
    }

    @Override
    public Optional<Concert> getConcertById(Long id) {
        return concertJpaRepository.findById(id);
    }

    @Override
    public List<Concert> getAllConcerts() {
        return concertJpaRepository.findAll();
    }

    @Override
    public List<Concert> getAvailableConcerts() {
        return concertJpaRepository.findAllByIsFinishedFalseAndIsSoldOutFalse();
    }

    @Override
    public void deleteConcertById(Long id) {
        concertJpaRepository.deleteById(id);
    }
}
