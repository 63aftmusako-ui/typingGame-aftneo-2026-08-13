package typingapp.controller;

import typingapp.model.*;
import typingapp.service.*;
import typingapp.ui.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class GameController {
	
    private final GameService gameService;
    private final GameEngine gameEngine;
    private final RomajiVariantService romajiVariantService;
    private final ScoreService scoreService;
    
    private final GamePanel gamePanel;

    private final GameSessionManager sessionManager;

    private GameTimer gameTimer;

    private String userName;
    private String difficulty;
    private int questionCounter;

    private TypingWord currentWord;
    
    private boolean isProcessingTimeout = false;
    private boolean isGameActive = false;
    
    private static final int QUESTION_TIME_LIMIT = 5;
    private static final int GAME_TIME_LIMIT = 60;
    
    private final JFrame frame;
    
    private javax.swing.Timer sushiTimer;
    
    //追加
    private javax.swing.Timer nextQuestionTimer;
    
    private boolean waitingNextQuestion = false;
    
    private String previousInput = "";
    
    
    private void installDocumentListener() {

        gamePanel.getInputField()
                 .getDocument()
                 .addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                handleInputChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleInputChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }
    
    public GameController(JFrame frame,GameService gameService,
    		GamePanel gamePanel, ScoreService scoreService,
            int questionCounter, RomajiVariantService romajiVariantService, 
            GameSessionManager sessionManager, RomajiNormalizationService normalizationService) {
		Objects.requireNonNull(gameService, "gameService must not be null");
		Objects.requireNonNull(gamePanel, "gamePanel must not be null");
		Objects.requireNonNull(scoreService, "scoreService must not be null");
		Objects.requireNonNull(romajiVariantService, "romajiVariantService must not be null");
		Objects.requireNonNull(sessionManager, "sessionManager must not be null");
		Objects.requireNonNull(normalizationService, "normalizationService must not be null");
		
	    this.frame = frame;
		this.gameService = gameService;
		this.gamePanel = gamePanel;
		this.scoreService = scoreService;
		this.romajiVariantService = romajiVariantService;
		this.sessionManager = sessionManager;
		this.gameEngine = new GameEngine(normalizationService, romajiVariantService);
		this.questionCounter = questionCounter;
        this.isProcessingTimeout = false;
        this.isGameActive = false;
        
        installDocumentListener();
	}
    
    private void delayNextQuestion(int delayMs) {

        // 既に予約済みなら止める
        if (nextQuestionTimer != null) {
            nextQuestionTimer.stop();
        }

        nextQuestionTimer = new javax.swing.Timer(delayMs, _ -> {

            if (!isGameActive) {
                return;
            }

            if (sessionManager.isFinished()) {
                finishGame();
            } else {
                loadNextWord();
            }

        });

        nextQuestionTimer.setRepeats(false);
        nextQuestionTimer.start();
    }
    
    private void onCorrectAnswer( String input, PartialResult partialResult) {

        previousInput = "";
        
    	gamePanel.getInputField().setEditable(false);
    	
        gameTimer.stopAll();

        if (waitingNextQuestion) {
            return;
        }

        waitingNextQuestion = true;

        
        JudgeResult judgeResult = new JudgeResult( EvaluationResult.CORRECT, input);

        WordResult result = new WordResult(
                        currentWord,
                        romajiVariantService.getPreview( currentWord.getMoraJoined()),
                        input,
                        judgeResult,
                        partialResult.wrongCharCount());

        sessionManager.record(result);

        gamePanel.showCorrect(input);

        gamePanel.updateRemainingQuestions(
                sessionManager.getRemainingQuestions());

        delayNextQuestion(500);
    }
    
    private void onTypingInput(String input) {
    	
        System.out.println("onTypingInput : " + input);

        if (!isGameActive || waitingNextQuestion) {
        	return;
        }

        // 問題切替直後の空入力を無視
        if (input.isEmpty()) {
        	return;
        }

        if (input.isBlank()) {

            gamePanel.updateTypingDisplay( new PartialResult("", "", "", 0, new boolean[0]));

            return;
        }
        
        PartialResult partialResult = gameEngine.evaluatePartial(input);

        gamePanel.updateTypingDisplay(partialResult);
        
        if (gameEngine.getMatcher().isComplete()) {
            onCorrectAnswer(input, partialResult);
        }
        
//        JudgeResult judgeResult = gameEngine.evaluate( input, currentWord, false);
//
//        System.out.println("judge = " + judgeResult.result());
//        System.out.println("complete = " + judgeResult.isCorrect());
        
//        if (gameEngine.isCompleteInput(input)) {
//            onCorrectAnswer(input, partialResult);
//        }
    }
    
    private void loadNextWord() {
    	
        if (!isGameActive) {
            return;
        }
        
    	System.out.println("loadNextWord");
    	
        waitingNextQuestion = false;
    	
        previousInput = "";
        
        Optional<TypingWord> opt = gameService.nextWord();

        if (opt.isEmpty()) {
            finishGame();
            return;
        }

        currentWord = opt.get();

        sessionManager.setCurrentWord(currentWord);

        gameEngine.startWord(currentWord);
        
        gamePanel.changeSushi();
        
        gamePanel.showWord(currentWord);

        gamePanel.showRomaji(romajiVariantService.getPreview(currentWord.getMoraJoined()));

        gamePanel.clearInput();

        gamePanel.getInputField().setEditable(true);
        gamePanel.getInputField().requestFocusInWindow();
        
        gamePanel.updateRemainingQuestions(sessionManager.getRemainingQuestions());
        System.out.println("showWord: " + romajiVariantService.getPreview(currentWord.getMoraJoined()));
        gameTimer.startQuestionTimer();
    }

    private void handleInputChanged() {

        if (!isGameActive) {
            return;
        }

        String currentInput = gamePanel.getInputText();

        // バックスペース
        if (currentInput.length() < previousInput.length()) {
            previousInput = currentInput;
            onTypingInput(currentInput);
            return;
        }

        onTypingInput(currentInput);

        previousInput = currentInput;
//        // 1文字追加
//        if (currentInput.length() == previousInput.length() + 1) {
//
//            boolean accepted = onTypingInput(currentInput);
//
//            if (accepted) {
//                previousInput = currentInput;
//            } else {
//                SwingUtilities.invokeLater(() ->
//                    gamePanel.setInputText(previousInput)
//                );
//            }
            
//        	onTypingInput(currentInput);
//
//        	previousInput = currentInput;

// 削除
// 正しい入力なら保存
//            if (gameEngine.getMatcher().getStates() != null
//                    && !gameEngine.getMatcher().getStates().isEmpty()) {
//
//                previousInput = currentInput;
//
//            } else {
//
//                // 間違った文字なので削除
//                SwingUtilities.invokeLater(() -> {
//                    gamePanel.setInputText(previousInput);
//                });
//            }

            return;
        }

        // その他（貼り付け等）
    
//    private void handleInputChanged() {
//
//        if (!isGameActive) {
//            return;
//        }
//
//        onTypingInput(gamePanel.getInputText());
//    }
    
    public void startGame(String userName, String difficulty, int questionCounter) {
    	
        Objects.requireNonNull(userName, "userName must not be null");
        Objects.requireNonNull(difficulty, "difficulty must not  be null");
        
        System.out.println("\n========== ゲーム開始 ==========");
        System.out.println("ユーザー: " + userName);
        System.out.println("難易度: " + difficulty);
        System.out.println("問題数: " + questionCounter);
        
        this.userName = userName;
        this.difficulty = difficulty;
        this.questionCounter = questionCounter;
   	
        isProcessingTimeout = false;
        // ✅ 修正：ゲーム開始フラグをON
        isGameActive = true;

        sessionManager.start(new GameSession(userName, difficulty, questionCounter));
        
        gameService.loadWords(difficulty, questionCounter);
        
        gameTimer = new GameTimer(
                GAME_TIME_LIMIT,
                QUESTION_TIME_LIMIT,
                this::finishGame,
                this::onTimeout,
                gamePanel::updateGameTime,
                gamePanel::updateQuestionTime
        );

    	System.out.println( gamePanel.getInputField().getKeyListeners().length);

    	loadNextWord();

        sushiTimer = new javax.swing.Timer(50,_ -> gamePanel.getSushiPanel().move());

        sushiTimer.start();
        
        SwingUtilities.invokeLater(() -> {
            gamePanel.getInputField().requestFocusInWindow();
        });
        
        gameTimer.startGameTimer();
        
        System.out.println("remaining: " + sessionManager.getSession().getRemainingQuestions());
        System.out.println("========== ゲーム開始完了 ==========\n");
    }
    
    private void onTimeout() {

        if (!isGameActive || isProcessingTimeout) {
            return;
        }

        previousInput = "";
        
        isProcessingTimeout = true;

        try {

            String input = gamePanel.getInputText();

            int wrongCount = gameEngine.calculateWrongCharCountForIncorrect(input);

            JudgeResult result = gameEngine.evaluate(input, currentWord, true);

            sessionManager.record( new WordResult( currentWord, romajiVariantService.getPreview(
            		currentWord.getMoraJoined()), input, result, wrongCount));

            gamePanel.showTimeout( result.displayText());

            gamePanel.updateRemainingQuestions( sessionManager.getRemainingQuestions());

            delayNextQuestion(1000);

        } finally {
            isProcessingTimeout = false;
        }
    }
    
    private void finishGame() {
        // ✅ 修正：ゲーム進行中フラグを最初にOFF（これ以上のイベント処理を受け付けない）
        isGameActive = false;
        
        if (nextQuestionTimer != null && nextQuestionTimer.isRunning()) {
            nextQuestionTimer.stop();
        }
        
        gameTimer.stopAll();
        
        if (sushiTimer != null) {
            sushiTimer.stop();
        }
        
        GameResult result = gameService.finishGame(
            sessionManager.getSession(),
            gameTimer.getGameTimeLimit(),
            gameTimer.getGameTimeRemaining()
        );
        
        System.out.println("\n========== スコア計算 ==========");
        System.out.println("ゲーム結果:");
        System.out.println("  正解数: " + result.correctCount() + "問");
        System.out.println("  惜しい数: " + result.nearCount() + "問");
        System.out.println("  不正解数: " + result.incorrectCount() + "問");
        System.out.println("  タイムアウト: " + result.timeoutCount() + "問");
        System.out.println("  正しく入力できた文字数: " + result.correctCharCount());
        System.out.println("  間違えた文字数: " + result.wrongCharCount());
        System.out.println("  最終スコア: " + result.finalScore() + "点");
        System.out.println("  残り時間: " + gameTimer.getGameTimeRemaining() + "秒");
        
        try {      
            Score score = scoreService.createScore(
                userName,
                difficulty,
                result.correctCount(),
                result.nearCount(),
                result.incorrectCount(),
                result.timeoutCount(),
                result.correctCharCount(),
                result.wrongCharCount(),
                result.averageTypingSpeed(),
                gameTimer.getGameTimeRemaining(),
                GAME_TIME_LIMIT,
                LocalDateTime.now()
            );
            scoreService.save(score);
            System.out.println("スコア保存完了: " + score.finalScore() + "点");

        } catch (Exception e) {
            System.err.println("スコア保存に失敗しました: " + e.getMessage());
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
        	frame.dispose();
            
            ScoreResultUI resultUI = new ScoreResultUI();
            resultUI.displayScore(result, this::restart);
        });
    }

    private void restart() {

        if (nextQuestionTimer != null) {
            nextQuestionTimer.stop();
        }

        if (sushiTimer != null) {
            sushiTimer.stop();
        }

        sessionManager.start(
                new GameSession(
                        userName,
                        difficulty,
                        questionCounter));

        gameService.loadWords( difficulty, questionCounter);

        startGame( userName, difficulty, questionCounter);
    }
}
