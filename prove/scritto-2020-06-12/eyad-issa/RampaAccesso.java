// Autore:              Eyad Issa
// Anno accademico:     2022/23

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RampaAccesso {

    public static enum Veicolo {
        NESSUNO, AUTOMOBILE, AMBULANZA;
    }

    private final Lock lock = new ReentrantLock();

    private final CountingCondition macchineAttesaIn = new CountingCondition(lock, "Macchine in attesa entrata");
    private final CountingCondition ambulanzeAttesaInSirenaOn = new CountingCondition(lock,
            "Ambulanze sirena ON in attesa entrata");
    private final CountingCondition ambulanzeAttesaInSirenaOff = new CountingCondition(lock,
            "Ambulanze sirena OFF in attesa entrata");
    private final CountingCondition macchineAttesaOut = new CountingCondition(lock, "Macchine in attesa uscita");
    private final CountingCondition ambulanzeAttesaOut = new CountingCondition(lock, "Ambulanze in attesa uscita");

    private Veicolo rampaIn = Veicolo.NESSUNO;
    private Veicolo rampaOut = Veicolo.NESSUNO;

    /* N massimo di soste nella camera calda */
    private final int maxCameraCalda;

    private int macchineCameraCalda = 0;
    private int ambulanzeCameraCalda = 0;

    public RampaAccesso(final int maxCameraCalda) {
        this.maxCameraCalda = maxCameraCalda;
    }

    private int veicoliCameraCalda() {
        return ambulanzeCameraCalda + macchineCameraCalda;
    }

    private boolean cameraCaldaPiena() {
        return veicoliCameraCalda() >= maxCameraCalda;
    }

    public void entraRampaEntrata(Veicolo veicolo, boolean sirena) throws InterruptedException {
        try {
            lock.lock();
            if (veicolo == Veicolo.AMBULANZA) {
                if (sirena) {
                    ambulanzeAttesaInSirenaOn.waitWhile(() -> cameraCaldaPiena()
                            // La rampa di ingresso è occupata
                            || rampaIn != Veicolo.NESSUNO

                            // C'è un ambulanza nel verso opposto
                            || rampaOut == Veicolo.AMBULANZA

                            // Precedenza a quelle che vogliono uscire
                            || ambulanzeAttesaOut.anyoneWaiting()
                            || macchineAttesaOut.anyoneWaiting());
                } else {
                    ambulanzeAttesaInSirenaOff.waitWhile(() -> cameraCaldaPiena()
                            // La rampa di ingresso è occupata
                            || rampaIn != Veicolo.NESSUNO

                            // C'è un ambulanza nel verso opposto
                            || rampaOut == Veicolo.AMBULANZA

                            // Precedenza alle uscite
                            || ambulanzeAttesaOut.anyoneWaiting()

                            // Precedenza a quelle in entrata con sirena on
                            || ambulanzeAttesaInSirenaOn.anyoneWaiting());
                }
            } else if (veicolo == Veicolo.AUTOMOBILE) {

                macchineAttesaIn.waitWhile(() -> cameraCaldaPiena()
                        // Rampa piena
                        || rampaIn != Veicolo.NESSUNO

                        // Precedenza ambulanze in entrata
                        || ambulanzeAttesaInSirenaOff.anyoneWaiting()
                        || ambulanzeAttesaInSirenaOn.anyoneWaiting()

                        // Macchine in camera calda non devono superare tot
                        || macchineCameraCalda > (maxCameraCalda / 2));

            } else
                throw new IllegalArgumentException();

            rampaIn = veicolo;

        } finally {
            lock.unlock();
        }

    }

    public void esciRampaEntrata(Veicolo veicolo) {
        try {
            lock.lock();

            rampaIn = Veicolo.NESSUNO;
            if (veicolo == Veicolo.AMBULANZA) {
                ambulanzeCameraCalda++;
            } else if (veicolo == Veicolo.AUTOMOBILE) {
                macchineCameraCalda++;
            } else
                throw new IllegalArgumentException();

            ambulanzeAttesaInSirenaOn.signal();
            ambulanzeAttesaInSirenaOff.signal();
            macchineAttesaIn.signal();

            ambulanzeAttesaOut.signal(); // Nel caso ci sia un ambulanza bloccata nella "camera calda"

        } finally {
            lock.unlock();
        }
    }

    public void entraRampaUscita(Veicolo veicolo) throws InterruptedException {
        try {
            lock.lock();

            if (veicolo == Veicolo.AMBULANZA) {
                ambulanzeAttesaOut.waitWhile(() ->
                // La rampa deve essere vuota
                rampaOut != Veicolo.NESSUNO
                        // Non ci devono essere ambulanze nel verso opposto
                        || rampaIn == Veicolo.AMBULANZA);

                ambulanzeCameraCalda--;

            } else if (veicolo == Veicolo.AUTOMOBILE) {
                macchineAttesaOut.waitWhile(() ->
                // La rampa deve essere vuota
                rampaOut != Veicolo.NESSUNO
                        // Precedenza alle amb. in uscita
                        || ambulanzeAttesaOut.anyoneWaiting());

                macchineCameraCalda--;
            } else
                throw new IllegalArgumentException();

            rampaOut = veicolo;
            // Segnala alla gente che aspettava si liberasse un posto
            ambulanzeAttesaInSirenaOn.signal();
            ambulanzeAttesaInSirenaOff.signal();
            macchineAttesaIn.signal();

        } finally {
            lock.unlock();
        }
    }

    public void esciRampaUscita() throws InterruptedException {
        try {
            lock.lock();

            rampaOut = Veicolo.NESSUNO;
            // Segnala alla gente che aspettava si liberasse la rampa
            ambulanzeAttesaOut.signal();
            macchineAttesaOut.signal();

            ambulanzeAttesaInSirenaOn.signal();
            ambulanzeAttesaInSirenaOff.signal();

        } finally {
            lock.unlock();
        }
    }

}
