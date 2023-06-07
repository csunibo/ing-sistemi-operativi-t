import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

public class CountingCondition {
    private final Condition condition;
    private final String description;
    private int waiting = 0;

    public CountingCondition(final Lock lock, String description) {
        this.description = description;
        condition = lock.newCondition();
    }

    private void printInfo() {
        final String nomeThread = Thread.currentThread().getName();
        System.out.println("[SEGNALI-" + description + "] \t\t" + nomeThread + " ha segnalato con una signalAll!");
        System.out.println("[SEGNALI-" + description + "] \t\tAttualmente sospesi: " + waiting);
    }

    public void signalAll() {
        printInfo();

        condition.signalAll();
    }

    public void signal() {
        printInfo();

        condition.signal();
    }

    public boolean nobodyWaiting() {
        return waiting == 0;
    }

    public void waitUntil(Supplier<Boolean> continueCondition) throws InterruptedException {
        while (!continueCondition.get()) {
            waiting++;
            condition.await();
            waiting--;
        }
    }

}
