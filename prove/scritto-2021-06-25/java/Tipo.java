// Autore:              Andreea Dornescu
// Anno accademico:     2022/23


import java.util.Random;

public enum Tipo {
    ADL, MIN;

    //random valore di un enum
    public static Tipo getRandomTipo() {
        Random random = new Random();
        int index = random.nextInt(Tipo.values().length);
        return Tipo.values()[index];
    }
}