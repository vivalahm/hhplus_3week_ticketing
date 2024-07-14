package com.hhplus.concertticketing.business.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "concert")
@Setter
@Getter
@NoArgsConstructor
public class Concert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String title;

    public Concert(String title) {
        this.title = title;
    }
}
