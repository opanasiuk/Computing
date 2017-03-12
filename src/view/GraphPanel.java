package view;

import model.Edge;
import model.Node;
import mouse.MouseHandler;
import mouse.MouseMotionHandler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JComponent {

    public static final int WIDE = 640;
    public static final int HIGH = 480;
    public static final int RADIUS = 35;
    public ControlPanel control;// = new ControlPanel(this);
    public BaseMenuBar menu;// = new BaseMenuBar(this);
    public int radius = RADIUS;
    public List<Node> nodes = new ArrayList<>();
    public List<Node> selected = new ArrayList<>();
    public List<Edge> edges = new ArrayList<>();
    public Point mousePt = new Point(WIDE / 2, HIGH / 2);
    public Rectangle mouseRect = new Rectangle();
    public boolean selecting = false;


    public GraphPanel() {
        this.setOpaque(true);
        this.addMouseListener(new MouseHandler(this));
        this.addMouseMotionListener(new MouseMotionHandler(this));
        control = new ControlPanel(this);
        menu = new BaseMenuBar(this);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDE, HIGH);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(new Color(0x00f0f0f0));
        g.fillRect(0, 0, getWidth(), getHeight());
        for (Edge e : edges) {
            e.draw(g);
        }
        for (Node n : nodes) {
            n.draw(g);
        }
        if (selecting) {
            g.setColor(Color.darkGray);
            g.drawRect(mouseRect.x, mouseRect.y,
                    mouseRect.width, mouseRect.height);
        }
    }
}
