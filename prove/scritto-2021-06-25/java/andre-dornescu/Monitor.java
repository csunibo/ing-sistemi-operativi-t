// Autore:              Andreea Dornescu
// Anno accademico:     2022/23


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor {
    //definisco limiti
    private final int NA; //numero ambulatori
    private final int MAXS; //capacità sala di aspetto

    //sincronizzazione:
    private Lock l = new ReentrantLock();

    //se c'è più di una direzione/priorità, una coda per ogni direzione:
    private Condition[] codaSala = new Condition[2];
    private Condition[] codaAmb = new Condition[3];

    //processi sospesi
    private int[] sospSala = new int[2];
    private int[] sospAmb = new int[3];

    //counter occupanti risorsa
    private int inSala, inAmbl;

    public Monitor (int NA, int MAXS){
        this.NA= NA;
        this.MAXS= MAXS;

        //all'inizio i counter nulli
        inAmbl=0;
        inSala=0;

        for (int i=0; i<2;i++){
            codaSala[i]=l.newCondition();
            sospSala[i]=0;
        }
        for (int i=0; i<3; i++){
            sospAmb[i]=0;
            codaAmb[i]=l.newCondition();
        }
    }

    //metodi entry
    public Codice entraSala (Tipo tipo) throws InterruptedException{ //ritorna il codice assegnato all'utente
        Codice codice;
        l.lock();
        //CERCO DI ENTRARE NELLA SALA DI ATTESA
        //CONDIZIONI SOSPENSIONE
        while (
                    (tipo == Tipo.ADL && inSala==MAXS) || (tipo== Tipo.MIN && inSala+2>MAXS) //SALA DI ATTESA PIENA
                    || (tipo== Tipo.MIN && sospSala[Tipo.ADL.ordinal()]>0) //GESTIONE PRIORITA
            ) {
                sospSala[tipo.ordinal()]++;
                codaSala[tipo.ordinal()].await();
                sospSala[tipo.ordinal()]--;
            }
            
            //aggiorno risorsa
            if (tipo == Tipo.MIN) inSala+=2;
            else inSala++;

            //assegno codice random
            codice = Codice.getRandomCodice();
            System.out.println("Utente di tipo " + tipo.name()+ " entra nella sala di attesa e riceve codice "+ codice.name()+ "\n");
        l.unlock();
        return codice;
    }
    public void entraAmb (Tipo tipo, Codice c) throws InterruptedException{
        l.lock();
        //CERCO DI ACCEDERE A UN AMBULATORIO
        //CONDIZIONI SOSPENSIONE
        while (
                inAmbl==NA //ambulatori pieni
                || (c == Codice.GIALLO && sospAmb[Codice.ROSSO.ordinal()]>0) || (c == Codice.VERDE && sospAmb[Codice.ROSSO.ordinal()]>0) ||(c == Codice.VERDE &&sospAmb[Codice.GIALLO.ordinal()]>0)
        ) {
            sospAmb[c.ordinal()]++;
            codaAmb[c.ordinal()].await();
            sospAmb[c.ordinal()]--;
        }

        //aggiorno risorsa
        inAmbl++;
        if (tipo == Tipo.MIN) {
            inSala -= 2;

            //si sono liberati 2 posti nella sala d'attesa
            if (sospSala[Tipo.ADL.ordinal()]>0) codaSala[Tipo.ADL.ordinal()].signalAll();
            else if (sospSala[Tipo.MIN.ordinal()]>0) codaSala[Tipo.MIN.ordinal()].signal();
        }
        else {
            inSala--;

            //si è liberato un posto nella sala d'attesa
            if (sospSala[Tipo.ADL.ordinal()]>0) codaSala[Tipo.ADL.ordinal()].signal();
        }
        System.out.println("Utente di tipo " + tipo.name()+ " con codice "+ c.name()+ " entra nell'ambulatorio\n");
        l.unlock();
    }

    public void esce () throws InterruptedException {
        l.lock();
        inAmbl--;

        //priorità: 1.ROSSO 2.GIALLO 3.VERDE
        if (sospAmb[Codice.ROSSO.ordinal()]>0) codaAmb[Codice.ROSSO.ordinal()].signal();
        else if (sospAmb[Codice.GIALLO.ordinal()]>0) codaAmb[Codice.GIALLO.ordinal()].signal();
        else if (sospAmb[Codice.VERDE.ordinal()]>0) codaAmb[Codice.VERDE.ordinal()].signal();
        System.out.println("Utente esce dall'ambulatorio\n");
        l.unlock();
    }
}