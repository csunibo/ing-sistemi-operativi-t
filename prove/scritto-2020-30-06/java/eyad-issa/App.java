import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App {
    public static void main(String[] args) throws Exception {

        final Monitor monitor = new Monitor(10);
        final Random random = new Random(System.currentTimeMillis());

        final Runnable macchina = () -> {
            try {
                final Monitor.Veicolo tipo = random.nextBoolean() ? Monitor.Veicolo.AUTO_PRIVATA
                        : Monitor.Veicolo.AUTO_PUBBLICA;
                monitor.entraPonte(tipo);
                Thread.sleep(1000);
                monitor.esciPonte(tipo);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        final Runnable barca = () -> {
            try {
                monitor.entraPonte(Monitor.Veicolo.BARCA);
                Thread.sleep(random.nextInt(4000));
                monitor.esciPonte(Monitor.Veicolo.BARCA);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        final int nThread = 50;
        List<Thread> threads = new ArrayList<>(nThread);
        for (int i = 0; i < nThread; i++) {
            final Thread thread = new Thread(random.nextBoolean() ? macchina : barca);
            thread.start();
            threads.add(thread);
        }

        for (Thread t : threads) {
            t.join();
        }

    }

}
