// ScoreService.java
package typingapp.service;

import typingapp.dao.ScoreDAO;
import typingapp.model.Score;

import java.time.LocalDateTime;
import java.util.Objects;

public class ScoreService {
    private final ScoreDAO scoreDAO;

    public ScoreService(ScoreDAO scoreDAO) {
        Objects.requireNonNull(scoreDAO, "scoreDAO must not be null");
        this.scoreDAO = scoreDAO;
    }

    // ✅ 統一されたパラメータ
    public Score createScore(
        String userName,
        String difficulty,
        int correctCount,
        int nearCount,
        int incorrectCount,
        int timeoutCount,
        int correctCharCount,
        int wrongCharCount,
        double averageTypingSpeed,
        int timeRemaining,
        int totalTime,
        LocalDateTime currentDate
    ) {
        Objects.requireNonNull(userName, "userName must not be null");
        Objects.requireNonNull(difficulty, "difficulty must not be null");
        Objects.requireNonNull(currentDate, "currentDate must not be null");
        
        if (totalTime <= 0) {
            throw new IllegalArgumentException("totalTime must be positive");
        }
        
        int finalScore = ScoreCalculator.calculateFinalScore(
            correctCount,
            nearCount,
            incorrectCount,
            timeoutCount,
            timeRemaining
        );

        double avgSpeed = ScoreCalculator.calculateTypingSpeed(
        	correctCharCount,
        	totalTime,
        	timeRemaining
        );

        return new Score(
            userName,
            difficulty,
            finalScore,
            correctCount,
            nearCount,
            incorrectCount,
            timeoutCount,
            correctCharCount,
            wrongCharCount,
            avgSpeed,
            timeRemaining,
            totalTime,
            currentDate
        );
    }
    
    public boolean save(Score score) {
        Objects.requireNonNull(score, "score must not be null");
        return scoreDAO.save(score);
    }
}