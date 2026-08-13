package typingapp.ui;

import java.util.List;

import typingapp.model.GameResult;
import typingapp.model.WordResult;
import typingapp.service.ResultEvaluationService;

public final class ScoreHtmlBuilder {

    private ScoreHtmlBuilder() {
    }

    public static String buildScoreTable(GameResult result) {

        return """
            <html>
            <body style='font-family:MS Gothic'>
            <h2>スコア結果</h2>

            <table border='1' cellpadding='4'>
            <tr><td>最終スコア</td><td>%d</td></tr>
            <tr><td>平均キータイプ数</td><td>%.2f</td></tr>
            <tr><td>正解問題数</td><td>%d</td></tr>
            <tr><td>惜しい問題数</td><td>%d</td></tr>
            <tr><td>不正解問題数</td><td>%d</td></tr>
            <tr><td>タイムアウト数</td><td>%d</td></tr>
            <tr><td>正しく入力できた文字数</td><td>%d</td></tr>
            <tr><td>間違えた文字数</td><td>%d</td></tr>
            <tr><td>残り時間</td><td>%d 秒</td></tr>
            </table>
            <br>
            """.formatted(
                result.finalScore(),
                result.averageTypingSpeed(),
                result.correctCount(),
                result.nearCount(),
                result.incorrectCount(),
                result.timeoutCount(),
                result.correctCharCount(),
                result.wrongCharCount(),
                result.timeRemaining()
        );
    }

    public static String buildEvaluationTable(
            ResultEvaluationService.EvaluationMessages msg) {

        return """
            <html>

            <table border='1' cellpadding='4'>
            <tr><th colspan='3'>評価</th></tr>

            <tr>
            <th>間違い判定</th>
            <th>タイムアウト判定</th>
            <th>平均キータイプ数</th>
            </tr>

            <tr>
            <td>%s</td>
            <td>%s</td>
            <td>%s</td>
            </tr>

            </table>

            <br>

            <table border='1'>
            <tr><th>総合評価</th></tr>
            <tr><td>%s</td></tr>
            </table>

            <br>
            """.formatted(
                msg.mistake(),
                msg.timeout(),
                msg.speed(),
                msg.rank()
        );
    }

    public static String buildWordTable(
            String title,
            List<WordResult> words,
            String color) {

        if (words.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("<html>");

        sb.append("<h3 style='color:")
          .append(color)
          .append("'>")
          .append(title)
          .append("</h3>");

        sb.append("<table border='1' cellpadding='4'>");

        sb.append("<tr>");
        sb.append("<th>単語</th>");
        sb.append("<th>正しいローマ字</th>");
        sb.append("<th>入力</th>");
        sb.append("</tr>");

        for (WordResult w : words) {

            sb.append("<tr>");
            sb.append("<td>").append(w.word().word()).append("</td>");
            sb.append("<td>").append(w.displayText()).append("</td>");
            sb.append("<td>").append(w.input()).append("</td>");
            sb.append("</tr>");
        }

        sb.append("</table><br>");
        sb.append("</html>");

        return sb.toString();
    }
}