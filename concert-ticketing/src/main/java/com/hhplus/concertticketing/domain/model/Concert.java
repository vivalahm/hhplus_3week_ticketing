package com.hhplus.concertticketing.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "concert")
@Setter
@Getter
@NoArgsConstructor
public class Concert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Boolean isSoldOut = false; // 매진 여부

    private Boolean isFinished = false; // 종료 여부


    public Concert(String title) {
        this.title = title;
    }

    public void soldOut(){
        this.isSoldOut = true;
    }

    public void finishConcert(){
        this.isFinished = true;
    }

    public void reopenSales(){
        this.isSoldOut = false;
    }
}
