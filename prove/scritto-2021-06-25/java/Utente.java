// Autore:              Andreea Dornescu
// Anno accademico:     2022/23


import java.util.Random;

public class Utente extends Thread{
    private Monitor m;
    private Random r;
    private Tipo t;

    public Utente(Monitor m, Random r, Tipo t){
        this.m = m;
        this.r =r;
        this.t=t;
    }

    public void run(){
        Codice codice;
        try{
            Thread.sleep(1000 + r.nextInt(100));
            codice= m.entraSala(t);
            Thread.sleep(r.nextInt(1000)); //sto occupando la risorsa...
            m.entraAmb(t,codice);
            Thread.sleep(1000 + r.nextInt(1000)); //sto occupando la risorsa...
            m.esce();
            Thread.sleep(r.nextInt(1000));
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
