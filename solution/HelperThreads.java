package cp2024.solution;

import cp2024.circuit.CircuitNode;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HelperThreads implements Runnable {
    protected CircuitNode n;
    protected CircuitNode[] args;
    protected BlockingQueue<Boolean> putQueue;
    protected BlockingQueue<Boolean> getQueue;
    protected ArrayList<Thread> threads;

    // Klasa, po której dziedziczą wszystkie typy wątków tworzących
    // wierzchołki. Odbywa się tu inicjalizacja obiektów, ale także
    // przerywanie, które wygląda podobnie w każdej klasie.
    public HelperThreads(CircuitNode n, BlockingQueue<Boolean> putQueue) throws InterruptedException {
        this.n = n;
        this.args = n.getArgs();
        this.putQueue = putQueue;
        this.getQueue = new LinkedBlockingQueue<>(1);
        this.threads = new ArrayList<>();
    }

    @Override
    public void run() {
    }

    protected void doInterruption() {
        for (Thread t : threads) {
            t.interrupt();
            try {
                t.join();
            } catch (InterruptedException ex) {

            }
        }
    }
}