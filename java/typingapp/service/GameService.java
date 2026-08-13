package typingapp.service;

import typingapp.model.*;
import typingapp.dao.WordDAO;

import java.util.*;
import java.util.Objects;

public class GameService {
	
    private final WordDAO wordDAO;
    private int currentIndex;
    private List<TypingWord> words;
    private TypingWord currentWord;
    
    private static final boolean TEST_MODE = true;
    
    public GameService(WordDAO wordDAO) {
    	Objects.requireNonNull(wordDAO, "wordDAO must not be null");
        this.wordDAO = wordDAO;
        this.currentIndex = 0;
        this.words = new ArrayList<>();
    }
    
    public void loadWords(String difficulty, int totalQuestions) {
        Objects.requireNonNull(difficulty, "difficulty must not be null");
        
        System.out.println("totalQuestions = " + totalQuestions);
        
        if (totalQuestions <= 0) {
            throw new IllegalArgumentException("totalQuestions must be greater than 0");
        }
        
        if (TEST_MODE) {

            System.out.println("テストモード：オン");

            TypingWord testWord = wordDAO.findTestWord();

            this.words = new ArrayList<>();

            for (int i = 0; i < totalQuestions; i++) {
                words.add(testWord);
            }

            currentIndex = 0;

            System.out.println("単語読み込み完了: " + words.size() + "問");
            return;
        }

        try {
	        List<TypingWord> allWords = wordDAO.findByDifficulty(difficulty);
	
            if (allWords.isEmpty()) {
                throw new RuntimeException("難易度'" + difficulty + "'の単語が見つかりません");
            }
            
	        Collections.shuffle(allWords);
	
	        int limit = Math.min(totalQuestions, allWords.size());
	
	        this.words = new ArrayList<>(allWords.subList(0, limit));
	        this.currentIndex = 0;
            
            System.out.println("単語読み込み完了: " + limit + "問");
            System.out.println("  最初の単語: " + words.get(0).word());
            
        } catch (Exception e) {
            System.err.println("単語取得エラー: " + e.getMessage());
            throw new RuntimeException("単語の取得に失敗しました", e);
        }
        System.out.println("words.size = " + words.size());
    }
    
    public Optional<TypingWord> nextWord() {
    	
        if (words == null || words.isEmpty() || currentIndex >= words.size()) {
            System.out.println("全ての単語を取得しました（残り: " + currentIndex + "問）");
            return Optional.empty();
        }

        currentWord = words.get(currentIndex);
        currentIndex++;
        
        return Optional.of(currentWord);
    }

    public TypingWord getCurrentWord() {
        if (currentWord == null) {
            throw new IllegalStateException("現在の単語が設定されていません");
        }
        
        return currentWord;
    }

    public GameResult finishGame(GameSession session, int timeLimit, int timeRemaining) {
        Objects.requireNonNull(session, "session must not be null");
        
        int correctCount = 0;
        int nearCount = 0;
        int incorrectCount = 0;
        int timeoutCount = 0;
        int correctCharCount = 0;
        int wrongCharCount = 0;
       
        List<WordResult> results = session.results();

        for (WordResult r : results) {
            EvaluationResult eval = r.result().result();
            
            switch (eval) {
                case CORRECT -> {
                    correctCount++;
                    correctCharCount += r.input().length();
                }
                case NEAR -> {
                    nearCount++;
                    correctCharCount += r.input().length();
                }
                case TIMEOUT -> {
                    timeoutCount++;
                    correctCharCount += r.input().length();
                }
                case INCORRECT -> {
                    incorrectCount++;
                    wrongCharCount += r.wrongCharCount();
                }
            }
            
            // 間違えた文字数を累積
            wrongCharCount += r.wrongCharCount();
        }

        int totalQuestions = results.size();
        
        double averageTypingSpeed = ScoreCalculator.calculateTypingSpeed(
            correctCharCount, 
            timeLimit, 
            timeRemaining
        );

        int finalScore = ScoreCalculator.calculateFinalScore(
            correctCount, 
            nearCount, 
            incorrectCount, 
            timeoutCount, 
            timeRemaining
        );
 
        System.out.println("ゲーム終了！");
        
        return new GameResult(
            session.userName(),
            session.difficulty(),
            correctCount,
            nearCount,
            incorrectCount,
            timeoutCount,
            correctCharCount,
            wrongCharCount,
            totalQuestions,
            averageTypingSpeed,
            finalScore,
            timeRemaining,
            timeLimit,
            results
        );
    }   
}