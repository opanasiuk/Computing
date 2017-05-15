package model;

import view.GraphPanel;

import java.awt.*;

/**
 * Created by Кумпутер on 14.05.2017.
 */
public class QueueTextArea {

    private String text = "";
    private GraphPanel panel;

    public QueueTextArea(GraphPanel panel) {
        this.panel = panel;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void draw(Graphics g) {
        Dimension d = panel.getSize();
        int x = 10;
        int y = d.height - 40;
        int height = 30;
        int width = d.width - 20;
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.ROMAN_BASELINE, 14));
        g.drawRect(x, y, width, height);
        g.drawString("Queue:", x + 5, y + 10);
        g.drawString(text, x + 5, y + 23);
    }
}
