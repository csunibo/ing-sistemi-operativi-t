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

    public void signalAll() {
        System.out.println("[SEGNALI] " + Thread.currentThread().getName() + " ha segnalato a " + description
                + " con una signalAll!");
        condition.signalAll();
    }

    public void signal() {
        System.out.println(
                "[SEGNALI] " + Thread.currentThread().getName() + " ha segnalato a " + description
                        + " con una signal!");
        System.out.println("[SEGNALI] Attualmente sospesi: " + waiting);
        condition.signal();
    }

    public boolean anyoneWaiting() {
        return waiting > 0;
    }

    public void waitWhile(Supplier<Boolean> waitCondition) throws InterruptedException {
        waiting++;
        while (waitCondition.get()) {
            condition.await();
        }
        waiting--;
    }

}