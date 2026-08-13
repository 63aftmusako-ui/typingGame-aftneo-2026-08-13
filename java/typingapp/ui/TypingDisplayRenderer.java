package typingapp.ui;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import typingapp.model.PartialResult;

public class TypingDisplayRenderer {

    private final AttributeSet green;
    private final AttributeSet red;
    private final AttributeSet gray;
    
    public TypingDisplayRenderer() {

        SimpleAttributeSet g = new SimpleAttributeSet();
        StyleConstants.setForeground(g, Color.GREEN);
        green = g;

        SimpleAttributeSet r = new SimpleAttributeSet();
        StyleConstants.setForeground(r, Color.RED);
        red = r;

        SimpleAttributeSet gr = new SimpleAttributeSet();
        StyleConstants.setForeground(gr, Color.GRAY);
        gray = gr;
    }

    public void render(JTextPane typingLabel, PartialResult result) {

        StyledDocument doc = typingLabel.getStyledDocument();

        try {
        	// 追加
            doc.remove(0, doc.getLength());

            String display = result.displayText();
//            String display = result.correctPart() + result.remainingPart();
//
////            if (result.input().length() <= result.correctPart().length()) {
////                display += result.remainingPart();
////            }

            boolean[] wrongFlags = result.wrongFlags();

//            int remainingStart = display.length() - result.remainingPart().length();
            int remainingStart = display.length() - result.remainingPart().length();
            for (int i = 0; i < display.length(); i++) {

                AttributeSet attr;
                if (i >= remainingStart) {
                    // 未入力
                    attr = gray;
                } else if (i < wrongFlags.length && wrongFlags[i]) {
                    // 一度でもミスした位置
                    attr = red;
                } else {
                    // 正しく入力した位置
                    attr = green;
                }
                
                doc.insertString(
                        doc.getLength(),
                        String.valueOf(display.charAt(i)),
                        attr);
            }
            
//            if (!result.remainingPart().isEmpty()) {
//
//                doc.insertString(
//                    doc.getLength(),
//                    result.remainingPart(),
//                    gray
//                );
//            }

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}