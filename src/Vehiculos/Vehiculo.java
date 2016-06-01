package Vehiculos;

import java.awt.Image;

/**
 * Examen Servicios y procesos - 1º Evaluación
 * -----------------------------------------
 *
 * @author Rubén
 */
public class Vehiculo {

    private String matricula;
    protected int x, y;
    protected Image imagenVehiculo;

    public Vehiculo() {
        this.imagenVehiculo = null;
        matricula = "";
        x = 0;
        y = 0;
    }

    public Vehiculo(String matricula, int x, int y, Image imagenVehiculo) {
        this.imagenVehiculo = imagenVehiculo;
        this.matricula = matricula;
        this.x = x;
        this.y = y;
    }
    
    public String getMatricula() {
        return matricula;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
