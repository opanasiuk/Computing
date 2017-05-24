package model;

import java.awt.*;

/**
 * Created by Кумпутер on 20.05.2017.
 */
public class Vertex {
    public int from;
    public int length;

    public int taskNumber;

    public String text;

    public static final int BLOCK_HEIGHT = 20;
    public static final int MUL_COEF = 20;

    public static final int START_POSITION = 50;

    public Vertex(int from, int length, String text, int taskNumber) {
        this.from = from;
        this.length = length;
        this.text = text;
        this.taskNumber = taskNumber;
    }

    public void draw(Graphics g, int y) {
        g.setColor(Color.ORANGE);
        int x = START_POSITION + MUL_COEF * from;
        int len = MUL_COEF * length;
        g.fillRect(x, y, len, BLOCK_HEIGHT);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, len, BLOCK_HEIGHT);
        x = x + len / 2;
        g.drawString(text, x, y + BLOCK_HEIGHT / 2);
    }
}
