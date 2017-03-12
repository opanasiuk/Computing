package view;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class ColorIcon implements Icon, Serializable {

    private static final int WIDE = 20;
    private static final int HIGH = 20;
    private Color color;

    public ColorIcon(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color);
        g.fillRect(x, y, WIDE, HIGH);
    }

    public int getIconWidth() {
        return WIDE;
    }

    public int getIconHeight() {
        return HIGH;
    }
}
