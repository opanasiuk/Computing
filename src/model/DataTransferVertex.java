package model;

import java.awt.*;

/**
 * Created by Кумпутер on 24.05.2017.
 */
public class DataTransferVertex extends Vertex {

    public int procFrom;
    public int procTo;

    public int layer;

    public DataTransferVertex(int from, int length, String text, int taskNumber,
                              int procFrom, int procTo) {
        super(from, length, text + procFrom + "->" + procTo + "(" + taskNumber + ")", taskNumber);
        this.procFrom = procFrom;
        this.procTo = procTo;
    }

    @Override
    public void draw(Graphics g, int y) {
        g.setColor(Color.CYAN);
        int x = START_POSITION + MUL_COEF * from;
        int len = MUL_COEF * length;
        g.fillRect(x, y - (layer + 1) * BLOCK_HEIGHT / 2, len, BLOCK_HEIGHT / 2);
        g.setColor(Color.BLACK);
        g.drawRect(x, y - (layer + 1) * BLOCK_HEIGHT / 2, len, BLOCK_HEIGHT / 2);
        x = x + 5;
        g.drawString(text, x, y - (layer + 1) * BLOCK_HEIGHT / 2 + BLOCK_HEIGHT / 2);
    }

    @Override
    public String toString() {
        return "DataTransferVertex{" +
                "procFrom=" + procFrom +
                ", procTo=" + procTo +
                ", layer=" + layer +
                '}';
    }
}
