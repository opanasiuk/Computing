package mouse;

import model.Node;
import view.GraphPanel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class MouseMotionHandler extends MouseMotionAdapter {

    private GraphPanel graphPanel;
    private Point delta = new Point();

    public MouseMotionHandler(GraphPanel graphPanel) {
        super();
        this.graphPanel = graphPanel;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (graphPanel.selecting) {
            graphPanel.mouseRect.setBounds(
                    Math.min(graphPanel.mousePt.x, e.getX()),
                    Math.min(graphPanel.mousePt.y, e.getY()),
                    Math.abs(graphPanel.mousePt.x - e.getX()),
                    Math.abs(graphPanel.mousePt.y - e.getY()));
            Node.selectRect(graphPanel.nodes, graphPanel.mouseRect);
        } else {
            delta.setLocation(
                    e.getX() - graphPanel.mousePt.x,
                    e.getY() - graphPanel.mousePt.y);
            Node.updatePosition(graphPanel.nodes, delta);
            graphPanel.mousePt = e.getPoint();
        }
        e.getComponent().repaint();
    }
}

