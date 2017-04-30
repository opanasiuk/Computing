package model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Кумпутер on 14.03.2017.
 */
public class Processor extends Node {

    /**
     * Construct a new node.
     *
     * @param n
     * @param p
     * @param r
     * @param color
     */
    public Processor(int n, Point p, int r, Color color) {
        super(n, p, r, color);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(this.color);
        try {
            g.drawImage(ImageIO.read(new File("resources/icons/proc_img.png")), b.x, b.y, b.width, b.height, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // g.fillRect(b.x, b.y, b.width, b.height);
        Color curColor = g.getColor();
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.ROMAN_BASELINE, 25));
        g.drawString("" + n, b.x + r - 5, b.y + r - 5);
        g.setColor(curColor);
        if (selected) {
            g.setColor(Color.darkGray);
            g.drawRect(b.x - 5, b.y - 5, b.width + 10, b.height + 10);
        }
    }
}
