package model;

import java.awt.*;
import java.io.Serializable;

import static view.GraphPanel.RADIUS;

/**
 * Created by Кумпутер on 12.03.2017.
 */
public class Edge implements Serializable {

    private Node n1;
    private Node n2;

    public Edge(Node n1, Node n2) {
        this.n1 = n1;
        this.n2 = n2;
    }

    public Node getN1() {
        return n1;
    }

    public Node getN2() {
        return n2;
    }

    public void draw(Graphics g) {
        Point p1 = n1.getLocation();
        Point p2 = n2.getLocation();
        g.setColor(Color.darkGray);
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
        double alpha = Math.atan((double) (p2.y - p1.y) / (double) (p2.x - p1.x));
        int k = p2.x < p1.x ? 1 : -1;
        int xn = p2.x + (int) (k * RADIUS * Math.cos(alpha));
        int yn = p2.y + (int) (k * RADIUS * Math.sin(alpha));
        int r = 10;
        double angle = Math.PI / 6;
        int x1 = xn + (int) (k * r * Math.cos(alpha + angle));
        int y1 = yn + (int) (k * r * Math.sin(alpha + angle));
        int x2 = xn + (int) (k * r * Math.cos(alpha - angle));
        int y2 = yn + (int) (k * r * Math.sin(alpha - angle));
        g.drawLine(xn, yn, x1, y1);
        g.drawLine(xn, yn, x2, y2);
    }
}
