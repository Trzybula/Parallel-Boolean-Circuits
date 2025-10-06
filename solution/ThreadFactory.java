package cp2024.solution;

import cp2024.circuit.CircuitNode;
import cp2024.circuit.LeafNode;
import cp2024.circuit.ThresholdNode;

import java.util.concurrent.BlockingQueue;

public class ThreadFactory {

    // Tworzy konkretny typ wątku per wierzchołek, jeśli użyto innego typu,
    // zwraca błąd.
    public static HelperThreads createThread(CircuitNode c,
                                             BlockingQueue<Boolean> putQueue)
            throws InterruptedException {
        switch (c.getType()) {
            case AND:
                return new ANDORThread(c, putQueue, false);
            case OR:
                return new ANDORThread(c, putQueue, true);
            case IF:
                return new IFThread(c, putQueue);
            case LEAF:
                return new LEAFThread((LeafNode) c, putQueue);
            case GT:
                if (c instanceof ThresholdNode thresholdNodegt) {
                    return new GTLThread(c, putQueue,
                            thresholdNodegt.getThreshold());
                }
            case LT:
                if (c instanceof ThresholdNode thresholdNodelt) {
                    return new GTLThread(c, putQueue,
                            thresholdNodelt.getThreshold());
                }
            case NOT:
                return new NOTThread(c, putQueue);
        }
        throw new RuntimeException("Illegal type " + c.getType());
    }
}