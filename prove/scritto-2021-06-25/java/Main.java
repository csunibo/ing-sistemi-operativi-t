// Autore:              Andreea Dornescu
// Anno accademico:     2022/23


import java.util.Random;

public class Main {
    //definisco limiti risorsa
    private final static int MAXS = 20;
    private final static int NA = 10;


    public static void main(String[] args){
        //creazione istanze
        final int thread_max=40; //numero massimo di thread
        Random r=new Random(System.currentTimeMillis());
        int dim = r.nextInt(5,thread_max); //numero effettivo thread
        Monitor m = new Monitor(NA,MAXS);
        Utente[] utenti = new Utente[dim];

        //creazione e attivazione thread
        for (int i =0 ; i<dim ; i++){
            Tipo tipo= Tipo.getRandomTipo();
            utenti[i]= new Utente(m,r,tipo);
        }
        for (int i =0 ; i<dim ; i++){
            utenti[i].start();
        }
        //attesa terminazione thread
        try {
            for (Utente t : utenti)
                t.join();
        } catch (InterruptedException e) {
            System.err.println("Il Thread main ha ricevuto una interruzione!");
            e.printStackTrace();
        }
    }
}