package utils;

import model.Edge;
import model.Node;
import model.Processor;
import view.GraphPanel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Кумпутер on 14.05.2017.
 */
public class Path {
    private List<Node> nodes;
    private List<Edge> edges;
    private int[][] graph;

    public Path(GraphPanel panel) {
        this.nodes = panel.nodes;
        this.edges = panel.edges;
        this.graph = Utils.getMatrix(nodes, edges);
    }

    public Path(int[][] a) {
        this.graph = a;
    }

    public int[] getDistanceToNodes(CriteriaType type) {
        int[] distances = new int[graph.length];
        Arrays.fill(distances, -1);
        getFreeNodes(type).stream().forEach(index ->
                distances[index] = (type == CriteriaType.TIME_FROM_END
                        ? nodes.get(index).getWeight() : 0));
        while (isNotDone(distances)) {
            for (int i = 0; i < graph.length; i++) {
                List<Integer> ch = type == CriteriaType.TIME_FROM_END
                        ? getParents(i) : getChilds(i);
                ch.stream().filter(indexOfChild -> distances[indexOfChild] == -1)
                        .forEach(indexOfChild -> {
                            int maxWeight = getMaxWeight(indexOfChild, distances, type);
                            if (maxWeight != -1) {
                                distances[indexOfChild] = maxWeight;
                            }
                        });
            }
        }
        return distances;
    }

    private List<Pair> getQueue0(CriteriaType type) {
        int[] distances = getDistanceToNodes(type);
        List<Pair> queue = new ArrayList<>();
        for (int i = 0; i < distances.length; i++) {
            queue.add(new Pair(i, distances[i]));
        }
        Collections.sort(queue,
                (p1, p2) -> {
                    switch (type) {
                        case TIME_FROM_END:
                            return p2.distance - p1.distance;
                        case TIME_FROM_BEGIN:
                            return p1.distance - p2.distance;
                        case NUMBER_OF_NODES:
                            return p1.distance - p2.distance == 0
                                    ? (getChilds(p2.index).size()
                                    + getParents(p2.index).size())
                                    - (getChilds(p1.index).size()
                                    + getParents(p1.index).size())
                                    : p1.distance - p2.distance;
                        default:
                            return 0;
                    }
                });

        return queue;
    }

    public List<Integer> getQueue(CriteriaType type) {
        return getQueue0(type).stream().map(pair -> pair.index).collect(Collectors.toList());
    }

    public String getStringQueue(CriteriaType type) {
        StringBuilder res = new StringBuilder();
        getQueue0(type).forEach(p -> {
            if (res.length() > 0) {
                res.append(", ");
            }
            res.append(p.index).append("(").append(p.distance).append(")");
        });
        return res.toString();
    }

    private boolean isNotDone(int[] a) {
        return Arrays.stream(a).anyMatch(d -> d == -1);
    }

    private int getMaxWeight(int n, int[] weight, CriteriaType type) {
        List<Integer> nds = type == CriteriaType.NUMBER_OF_NODES
                || type == CriteriaType.TIME_FROM_BEGIN ? getParents(n) : getChilds(n);
        int max = Integer.MIN_VALUE;
        for (Integer node : nds) {
            if (weight[node] == -1) {
                return -1;
            }
            int tmax = weight[node] + (type == CriteriaType.NUMBER_OF_NODES
                    ? 1 : type == CriteriaType.TIME_FROM_END ? nodes.get(n)
                    .getWeight() : nodes.get(node).getWeight());
            if (max < tmax) {
                max = tmax;
            }
        }
        return max;
    }

    public List<Integer> getFreeNodes(CriteriaType type) {
        ArrayList<Integer> nodes = new ArrayList<>();
        for (int i = 0; i < graph.length; i++) {
            boolean isFree = true;
            for (int j = 0; j < graph[i].length; j++) {
                int con = type == CriteriaType.TIME_FROM_END ? graph[i][j] : graph[j][i];
                if (con == 1) {
                    isFree = false;
                }
            }
            if (isFree) {
                nodes.add(i);
            }
        }
        return nodes;
    }

    public List<Integer> getChilds(int n) {
        ArrayList<Integer> nodes = new ArrayList<>();
        for (int i = 0; i < graph[n].length; i++) {
            if (graph[n][i] == 1) {
                nodes.add(i);
            }
        }
        return nodes;
    }

    public List<Integer> getParents(int n) {
        ArrayList<Integer> nodes = new ArrayList<>();
        for (int i = 0; i < graph[n].length; i++) {
            if (graph[i][n] == 1) {
                nodes.add(i);
            }
        }
        return nodes;
    }

    public List<Integer> getProcessorQueue() {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < graph.length; i++) {
            map.put(i, getChilds(i).size() + getParents(i).size());
        }
        return map.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue() - e1.getValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<Processor> getMinPath(int from, int to) {
        List<Processor> visited = new LinkedList<>();
        Processor p = (Processor) nodes.get(from);
        return dfs(p, (Processor) nodes.get(to), visited);
    }

    private List<Processor> dfs(Processor proc, Processor destiny,
                                List<Processor> visited) {
        if (proc.equals(destiny)) {
            List<Processor> list = new LinkedList<>();
            list.add(proc);
            return list;
        }
        visited.add(proc);
        List<Processor> currentPath = null;
        for (Processor processor : proc.child) {
            if (!visited.contains(processor)) {
                List<Processor> path = dfs(processor, destiny, visited);
                if (currentPath == null
                        || (path != null && path.size() < currentPath.size())) {
                    currentPath = path;
                }
            }
        }
        if (currentPath == null || currentPath.size() == 0) {
            return null;
        }
        currentPath.add(proc);
        return currentPath;
    }

    private static class Pair {
        int index;
        int distance;

        public Pair(int index, int distance) {
            this.index = index;
            this.distance = distance;
        }
    }

    public enum CriteriaType {
        NUMBER_OF_NODES,
        TIME_FROM_BEGIN,
        TIME_FROM_END
    }

}
