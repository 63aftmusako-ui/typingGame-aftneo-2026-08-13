package typingapp.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import typingapp.model.PartialResult;
import typingapp.model.TypingWord;

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;

    private final JLabel wordLabel;
    private final JLabel readingLabel;
    private final JLabel normalizedArea1;

    private final JTextField inputField;
    private final JTextPane typingLabel;

    private final JLabel quizRewardLabel;
    private final JLabel questionTimerLabel;
    private final JLabel gameTimerLabel;

    private final JLabel resultLabel;

    private final SushiPanel sushiPanel;
    private final JLabel scoreLabel;
    private final JLabel tipsLabel;
    
    private final String difficulty;
    
    private final String[] tips = {
    	    "<html>TIPS : ローマ字は複数候補がある場合があります<br>"
    	    + "TIPS : 「し」は shi / si の両方が使えます</html>",

    	    "<html>TIPS : 基本的に半角英数字入力(A)モードにしてください<br>"
    	    + "TIPS : 「・」はひらがな入力モードにする必要があります</html>"
    	};

    	private int tipIndex = 0;
    	private Timer tipTimer;
    	
    	private final TypingDisplayRenderer renderer;
    	       
    	public GamePanel(int totalQuestions, String difficulty) {
    		System.out.println("GamePanel created");
    	    this.difficulty = difficulty;
    	    
            setOpaque(false);
    	    
            setLayout(null);

    	    wordLabel = new JLabel("", SwingConstants.CENTER);

    	    readingLabel = new JLabel("", SwingConstants.CENTER);

    	    normalizedArea1 = new JLabel("", SwingConstants.CENTER);

    	    inputField = new JTextField();

    	    typingLabel = new JTextPane();
    	    typingLabel.setEditable(false);
    	    typingLabel.setFocusable(false);
    	    
    	    quizRewardLabel = new JLabel("残り問題数 : " + totalQuestions);

    	    questionTimerLabel =
    	            new JLabel("問題残り時間 : 0秒");

    	    gameTimerLabel =
    	            new JLabel("ゲーム残り時間 : 0秒");

    	    resultLabel = new JLabel("");

    	    sushiPanel = new SushiPanel(difficulty);

    	    scoreLabel = new JLabel("現在の点数 : 0");

    	    tipsLabel = new JLabel("", SwingConstants.LEFT);

    	    renderer = new TypingDisplayRenderer();
    	    
    	    initializeBounds();
    	    addComponents();
    	    
    	    // --------------------------
    	    // Tips初期表示
    	    // --------------------------
    	    tipsLabel.setText(tips[0]);

    	    tipTimer = new Timer(3000, _ -> {
    	        tipIndex = (tipIndex + 1) % tips.length;
    	        tipsLabel.setText(tips[tipIndex]);
    	    });

    	    tipTimer.start();
    	}
    	
    	private void initializeBounds() {

    	    gameTimerLabel.setBounds(15,75,220,35);

    	    readingLabel.setBounds(220,55,360,35);
    	    wordLabel.setBounds(220,90,360,35);

    	    normalizedArea1.setBounds(220,145,360,35);

    	    typingLabel.setBounds(220,185,360,35);
    	    inputField.setBounds(220,225,360,35);
 	    
    	    questionTimerLabel.setBounds(15,325,300,35);
    	    quizRewardLabel.setBounds(15,350,180,35);
    	    resultLabel.setBounds(15,380,180,35);

    	    tipsLabel.setBounds(220,365,360,35);

    	    Font sideFont = new Font("MS Gothic", Font.BOLD, 14);

    	    readingLabel.setFont(new Font("MS Gothic", Font.PLAIN, 16));

    	    wordLabel.setFont(new Font("MS Gothic", Font.BOLD, 24));

    	    normalizedArea1.setFont(new Font("MS Gothic", Font.BOLD, 16));

    	    typingLabel.setFont(new Font("Consolas", Font.BOLD, 20));

    	    inputField.setFont(new Font("Consolas", Font.BOLD, 18));

    	    quizRewardLabel.setFont(sideFont);
    	    gameTimerLabel.setFont(sideFont);
    	    questionTimerLabel.setFont(sideFont);
    	    scoreLabel.setFont(sideFont);

    	    resultLabel.setFont(new Font("MS Gothic", Font.BOLD, 14));

    	    tipsLabel.setFont(new Font("MS Gothic", Font.PLAIN, 12));

    	    typingLabel.setBackground(new Color(235, 235, 235));

    	    typingLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    	}
    
    private void addComponents() {

        add(wordLabel);
        add(readingLabel);
        add(normalizedArea1);

        add(typingLabel);
        add(inputField);
        
        add(quizRewardLabel);
        add(questionTimerLabel);
        add(gameTimerLabel);

        add(resultLabel);

        add(sushiPanel);
        
        add(scoreLabel);
        add(tipsLabel);
    }

    @Override
    public void doLayout() {

        super.doLayout();

        sushiPanel.setBounds(
            0,
            00,
            getWidth(),
            getHeight()
        );
    }
    
    public JTextField getInputField() { return inputField; }

    public String getInputText() { return inputField.getText().trim(); }

    public void setInputText(String text) { inputField.setText(text); }
    
    public void clearInput() {
    	
    	typingLabel.setText("");
        inputField.setText("");
    }
    
    private void setWordText(String text) {

        int size = 20;

        if (text.length() > 15) {
            size = 15;
        }

        if (text.length() > 25) {
            size = 10;
        }

        if (text.length() > 35) {
            size = 5;
        }

        wordLabel.setFont(new Font("Meiryo", Font.BOLD, size));
        wordLabel.setText(text);
    }

    public void showWord(TypingWord word) {
    	
    	setWordText(word.word());
        readingLabel.setText(word.hiragana());
    }

    public void showRomaji(String romaji) {
        normalizedArea1.setText("ローマ字 : " + romaji);
    }

    public void updateRemainingQuestions(int remain) {
        quizRewardLabel.setText("残り問題数 : " + remain);
    }

    public void updateQuestionTime(int remain) {
        questionTimerLabel.setText("問題残り時間 : " + remain + "秒");
    }

    public void updateGameTime(int remain) {
        gameTimerLabel.setText("ゲーム残り時間 : " + remain + "秒");
    }

    public void showCorrect(String text) {
        resultLabel.setText("〇 正解 : " + text);
    }

    public void showNear(String text) {
        resultLabel.setText("△ 惜しい : " + text);
    }

    public void showMiss(String text) {
        resultLabel.setText("✕ 不正解 : " + text);
    }

    public void showTimeout(String text) {
        resultLabel.setText("⌛ 時間切れ : " + text);
    }
    
    public void updateTypingDisplay(PartialResult result) {
        renderer.render(typingLabel, result);
    }
    
    public SushiPanel getSushiPanel() {
        return sushiPanel;
    }
    
    public JTextPane getTypingLabel() {
        return typingLabel;
    }
    
    public void changeSushi(){
        sushiPanel.changeSushi(difficulty);
    }
    
}