package view;

import model.Node;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JToolBar {

    public JButton btnNewNode;
    public ColorIcon hueIcon;
    public JPopupMenu popup;
    private Action newNode;
    private Action color;
    private Action connect;
    private Action delete;
    private Action random;
    private Action matrix;
    private JButton btnConnect;
    private JButton btnDelete;

    ControlPanel(GraphPanel graphPanel) {
        delete = new Actions.DeleteAction("Delete", graphPanel);
        connect = new Actions.ConnectAction("Connect", graphPanel);
        color = new Actions.ColorAction("Color", graphPanel);
        newNode = new Actions.NewNodeAction("New", graphPanel);
        popup = new JPopupMenu();
        random = new Actions.RandomAction("Random", graphPanel);
        matrix = new Actions.GetMatrixAction("Matrix", graphPanel);
        btnNewNode = new JButton(newNode);
        btnConnect = new JButton(connect);
        btnDelete = new JButton(delete);
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
        this.addSeparator();
        this.add(new JButton(color));
        this.add(new JLabel(hueIcon));
        this.add(new JButton(matrix));
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

        popup.add(new JMenuItem(newNode));
        popup.add(new JMenuItem(color));
        popup.add(new JMenuItem(connect));
        popup.add(new JMenuItem(delete));
    }
}