package cp2024.solution;

import cp2024.circuit.CircuitNode;

import java.util.concurrent.BlockingQueue;

public class NOTThread extends HelperThreads implements Runnable  {

    // Obsługuje wierzchołek NOT negując otrzymaną wartość, przekazuje ją
    // poprzez kolejkę blokującą.
    public NOTThread(CircuitNode n,
                     BlockingQueue<Boolean> putQueue) throws InterruptedException {
        super(n, putQueue);
    }

    @Override
    public void run() {
        try {
            Thread thread = new Thread(ThreadFactory.createThread(args[0],
                    getQueue));
            threads.add(thread);
            thread.start();

            boolean b = getQueue.take();
            putQueue.put(!b);
            thread.join();
        } catch (InterruptedException e) {
            doInterruption();
        }
    }
}