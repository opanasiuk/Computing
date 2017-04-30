package model;

import java.awt.*;

/**
 * Created by Кумпутер on 14.03.2017.
 */
public class Connection extends Edge {

    public Connection(Node n1, Node n2) {
        super(n1, n2);
    }

    @Override
    public void draw(Graphics g) {
        Point p1 = n1.getLocation();
        Point p2 = n2.getLocation();
        g.setColor(Color.darkGray);
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
    }
}
