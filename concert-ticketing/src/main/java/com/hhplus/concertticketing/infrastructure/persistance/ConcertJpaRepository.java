package com.hhplus.concertticketing.infrastructure.persistance;

import com.hhplus.concertticketing.business.model.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
}
