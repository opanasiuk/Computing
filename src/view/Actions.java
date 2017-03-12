package view;

import model.Edge;
import model.Kind;
import model.Node;
import utils.Utils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

/**
 * Created by Кумпутер on 12.03.2017.
 */
public class Actions {

    public static class ClearAction extends AbstractAction {

        private GraphPanel graphPanel;

        public ClearAction(String name, GraphPanel graphPanel) {
            super(name);
            this.graphPanel = graphPanel;
        }

        public void actionPerformed(ActionEvent e) {
            graphPanel.nodes.clear();
            graphPanel.edges.clear();
            graphPanel.repaint();
        }
    }

    public static class ColorAction extends AbstractAction {

        private GraphPanel graphPanel;

        private ControlPanel control;

        public ColorAction(String name, GraphPanel graphPanel) {
            super(name);
            this.graphPanel = graphPanel;
        }

        public void actionPerformed(ActionEvent e) {
            Color color = graphPanel.control.hueIcon.getColor();
            color = JColorChooser.showDialog(
                    graphPanel, "Виберіть колір", color);
            if (color != null) {
                Node.updateColor(graphPanel.nodes, color);
                control.hueIcon.setColor(color);
                control.repaint();
                graphPanel.repaint();
            }
        }
    }

    public static class SaveAction extends AbstractAction {

        private GraphPanel graphPanel;

        public SaveAction(String name, GraphPanel graphPanel) {
            super(name);
            this.graphPanel = graphPanel;
        }

        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Файл проекту - Graph", "dsf");
            fileChooser.setFileFilter(filter);
            int choosedOption = fileChooser.showSaveDialog(graphPanel);
            if (choosedOption == JFileChooser.APPROVE_OPTION) {
                try (ObjectOutputStream out = new ObjectOutputStream(
                        new FileOutputStream(fileChooser.getSelectedFile()))) {
                    java.util.List<Object> list = new ArrayList<>();
                    list.addAll(graphPanel.nodes);
                    list.addAll(graphPanel.edges);
                    out.writeObject(list);
                    out.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static class LoadAction extends AbstractAction {

        private GraphPanel graphPanel;

        public LoadAction(String name, GraphPanel graphPanel) {
            super(name);
            this.graphPanel = graphPanel;
        }

        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Файл проекту - Graph", "dsf");
            fileChooser.setFileFilter(filter);
            int choosedOption = fileChooser.showOpenDialog(graphPanel);
            if (choosedOption == JFileChooser.APPROVE_OPTION) {
                try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileChooser.getSelectedFile()))) {
                    java.util.List<Object> list = (java.util.List<Object>) in.readObject();
                    graphPanel.nodes.clear();
                    graphPanel.edges.clear();
                    for (Object o : list) {
                        if (o instanceof Node) {
                            graphPanel.nodes.add((Node) o);
                        } else if (o instanceof Edge) {
                            graphPanel.edges.add((Edge) o);
                        }
                    }
                    graphPanel.repaint();
                } catch (IOException | ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static class ConnectAction extends AbstractAction {

        private GraphPanel graphPanel;

        public ConnectAction(String name, GraphPanel graphPanel) {
            super(name);
            this.graphPanel = graphPanel;
        }

        public void actionPerformed(ActionEvent e) {
            List<Node> nodes = graphPanel.nodes;
            List<Node> selected = graphPanel.selected;
            List<Edge> edges = graphPanel.edges;
            Node.getSelected(nodes, selected);
            if (selected.size() > 1) {
                for (int i = 0; i < selected.size() - 1; ++i) {
                    Node n1 = selected.get(i);
                    Node n2 = selected.get(i + 1);
                    Edge edge = new Edge(n1, n2);
                    edges.add(edge);
                    if (Utils.isCyclic(Utils.getMatrix(nodes, edges))) {
                        edges.remove(edge);
                        JOptionPane.showMessageDialog(graphPanel,
                                "Graph must be acyclic");
                    }
                }
            }
            graphPanel.repaint();
        }
    }

    public static class DeleteAction extends AbstractAction {

        private GraphPanel graphPanel;

        public DeleteAction(String name, GraphPanel graphPanel) {
            super(name);
            this.graphPanel = graphPanel;
        }

        public void actionPerformed(ActionEvent e) {
            ListIterator<Node> iter = graphPanel.nodes.listIterator();
            while (iter.hasNext()) {
                Node n = iter.next();
                if (n.isSelected()) {
                    deleteEdges(n);
                    iter.remove();
                }
            }
            graphPanel.repaint();
        }

        private void deleteEdges(Node n) {
            ListIterator<Edge> iter = graphPanel.edges.listIterator();
            while (iter.hasNext()) {
                Edge e = iter.next();
                if (e.getN1() == n || e.getN2() == n) {
                    iter.remove();
                }
            }
        }
    }

    public static class NewNodeAction extends AbstractAction {

        private GraphPanel graphPanel;

        public NewNodeAction(String name, GraphPanel graphPanel) {
            super(name);
            this.graphPanel = graphPanel;
        }

        public void actionPerformed(ActionEvent e) {
            Node.selectNone(graphPanel.nodes);
            Point p = graphPanel.mousePt.getLocation();
            Color color = graphPanel.control.hueIcon.getColor();
            Node n = new Node(Utils.getFreeNumber(graphPanel.nodes), p, graphPanel.radius, color, Kind.Circular);
            n.setSelected(true);
            graphPanel.nodes.add(n);
            graphPanel.repaint();
        }
    }

    public static class RandomAction extends AbstractAction {

        private GraphPanel graphPanel;

        public RandomAction(String name, GraphPanel graphPanel) {
            super(name);
            this.graphPanel = graphPanel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Random rnd = new Random();
            for (int i = 0; i < 16; i++) {
                Point p = new Point(rnd.nextInt(GraphPanel.WIDE), rnd.nextInt(GraphPanel.HIGH));
                graphPanel.nodes.add(new Node(Utils.getFreeNumber(graphPanel.nodes),
                        p, graphPanel.radius, new Color(rnd.nextInt()), Kind.Circular));
            }
            graphPanel.repaint();
        }
    }

    public static class GetMatrixAction extends AbstractAction {

        private GraphPanel graphPanel;

        public GetMatrixAction(String name, GraphPanel graphPanel) {
            super(name);
            this.graphPanel = graphPanel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            java.util.List<Node> trailingNodes = Utils.getTrailingNodes(
                    graphPanel.nodes, graphPanel.edges);
            if (trailingNodes.size() > 0) {
                StringBuilder messageText = new StringBuilder();
                messageText.append("Graph has trailing node" + (trailingNodes.size() > 1 ? "s" : "") + ": ");
                for (Node trailingNode : trailingNodes) {
                    messageText.append(trailingNode.n).append(" ");
                }
                JOptionPane.showMessageDialog(graphPanel, messageText);
            }
        }
    }
}
