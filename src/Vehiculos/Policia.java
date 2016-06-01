package Vehiculos;

import Escenario.PuenteControlador;
import Utils.Utilidades;
import java.applet.Applet;
import java.awt.Image;

/**
 * Examen Servicios y procesos - 1º Evaluación
 * -----------------------------------------
 * @author Rubén
 */
public class Policia extends Coche{
    
    private String numPatrulla;
    private String nomUnidad;
    private String[] listaNomUnidad = {"TANGO", "ALFA", "BETA", "ROMA", "WHISKEY", "SALSA", "AGUACATE"};

    public Policia(int x, int y, Image imagenVehiculo, Applet escenario, PuenteControlador puenteControlador) {
        super(TIPO_COCHE.POLICIA, Coche.generarMatriculaCoche(Coche.TIPO_COCHE.POLICIA), x, y, imagenVehiculo, escenario, puenteControlador);
        this.numPatrulla = generarSeleccionAleatorio();
        this.nomUnidad = generarSeleccionAleatorio(listaNomUnidad);
    }

    public String getNumPatrulla(){
        return numPatrulla;
    }
    
    public String getNomUnidad(){
        return nomUnidad;
    }
    
    private String generarSeleccionAleatorio() {
        return "UP" + Utilidades.generarAleatorio(0, 10) + Utilidades.generarAleatorio(0, 10);
    }

    private String generarSeleccionAleatorio(String[] lista) {
        return lista[Utilidades.generarAleatorio(0, lista.length)];
    }
    
}
