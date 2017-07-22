package view;

import main.SystemSoftware;
import model.Node;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JToolBar {

    public JButton btnNewNode;
    public ColorIcon hueIcon;
    public JPopupMenu popup;
    public JComboBox cmbQueue;
    public JComboBox cmbAlg;
    public JComboBox cmbLinks;

    ControlPanel(GraphPanel graphPanel) {
        Action delete = new Actions.DeleteAction("Delete", graphPanel);
        Action connect = new Actions.ConnectAction("Connect", graphPanel);
        Action color = new Actions.ColorAction("Color", graphPanel);
        Action newNode = new Actions.NewNodeAction("New", graphPanel);
        popup = new JPopupMenu();
        Action random = new Actions.RandomAction("Random", graphPanel);

        btnNewNode = new JButton(newNode);
        JButton btnConnect = new JButton(connect);
        JButton btnDelete = new JButton(delete);
        hueIcon = new ColorIcon(Color.blue);

        cmbQueue = new JComboBox(new String[]{"Queue alg. 3", "Queue alg. 7", "Queue alg. 16"});
        cmbAlg = new JComboBox(new String[]{"Alg. 1", "Alg. 6"});
        cmbLinks = new JComboBox(new Integer[] {1, 2, 3, 4, 5});

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
            this.add(cmbQueue);
            this.add(cmbAlg);
            this.add(cmbLinks);
            this.add(new JButton(new Actions.ModelingAction("Get Result",
                    graphPanel, SystemSoftware.stp)));
            this.add(new JButton(new Actions.StatisticsAction("Statistics",
                    graphPanel, SystemSoftware.stp)));
        } else {
            Action freeNodes = new Actions.FreeNodesAction("FreeNodes", (SystemTopologyPanel) graphPanel);
            this.add(new JButton(freeNodes));
        }
        popup.add(new JMenuItem(newNode));
        popup.add(new JMenuItem(color));
        popup.add(new JMenuItem(connect));
        popup.add(new JMenuItem(delete));
    }
}