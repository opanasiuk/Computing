package main;

import view.GraphPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Кумпутер on 12.03.2017.
 */
public class SystemSoftware {
    public static void main(String[] args) throws Exception {
        EventQueue.invokeLater(() -> {
            JFrame f = new JFrame("GraphPanel");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            GraphPanel gp = new GraphPanel();
            f.setJMenuBar(gp.menu);
            f.add(gp.control, BorderLayout.NORTH);
            f.add(new JScrollPane(gp), BorderLayout.CENTER);
            f.getRootPane().setDefaultButton(gp.control.btnNewNode);
            f.pack();
            f.setLocationByPlatform(true);
            f.setVisible(true);
        });
    }
}
