package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Concert;
import com.hhplus.concertticketing.business.repository.ConcertRepository;
import com.hhplus.concertticketing.presentation.dto.ConcertOptionDto;
import com.hhplus.concertticketing.presentation.dto.SeatDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConcertService {
    private final ConcertRepository concertRepository;

    public ConcertService(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
    }

    public Concert saveConcert(Concert concert){
        return concertRepository.saveConcert(concert);
    }

    public Optional<Concert> getConcertById(Long id){
        return concertRepository.getConcertById(id);
    }

    public List<Concert> getAllConcert(){
        return concertRepository.getAllConcerts();
    }

    public void deleteConcertById(Long id){
        concertRepository.deleteConcertById(id);
    }
}
