package utils;

import model.Edge;
import model.Node;
import model.Vertex;
import view.GraphPanel;
import view.SystemTopologyPanel;

import java.util.*;

/**
 * Created by Кумпутер on 20.05.2017.
 */
public class Modeling {
    private List<Node> nodes;
    private List<Edge> edges;

    private List<Node> processors;
    private List<Edge> connections;

    private Path.CriteriaType type;

    private List<Integer> queueTask;

    private Path pathTask;
    private Path pathProc;

    private Map<Integer, List<Vertex>> result;

    public Modeling(GraphPanel panel, SystemTopologyPanel sysPanel, Path.CriteriaType type) {
        this.nodes = panel.nodes;
        this.edges = panel.edges;
        this.processors = sysPanel.nodes;
        this.connections = sysPanel.edges;
        this.type = type;
        this.pathTask = new Path(panel);
        this.pathProc = new Path(sysPanel);
        this.queueTask = this.pathTask.getQueue(type);
        result = new HashMap<>();
        proceed();
    }

    public Map<Integer, List<Vertex>> getResult() {
        return result;
    }

    private void proceed() {
        int[] procWorkTime = new int[processors.size()];
        int[] taskEndTime = new int[nodes.size()];

        List<Integer> freeNodes = pathTask.getFreeNodes(Path.CriteriaType.TIME_FROM_BEGIN);
        List<Integer> queueProc = pathProc.getProcessorQueue();
        Iterator<Integer> iter = queueProc.iterator();
        while (iter.hasNext()) {
            int index = iter.next();
            if (freeNodes.indexOf(index) > -1 && queueProc.size() > 0) {
                int from = 0;
                int length = nodes.get(index).getWeight();
                String text = nodes.get(index).getN() + "";
                int proc = queueProc.remove(0);
                addVertex(proc, new Vertex(from, length, text));
                procWorkTime[proc] += procWorkTime[proc] + length;
                taskEndTime[index] = length;
                iter.remove();
            }
        }
        while (!queueProc.isEmpty()) {
            iter = queueProc.iterator();
            while (iter.hasNext()) {
                int index = iter.next();
                int begin = getTimeOfBegin(taskEndTime, index);
                int proc = getRandomFreeProcessor(procWorkTime, begin);
                iter.remove();
            }
        }
    }

    private void addVertex(int proc, Vertex v) {
        if (result.containsKey(proc)) {
            List<Vertex> vertexes = result.get(proc);
            vertexes.add(v);
            result.put(proc, vertexes);
        } else {
            result.put(proc, Arrays.asList(v));
        }
    }

    private int getTimeOfBegin(int[] end, int n) {
        int max = Integer.MIN_VALUE;
        for (int i : pathTask.getParents(n)) {
            if (end[i] == -1) {
                return -1;
            }
            if (max < end[i]) {
                max = end[i];
            }
        }
        return max;
    }

    private int getRandomFreeProcessor(int [] arr, int time) {
        List<Integer> freeProc = new ArrayList<>();
        Random r = new Random();
        if (time == 0) {
            return r.nextInt(arr.length);
        }
        for (int n : arr) {
            if (n <= time) {
                freeProc.add(n);
            }
        }
        if (freeProc.isEmpty()) {
            return r.nextInt(arr.length);
        } else {
            return freeProc.get(r.nextInt(freeProc.size()));
        }
    }

    private int getTimeOfTransfer() {
        return 0;
    }
}
