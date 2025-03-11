package com.sauranet.reports.repositories;

import com.sauranet.reports.entities.ScoreCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScoreCardRepository extends JpaRepository<ScoreCard, Integer> {
    List<ScoreCard> findByStudent_RollNumber(Integer rollNumber);
}
