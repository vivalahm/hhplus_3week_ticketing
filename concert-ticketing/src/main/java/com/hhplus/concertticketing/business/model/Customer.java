package com.hhplus.concertticketing.business.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customer")
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double point = 0.0;

    @Version
    private Long version = 0L; // 버전 필드 초기화

    public void chargePoint(Double point) {
        if(point <= 0){
            throw new IllegalArgumentException("포인트 충전은 0보다 커야합니다.");
        }
        this.point += point;
    }


    public void usePoint(Double amount){
        if(amount < 0){
            throw new IllegalStateException("포인트 사용은 0원 이상만 가능합니다.");
        }

        if(amount > this.point){
            throw new IllegalStateException("잔액이 부족합니다.");
        }

        this.point -= amount;
    }
}
