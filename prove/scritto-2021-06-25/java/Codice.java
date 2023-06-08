// Autore:              Andreea Dornescu
// Anno accademico:     2022/23


import java.util.Random;

public enum Codice {
    ROSSO, GIALLO, VERDE;

    //random valore di un enum
    public static Codice getRandomCodice() {
        Random random = new Random();
        int index = random.nextInt(Codice.values().length);
        return Codice.values()[index];
    }
}