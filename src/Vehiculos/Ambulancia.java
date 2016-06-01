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
public class Ambulancia extends Coche {

    private int numAmbulancia;
    private String hospital;
    private String[] listaHospitales = {"Complejo Hospitalario Público Regional de Málaga (Antiguo Carlos de Haya)",
                                        "Hospital Universitario Virgen de la Victoria",
                                        "Hospital Civil",
                                        "Hospital Materno-Infantil"};

    public Ambulancia(int x, int y, Image imagenVehiculo, Applet escenario, PuenteControlador puenteControlador) {
        super(TIPO_COCHE.AMBULANCIA, Coche.generarMatriculaCoche(Coche.TIPO_COCHE.AMBULANCIA), x, y, imagenVehiculo, escenario, puenteControlador);
        this.numAmbulancia = generarSeleccionAleatorio();
        this.hospital = generarSeleccionAleatorio(listaHospitales);
    }

    public int getNumAmbulancia(){
        return numAmbulancia;
    }
    
    public String getHospital(){
        return hospital;
    }
    
    private String generarSeleccionAleatorio(String[] lista) {
        return lista[Utilidades.generarAleatorio(0, lista.length)];
    }

    private int generarSeleccionAleatorio() {
        String numAmbul= "";
        int numAmb = 0;
        for (int i = 0; i < 3; i++) {
            numAmbul += Utilidades.generarAleatorio(0, 10);
        }
        try {
            numAmb = Integer.valueOf(numAmbul);
        } catch (NumberFormatException e) {
            System.out.println("Error en la conversión de la matrícula de la ambulancia: " + e);
        }
        return numAmb;
    }

}
