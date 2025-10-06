package cp2024.solution;

import cp2024.circuit.CircuitNode;
import cp2024.circuit.NodeType;

import java.util.concurrent.BlockingQueue;

public class GTLThread extends HelperThreads implements Runnable {

    private int threshold;

    // Obsługuje wierzchołek GTT, gdy tylko przekroczy oczekiwaną liczbę
    // true, przerywa obliczenia i zwraca wynik. Komunikacja między wątkami
    // odbywa się poprzez kolejkę blokującą.
    public GTLThread(CircuitNode n,
                     BlockingQueue<Boolean> putQueue, int threshold)
            throws InterruptedException {
        super(n, putQueue);
        this.threshold = threshold;
    }

    @Override
    public void run() {
        try {
            for (CircuitNode arg : args) {
                Thread thread = new Thread(ThreadFactory.createThread(arg,
                        getQueue));
                threads.add(thread);
                thread.start();
            }

            int j = 0;

            if (n.getType() == NodeType.GT) {
                for (int i = 0; i < args.length; i++) {
                    boolean b = getQueue.take();
                    if (b) {
                        j++;
                        if (j > threshold)
                            break;
                    }
                }
                if (j > threshold) {
                    putQueue.put(true);
                    doInterruption();
                } else {
                    putQueue.put(false);
                    for (Thread t : threads) {
                        t.join();
                    }
                }
            }
            else {
                for (int i = 0; i < args.length; i++) {
                    boolean b = getQueue.take();
                    if (b) {
                        j++;
                        if (j >= threshold)
                            break;
                    }
                }
                if (j >= threshold) {
                    putQueue.put(false);
                    doInterruption();
                } else {
                    putQueue.put(true);
                    for (Thread t : threads) {
                        t.join();
                    }
                }
            }
        } catch (InterruptedException e) {
            doInterruption();
        }
    }
}

