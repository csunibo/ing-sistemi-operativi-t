import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class Monitor {

    public static enum Veicolo {
        AUTO_PUBBLICA, AUTO_PRIVATA, BARCA;
    }

    private final Lock lock = new ReentrantLock();
    private final CountingCondition attesaAutoPrivate = new CountingCondition(lock, "auto pri");
    private final CountingCondition attesaAutoPubbliche = new CountingCondition(lock, "auto pub");
    private final CountingCondition attesaBarche = new CountingCondition(lock, "barche");

    // Variabili di stato
    private boolean ponteAperto = false;
    private int autoInTransito = 0;
    private int barcheInTransito = 0;

    // Variabili passate dal costruttore
    private final int maxMacchineInTransito;

    public Monitor(final int maxMacchine) {
        this.maxMacchineInTransito = maxMacchine;
    }

    public void entraPonte(Veicolo veicolo) throws InterruptedException {
        try {
            lock.lock();

            System.out.println("Sta entrando un " + veicolo);

            if (veicolo == Veicolo.BARCA) {
                final Supplier<Boolean> condition = () -> {
                    return ponteAperto || autoInTransito == 0;
                };
                attesaBarche.waitUntil(condition);

                // Apri ponte se era chiuso
                if (!ponteAperto) {
                    ponteAperto = true;
                }
                barcheInTransito++;

            } else if (veicolo == Veicolo.AUTO_PUBBLICA) {
                final Supplier<Boolean> condition = () -> {

                    return (!ponteAperto || barcheInTransito == 0)
                            && autoInTransito < maxMacchineInTransito
                            && attesaBarche.nobodyWaiting();
                };
                attesaAutoPubbliche.waitUntil(condition);

                // Se aperto chiudi il ponte
                if (ponteAperto) {
                    ponteAperto = false;
                }
                autoInTransito++;

            } else if (veicolo == Veicolo.AUTO_PRIVATA) {
                final Supplier<Boolean> condition = () -> {
                    return (!ponteAperto || barcheInTransito == 0)
                            && autoInTransito < maxMacchineInTransito
                            && attesaBarche.nobodyWaiting()
                            && attesaAutoPubbliche.nobodyWaiting();
                };
                attesaAutoPrivate.waitUntil(condition);

                // Se aperto chiudi il ponte
                if (ponteAperto) {
                    ponteAperto = false;
                }
                autoInTransito++;

                printInfo();
            } else
                throw new IllegalArgumentException();

        } finally {
            lock.unlock();
        }
    }

    private void printInfo() {
        System.out.println("ponteAperto = " + ponteAperto + "\nautoInTransito = " + autoInTransito
                + "\nbarcheIntransito = " + barcheInTransito);
    }

    public void esciPonte(Veicolo veicolo) {
        try {
            lock.lock();

            System.out.println("Esce un " + veicolo);
            printInfo();
            if (veicolo == Veicolo.BARCA) {
                barcheInTransito--;

                attesaBarche.signalAll();
                attesaAutoPubbliche.signalAll();
                attesaAutoPrivate.signalAll();

            } else if (veicolo == Veicolo.AUTO_PRIVATA || veicolo == Veicolo.AUTO_PUBBLICA) {
                autoInTransito--;

                attesaBarche.signalAll();
                attesaAutoPubbliche.signalAll();
                attesaAutoPrivate.signalAll();
            } else
                throw new IllegalArgumentException();

        } finally {
            lock.unlock();
        }

    }

}
