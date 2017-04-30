package view;

import model.Connection;
import model.Edge;
import model.Node;
import model.Processor;
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
import java.util.stream.Collectors;

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
                graphPanel.control.hueIcon.setColor(color);
                graphPanel.control.repaint();
                graphPanel.repaint();
            }
        }
    }

    public static class SaveAction extends AbstractAction {

        private GraphPanel graphPanel;

        private SystemTopologyPanel systemTopologyPanel;

        public SaveAction(String name, GraphPanel graphPanel, SystemTopologyPanel systemTopologyPanel) {
            super(name);
            this.graphPanel = graphPanel;
            this.systemTopologyPanel = systemTopologyPanel;
        }

        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Project file - Graph", "dsf");
            fileChooser.setFileFilter(filter);
            int choosedOption = fileChooser.showSaveDialog(graphPanel);
            if (choosedOption == JFileChooser.APPROVE_OPTION) {
                try (ObjectOutputStream out = new ObjectOutputStream(
                        new FileOutputStream(fileChooser.getSelectedFile()))) {
                    java.util.List<Object> list = new ArrayList<>();
                    list.addAll(graphPanel.nodes);
                    list.addAll(graphPanel.edges);
                    list.addAll(systemTopologyPanel.edges);
                    list.addAll(systemTopologyPanel.nodes);
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

        private SystemTopologyPanel systemTopologyPanel;

        public LoadAction(String name, GraphPanel graphPanel, SystemTopologyPanel systemTopologyPanel) {
            super(name);
            this.graphPanel = graphPanel;
            this.systemTopologyPanel = systemTopologyPanel;
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
                        if (o instanceof Connection) {
                            systemTopologyPanel.edges.add((Connection) o);
                        } else if (o instanceof Processor) {
                            systemTopologyPanel.nodes.add((Processor) o);
                        } else if (o instanceof Node) {
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
                boolean isSystemTopologyMode = graphPanel instanceof SystemTopologyPanel;
                String message = "Input the weight of connection";
                int weight = 0;
                for (int i = 0; i < selected.size() - 1; ++i) {
                    Node n1 = selected.get(i);
                    Node n2 = selected.get(i + 1);
                    try {
                        weight = Integer.parseInt(JOptionPane.showInputDialog(graphPanel, message));
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(graphPanel, "Wrong number!");
                    }
                    Edge edge = isSystemTopologyMode
                            ? new Connection(n1, n2) : new Edge(n1, n2);
                    edge.setWeight(weight);
                    edges.add(edge);
                    if (graphPanel instanceof SystemTopologyPanel && Utils.isCyclic(Utils.getMatrix(nodes, edges))) {
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
            boolean isSystemTopologyMode = graphPanel instanceof SystemTopologyPanel;
            String message = isSystemTopologyMode ? "Input the processor productivity" : "Input the weight of node";
            int weight = 0;
            try {
                weight = Integer.parseInt(JOptionPane.showInputDialog(graphPanel, message));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(graphPanel, "Wrong number!");
            }
            Node.selectNone(graphPanel.nodes);
            Point p = graphPanel.mousePt.getLocation();
            Color color = graphPanel.control.hueIcon.getColor();
            Node n = isSystemTopologyMode
                    ? new Processor(Utils.getFreeNumber(graphPanel.nodes), p, graphPanel.radius, color)
                    : new Node(Utils.getFreeNumber(graphPanel.nodes), p, graphPanel.radius, color);
            n.setWeight(weight);
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
            JTextField minWeightTxt = new JTextField();
            JTextField maxWeightTxt = new JTextField();
            JTextField numberOfNodesTxt = new JTextField();
            JTextField coefCovallTxt = new JTextField();
            final JComponent[] inputs = new JComponent[]{
                    new JLabel("Min weight of node"),
                    minWeightTxt,
                    new JLabel("Max weight of node"),
                    maxWeightTxt,
                    new JLabel("Number of nodes"),
                    numberOfNodesTxt,
                    new JLabel("Coefficient"),
                    coefCovallTxt
            };
            int result = JOptionPane.showConfirmDialog(graphPanel, inputs, "Input ", JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                int minWeight = minWeightTxt.getText().isEmpty() ? 0 : Integer.parseInt(minWeightTxt.getText());
                int maxWeight = maxWeightTxt.getText().isEmpty() ? 0 : Integer.parseInt(maxWeightTxt.getText());
                int numOfNodes = numberOfNodesTxt.getText().isEmpty() ? 0 : Integer.parseInt(numberOfNodesTxt.getText());
                int coef = coefCovallTxt.getText().isEmpty() ? 0 : Integer.parseInt(coefCovallTxt.getText());
                Random rnd = new Random();
                for (int i = 0; i < numOfNodes; i++) {
                    Point p = new Point(rnd.nextInt(GraphPanel.WIDE), rnd.nextInt(GraphPanel.HIGH));
                    Node n = new Node(Utils.getFreeNumber(graphPanel.nodes),
                            p, graphPanel.radius, new Color(rnd.nextInt()));
                    n.setWeight(rnd.nextInt(maxWeight - minWeight + 1) + minWeight);
                    graphPanel.nodes.add(n);
                }
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
                messageText.append(trailingNodes.stream().map(n -> String.valueOf(n.getN())).collect(Collectors.joining(", ")));
                JOptionPane.showMessageDialog(graphPanel, messageText);
            }
        }
    }
}
