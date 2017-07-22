package view;

import main.SystemSoftware;
import model.DataTransferVertex;
import model.Node;
import model.Vertex;
import utils.Modeling;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Кумпутер on 21.05.2017.
 */
public class GanttDiagram extends JComponent {

    private Map<Integer, LinkedList<Vertex>> result;

    private Map<Integer, LinkedList<DataTransferVertex>> dataTransfer;

    private static final int TOP_PAD = 50;

    private int startY;

    public GanttDiagram(Modeling m) {
        this.result = m.getCalculationMap();
        this.dataTransfer = m.getDataTransfer();
        this.startY = TOP_PAD + (3 * SystemSoftware.stp.nodes.size() - 1)* Vertex.BLOCK_HEIGHT;
    }

    @Override
    protected void paintComponent(Graphics g) {
        drawLegend(g);
        for (Map.Entry<Integer, LinkedList<Vertex>> entry : result.entrySet()) {
            int i = entry.getKey();
            for (Vertex vertex : entry.getValue()) {
                vertex.draw(g, startY - i * 3 * Vertex.BLOCK_HEIGHT);
            }
        }

        for (Map.Entry<Integer, LinkedList<DataTransferVertex>> entry : dataTransfer.entrySet()) {
            int i = entry.getKey();
            for (DataTransferVertex vertex : entry.getValue()) {
                vertex.draw(g, startY - i * 3 * Vertex.BLOCK_HEIGHT);
            }
        }
    }

    private void drawLegend(Graphics g) {
        g.setColor(Color.BLACK);
        int i = 0;
        for (Node node : SystemSoftware.stp.nodes) {
            int y = startY + 10 - Vertex.BLOCK_HEIGHT * 3 * i;
            g.drawLine(Vertex.START_POSITION, y, Vertex.START_POSITION - 4, y);
            g.drawString(node.getN() + "", Vertex.START_POSITION - 10, y);
            i++;
        }
        int tx = Vertex.START_POSITION + Vertex.MUL_COEF;
        i = 1;
        while(tx <= 1000) {
            g.setColor(Color.GRAY);
            g.drawLine(tx, TOP_PAD, tx, startY + Vertex.BLOCK_HEIGHT);
            g.setColor(Color.BLACK);
            g.drawLine(tx, startY + Vertex.BLOCK_HEIGHT, tx, startY + Vertex.BLOCK_HEIGHT + 4);
            g.drawString("" + i, tx, startY + Vertex.BLOCK_HEIGHT + 15);
            tx += Vertex.MUL_COEF;
            i++;
        }
        g.drawLine(Vertex.START_POSITION, startY, Vertex.START_POSITION, TOP_PAD);
        g.drawLine(Vertex.START_POSITION, startY + Vertex.BLOCK_HEIGHT,
                1000, startY + Vertex.BLOCK_HEIGHT);
    }
}
