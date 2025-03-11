package com.sauranet.reports.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rollNumber;
    private String name;
    private String year;
    private String advisor;
    private String gradingPeriod;

    // One-to-Many relationship managed by the Student entity
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScoreCard> scoreCards;
}