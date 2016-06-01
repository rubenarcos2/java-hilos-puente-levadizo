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
public class Particular extends Coche {

    private String marca, modelo, color;
    private String[] listaColores = {"Rojo", "Amarillo", "Verde", "Azul", "Plateado", "Negro", "Blanco"};
    private String[][] listaMarcasModelos = {{"Citröen", "C1", "C2", "C3", "C4", "C5", "C6", "C8"},
    {"Peugeot", "108", "208", "308", "408", "608"},
    {"Renault", "Megane", "Laguna", "Scenic"},
    {"Ford", "KA", "Fiesta", "Mondeo", "Focus"},
    {"Audi", "A5", "A6", "A8"},
    {"Mercedes-Benz", "Clase A", "Clase B", "Clase C"},
    {"Volkswagen", "Polo", "Golf", "Passat"},
    {"Ferrari", "F40", "California", "458 Italia", "Enzo", "F430", "Testarossa", "488 GTB"},
    {"Lamborghini", "Murciélago", "Veneno", "Aventador", "Diablo"}};

    public Particular(int x, int y, Image imagenVehiculo, Applet escenario, PuenteControlador puenteControlador) {
        super(TIPO_COCHE.PARTICULAR, Coche.generarMatriculaCoche(Coche.TIPO_COCHE.PARTICULAR), x, y, imagenVehiculo, escenario, puenteControlador);
        generarSeleccionAleatorio(listaMarcasModelos);
        generarSeleccionAleatorio(listaColores);
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public String getColor() {
        return color;
    }

    private void generarSeleccionAleatorio(String[] lista) {
        color = lista[Utilidades.generarAleatorio(0, lista.length)];
    }

    private void generarSeleccionAleatorio(String[][] lista) {
        int posMarca = Utilidades.generarAleatorio(0, lista.length);
        marca = lista[posMarca][0];
        modelo = lista[posMarca][Utilidades.generarAleatorio(1, lista[posMarca].length)];
    }
}
