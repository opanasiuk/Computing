package utils;

import model.Edge;
import model.Node;
import view.GraphPanel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Кумпутер on 14.05.2017.
 */
public class Path {
    private List<Node> nodes;
    private List<Edge> edges;
    private int [][] graph;

    public Path(GraphPanel panel) {
        this.nodes = panel.nodes;
        this.edges = panel.edges;
        this.graph = Utils.getMatrix(nodes, edges);
    }

    public Path(int [][] a) {
        this.graph = a;
    }

    public int [] getDistanceToNodes() {
        int [] distances = new int[graph.length];
        Arrays.fill(distances, -1);
        getFreeNodes().stream().forEach(index -> distances[index] = 0);
        while(isNotDone(distances)) {
            for (int i = 0; i < graph.length; i++) {
                List<Integer> ch = getChilds(i);
                ch.stream().filter(indexOfChild -> distances[indexOfChild] == -1).forEach(indexOfChild -> {
                    int maxWeight = getMaxWeightOfParent(indexOfChild, distances);
                    if (maxWeight != -1) {
                        distances[indexOfChild] = maxWeight;
                    }
                });
            }
        }
        return distances;
    }

    public String getQueueAlg16() {
        StringBuilder res = new StringBuilder();
        int [] distances = getDistanceToNodes();
        List<Pair> queue = new ArrayList<>();
        for (int i = 0; i < distances.length; i++) {
            queue.add(new Pair(i, distances[i]));
        }
        Collections.sort(queue, (p1, p2) -> p1.distance - p2.distance);
        queue.forEach(p -> {
            if (res.length() > 0) {
                res.append(", ");
            }
            res.append(p.index).append("(").append(p.distance).append(")");
        });
        return res.toString();
    }

    private boolean isNotDone(int [] a) {
        return Arrays.stream(a).anyMatch(d -> d == -1);
    }

    private int getMaxWeightOfParent(int n, int [] weight) {
        List<Integer> parents = getParents(n);
        int max = Integer.MIN_VALUE;
        for (Integer parent : parents) {
            if (weight[parent] == -1) {
                return -1;
            }
            if (max < weight[parent] + nodes.get(parent).getWeight()) {
                max = weight[parent] + nodes.get(parent).getWeight();
            }

        }
        return max;
    }

    private List<Integer> getFreeNodes() {
        ArrayList<Integer> nodes = new ArrayList<>();
        for (int i = 0; i < graph.length; i++) {
            boolean isFree = true;
            for (int j = 0; j < graph[i].length; j++) {
                if (graph[j][i] == 1) {
                    isFree = false;
                }
            }
            if (isFree) {
                nodes.add(i);
            }
        }
        return nodes;
    }

    private List<Integer> getChilds(int n) {
        ArrayList<Integer> nodes = new ArrayList<>();
        for (int i = 0; i < graph[n].length; i++) {
            if (graph[n][i] == 1) {
                nodes.add(i);
            }
        }
        return nodes;
    }

    private List<Integer> getParents(int n) {
        ArrayList<Integer> nodes = new ArrayList<>();
        for (int i = 0; i < graph[n].length; i++) {
            if (graph[i][n] == 1) {
                nodes.add(i);
            }
        }
        return nodes;
    }

    private static class Pair {
        int index;
        int distance;

        public Pair(int index, int distance) {
            this.index = index;
            this.distance = distance;
        }
    }

    enum CriteriaType {
        NUMBER_OF_NODES,
        TIME_FROM_BEGIN,
        TIME_FROM_END
    }

}
