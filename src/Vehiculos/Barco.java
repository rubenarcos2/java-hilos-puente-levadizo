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
public class Barco extends Vehiculo implements Runnable {

    public enum TIPO_BARCO {

        CRUCERO, VELERO
    };

    public enum ESTADO_BARCO {

        NAVEGANDO_ANTES, CRUZANDO_PUENTE, NAVEGANDO_DESPUES
    };

    private TIPO_BARCO tipoBarco;
    private ESTADO_BARCO estadoBarco;
    private static String nombre, pais;
    //Código de abreviatura de países de la UE según norma ISO-3162-Alfa-2
    //http://publications.europa.eu/code/es/es-370100.htm
    private static String[] listaAbrePaises = {"BE", "BG", "CZ", "DK", "DE", "EE", "IE", "EL", "ES", "FR", "HR", "IT", "CY", "LV", "LT", "LU", "HU", "MT", "NL", "AT", "PL", "PT", "RO", "SI", "SK", "FI", "SE", "UK"};
    private static String[] listaPaises = {"Bélgica", "Bulgaria", "Chequia", "Dinamarca", "Alemania", "Estonia", "Irlanda", "Grecia", "España", "Francia", "Croacia", "Italia", "Chipre", "Letonia", "Lituania", "Luxemburgo", "Hungría", "Malta", "Países Bajos", "Austria", "Polonia", "Portugal", "Rumanía", "Eslovenia", "Eslovaquia", "Finlandia", "Suecia", "Reino Unido"};
    private static String[] listaNombresCruceros = {"CostaConcordia", "CostaDiscordia", "TierraAdentro", "MarRevuelto", "ArenasDePiedra"};
    private static String[] listaNombresVeleros = {"Graco I", "Graco II", "Cocomo", "DTD", "Schema II"};
    private Applet escenario;
    private PuenteControlador puenteControlador;
    private Thread hilo;
    private int velocidad, velocidadAntigua;
    private boolean seEncuentraEnRio, detectadaColision;
    private Rectangle rectanguloColisiones;

    public Barco(TIPO_BARCO tipoBarcos, String nombre, String matricula, int x, int y, Image imagenVehiculo, Applet escenario, PuenteControlador puenteControlador) {
        super(matricula, x, y, imagenVehiculo);
        this.tipoBarco = tipoBarcos;
        this.puenteControlador = puenteControlador;
        this.nombre = nombre;
        this.escenario = escenario;
        velocidad = Utilidades.generarAleatorio(3, 6);
        velocidadAntigua = velocidad;
        seEncuentraEnRio = true;
        detectadaColision = false;
        rectanguloColisiones = new Rectangle(x, y - 50, imagenVehiculo.getWidth(escenario), imagenVehiculo.getHeight(escenario) + 100);
        hilo = new Thread(this, "Barco");
        hilo.start();
    }

    @Override
    public void run() {
        while (seEncuentraEnRio) {
            try {
                puenteControlador.solicitarPasoPuenteBarco(this);
                puenteControlador.detectorColisionesBarco(this);
                //System.out.println("[BARCO] - Solicita pasa al puente. (" + tipoBarco + ")");
                Thread.sleep(velocidad);
                if (y < 270 + imagenVehiculo.getHeight(escenario)) {
                    estadoBarco = ESTADO_BARCO.NAVEGANDO_ANTES;
                } else if (y >= 270 + imagenVehiculo.getHeight(escenario) && y <= 1030 + imagenVehiculo.getHeight(escenario)) {
                    estadoBarco = ESTADO_BARCO.CRUZANDO_PUENTE;
                } else if (y > 1030 + imagenVehiculo.getHeight(escenario)) {
                    estadoBarco = ESTADO_BARCO.NAVEGANDO_DESPUES;
                }
                if (y >= (escenario.getHeight() + imagenVehiculo.getHeight(escenario)) * 2 + imagenVehiculo.getHeight(escenario) - 20) {
                    seEncuentraEnRio = false;
                }
                if (!detectadaColision) {
                    velocidad = velocidadAntigua;
                    y++;
                } else {
                    if (tipoBarco == TIPO_BARCO.CRUCERO) {
                        x = 960;
                    } else {
                        x = 1500;
                    }
                    y++;
                }
            } catch (InterruptedException ex) {
                System.out.println("[LOG] - Error en la interrupción del barco: " + ex);
            }
        }
    }

    public void paint(Graphics2D g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform at = new AffineTransform();
        at.scale(0.3, 0.3);
        at.translate(x, y);
        rectanguloColisiones.setLocation(x, y);
        g2d.setColor(Color.red);
        g2d.draw(rectanguloColisiones);
        g2d.drawImage(imagenVehiculo, at, escenario);
        escenario.repaint();
    }

    public TIPO_BARCO getTipoBarco() {
        return tipoBarco;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPais() {
        return pais;
    }

    public static String generarMatriculaBarco(TIPO_BARCO tipoBarco) {
        String matricula = "";
        int numPais = Utilidades.generarAleatorio(0, listaAbrePaises.length);
        pais = listaPaises[numPais];

        if (tipoBarco == TIPO_BARCO.CRUCERO) {
            matricula = listaAbrePaises[numPais];
            for (int i = 0; i < 4; i++) {
                matricula += String.valueOf(Utilidades.generarAleatorio(0, 10));
            }
            matricula += String.valueOf((char) Utilidades.generarAleatorio(65, 90));
        } else {
            matricula = matricula += listaAbrePaises[numPais];
            for (int i = 0; i < 2; i++) {
                matricula += String.valueOf(Utilidades.generarAleatorio(0, 10));
            }
        }
        return matricula;
    }

    public static String generarNombreAleatorio(TIPO_BARCO tipoBarco) {
        String nombreBarcos = "";
        if (tipoBarco == TIPO_BARCO.CRUCERO) {
            nombreBarcos = listaNombresCruceros[Utilidades.generarAleatorio(0, listaNombresCruceros.length)];
        } else {
            nombreBarcos = listaNombresVeleros[Utilidades.generarAleatorio(0, listaNombresVeleros.length)];
        }
        return nombreBarcos;
    }

    public boolean getSeEncuentraEnRio() {
        return seEncuentraEnRio;
    }

    public ESTADO_BARCO getEstadoBarco() {
        return estadoBarco;
    }

    public String getEstadoHilo() {
        return hilo.getState().toString();
    }

    public int getVelocidad() {
        return velocidad;
    }

    public Rectangle getRectanguloColisiones() {
        return rectanguloColisiones;
    }

    public void setDetectadaColision(boolean colision, Barco barco) {
        detectadaColision = colision;
    }

    public boolean getDetectadaColision() {
        return detectadaColision;
    }
}
