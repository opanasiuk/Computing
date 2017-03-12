package view;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class BaseMenuBar extends JMenuBar {

    private JMenu fileMenu, editMenu, aboutMenu;
    private JMenuItem newMenuItem, saveMenuItem, loadMenuItem, exitMenuItem;
    private GraphPanel graphPanel;

    BaseMenuBar(GraphPanel graphPanel) {
        super();
        this.graphPanel = graphPanel;
        init();
        addComponents();
    }

    private void init() {
        fileMenu = new JMenu("File");
        newMenuItem = new JMenuItem(new Actions.ClearAction("New", graphPanel));
        saveMenuItem = new JMenuItem(new Actions.SaveAction("Save", graphPanel));
        loadMenuItem = new JMenuItem(new Actions.LoadAction("Load", graphPanel));
        exitMenuItem = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        exitMenuItem.setText("Exit");
        editMenu = new JMenu("Edit");
        aboutMenu = new JMenu("About");
    }

    private void addComponents() {
        fileMenu.add(newMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        this.add(fileMenu);
        this.add(editMenu);
        this.add(aboutMenu);
    }
}
