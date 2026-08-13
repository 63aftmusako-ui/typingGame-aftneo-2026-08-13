package typingapp.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

import typingapp.model.GameResult;
import typingapp.model.WordResult;
import typingapp.service.ResultEvaluationService;

public class ScoreResultUI {

    public void displayScore(GameResult result,
                             Runnable retryAction) {

        JFrame frame = new JFrame("スコア結果");
        frame.setSize(640, 360);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(content);

        // ===== スコア =====

        content.add(new JLabel(
                ScoreHtmlBuilder.buildScoreTable(result)));

        // ===== 評価 =====

        ResultEvaluationService service =
                new ResultEvaluationService();

        ResultEvaluationService.EvaluationMessages messages =
                service.generate(result);

        content.add(new JLabel(
                ScoreHtmlBuilder.buildEvaluationTable(messages)));

        // ===== 単語分類 =====

        List<WordResult> correct =
                result.wordResults().stream()
                        .filter(WordResult::isCorrect)
                        .collect(Collectors.toList());

        List<WordResult> near =
                result.wordResults().stream()
                        .filter(WordResult::isNear)
                        .collect(Collectors.toList());

        List<WordResult> incorrect =
                result.wordResults().stream()
                        .filter(WordResult::isIncorrect)
                        .collect(Collectors.toList());

        List<WordResult> timeout =
                result.wordResults().stream()
                        .filter(WordResult::isTimeout)
                        .collect(Collectors.toList());

        content.add(new JLabel(
                ScoreHtmlBuilder.buildWordTable(
                        "正解した単語",
                        correct,
                        "black")));

        content.add(new JLabel(
                ScoreHtmlBuilder.buildWordTable(
                        "間違えた単語",
                        incorrect,
                        "red")));

        content.add(new JLabel(
                ScoreHtmlBuilder.buildWordTable(
                        "惜しい単語",
                        near,
                        "orange")));

        content.add(new JLabel(
                ScoreHtmlBuilder.buildWordTable(
                        "タイムアウトした単語",
                        timeout,
                        "gray")));

        // ===== ボタン =====

        JPanel buttonPanel = new JPanel();

        JButton retry = new JButton("もう一度挑戦");

        retry.addActionListener(_ -> {
            frame.dispose();
            retryAction.run();
        });

        JButton exit = new JButton("終了");

        exit.addActionListener(_ -> {

            int choice = JOptionPane.showConfirmDialog(
                    frame,
                    "本当に終了しますか？",
                    "終了確認",
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        buttonPanel.add(retry);
        buttonPanel.add(exit);

        root.add(scrollPane, BorderLayout.CENTER);
        root.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(root);
        frame.setVisible(true);
    }
}