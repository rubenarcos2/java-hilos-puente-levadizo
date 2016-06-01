package Vehiculos;

import Escenario.PuenteControlador;
import Utils.Utilidades;
import java.applet.Applet;
import java.awt.Image;

/**
 * Examen Servicios y procesos - 1º Evaluación
 * -----------------------------------------
 *
 * @author Rubén
 */
public class Velero extends Barco {

    private int metrosEslora;
    public int velocidad;

    public Velero(int x, int y, Image imagenVehiculo, Applet escenario, PuenteControlador puenteControlador) {
        super(Barco.TIPO_BARCO.VELERO, Barco.generarNombreAleatorio(Barco.TIPO_BARCO.VELERO), Barco.generarMatriculaBarco(Barco.TIPO_BARCO.VELERO), x, y, imagenVehiculo, escenario, puenteControlador);
        this.metrosEslora = generarSeleccionAleatorio();
    }

    public int getMetrosEslora() {
        return metrosEslora;
    }

    private int generarSeleccionAleatorio() {
        String numPasajero = "";
        int numPas = 0;
        for (int i = 0; i < 2; i++) {
            numPasajero += Utilidades.generarAleatorio(0, 10);
        }
        try {
            numPas = Integer.valueOf(numPasajero);
        } catch (NumberFormatException e) {
            System.out.println("Error en la conversión de los metros de eslora del velero: " + e);
        }
        return numPas;
    }
}
