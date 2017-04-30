package model;

import java.awt.*;
import java.io.Serializable;
import java.util.Collections;

/**
 * Created by Кумпутер on 12.03.2017.
 */
public class Node implements Serializable {

    public int n; //node number
    protected Point p;
    protected int r;
    protected Color color;
    protected boolean selected = false;
    protected Rectangle b = new Rectangle();
    protected long timeSelected;
    protected int weight;

    /**
     * Construct a new node.
     */
    public Node(int n, Point p, int r, Color color) {
        this.n = n;
        this.p = p;
        this.r = r;
        this.color = color;
        setBoundary(b);
    }

    /**
     * Collected all the selected nodes in list.
     */
    public static void getSelected(java.util.List<Node> list, java.util.List<Node> selected) {
        selected.clear();
        for (Node n : list) {
            if (n.isSelected()) {
                selected.add(n);
            }
        }
        Collections.sort(selected, (o1, o2) -> {
            if (o1.timeSelected == o2.timeSelected) {
                return 0;
            } else {
                return o1.timeSelected > o2.timeSelected ? 1 : -1;
            }
        });
    }

    /**
     * Select no nodes.
     */
    public static void selectNone(java.util.List<Node> list) {
        for (Node n : list) {
            n.setSelected(false);
        }
    }

    /**
     * Select a single node; return true if not already selected.
     */
    public static boolean selectOne(java.util.List<Node> list, Point p) {
        for (Node n : list) {
            if (n.contains(p)) {
                if (!n.isSelected()) {
                    Node.selectNone(list);
                    n.setSelected(true);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Select each node in r.
     */
    public static void selectRect(java.util.List<Node> list, Rectangle r) {
        for (Node n : list) {
            n.setSelected(r.contains(n.p));
        }
    }

    /**
     * Toggle selected state of each node containing p.
     */
    public static void selectToggle(java.util.List<Node> list, Point p) {
        for (Node n : list) {
            if (n.contains(p)) {
                n.setSelected(!n.isSelected());
            }
        }
    }

    /**
     * Update each node's position by d (delta).
     */
    public static void updatePosition(java.util.List<Node> list, Point d) {
        for (Node n : list) {
            if (n.isSelected()) {
                n.p.x += d.x;
                n.p.y += d.y;
                n.setBoundary(n.b);
            }
        }
    }

    /**
     * Update each node's radius r.
     */
    public static void updateRadius(java.util.List<Node> list, int r) {
        for (Node n : list) {
            if (n.isSelected()) {
                n.r = r;
                n.setBoundary(n.b);
            }
        }
    }

    /**
     * Update each node's color.
     */
    public static void updateColor(java.util.List<Node> list, Color color) {
        for (Node n : list) {
            if (n.isSelected()) {
                n.color = color;
            }
        }
    }

    public int getN() {
        return n;
    }

    /**
     * Calculate this node's rectangular boundary.
     */
    private void setBoundary(Rectangle b) {
        b.setBounds(p.x - r, p.y - r, 2 * r, 2 * r);
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Draw this node.
     */
    public void draw(Graphics g) {
        g.setColor(this.color);
        Color curColor = g.getColor();
        g.fillOval(b.x, b.y, b.width, b.height);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.ROMAN_BASELINE, 25));
        g.drawString("" + n, b.x + r - 5, b.y + r - 5);
        g.drawLine(b.x, b.y + b.height / 2, b.x + b.width, b.y + b.height / 2);
        g.drawString("" + weight, b.x + r - 5, b.y + r + 25);
        g.setColor(curColor);
        if (selected) {
            g.setColor(Color.darkGray);
            g.drawRect(b.x - 5, b.y - 5, b.width + 10, b.height + 10);
        }
    }

    /**
     * Return this node's location.
     */
    public Point getLocation() {
        return p;
    }

    /**
     * Return true if this node contains p.
     */
    public boolean contains(Point p) {
        return b.contains(p);
    }

    /**
     * Return true if this node is selected.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Mark this node as selected.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        this.timeSelected = System.currentTimeMillis();
    }

    public long getTimeSelected() {
        return timeSelected;
    }
}
