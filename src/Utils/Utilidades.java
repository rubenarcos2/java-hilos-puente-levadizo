package Utils;

/**
 *
 * @author Rubén
 */
public class Utilidades {

    public static int generarAleatorio(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min) + min);
    }

}
