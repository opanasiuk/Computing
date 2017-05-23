package main;

import view.BaseMenuBar;
import view.GraphPanel;
import view.MainTabbedPane;
import view.SystemTopologyPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Created by Кумпутер on 12.03.2017.
 */
public class SystemSoftware {
    public static GraphPanel gp;
    public static SystemTopologyPanel stp;
    public static void main(String[] args) throws Exception {
        EventQueue.invokeLater(() -> {
            JFrame f = new JFrame("GraphPanel");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            stp = new SystemTopologyPanel();
            gp = new GraphPanel();
            JPanel gpPanel = new JPanel(new BorderLayout());
            gpPanel.add(gp.control, BorderLayout.NORTH);
            gpPanel.add(new JScrollPane(gp));
            JPanel stpPanel = new JPanel(new BorderLayout());
            stpPanel.add(stp.control, BorderLayout.NORTH);
            stpPanel.add(new JScrollPane(stp));
            BaseMenuBar menu = new BaseMenuBar(gp, stp);
            f.setJMenuBar(menu);
            f.add(new MainTabbedPane(
                    Arrays.asList(gpPanel, stpPanel),
                    Arrays.asList("Graph", "System topology")));
            f.getRootPane().setDefaultButton(gp.control.btnNewNode);
            f.pack();
            f.setLocationByPlatform(true);
            f.setVisible(true);
        });
    }
}
