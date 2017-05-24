package utils;

import model.*;
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

    private Map<Integer, LinkedList<Vertex>> calculation;

    private Map<Integer, LinkedList<DataTransferVertex>> dataTransfer;

    public Modeling(GraphPanel panel, SystemTopologyPanel sysPanel, Path.CriteriaType type) {
        this.nodes = panel.nodes;
        this.edges = panel.edges;
        this.processors = sysPanel.nodes;
        this.connections = sysPanel.edges;
        this.type = type;
        this.pathTask = new Path(panel);
        this.pathProc = new Path(sysPanel);
        this.queueTask = this.pathTask.getQueue(type);
        calculation = new HashMap<>();
        dataTransfer = new HashMap<>();
        proceed();
    }

    public Map<Integer, LinkedList<Vertex>> getCalculationMap() {
        return calculation;
    }

    private void proceed() {
        int[] procWorkTime = new int[processors.size()];
        int[] taskEndTime = new int[nodes.size()];

        List<Integer> freeNodes = pathTask.getFreeNodes(Path.CriteriaType.TIME_FROM_BEGIN);
        List<Integer> queueProc = pathProc.getProcessorQueue();
        Iterator<Integer> iter = queueTask.iterator();
        while (iter.hasNext()) {
            int index = iter.next();
            if (freeNodes.indexOf(index) > -1 && queueProc.size() > 0) {
                int from = 0;
                int length = nodes.get(index).getWeight();
                int taskNumber = nodes.get(index).getN();
                String text = taskNumber + "";

                int proc = queueProc.remove(0);
                addVertex(proc, new CalculationVertex(from, length, text, taskNumber));
                procWorkTime[proc] += procWorkTime[proc] + length;
                taskEndTime[index] = length;
                iter.remove();
            }
        }
        while (!queueTask.isEmpty()) {
            iter = queueTask.iterator();
            while (iter.hasNext()) {
                int index = iter.next();
                int begin = getTimeOfBegin(taskEndTime, index);
                if (begin == -1) {
                    continue;
                }
                int proc = getRandomFreeProcessor(procWorkTime, begin);
                int transferTime = getTimeOfTransfer(index, proc, taskEndTime);
                begin = transferTime != -1 ? transferTime : begin;
                int length = nodes.get(index).getWeight();
                taskEndTime[index] = begin + length;
                addVertex(proc, new Vertex(begin, length, "" + index, index));
                iter.remove();
            }
        }
    }

    private void addVertex(int proc, Vertex v) {
        if (calculation.containsKey(proc)) {
            LinkedList<Vertex> vertexes = calculation.get(proc);
            vertexes.add(v);
            calculation.put(proc, vertexes);
        } else {
            LinkedList<Vertex> list = new LinkedList<>();
            list.add(v);
            calculation.put(proc, list);
        }
    }

    private void addDataTransfer(int proc, DataTransferVertex v) {
        if (dataTransfer.containsKey(proc)) {
            LinkedList<DataTransferVertex> vertexes = dataTransfer.get(proc);
            vertexes.add(v);
            dataTransfer.put(proc, vertexes);
        } else {
            LinkedList<DataTransferVertex> list = new LinkedList<>();
            list.add(v);
            dataTransfer.put(proc, list);
        }
    }

    private int getTimeOfBegin(int[] end, int n) {
        int max = -1;
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

    private List<Integer> getProcWhereParentTask(int taskN) {
        List<Integer> res = new ArrayList<>();
        for (Integer i : pathTask.getParents(taskN)) {
            for (Map.Entry<Integer, LinkedList<Vertex>> entry : calculation.entrySet()) {
                for (Vertex vertex : entry.getValue()) {
                    if (vertex.taskNumber == i) {
                        res.add(entry.getKey());
                        break;
                    }
                }

            }
        }
        return res;
    }

    private int getParentTaskNum(int taskN, int procN) {
        List<Integer> parents = pathTask.getParents(taskN);
        for (Vertex vertex : calculation.get(procN)) {
            int n = vertex.taskNumber;
            if (parents.contains(n)) {
                return vertex.taskNumber;
            }
        }
        return 0;
    }

    private int getRandomFreeProcessor(int[] arr, int time) {
        List<Integer> freeProc = new ArrayList<>();
        Random r = new Random();
        if (time == 0) {
            return r.nextInt(arr.length);
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] <= time) {
                freeProc.add(i);
            }
        }
        if (freeProc.isEmpty()) {
            return r.nextInt(arr.length);
        } else {
            return freeProc.get(r.nextInt(freeProc.size()));
        }
    }

    private Edge findEdge(Node n1, Node n2) {
        for (Edge edge : edges) {
            if ((edge.getN1().equals(n1) && edge.getN2().equals(n2))
                    || (edge.getN2().equals(n1) && edge.getN1().equals(n2))) {
                return edge;
            }
        }
        return null;
    }

    private int getTimeOfTransfer(int taskN, int procTo, int[] dist) {
        int time = -1;
        for (Integer procN : getProcWhereParentTask(taskN)) {
            if (procN != procTo) {
                int parentTaskN = getParentTaskNum(taskN, procN);
                List<Processor> l = pathProc.getMinPath(procN, procTo);
                int t = 0;
                for (int i = l.size() - 1; i > 0; i--) {
                    Node nodeFrom = nodes.get(parentTaskN);
                    Node nodeTo = nodes.get(taskN);
                    int prFrom = l.get(i).getN();
                    int prTo = l.get(i - 1).getN();
                    int length = findEdge(nodeFrom, nodeTo).getWeight();
                    int from = dist[parentTaskN] + t;
                    int timeTo = from + length;
                    from = getTimeBeginingOfSending(prFrom, from, timeTo, procTo);
                    t += length;
                    DataTransferVertex vert =
                            new DataTransferVertex(from, length, "(" + parentTaskN + ")", taskN, prFrom, prTo);
                    vert.layer = getNumberOfSending(prFrom, vert);
                    addDataTransfer(prFrom, vert);
                    if (time < from + length) {
                        time = from + length;
                    }
                }

            }
        }
        return time;
    }

    private int getNumberOfSending(int procN, DataTransferVertex vert) {
        if (!dataTransfer.containsKey(procN)) {
            return 0;
        }
        int vertFrom = vert.from;
        int vertTo = vert.length + vertFrom;
        int res = 0;
        for (DataTransferVertex tmpVert : dataTransfer.get(procN)) {
            int tmpVertFrom = tmpVert.from;
            int tmpVertTo = tmpVert.length + tmpVertFrom;
            if ((vertFrom >= tmpVertFrom && vertFrom <= tmpVertTo)
                    || (vertTo >= tmpVertFrom && vertTo <= tmpVertFrom)) {
                res++;
            }
        }
        return res;
    }

    private int getTimeBeginingOfSending(int procN, int vertFrom, int vertTo, int procTo) {
        if (!dataTransfer.containsKey(procN)) {
            return vertFrom;
        }
        int res = 0;
        for (DataTransferVertex tmpVert : dataTransfer.get(procN)) {
            int tmpVertFrom = tmpVert.from;
            int tmpVertTo = tmpVert.length + tmpVertFrom;
            if ((vertFrom >= tmpVertFrom && vertFrom <= tmpVertTo)
                    || (vertTo >= tmpVertFrom && vertTo <= tmpVertFrom)) {
                if (procTo == tmpVert.procTo) {
                    return tmpVertTo;
                }
            }
        }
        return vertFrom;
    }

    public Map<Integer, LinkedList<DataTransferVertex>> getDataTransfer() {
        return dataTransfer;
    }
}
