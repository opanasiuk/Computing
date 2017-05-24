package utils;

import model.Connection;
import model.Edge;
import model.Node;
import model.Processor;
import view.GraphPanel;
import view.SystemTopologyPanel;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Кумпутер on 05.03.2017.
 */
public class Utils {

    public static int[][] matrix;
    public static int[] color;
    private static boolean cyclic;

    public static int[][] getMatrix(List<Node> nodes, List<Edge> edges) {
        int[][] matrix = new int[nodes.size()][nodes.size()];
        for (Edge edge : edges) {
            int n1Number = nodes.indexOf(edge.getN1());
            int n2Number = nodes.indexOf(edge.getN2());
            if (n1Number >= 0 && n2Number >= 0) {
                matrix[n1Number][n2Number] = 1;
                if (edge instanceof Connection) {
                    matrix[n2Number][n1Number] = 1;
                }
            }
        }
        return matrix;
    }

    public static boolean hasFreeNodes(SystemTopologyPanel panel) {
        List<Processor> visited = new LinkedList<>();
        dfs((Processor) panel.nodes.get(0), visited);
        return panel.nodes.size() != visited.size();
    }

    private static void dfs(Processor proc, List<Processor> visited) {
        visited.add(proc);
        for (Processor processor : proc.child) {
            if (!visited.contains(processor)) {
                dfs(processor, visited);
            }
        }
    }

    public static boolean isCyclic(int[][] matrix) {
        Utils.matrix = matrix;
        Utils.color = new int[matrix.length];
        Utils.cyclic = false;

        for (int i = 0; i < matrix.length; i++) {
            dfs(i);
        }
        return cyclic;
    }

    private static void dfs(int n) {
        if (color[n] == 2) {
            return;
        }
        if (cyclic) {
            return;
        }
        if (color[n] == 1) {
            cyclic = true;
            return;
        }
        color[n] = 1;
        for (int i = 0; i < matrix[n].length; i++) {
            if (matrix[n][i] == 1) {
                dfs(i);
                if (cyclic) {
                    return;
                }
            }
        }
        color[n] = 2;
    }

    public static boolean hasDontConnected(GraphPanel panel) {
        Utils.matrix = getMatrix(panel.nodes, panel.edges);
        Utils.color = new int[matrix.length];
        Utils.cyclic = false;

        for (int i = 0; i < matrix.length; i++) {
            dfs(i);
        }
        for (int i = 0; i < color.length; i++) {

        }
        return cyclic;
    }

    public static boolean isCyclic(GraphPanel panel) {
        return isCyclic(getMatrix(panel.nodes, panel.edges));
    }

    public static int getFreeNumber(List<Node> nodes) {
        int t = 0;
        for (int i = 0; i < nodes.size() + 1; i++) {
            boolean isFree = true;
            for (Node node : nodes) {
                if (node.n == i) {
                    isFree = false;
                    break;
                }
            }
            if (isFree) {
                t = i;
                break;
            }
        }
        return t;
    }
}