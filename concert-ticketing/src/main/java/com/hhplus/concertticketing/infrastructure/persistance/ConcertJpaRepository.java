package com.hhplus.concertticketing.infrastructure.persistance;

import com.hhplus.concertticketing.domain.model.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
    List<Concert> findAllByIsFinishedFalseAndIsSoldOutFalse();
}
