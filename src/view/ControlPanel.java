package view;

import model.Node;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JToolBar {

    public JButton btnNewNode;
    public ColorIcon hueIcon;
    public JPopupMenu popup;

    ControlPanel(GraphPanel graphPanel) {
        Action delete = new Actions.DeleteAction("Delete", graphPanel);
        Action connect = new Actions.ConnectAction("Connect", graphPanel);
        Action color = new Actions.ColorAction("Color", graphPanel);
        Action newNode = new Actions.NewNodeAction("New", graphPanel);
        popup = new JPopupMenu();
        Action random = new Actions.RandomAction("Random", graphPanel);
        Action freeNodes = new Actions.FreeNodesAction("FreeNodes", graphPanel);
        btnNewNode = new JButton(newNode);
        JButton btnConnect = new JButton(connect);
        JButton btnDelete = new JButton(delete);
        hueIcon = new ColorIcon(Color.blue);

        btnNewNode.setIcon(new ImageIcon("resources/icons/add_icon.png"));
        btnNewNode.setText(null);
        btnNewNode.setToolTipText("Add new node");
        btnConnect.setIcon(new ImageIcon("resources/icons/connect.png"));
        btnConnect.setText(null);
        btnConnect.setToolTipText("Connect nodes");
        btnDelete.setIcon(new ImageIcon("resources/icons/delete.png"));
        btnDelete.setText(null);
        btnDelete.setToolTipText("Delete node");
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBackground(Color.lightGray);
        this.add(btnNewNode);
        this.add(btnConnect);
        this.add(btnDelete);
        if (!(graphPanel instanceof SystemTopologyPanel)) {
            this.addSeparator();
            this.add(new JButton(color));
            this.add(new JLabel(hueIcon));
            JSpinner js = new JSpinner();
            js.setModel(new SpinnerNumberModel(GraphPanel.RADIUS, 5, 100, 5));
            js.addChangeListener(e -> {
                JSpinner s = (JSpinner) e.getSource();
                graphPanel.radius = (Integer) s.getValue();
                Node.updateRadius(graphPanel.nodes, graphPanel.radius);
                graphPanel.repaint();
            });
            this.add(new JLabel("Size:"));
            this.add(js);
            this.add(new JButton(random));
        } else {
            this.add(new JButton(freeNodes));
        }
        popup.add(new JMenuItem(newNode));
        popup.add(new JMenuItem(color));
        popup.add(new JMenuItem(connect));
        popup.add(new JMenuItem(delete));
    }
}