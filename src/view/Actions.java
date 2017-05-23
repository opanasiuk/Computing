package view;

import model.Connection;
import model.Edge;
import model.Node;
import model.Processor;
import utils.Modeling;
import utils.Path;
import utils.Utils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

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
                        weight = isSystemTopologyMode ? 1 : Integer.parseInt(JOptionPane.showInputDialog(graphPanel, message));
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(graphPanel, "Wrong number!");
                    }
                    Edge edge = isSystemTopologyMode
                            ? new Connection(n1, n2) : new Edge(n1, n2);
                    edge.setWeight(weight);
                    edges.add(edge);
                    if (graphPanel instanceof GraphPanel && Utils.isCyclic(Utils.getMatrix(nodes, edges))) {
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
                weight = isSystemTopologyMode ? 1
                        : Integer.parseInt(JOptionPane.showInputDialog(graphPanel, message));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(graphPanel, "Wrong number!");
            }
            Node.selectNone(graphPanel.nodes);
            //Point p = graphPanel.mousePt.getLocation();
            Point p = new Point(50, 50);
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
                double coef = coefCovallTxt.getText().isEmpty() ? 0 : Double.parseDouble(coefCovallTxt.getText());
                graphPanel.nodes.clear();
                graphPanel.edges.clear();
                Random rnd = new Random();
                int cx = GraphPanel.WIDE / 2;
                int cy = GraphPanel.HIGH / 2;
                int r = 180;
                double angleDiff = 2 * Math.PI / (double) numOfNodes;
                double angle = 0.0;
                for (int i = 0; i < numOfNodes; i++) {
                    Point p = new Point((int) (cx + r * Math.sin(angle)), (int) (cy - r * Math.cos(angle)));
                    angle += angleDiff;
                    Node n = new Node(Utils.getFreeNumber(graphPanel.nodes),
                            p, graphPanel.radius, graphPanel.control.hueIcon.getColor());
                    n.setWeight(rnd.nextInt(maxWeight - minWeight + 1) + minWeight);
                    graphPanel.nodes.add(n);
                }
                int k = (int) (graphPanel.nodes.stream().map(Node::getWeight).reduce((n1, n2) -> n1 + n2).get()
                        * (1.0 / coef - 1.0));
                Queue<Integer> edgesWeight = new LinkedList<>();
                while (k > 0) {
                    int t = k > 20 ? k / 4 : k / 2;
                    int rand = t + rnd.nextInt(t + 1);
                    int weight = rand == 0 ? 1 : rand;
                    weight = weight > k ? k : weight;
                    edgesWeight.add(weight);
                    k -= weight;
                }
                int executions = 0;
                while (edgesWeight.size() > 0) {
                    if (++executions > 100000) {
                        JOptionPane.showMessageDialog(graphPanel, "Cant create graph try one more time!");
                        return;
                    }
                    Node sourceNode = graphPanel.nodes.get(rnd.nextInt(numOfNodes));
                    Node targetNode = graphPanel.nodes.get(rnd.nextInt(numOfNodes));
                    if (graphPanel.edges.stream().filter(ed -> ed.getN1().equals(sourceNode))
                            .filter(ed -> ed.getN2().equals(targetNode)).count() > 0 || sourceNode.equals(targetNode)) {
                        continue;
                    }
                    int weight = edgesWeight.poll();
                    graphPanel.edges.add(new Edge(sourceNode, targetNode, weight));
                    if (Utils.isCyclic(graphPanel)) {
                        graphPanel.edges.remove(graphPanel.edges.size() - 1);
                        edgesWeight.add(weight);
                    }
                }
            }
            graphPanel.repaint();
        }
    }



    public static class FreeNodesAction extends AbstractAction {

        private GraphPanel graphPanel;
        private SystemTopologyPanel sysPanel;

        private static final int DEFAULT_WIDTH = 600;
        private static final int DEFAULT_HEIGHT = 400;

        public FreeNodesAction(String name, GraphPanel graphPanel, SystemTopologyPanel sysPanel) {
            super(name);
            this.graphPanel = graphPanel;
            this.sysPanel = sysPanel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Frame fr = new JFrame("Modeling");
            fr.setSize(DEFAULT_WIDTH + 20, DEFAULT_HEIGHT + 20);
            JPanel gpPanel = new JPanel(new BorderLayout());
            Modeling m = new Modeling(graphPanel, sysPanel,
                    Path.CriteriaType.TIME_FROM_BEGIN);
            GanttDiagram gd = new GanttDiagram(m.getResult());
            fr.add(gpPanel.add(new JScrollPane(gd)));
            fr.setVisible(true);
        }
    }

}
