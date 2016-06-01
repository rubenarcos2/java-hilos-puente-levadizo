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
public class Crucero extends Barco {

    private int numPasajeros;
    private String naviera;
    //http://www.cruceros.es/Naviera/
    private String[] listaNavieras = {"AIDA Cruises", "Azamara Club Cruises", "Carnival Cruise Lines", "Celebrity Cruises", "Club Med Cruises", "Costa Cruceros", "Croisi Europe", "Crystal Cruises", "Cunard", "DERTOUR Cruceros", "Disney Cruise Line", "Hapag-Lloyd Cruises", "Holland America", "Hurtigruten", "MSC Cruceros", "Nicko Cruises", "Oceania Cruises", "P&O Cruises", "Panavision", "Plantours Kreuzfahrten", "Politours", "Ponant", "Princess Cruises", "Pullmantur", "Regent Seven Seas Cruises", "Royal Caribbean International", "Sea Cloud Cruises", "Seabourn", "SeaDream Yacht Club", "Silversea Cruises", "Star Clippers", "TUI Cruises", "Windstar Cruises"};

    public Crucero(int x, int y, Image imagenVehiculo, Applet escenario, PuenteControlador puenteControlador) {
        super(TIPO_BARCO.CRUCERO, Barco.generarNombreAleatorio(TIPO_BARCO.CRUCERO), Barco.generarMatriculaBarco(TIPO_BARCO.CRUCERO), x, y, imagenVehiculo, escenario, puenteControlador);
        this.numPasajeros = generarSeleccionAleatorio();
        naviera = generarSeleccionAleatorio(listaNavieras);
    }

    public int getNumPasajero() {
        return numPasajeros;
    }

    public String getNaviera() {
        return naviera;
    }

    private int generarSeleccionAleatorio() {
        String numPasajero = "";
        int numPas = 0;
        for (int i = 0; i < 4; i++) {
            numPasajero += Utilidades.generarAleatorio(0, 10);
        }
        try {
            numPas = Integer.valueOf(numPasajero);
        } catch (NumberFormatException e) {
            System.out.println("Error en la conversión del número de pasajeros del crucero: " + e);
        }
        return numPas;
    }

    private String generarSeleccionAleatorio(String[] lista) {
        return lista[Utilidades.generarAleatorio(0, lista.length)];
    }
}
