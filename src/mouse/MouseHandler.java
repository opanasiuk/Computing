package mouse;

import model.Node;
import view.GraphPanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseHandler extends MouseAdapter {

    private GraphPanel graphPanel;

    public MouseHandler(GraphPanel graphPanel) {
        super();
        this.graphPanel = graphPanel;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        graphPanel.selecting = false;
        graphPanel.mouseRect.setBounds(0, 0, 0, 0);
        if (e.isPopupTrigger()) {
            showPopup(e);
        }
        e.getComponent().repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        graphPanel.mousePt = e.getPoint();
        if (e.isShiftDown()) {
            Node.selectToggle(graphPanel.nodes, graphPanel.mousePt);
        } else if (e.isPopupTrigger()) {
            Node.selectOne(graphPanel.nodes, graphPanel.mousePt);
            showPopup(e);
        } else if (Node.selectOne(graphPanel.nodes, graphPanel.mousePt)) {
            graphPanel.selecting = false;
        } else {
            Node.selectNone(graphPanel.nodes);
            graphPanel.selecting = true;
        }
        e.getComponent().repaint();
    }

    private void showPopup(MouseEvent e) {
        graphPanel.control.popup.show(e.getComponent(), e.getX(), e.getY());
    }
}
