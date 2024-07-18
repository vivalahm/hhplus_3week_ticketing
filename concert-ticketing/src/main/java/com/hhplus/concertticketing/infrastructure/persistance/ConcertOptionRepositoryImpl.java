package com.hhplus.concertticketing.infrastructure.persistance;

import com.hhplus.concertticketing.business.model.ConcertOption;
import com.hhplus.concertticketing.business.repository.ConcertOptionRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ConcertOptionRepositoryImpl implements ConcertOptionRepository {
    private final ConcertOptionJpaRepository concertOptionJpaRepository;

    public ConcertOptionRepositoryImpl(ConcertOptionJpaRepository concertOptionJpaRepository) {
        this.concertOptionJpaRepository = concertOptionJpaRepository;
    }

    @Override
    public ConcertOption saveConcertOption(ConcertOption concertOption) {
        return concertOptionJpaRepository.save(concertOption);
    }

    @Override
    public Optional<ConcertOption> getConcertOptionById(Long id) {
        return concertOptionJpaRepository.findById(id);
    }

    @Override
    public List<ConcertOption> getAllAvailableDatesByConcertId(Long concertId, LocalDateTime currentDateTime) {
        return concertOptionJpaRepository.findAvailableDatesByConcertIdAndConcertDate(concertId, currentDateTime);
    }

    @Override
    public List<ConcertOption> getAllByConcertId(Long concertId) {
        return concertOptionJpaRepository.findAllByConcertId(concertId);
    }

    @Override
    public List<ConcertOption> getByConcertIdAndIsAvailable(Long concertId, Boolean isAvailable) {
        return concertOptionJpaRepository.findByConcertIdAndIsAvailable(concertId, isAvailable);
    }

    @Override
    public void deleteConcertOption(Long id) {
        concertOptionJpaRepository.deleteById(id);
    }
}
