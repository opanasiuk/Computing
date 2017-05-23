package view;

import model.Vertex;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Кумпутер on 21.05.2017.
 */
public class GanttDiagram extends JComponent {

    private Map<Integer, java.util.List<Vertex>> result;

    private java.util.List<Vertex> vertexList;

    private static final int TOP_PAD = 30;

    private int startY;

    public GanttDiagram(Map<Integer, List<Vertex>> result) {
        this.result = result;
        this.startY = TOP_PAD + (2 * result.size() - 1)* Vertex.BLOCK_HEIGHT;
    }

    @Override
    protected void paintComponent(Graphics g) {
        drawLegend(g);
        for (Map.Entry<Integer, List<Vertex>> entry : result.entrySet()) {
            int i = entry.getKey();
            for (Vertex vertex : entry.getValue()) {
                vertex.draw(g, startY - i * 2 * Vertex.BLOCK_HEIGHT);
            }
        }
    }

    private void drawLegend(Graphics g) {
        g.setColor(Color.BLACK);
        int i = 0;
        for (Integer procNum : result.keySet()) {
            int y = startY + 10 - Vertex.BLOCK_HEIGHT * 2 * i;
            g.drawLine(Vertex.START_POSITION, y, Vertex.START_POSITION - 4, y);
            g.drawString(procNum + "", Vertex.START_POSITION - 10, y);
            i++;
        }
        int tx = Vertex.START_POSITION + Vertex.MUL_COEF;
        i = 1;
        while(tx <= 400) {
            g.drawLine(tx, startY + Vertex.BLOCK_HEIGHT, tx, startY + Vertex.BLOCK_HEIGHT + 4);
            g.drawString("" + i, tx, startY + Vertex.BLOCK_HEIGHT + 15);
            tx += Vertex.MUL_COEF;
            i++;
        }
        g.drawLine(Vertex.START_POSITION, startY, Vertex.START_POSITION, TOP_PAD);
        g.drawLine(Vertex.START_POSITION, startY + Vertex.BLOCK_HEIGHT,
                400, startY + Vertex.BLOCK_HEIGHT);
    }
}
