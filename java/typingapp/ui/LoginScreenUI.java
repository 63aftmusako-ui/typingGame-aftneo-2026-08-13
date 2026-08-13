package typingapp.ui;

import typingapp.controller.LoginController;
import typingapp.service.LoginService;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;
import javax.swing.*;

public class LoginScreenUI {
    private JFrame frame;					// フレームを使用するので宣言
    private JTextField usernameField;		// ユーザー名を格納するフィールドの宣言
    private JPasswordField passwordField;	// パスワードを格納するフィールドの宣言
    private JButton loginButton;			// ログインボタン
    private JButton signupButton;			// 新規登録ボタン
    private JCheckBox showPasswordCheckBox;	// パスワードをデフォルトで隠して表示するので、それを解除するためのチェックボタンを追加するために宣言
 
    private JLabel messageLabel;			// メッセージを表示するためのラベル宣言
    
    private String userName;				// ユーザー名の宣言
    private Consumer<String> loginCallback;

    private LoginController loginController;
	
    public LoginScreenUI(LoginController loginController) {
        this.loginController = loginController;
        createUI();
    }
    
    public void setLoginCallback(Consumer<String> callback) {
        this.loginCallback = callback;
    }
    
    class BackgroundPanel extends JPanel {

        private static final long serialVersionUID = 1L;
        
		private Image image;

        public BackgroundPanel(String path) {
            image = new ImageIcon( getClass().getClassLoader().getResource(path)).getImage();
        }

	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);

	        int panelWidth = getWidth();
	        int panelHeight = getHeight();

//	        int imageWidth = image.getWidth(this);
//	        int imageHeight = image.getHeight(this);
//
//	        double scale = Math.max(
//	            (double)panelWidth / imageWidth,
//	            (double)panelHeight / imageHeight
//	        );
//
//	        int drawWidth = (int)(imageWidth * scale);
//	        int drawHeight = (int)(imageHeight * scale);
//
//	        int x = (panelWidth - drawWidth) / 2;
//	        int y = (panelHeight - drawHeight) / 2;
//
//	        System.out.println("StartGameUI");
//            System.out.println("panel=" + panelWidth + "x" + panelHeight + " image=" + imageWidth + "x" + imageHeight);

//	        g.drawImage( image, x, y, drawWidth, drawHeight, this);
	        g.drawImage( image, 0, 0, panelWidth, panelHeight, this);
	    }
    }
    
    //背景画像基準で部品配置
    private void updateComponentPosition(BackgroundPanel panel) {

        double baseWidth = 1920.0;
        double baseHeight = 1080.0;

        double scaleX = panel.getWidth() / baseWidth;
        double scaleY = panel.getHeight() / baseHeight;;

        // ユーザー名
        usernameField.setBounds(
                (int)(1295 * scaleX),
                (int)(270 * scaleY),
                (int)(578 * scaleX),
                (int)(80 * scaleY)
        );

        // パスワード
        passwordField.setBounds(
                (int)(1302 * scaleX),
                (int)(390 * scaleY),
                (int)(573 * scaleX),
                (int)(80 * scaleY)
        );

        // チェックボックス
        showPasswordCheckBox.setBounds(
                (int)(1490 * scaleX),
                (int)(562 * scaleY),
                (int)(70 * scaleX),
                (int)(90 * scaleY)
        );

        // ログインボタン
        loginButton.setBounds(
                (int)(1300 * scaleX),
                (int)(722 * scaleY),
                (int)(470 * scaleX),
                (int)(145 * scaleY)
        );
        
        // 新規登録ボタン
        signupButton.setBounds(
                (int)(1300 * scaleX),
                (int)(880 * scaleY),
                (int)(470 * scaleX),
                (int)(145 * scaleY)
        );
    }
    
    private void createUI() {
        frame = new JFrame("ログイン画面");
        frame.setSize(640, 360);
        frame.setLocationRelativeTo(null);  // ★画面中央に表示
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // フォント設定
        Font labelFont = new Font("SansSerif", Font.BOLD, 16);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 18);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 16);

        // メインパネル
        BackgroundPanel panel = new BackgroundPanel("images/アフターネオ武蔵小杉寿司タイピングログイン画面.png");

        panel.setLayout(null);

//        GridBagConstraints gbc = new GridBagConstraints();

//        gbc.insets = new Insets(12, 10, 0, 10);
//        gbc.fill = GridBagConstraints.NONE;
//        gbc.anchor = GridBagConstraints.EAST;
//        gbc.weightx = 1.0;

//		  ユーザー名
//        JLabel userLabel = new JLabel("ユーザー名:");
//        userLabel.setFont(labelFont);
//        panel.add(userLabel, gbc);

//      gbc.gridx = 1;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.anchor = GridBagConstraints.EAST;
        usernameField = new JTextField();
        usernameField.setFont(fieldFont);
        usernameField.setBorder(null);
        usernameField.setText("kaiseyokote");
        
        panel.add(usernameField);

        // パスワード
//		JLabel passLabel = new JLabel("パスワード:");
//		passLabel.setFont(labelFont);
//      panel.add(passLabel, gbc);

//      gbc.gridx = 1;
//      gbc.gridy = 1;
        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);
        passwordField.setBorder(null);
        passwordField.setText("musako63369");
        
        panel.add(passwordField);

        // パスワード表示チェックボックス
        char defaultEcho = passwordField.getEchoChar();
        
        showPasswordCheckBox = new JCheckBox();
        showPasswordCheckBox.setFont(fieldFont);
        showPasswordCheckBox.setOpaque(false);
        
        // 背景を黒
        showPasswordCheckBox.setBackground(Color.BLACK);
        // 文字を白
        showPasswordCheckBox.setForeground(Color.WHITE);
        usernameField.setBorder(null);
        
        showPasswordCheckBox.addActionListener(_ -> {

            if(showPasswordCheckBox.isSelected()) {

                passwordField.setEchoChar((char)0);

            } else {

                passwordField.setEchoChar(defaultEcho);
            }
        });
        
        panel.add(showPasswordCheckBox);
        
        // ボタンパネル（縦配置）
//        buttonPanel = new JPanel();
////        GridBagConstraints gbcButton = new GridBagConstraints();
////        gbc.insets = new Insets(5, 10, 5, 10);
////        gbc.fill = GridBagConstraints.NONE;
////        gbc.anchor = GridBagConstraints.EAST;
////        gbc.weightx = 1.0;
//        buttonPanel.setLayout(new GridLayout(2,1,0,20));
//        buttonPanel.setOpaque(false);

        // ログインボタン
        loginButton = new JButton();
        loginButton.setFont(buttonFont);
        loginButton.setOpaque(false);
        loginButton.setContentAreaFilled(false);

        // 枠を表示
//        loginButton.setBorder(BorderFactory.createLineBorder(Color.RED, 3));

        // 枠を透明
        loginButton.setBorderPainted(true);

        // フォーカス枠を消す
        loginButton.setFocusPainted(false);

        // 文字色
        loginButton.setForeground(null);
        panel.add(loginButton);
//        gbcButton.gridx = 0;
//        gbcButton.gridy = 0;
//        buttonPanel.add(loginButton);

//		空白ラベルを追加して間隔を空ける
//		gbcButton.gridy = 1;
//		buttonPanel.add(Box.createVerticalStrut(20), gbcButton);

        // 新規登録ボタン
        signupButton = new JButton();
        signupButton.setOpaque(false);
        signupButton.setContentAreaFilled(false);
        
        signupButton.setBorderPainted(true);
//        signupButton.setBorder(BorderFactory.createLineBorder(Color.BLUE,3));
        
        signupButton.setFocusPainted(false);

        signupButton.setForeground(null);
        panel.add(signupButton);
//        gbcButton.gridy = 2;
//        buttonPanel.add(loginButton);
//        buttonPanel.add(signUpButton);
//        gbc.gridx = 1;
//        gbc.gridy = 3;
//        gbc.weightx = 1.0;
//        gbc.anchor = GridBagConstraints.EAST;
//        panel.add(buttonPanel);

        // メッセージラベル（エラーメッセージ表示用）
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(labelFont);

        // ログインボタンのアクションリスナー(ログイン時の処理)


        loginButton.addActionListener(_ -> {

            LoginService.LoginResult result =
            		loginController.login(
                        usernameField.getText(),
                        new String(passwordField.getPassword())
                    );

            switch (result) {
                case SUCCESS -> {
                    messageLabel.setText("ログイン成功！");
                    userName = usernameField.getText();
                    frame.dispose();
                    
                    if(loginCallback != null) {
                        loginCallback.accept(userName);
                    }
                }
                case USER_NOT_FOUND ->
                    messageLabel.setText("ユーザー名が存在しません");

                case WRONG_PASSWORD ->
                	// セキュリティ配慮のためパスワードは返さない
                    messageLabel.setText("パスワードが間違っています");
                    
                case INVALID_INPUT ->
                    messageLabel.setText("入力が不正です");

                case SYSTEM_ERROR -> 
                    JOptionPane.showMessageDialog(
                            frame,
                            "システムエラーが発生しました。",
                            "エラー",
                            JOptionPane.ERROR_MESSAGE);
            }
            
        });

        // 新規登録ボタンのアクションリスナー
        signupButton.addActionListener(_ -> {
            SignUpScreenUI ui = new SignUpScreenUI(loginController);
            ui.setLoginCallback(loginCallback);

        });
        // 各パネルをフレームに追加
        // サイズ変更時に再配置

        panel.addComponentListener( new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    updateComponentPosition(panel);
                }
            }
        );
//        gbc.gridx = 1;
//        gbc.gridy = 4;
//        gbc.weightx = 1.0;
//        gbc.anchor = GridBagConstraints.EAST;
//
//        panel.add(buttonPanel, gbc);


        frame.add(messageLabel, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);

        frame.setVisible(true);
        
        updateComponentPosition(panel);
    }


    public String getUserName() {
        return userName;
    }
    
    public void showLogin() {
        frame.setVisible(true);
    }
}