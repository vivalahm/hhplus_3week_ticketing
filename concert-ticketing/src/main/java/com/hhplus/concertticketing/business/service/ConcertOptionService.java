package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.ConcertOption;
import com.hhplus.concertticketing.business.repository.ConcertOptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConcertOptionService {
    private final ConcertOptionRepository concertOptionRepository;

    public ConcertOptionService(ConcertOptionRepository concertOptionRepository) {
        this.concertOptionRepository = concertOptionRepository;
    }

    public ConcertOption saveConcertOption(ConcertOption concertOption) {
        return concertOptionRepository.saveConcertOption(concertOption);
    }

    public Optional<ConcertOption> getConcertOptionById(Long concertOptionId) {
        return concertOptionRepository.getConcertOptionById(concertOptionId);
    }

    public List<ConcertOption> getAvailableConcertOptions(Long concertOptionId, LocalDateTime currentDateTime){
        List<ConcertOption> concertOptionList = concertOptionRepository.getAllAvailableDatesByConcertId(concertOptionId, currentDateTime);
        if(concertOptionList.isEmpty()){
            throw new IllegalStateException("예약가능한 콘서트 옵션이 없습니다.");
        }
        return concertOptionList;
    }

    public void deleteConcertOptionById(Long concertOptionId) {
        concertOptionRepository.deleteConcertOption(concertOptionId);
    }
}
