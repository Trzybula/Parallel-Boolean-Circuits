package cp2024.solution;

import cp2024.circuit.Circuit;
import cp2024.circuit.CircuitValue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class ParallelCircuitValue implements CircuitValue {
    private boolean value;
    private AtomicBoolean done = new AtomicBoolean(false);
    private AtomicBoolean interrupted = new AtomicBoolean(false);
    private final Thread firstThread;
    private BlockingQueue<Boolean> getQueue;
    private Semaphore s;

    // Obliczenia Value rozpoczynają się w tej klasie, inicjuję pierwszy
    // wątek, który można nazwać Rootem, który później tworzy kolejne wątki
    // w zależności od potrzeby.
    public ParallelCircuitValue(Circuit c) {
        this.getQueue =
                new LinkedBlockingQueue<>(1);
        Thread t = new Thread(new Root(this, c,
                getQueue));
        firstThread = t;
        t.start();
        this.s = new Semaphore(1, true);
    }

    // Ustawienie obliczonej wartości i napisanie, że już obliczono wartość
    // przez Roota.
    public void setDoneAndValue(boolean b) {
        try {
            s.acquire();
            if (!interrupted.get())
                done.set(true);
            this.value = b;
            s.release();
        } catch (InterruptedException e) {

        }
    }

    // Sprawdza, czy obliczenia nie zostały przerwane. Jeśli były, to
    // zwracamy Interrupted Exception.
    @Override
    public boolean getValue() throws InterruptedException {
        /* FIX ME */
        try {
            s.acquire();
            if (interrupted.get() && !done.get()) {
                s.release();
                throw new InterruptedException();
            }
            s.release();
            firstThread.join();
            return value;
        } catch (InterruptedException e) {
            firstThread.interrupt();
            firstThread.join();
            throw new InterruptedException();
        }
    }

    // Przerywa obliczenia w momencie gdy done nie jest skończone.
    public void interrupt() {
        try {
            s.acquire();
            if (!done.get()) {
                interrupted.set(true);
                s.release();
                firstThread.interrupt();
                firstThread.join();
            }
            else {
                s.release();
            }
        } catch(InterruptedException e){

        }
    }
        // Pierwszy wątek do obliczania wartości Circuitu inicjuje resztę wątków
    // i obliczenia.
    private class Root implements Runnable {

        private Thread firstThread;
        private ParallelCircuitValue it;
        private BlockingQueue<Boolean> getQueue;
        private Circuit c;

        public Root(ParallelCircuitValue it, Circuit c,
                             BlockingQueue<Boolean> getQueue) {
            this.it = it;
            this.getQueue = getQueue;
            this.c = c;
        }

        @Override
        public void run(){
            try {
                Thread t = new Thread(ThreadFactory.createThread(c.getRoot(),
                        getQueue));
                firstThread = t;
                t.start();

                boolean value = getQueue.take();
                it.setDoneAndValue(value);

                firstThread.join();
            } catch (InterruptedException e) {
                firstThread.interrupt();
                try {
                    firstThread.join();
                } catch (InterruptedException ex) {

                }
            }
        }
    }
}
