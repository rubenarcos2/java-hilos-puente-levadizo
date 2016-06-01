package Vehiculos;

import Escenario.PuenteControlador;
import Utils.Utilidades;
import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

/**
 * Examen Servicios y procesos - 1º Evaluación
 * -----------------------------------------
 *
 * @author Rubén
 */
public class Coche extends Vehiculo implements Runnable {

    public enum TIPO_COCHE {

        AMBULANCIA, POLICIA, PARTICULAR
    };

    public enum ESTADO_COCHE { //PENDIENTE: en caso de adelantar policia: PARADO, CIRCULANDO, APARTADO 

        CIRCULANDO_ANTES, CRUZANDO_PUENTE, CIRCULANDO_DESPUES, APARTANDOSE, APARTADO
    };

    private TIPO_COCHE tipoCoche;
    private ESTADO_COCHE estadoCoche;
    private static String[] listaLetrasMatriculaDenegadas = {"A", "E", "I", "O", "U", "Ñ", "Q", "CH", "LL"};
    private Applet escenario;
    private PuenteControlador puenteControlador;
    private Thread hilo;
    private double velocidad, velocidadAntigua, angulo, xAntigua;
    private boolean seEncuentraEnPuente, detectadaColision;
    private Rectangle rectanguloColisiones;

    public Coche(TIPO_COCHE tipoCoche, String matricula, int x, int y, Image imagenCoche, Applet escenario, PuenteControlador puenteControlador) {
        super(matricula, x, y, imagenCoche);
        this.tipoCoche = tipoCoche;
        this.escenario = escenario;
        this.puenteControlador = puenteControlador;
        velocidad = Utilidades.generarAleatorio(2, 5);
        velocidadAntigua = velocidad;
        angulo = 0;
        seEncuentraEnPuente = true;
        detectadaColision = false;
        rectanguloColisiones = new Rectangle(x - 50, y, imagenVehiculo.getWidth(escenario) + 150, 1);
        hilo = new Thread(this, "Coche");
        hilo.start();
    }

    @Override
    public void run() {
        while (seEncuentraEnPuente) {
            try {
                puenteControlador.solicitarPasoPuenteCoche(this);
                puenteControlador.detectorColisionesCoche(this);
                Thread.sleep((long) velocidad);
                if (estadoCoche != ESTADO_COCHE.APARTANDOSE && estadoCoche != ESTADO_COCHE.APARTADO) {
                    if (tipoCoche == TIPO_COCHE.PARTICULAR && !detectadaColision) {
                        y = 980; //La vuelta al carril tras apartarse
                    }
                    if (x < 200 && y < 1030) {
                        estadoCoche = ESTADO_COCHE.CIRCULANDO_ANTES;
                    } else if (x >= 200 && x <= 2050 && y < 1030) {
                        estadoCoche = ESTADO_COCHE.CRUZANDO_PUENTE;
                    } else if (x > 2050 && y < 1030) {
                        estadoCoche = ESTADO_COCHE.CIRCULANDO_DESPUES;
                    }
                } else {
                    if (y >= 980 && y < 1030) {
                        y++; //El movimiento desde el estado APARTANDOSE hasta APARTADO (parado en el arcén)
                    } else if (y <= 1030) {
                        estadoCoche = ESTADO_COCHE.APARTADO;
                    }
                }
                if (!detectadaColision) {
                    velocidad = velocidadAntigua;
                    x++;
                    xAntigua = x;
                } else {
                    velocidad = 1;
                    y = 810;
                    x++;
                }
            } catch (InterruptedException ex) {
                System.out.println("[LOG] - Error en la interrupción del coche: " + ex);
            }
            if (x >= (escenario.getWidth() + imagenVehiculo.getWidth(escenario)) * 2 + imagenVehiculo.getWidth(escenario)) {
                seEncuentraEnPuente = false;
            }
        }
        // Es una última petición al puenteControlador para reactivar a los particulares,
        // cuando salga el NO partícular del puente
        puenteControlador.solicitarPasoPuenteCoche(this);
        hilo.interrupt();
    }

    public void paint(Graphics2D g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform at = new AffineTransform();
        at.scale(0.3, 0.3);
        at.translate(x, y);
        if (estadoCoche == ESTADO_COCHE.APARTANDOSE) {
            at.rotate(Math.toRadians(angulo++));
        } else if (estadoCoche == ESTADO_COCHE.APARTADO && angulo < 45) {
            at.rotate(Math.toRadians(angulo));
        }
        rectanguloColisiones.setLocation(x, y);
        g2d.setColor(Color.red);
        g2d.draw(rectanguloColisiones);
        g2d.drawImage(imagenVehiculo, at, escenario);
        escenario.repaint();
    }

    public TIPO_COCHE getTipoCoche() {
        return tipoCoche;
    }

    public static String generarMatriculaCoche(TIPO_COCHE tipoCoche) {
        String matricula = "";
        if (tipoCoche == TIPO_COCHE.AMBULANCIA || tipoCoche == TIPO_COCHE.PARTICULAR) {
            for (int i = 0; i < 4; i++) {
                matricula += String.valueOf(Utilidades.generarAleatorio(0, 10));
            }
            for (int i = 0; i < 3; i++) {
                char letra = 0;
                do {
                    letra = (char) Utilidades.generarAleatorio(65, 90);
                } while (comprobarLetraMatriculaCoche(letra));
                matricula += String.valueOf(letra);
            }
        } else {
            matricula = "DGP";
            for (int i = 0; i < 4; i++) {
                matricula += String.valueOf(Utilidades.generarAleatorio(0, 10));
            }
        }
        return matricula;
    }

    //Comprueba si la letra de la matrícula generada está permitida.
    /*Según la ley: https://www.boe.es/buscar/act.php?id=BOE-A-1999-1826
     En las placas de matrícula se inscribirán dos grupos de caracteres constituidos
     por un número de cuatro cifras, que irá desde el 0000 al 9999, y de tres letras,
     empezando por las letras BBB y terminando por las letras ZZZ, suprimiéndose las
     cinco vocales, y las letras Ñ, Q, CH y LL.
     */
    private static boolean comprobarLetraMatriculaCoche(char letra) {
        boolean validacion = false;
        for (int i = 0; i < listaLetrasMatriculaDenegadas.length; i++) {
            if (String.valueOf(letra).equals(listaLetrasMatriculaDenegadas[i])) {
                validacion = true;
            }
        }
        return validacion;
    }

    public boolean getSeEncuentraEnPuente() {
        return seEncuentraEnPuente;
    }

    public ESTADO_COCHE getEstado() {
        return estadoCoche;
    }

    public void setEstado(ESTADO_COCHE estadoCoche) {
        this.estadoCoche = estadoCoche;
    }

    public Rectangle getRectanguloColisiones() {
        return rectanguloColisiones;
    }

    public void setDetectadaColision(boolean colision, Coche coche) {
        detectadaColision = colision;
    }

    public boolean getDetectadaColision() {
        return detectadaColision;
    }
}
