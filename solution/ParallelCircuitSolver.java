package cp2024.solution;

import cp2024.circuit.CircuitSolver;
import cp2024.circuit.CircuitValue;
import cp2024.circuit.Circuit;
import cp2024.demo.BrokenCircuitValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class ParallelCircuitSolver implements CircuitSolver {

    private AtomicBoolean acceptComputations = new AtomicBoolean(true);
    private ArrayList<ParallelCircuitValue> circuits = new ArrayList<>(); //
    private List<ParallelCircuitValue> synchronizedList =
            Collections.synchronizedList(circuits);
    private Semaphore s = new Semaphore(1, true);

    // Sprawdza, czy może przyjąć zlecenie. Jeśli tak, tworzy nowy
    // ParallelCircuitValue, dodaje go do listy, a następniego zwraca.
    // Wszystkie obliczenia mają miejsce w środku ParallelCircuitValue, aby
    // umożliwić współbieżne wykonywanie się wielu obliczeń naraz. Jeśli nie
    // może przyjąć, tworzy BrokenParallelCircuitValue, który jako value ma
    // InterruptedException.
    @Override
    public CircuitValue solve(Circuit c) {
        /* FIX ME */
        try {
            s.acquire();
            if (acceptComputations.get()) {
                ParallelCircuitValue v = new ParallelCircuitValue(c);
                synchronizedList.add(v);
                s.release();
                return v;
            } else {
                s.release();
                return new BrokenParallelCircuitValue();
            }
        } catch (InterruptedException e) {
            return new BrokenParallelCircuitValue();
        }
    }

    // Przerywa wszystkie obliczenia działające w Solverze; sprawia też, że
    // nowe nie będą przyjmowane.
    @Override
    public void stop() {
        /*FIX ME*/
        try {
            s.acquire();
            acceptComputations.set(false);
            s.release();
            for (ParallelCircuitValue circuit : synchronizedList) {
                circuit.interrupt();
            }
        } catch (InterruptedException e) {}
    }
}
