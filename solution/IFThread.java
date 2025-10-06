package cp2024.solution;

import cp2024.circuit.CircuitNode;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class IFThread extends HelperThreads implements Runnable {

    // Oblicza wierzchołek IF, aby było to możliwe leniwie tworzę dodatkową
    // klasę Pair<(a/b/c), (wartość wierzchołka)> oraz klasę na IFHelper, aby
    // móc dostać wartość bez zmieniania dziedziczenia.
    public IFThread(CircuitNode n, BlockingQueue<Boolean> putQueue)
            throws InterruptedException {
        super(n, putQueue);
    }

    @Override
    public void run() {
        try {

            BlockingQueue<Pair<Integer, Boolean>> getQueue =
                    new LinkedBlockingQueue<>();
            for (int i = 0; i < 3; i++) {
                Thread thread = new Thread(new IFhelper(i, getQueue));
                threads.add(thread);
                thread.start();
            }

            Pair<Integer, Boolean> first = null;
            Pair<Integer, Boolean> second = null;
            Pair<Integer, Boolean> third;

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    first = getQueue.take();
                }
                else if (i == 1) {
                    second = getQueue.take();
                    if (second.getFirst() + first.getFirst() == 3) {
                        if (second.getSecond() == first.getSecond()) {
                            putQueue.put(second.getSecond());
                            break;
                        }
                    }
                    else if (first.getFirst() == 0 && first.getSecond()
                            && second.getFirst() == 1) {
                        putQueue.put(second.getSecond());
                        break;
                    }
                    else if (first.getFirst() == 0 && !first.getSecond()
                            && second.getFirst() == 2) {
                        putQueue.put(second.getSecond());
                        break;
                    }
                    else if (second.getFirst() == 0) {
                        if (second.getSecond() && first.getFirst() == 1) {
                            putQueue.put(first.getSecond());
                            break;
                        }
                        else if (!second.getSecond() && first.getFirst() == 2) {
                            putQueue.put(first.getSecond());
                            break;
                        }
                    }
                }
                else {
                    third = getQueue.take();
                    if (third.getFirst() == 0) {
                        if (third.getSecond()) {
                            if (first.getFirst() == 1) {
                                putQueue.put(first.getSecond());
                            }
                            else {
                                putQueue.put(second.getSecond());
                            }
                            break;
                        }
                        else {
                            if (first.getFirst() == 2) {
                                putQueue.put(first.getSecond());
                            }
                            else {
                                putQueue.put(second.getSecond());
                            }
                            break;
                        }
                    }
                    else {
                        putQueue.put(third.getSecond());
                        break;
                    }
                }
            }

            doInterruption();

        } catch (InterruptedException e) {
            doInterruption();
        }
    }


    // Dodatkowa klasa
    private class IFhelper implements Runnable {

        private int id;
        private BlockingQueue<Pair<Integer, Boolean>> putQueue;
        private BlockingQueue<Boolean> getQueue;
        private Thread firstThread;

        public IFhelper(int id,
                        BlockingQueue<Pair<Integer, Boolean>> putQueue) {
            this.id = id;
            this.putQueue = putQueue;
            this.getQueue = new LinkedBlockingQueue<>(1);
        }

        @Override
        public void run() {
            try {
                Thread thread = new Thread(ThreadFactory.createThread(args[id],
                        getQueue));
                firstThread = thread;
                thread.start();

                boolean b;
                b = getQueue.take();
                Pair<Integer, Boolean> p = new Pair<>(id, b);
                putQueue.put(p);
            } catch (InterruptedException e) {
                firstThread.interrupt();
                try {
                    firstThread.join();
                } catch (InterruptedException ex) {}
            }
        }
    }

    private class Pair <T1, T2> {
        private final T1 first;
        private final T2 second;

        public Pair(T1 first, T2 second) {
            this.first = first;
            this.second = second;
        }

        public T1 getFirst() {
            return first;
        }

        public T2 getSecond() {
            return second;
        }
    }
}
