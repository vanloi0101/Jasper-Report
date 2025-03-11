package com.sauranet.reports.services;

import com.sauranet.reports.entities.ScoreCard;
import com.sauranet.reports.repositories.ScoreCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoreCardService {

    private final ScoreCardRepository scoreCardRepository;

    public List<ScoreCard> getAllScoreCards() {
        return scoreCardRepository.findAll();
    }
    public List<ScoreCard> getScoreCardsByStudentId(Integer rollNumber) {
        return scoreCardRepository.findByStudent_RollNumber(rollNumber);
    }
}
