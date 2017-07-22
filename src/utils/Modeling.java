package utils;

import model.*;
import view.GraphPanel;
import view.SystemTopologyPanel;

import java.util.*;

/**
 * Created by Кумпутер on 20.05.2017.
 */
public class Modeling {

    private int linksNumber;

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

    private Map<Integer, LinkedList<Pair>> taskOnProc;

    public Modeling(GraphPanel panel, SystemTopologyPanel sysPanel,
                    Path.CriteriaType type, int linksNumber, boolean isAlg1) {
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
        taskOnProc = new HashMap<>();
        this.linksNumber = linksNumber;
        if (isAlg1) {
            proceedAlg1();
        } else {
            proceedAlg6();
        }
    }

    public Map<Integer, LinkedList<Vertex>> getCalculationMap() {
        return calculation;
    }

    private void proceedAlg1() {
        int[] procWorkTime = new int[processors.size()];
        int[] taskEndTime = new int[nodes.size()];
        int[] mock = new int[]{2, 1, 2, 1};
        List<Integer> freeNodes = pathTask.getFreeNodes(type);
        List<Integer> queueProc = pathProc.getProcessorQueue();
        Iterator<Integer> iter = queueTask.iterator();
        while (iter.hasNext()) {
            int index = iter.next();
            if (freeNodes.indexOf(index) > -1 && queueProc.size() > 0) {
                int length = nodes.get(index).getWeight();
                int taskNumber = nodes.get(index).getN();
                String text = taskNumber + "";

                int proc = getRandomFreeProcessor(procWorkTime, 0);
                queueProc.remove(0);
                int from = getTimeOfBegin(taskEndTime, index) != -1
                        ? getTimeOfBegin(taskEndTime, index) : procWorkTime[proc];
                addVertex(proc, new CalculationVertex(from, length, text, taskNumber));
                procWorkTime[proc] += procWorkTime[proc] + length;
                taskEndTime[index] = length;
                addTask(proc, taskNumber, taskEndTime[index]);
                iter.remove();
            }
        }
        int t = 0;
        while (!queueTask.isEmpty()) {
            iter = queueTask.iterator();
            while (iter.hasNext()) {
                int index = iter.next();
                int begin = getTimeOfBegin(taskEndTime, index);
                if (begin == -1) {
                    continue;
                }
                int proc = getRandomFreeProcessor(procWorkTime, begin);
                int transferTime = getTimeOfTransfer(index, proc, taskEndTime, true);
                begin = transferTime != -1
                        ? Math.max(begin, Math.max(transferTime, procWorkTime[proc]))
                        : Math.max(begin, procWorkTime[proc]);
                int length = nodes.get(index).getWeight();
                taskEndTime[index] = begin + length;
                procWorkTime[proc] += begin + length;
                addVertex(proc, new Vertex(begin, length, "" + index, index));
                addTask(proc, index, taskEndTime[index]);
                iter.remove();
            }
        }
    }

    private void proceedAlg6() {
        int[] procWorkTime = new int[processors.size()];
        int[] taskEndTime = new int[nodes.size()];
        int[] mock = new int[]{2, 1, 2, 1};
        List<Integer> freeNodes = pathTask.getFreeNodes(type);
        List<Integer> queueProc = pathProc.getProcessorQueue();
        Iterator<Integer> iter = queueTask.iterator();
        while (iter.hasNext()) {
            int index = iter.next();
            if (freeNodes.indexOf(index) > -1 && queueProc.size() > 0) {
                int length = nodes.get(index).getWeight();
                int taskNumber = nodes.get(index).getN();
                String text = taskNumber + "";

                int proc = queueProc.get(0);
                queueProc.remove(0);
                int from = getTimeOfBegin(taskEndTime, index) != -1
                        ? getTimeOfBegin(taskEndTime, index) : procWorkTime[proc];
                addVertex(proc, new CalculationVertex(from, length, text, taskNumber));
                procWorkTime[proc] += procWorkTime[proc] + length;
                taskEndTime[index] = length;
                addTask(proc, taskNumber, taskEndTime[index]);
                iter.remove();
            }
        }
        int t = 0;
        while (!queueTask.isEmpty()) {
            iter = queueTask.iterator();
            while (iter.hasNext()) {
                int taskN = iter.next();
                int begin = getTimeOfBegin(taskEndTime, taskN);
                if (begin == -1) {
                    continue;
                }
                Pair p = findNextBestProc(taskN, taskEndTime, begin, procWorkTime);
                int proc = p.procN;
                int transferTime = p.taskN;
                begin = transferTime != -1
                        ? Math.max(begin, Math.max(transferTime, procWorkTime[proc]))
                        : Math.max(begin, procWorkTime[proc]);
                int length = nodes.get(taskN).getWeight();
                taskEndTime[taskN] = begin + length;
                procWorkTime[proc] = begin + length;
                addVertex(proc, new Vertex(begin, length, "" + taskN, taskN));
                addTask(proc, taskN, taskEndTime[taskN]);
                iter.remove();
            }
        }
    }

    private Pair findNextBestProc(int taskN, int [] taskEndTime, int begin, int[] procWorkTime) {
        int proc = -1;
        int time = Integer.MAX_VALUE;
        for (int i = 0; i < processors.size(); i++) {
            int t = getTimeOfTransfer(taskN, i, taskEndTime, false);
            t = t == -1 ? Math.max(begin, procWorkTime[i]) : t + Math.max(begin, procWorkTime[i]);
            if (t != -1) {
                if (t <= time) {
                    time = t;
                    proc = i;
                }
            } else {
                time = 0;
                proc = i;
            }
        }
        time = getTimeOfTransfer(taskN, proc, taskEndTime, true);
        return new Pair(proc, time);
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

    private void addTask(int proc, int task, int time) {
        if (taskOnProc.containsKey(proc)) {
            LinkedList<Pair> tasks = taskOnProc.get(proc);
            tasks.add(new Pair(task, time));
            taskOnProc.put(proc, tasks);
        } else {
            LinkedList<Pair> list = new LinkedList<>();
            list.add(new Pair(task, time));
            taskOnProc.put(proc, list);
        }
    }

    private int getTimeOfBegin(int[] end, int n) {
        int max = -1;
        if (pathTask.getParents(n).size() == 0) {
            return 0;
        }
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

    private List<Pair> getProcWhereParentTask(int taskN) {
        List<Pair> res = new ArrayList<>();
        for (Integer i : pathTask.getParents(taskN)) {
            for (Map.Entry<Integer, LinkedList<Vertex>> entry : calculation.entrySet()) {
                for (Vertex vertex : entry.getValue()) {
                    if (vertex.taskNumber == i) {
                        res.add(new Pair(entry.getKey(), vertex.taskNumber));
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

    private List<Integer> getFreeProc(int[] arr, int time) {
        List<Integer> freeProc = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] <= time) {
                freeProc.add(i);
            }
        }
        return freeProc;
    }

    private int getRandomFreeProcessor(int[] arr, int time) {
        List<Integer> freeProc = getFreeProc(arr, time);
        Random r = new Random();
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

    private int getTimeOfTransfer(int taskN, int procTo, int[] dist, boolean isFinal) {
        int time = -1;
        for (Pair pair : getProcWhereParentTask(taskN)) {
            int procN = pair.procN;
            if (procN != procTo) {
                int parentTaskN = pair.taskN;
                if (!(taskOnProc.containsKey(procTo)
                        && taskOnProc.get(procTo).contains(new Pair(parentTaskN, 0)))) {
                    List<Processor> l = pathProc.getMinPath(procN, procTo);
                    int t = 0;
                    int k = 0;
                    for (int i = l.size() - 1; i > 0; i--) {
                        Node nodeFrom = nodes.get(parentTaskN);
                        Node nodeTo = nodes.get(taskN);
                        int prFrom = l.get(i).getN();
                        int prTo = l.get(i - 1).getN();
                        int length = findEdge(nodeFrom, nodeTo).getWeight();
                        int from = k++ > 0 ? t : dist[parentTaskN];
                        int timeTo = from + length;
                        from = getTimeBeginingOfSending(prFrom, from, timeTo, procTo);
                        DataTransferVertex vert =
                                new DataTransferVertex(from, length,
                                        "(" + parentTaskN + ")", taskN, prFrom, prTo);
                        vert.layer = getNumberOfSending(prFrom, vert);
                        t = vert.from + vert.length;
                        if (isFinal) {
                            addDataTransfer(prFrom, vert);
                        }
                        if (time < vert.from + vert.length) {
                            time = vert.from + vert.length;
                        }
                        if (isFinal) {
                            addTask(prTo, parentTaskN, t);
                        }
                    }
                } else {
                    for (Pair p : taskOnProc.get(procTo)) {
                        if (p.procN == parentTaskN) {
                            time = p.taskN;
                        }
                    }
                }
            }
        }
        return time;
    }

    private int getNumberOfSending(int procN, DataTransferVertex vert) {
        int vertFrom = vert.from;
        int vertTo = vert.length + vertFrom;
        int res = 0;
        if (dataTransfer.containsKey(procN)) {
            for (DataTransferVertex tmpVert : dataTransfer.get(procN)) {
                int tmpVertFrom = tmpVert.from;
                int tmpVertTo = tmpVert.length + tmpVertFrom;
                if ((vertFrom >= tmpVertFrom && vertFrom <= tmpVertTo)
                        || (vertTo >= tmpVertFrom && vertTo <= tmpVertFrom)) {
                    res++;
                    if (res >= linksNumber ||
                            (tmpVert.procFrom == vert.procFrom
                                    && tmpVert.procTo == vert.procTo)) {
                        vert.from = Math.max(vert.from, tmpVertTo);
                    }
                }
            }
        }
        if (dataTransfer.containsKey(vert.procTo)) {
            int t = 0;
            for (DataTransferVertex tmpVert : dataTransfer.get(vert.procTo)) {
                int tmpVertFrom = tmpVert.from;
                int tmpVertTo = tmpVert.length + tmpVertFrom;
                if ((vertFrom >= tmpVertFrom && vertFrom <= tmpVertTo)
                        || (vertTo >= tmpVertFrom && vertTo <= tmpVertFrom)) {
                    t++;
                    if (t >= linksNumber &&
                            !(tmpVert.procFrom == vert.procTo
                                    && tmpVert.procTo == vert.procFrom)) {
                        vert.from = Math.max(vert.from, tmpVertTo);
                    }
                }
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

    public int getTimeCalculationOneProc() {
        return nodes.stream()
                .map(node -> node.getWeight())
                .reduce((integer, integer2) -> integer + integer2)
                .get();
    }

    public int getTimeCalculation() {
        int time = Integer.MIN_VALUE;
        for (LinkedList<Vertex> vertices : calculation.values()) {
            for (Vertex vertice : vertices) {
                if (vertice.from + vertice.length > time) {
                    time = vertice.from + vertice.length;
                }
            }
        }
        return time;
    }

    public int getCriticalPath() {
        return pathTask.getCriticalPath();
    }

    private static class Pair {
        int procN;
        int taskN;

        public Pair(int procN, int taskN) {
            this.procN = procN;
            this.taskN = taskN;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair pair = (Pair) o;

            return procN == pair.procN;
        }

        @Override
        public int hashCode() {
            int result = procN;
            result = 31 * result + taskN;
            return result;
        }
    }
}
