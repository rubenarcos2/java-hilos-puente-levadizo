package Escenario;

import java.applet.Applet;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Examen Servicios y procesos - 1º Evaluación
 * -----------------------------------------
 *
 * @author Rubén
 */
public class Puente extends Thread implements Runnable {

    public enum ESTADO_PUENTE {

        BAJADO, BAJANDO, SUBIENDO, SUBIDO
    };
    private ESTADO_PUENTE estadoPuente, ultimoEstado;
    private Applet escenario;
    private Thread hilo;
    private Image tramoFijoIzq, tramoMovilIzq, tramoMovilDer, tramoFijoDer, semaforoAbierto, semaforoCerrado, barreraIzq, barreraIzqAbierta;
    private URL urlTramoFijoIzq, urlTramoMovilIzq, urlTramoMovilDer, urlTramoFijoDer, urlSemaforoAbierto, urlSemaforoCerrado, urlBarreraIzq, urlBarreraIzqAbierta;
    private int angulo, velocidad, numSubidasPuente;
    private boolean semaforo;

    public Puente(Applet escenario) {
        try {
            this.escenario = escenario;
            urlTramoFijoIzq = new URL(escenario.getCodeBase(), "imagenes/TramoFijoIzq.png");
            urlTramoMovilIzq = new URL(escenario.getCodeBase(), "imagenes/TramoMovilIzqTrans.png");
            urlTramoMovilDer = new URL(escenario.getCodeBase(), "imagenes/TramoMovilDerTrans.png");
            urlTramoFijoIzq = new URL(escenario.getCodeBase(), "imagenes/TramoFijoIzq.png");
            urlTramoFijoDer = new URL(escenario.getCodeBase(), "imagenes/TramoFijoDer.png");
            urlSemaforoAbierto = new URL(escenario.getCodeBase(), "imagenes/SemaforoAbierto.png");
            urlSemaforoCerrado = new URL(escenario.getCodeBase(), "imagenes/SemaforoCerrado.png");
            urlBarreraIzq = new URL(escenario.getCodeBase(), "imagenes/BarreraIzq.png");
            urlBarreraIzqAbierta = new URL(escenario.getCodeBase(), "imagenes/BarreraIzqAbierta.png");
            tramoFijoIzq = ImageIO.read(urlTramoFijoIzq);
            tramoMovilIzq = ImageIO.read(urlTramoMovilIzq);
            tramoMovilDer = ImageIO.read(urlTramoMovilDer);
            tramoFijoDer = ImageIO.read(urlTramoFijoDer);
            semaforoAbierto = ImageIO.read(urlSemaforoAbierto);
            semaforoCerrado = ImageIO.read(urlSemaforoCerrado);
            barreraIzq = ImageIO.read(urlBarreraIzq);
            barreraIzqAbierta = ImageIO.read(urlBarreraIzqAbierta);
        } catch (MalformedURLException ex) {
            System.out.println("Error en la carga de imágenes: " + ex);
        } catch (IOException ex) {
            System.out.println("Error en el acceso a la imagen: " + ex);
        }
        angulo = 0;
        velocidad = 20;
        numSubidasPuente = 0;
        semaforo = true;
        hilo = new Thread(this, "Puente");
        hilo.start();
        estadoPuente = ESTADO_PUENTE.BAJADO;
    }

    @Override
    public void run() {
        while (true) {
            ultimoEstado = estadoPuente;
            try {
                Thread.sleep(velocidad);
                if (angulo > 0 && angulo <= 70 && estadoPuente == ESTADO_PUENTE.BAJANDO) {
                    angulo--;
                } else if (angulo >= 0 && angulo < 70 && estadoPuente == ESTADO_PUENTE.SUBIENDO) {
                    angulo++;
                } else if (angulo == 0) {
                    estadoPuente = ESTADO_PUENTE.BAJADO;
                    if(ultimoEstado != ESTADO_PUENTE.BAJADO){
                        semaforo = true;
                        numSubidasPuente++;
                    }
                } else if (angulo == 70) {
                    estadoPuente = ESTADO_PUENTE.SUBIDO;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Puente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void paint(Graphics2D g) {
        Graphics2D g2d = (Graphics2D) g;
        dibujarPuente(g2d);
        escenario.repaint();
    }

    public void dibujarPuente(Graphics2D g2d) {
        AffineTransform atTramoFijoIzq = new AffineTransform();
        AffineTransform atTramoFijoDer = new AffineTransform();
        AffineTransform atTramoMovilIzq = new AffineTransform();
        AffineTransform atTramoMovilDer = new AffineTransform();
        atTramoFijoIzq.translate(0, 300 - tramoFijoIzq.getHeight(escenario) / 2); //X: Horizontal / Y: Vertical
        atTramoMovilIzq.translate(0 + tramoFijoIzq.getWidth(escenario), 300 - tramoMovilIzq.getHeight(escenario) / 2); //X: Horizontal / Y: Vertical
        atTramoMovilIzq.rotate(Math.toRadians(-angulo), 0, tramoMovilDer.getWidth(escenario) / 1.5);
        atTramoMovilDer.translate(0 + tramoFijoIzq.getWidth(escenario) + tramoMovilIzq.getWidth(escenario), 300 - tramoMovilDer.getHeight(escenario) / 2); //X: Horizontal / Y: Vertical
        atTramoMovilDer.rotate(Math.toRadians(angulo), tramoMovilDer.getHeight(escenario) * 1.5, tramoMovilDer.getWidth(escenario) / 1.5);
        atTramoFijoDer.translate(tramoFijoIzq.getWidth(escenario) + tramoMovilIzq.getWidth(escenario) + tramoMovilDer.getWidth(escenario), 300 - tramoFijoDer.getHeight(escenario) / 2); //X: Horizontal / Y: Vertical
        g2d.drawImage(tramoMovilIzq, atTramoMovilIzq, escenario);
        g2d.drawImage(tramoMovilDer, atTramoMovilDer, escenario);
        g2d.drawImage(tramoFijoIzq, atTramoFijoIzq, escenario);
        g2d.drawImage(tramoFijoDer, atTramoFijoDer, escenario);
        if (semaforo) {
            g2d.drawImage(semaforoAbierto, 150, 130, escenario);
            g2d.drawImage(barreraIzqAbierta, 150, 355, escenario);
        } else {
            g2d.drawImage(semaforoCerrado, 150, 130, escenario);
            g2d.drawImage(barreraIzq, 150, 298, escenario);
        }

    }
    
    public boolean getSemaforo(){
        return semaforo;
    }
    
    public void setSemaforo(boolean semaforo){
        this.semaforo = semaforo;
    }

    public void setAumentarVelocidad() {
        if (velocidad >= 10) {
            velocidad -= 10;
        }
    }

    public void setDisminuirVelocidad() {
        if (velocidad <= 10000) {
            velocidad += 10;
        }
    }

    public int getDatosPuente() {
        return numSubidasPuente;
    }

    public ESTADO_PUENTE getEstado() {
        return estadoPuente;
    }

    public void setEstado(ESTADO_PUENTE estadoPuente) {
        this.estadoPuente = estadoPuente;
    }
}
