import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App {
    public static void main(String[] args) throws Exception {

        final RampaAccesso monitor = new RampaAccesso(10);
        final Random random = new Random(System.currentTimeMillis());

        final Runnable veicoloRunnable = () -> {
            try {
                Boolean sirena = random.nextBoolean();
                RampaAccesso.Veicolo veicolo = random.nextBoolean() ? RampaAccesso.Veicolo.AMBULANZA
                        : RampaAccesso.Veicolo.AUTOMOBILE;

                monitor.entraRampaEntrata(veicolo, sirena);
                System.out.println(veicolo + " n." + Thread.currentThread().getName() + ": -> RAMPA    OSPEDALE");
                Thread.sleep(random.nextInt(1000));
                monitor.esciRampaEntrata(veicolo);
                System.out.println(veicolo + " n." + Thread.currentThread().getName() + ": -> RAMPA -> OSPEDALE");

                Thread.sleep(random.nextInt(1000));

                monitor.entraRampaUscita(veicolo);
                System.out.println(veicolo + " n." + Thread.currentThread().getName() + ":    RAMPA <- OSPEDALE");
                Thread.sleep(random.nextInt(1000));
                System.out.println(veicolo + " n." + Thread.currentThread().getName() + ": <- RAMPA <- OSPEDALE");
                monitor.esciRampaUscita();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        List<Thread> threads = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            Thread amb = new Thread(veicoloRunnable);
            amb.start();
            threads.add(amb);

        }

        for (Thread t : threads) {
            t.join();
        }

    }

}
