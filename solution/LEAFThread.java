package cp2024.solution;

import cp2024.circuit.LeafNode;

import java.util.concurrent.BlockingQueue;

public class LEAFThread extends HelperThreads implements Runnable  {

    private LeafNode n;

    // Obsługuje liście, zwracając z nich wartości, jeśli zostanie przerwany
    // nic nie robi. Wpp. przekazuje wartość przez kolejkę blokującą.
    public LEAFThread(LeafNode n,
                      BlockingQueue<Boolean> putQueue) throws InterruptedException {
        super(n, putQueue);
        this.n = n;
    }

    @Override
    public void run() {
        try {
            putQueue.put(n.getValue());
        } catch (InterruptedException e) {
            //System.out.println("nie żyję");
        }
    }
}