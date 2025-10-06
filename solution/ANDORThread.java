package cp2024.solution;

import cp2024.circuit.CircuitNode;

import java.util.concurrent.BlockingQueue;

public class ANDORThread extends HelperThreads implements Runnable {

    // Obsługuje wierzchołek AND, tworzy wątki, które obliczają wartości,
    // gdy tylko natkniemy się na false to przerywamy obliczenia, ponieważ
    // wiadomo, że AND musi zwrócić false. Wpp. czekamy na wszystkie wątki.
    // Wartości przekazujemy kolejką blokującą między wątkami.
    // Obsługuje wierzchołek OR. Gdy tylko pojawi się true, to zwraca tę
    // wartość, przerywając resztę wątków.
    // Desired: dla AND - false (wtedy wiemy, że należy zwrócić false), dla
    // OR - true (wtedy zwracamy true)
    private boolean desired;
    public ANDORThread(CircuitNode n, BlockingQueue<Boolean> putQueue,
                     boolean desired)
            throws InterruptedException {
        super(n, putQueue);
        this.desired = desired;
    }

    @Override
    public void run() {
        try {
            for (CircuitNode arg : args) {
                Thread thread = new Thread(ThreadFactory.createThread(arg, getQueue));
                threads.add(thread);
                thread.start();
            }

            boolean b = true;
            for (int i = 0; i < args.length; i++) {
                b = getQueue.take();
                if (b == desired) break;
            }

            if (b == desired) {
                putQueue.put(desired);
                doInterruption();
            } else {
                putQueue.put(!desired);
                for (Thread t : threads) {
                    t.join();
                }
            }
        } catch (InterruptedException e) {
            doInterruption();
        }
    }
}
