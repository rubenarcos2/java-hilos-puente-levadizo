
import Escenario.Puente;
import Escenario.PuenteControlador;
import Utils.Utilidades;
import Vehiculos.Ambulancia;
import Vehiculos.Barco;
import Vehiculos.Coche;
import Vehiculos.Crucero;
import Vehiculos.Particular;
import Vehiculos.Policia;
import Vehiculos.Velero;
import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.Timer;

/**
 * Examen Servicios y procesos - 1º Evaluación
 * -----------------------------------------
 *
 * @author Rubén
 */
public class Main extends Applet implements ActionListener {

    private static final int ANCHOPANTALLA = 800;
    private static final int ALTOPANTALLA = 600;
    private BufferedImage fondoPantalla;
    private URL urlFondoPantalla, urlImagenCoche, urlImagenCoche2, urlImagenPolicia, urlImagenAmbulancia, urlImagenCrucero, urlImagenVelero, urlHistoricoUsoPuente;
    private Graphics2D pantalla;
    private Image offscreen, imagenCoche, imagenCoche2, imagenPolicia, imagenAmbulancia, imagenCrucero, imagenVelero, imagenHistoricoUsoPuente;
    private Puente puente;
    private PuenteControlador puenteControlador;
    private int posCarrilDerecho, posCarrilIzquierdo;
    private int[] datosUsoPuente;
    private ArrayList<Coche> listaCoches;
    private ArrayList<Barco> listaBarcos;
    private Timer temporizador;

    @Override
    public void init() {
        setSize(ANCHOPANTALLA, ALTOPANTALLA);
        //Relacionado con el doble buffer
        offscreen = createImage(getWidth(), getHeight());
        pantalla = (Graphics2D) offscreen.getGraphics();
        //Imágenes y rutas de imágenes
        try {
            urlFondoPantalla = new URL(getCodeBase(), "imagenes/FondoSuperpuesto.png");
            fondoPantalla = ImageIO.read(urlFondoPantalla);
            urlImagenCoche = new URL(getCodeBase(), "imagenes/CocheAzul.png");
            imagenCoche = ImageIO.read(urlImagenCoche);
            urlImagenCoche2 = new URL(getCodeBase(), "imagenes/CocheRojo.png");
            imagenCoche2 = ImageIO.read(urlImagenCoche2);
            urlImagenPolicia = new URL(getCodeBase(), "imagenes/PoliciaArriba.png");
            imagenPolicia = ImageIO.read(urlImagenPolicia);
            urlImagenAmbulancia = new URL(getCodeBase(), "imagenes/AmbulanciaArriba.png");
            imagenAmbulancia = ImageIO.read(urlImagenAmbulancia);
            urlImagenCrucero = new URL(getCodeBase(), "imagenes/cruceroVerticalPeq.png");
            imagenCrucero = ImageIO.read(urlImagenCrucero);
            urlImagenVelero = new URL(getCodeBase(), "imagenes/VeleroVertical.png");
            imagenVelero = ImageIO.read(urlImagenVelero);
            urlHistoricoUsoPuente = new URL(getCodeBase(), "imagenes/HistoricoUsoPuente.png");
            imagenHistoricoUsoPuente = ImageIO.read(urlHistoricoUsoPuente);
        } catch (MalformedURLException ex) {
            System.out.println("[LOG] - Error en la ruta de la imagen: " + ex);
        } catch (IOException ex) {
            System.out.println("[LOG] - Error en la ruta de la imagen: " + ex);
        }
        posCarrilDerecho = 980;
        posCarrilIzquierdo = 810;
        temporizador = new Timer(10, this);
        temporizador.setInitialDelay(1000);
        //Hace que aunque se colapse la aplicación, no sufra retraso.
        //temporizador.setCoalesce(true);
        listaCoches = new ArrayList<>();
        listaBarcos = new ArrayList<>();
        datosUsoPuente = new int[3];
    }

    @Override
    public void start() {
        puente = new Puente(this);
        puenteControlador = new PuenteControlador(puente, listaCoches, listaBarcos);
        temporizador.start();
    }

    @Override
    public void stop() {
        temporizador.stop();
    }

    @Override
    public void update(Graphics g) {
        paint(g);
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            System.out.println("ERROR en los FPS" + ex);
        }
    }

    @Override
    public void paint(Graphics g) {
        pantalla = (Graphics2D) g;
        pantalla.drawImage(fondoPantalla, 0, 0, this);
        for (int i = 0; i < listaBarcos.size(); i++) {
            if (!listaBarcos.get(i).getSeEncuentraEnRio()) {
                listaBarcos.remove(i);
            } else {
                listaBarcos.get(i).paint(pantalla);
            }
        }
        puente.paint(pantalla);
        for (int i = 0; i < listaCoches.size(); i++) {
            if (!listaCoches.get(i).getSeEncuentraEnPuente()) {
                listaCoches.remove(i);
            } else {
                listaCoches.get(i).paint(pantalla);
            }
        }
        dibujarHistoricoUsoPuente(pantalla);
        pantalla.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponents(pantalla);
    }

    private void crearCoches() {
        datosUsoPuente[1]++;//Datos del histórico uso puente
        int opcion = Utilidades.generarAleatorio(0, 6); //3 particulares de cada 5 coches
        if (opcion < 4) {
            listaCoches.add(new Particular(0, posCarrilDerecho, imagenCoche, this, puenteControlador));
        } else if (opcion == 4) {
            listaCoches.add(new Policia(0, posCarrilIzquierdo, imagenPolicia, this, puenteControlador));
        } else if (opcion == 5) {
            listaCoches.add(new Ambulancia(0, posCarrilIzquierdo, imagenAmbulancia, this, puenteControlador));
        }
        imprimirDatosCoches(listaCoches.get(listaCoches.size() - 1));
    }

    private void crearBarcos() {
        datosUsoPuente[2]++;//Datos del histórico uso puente
        int opcion = Utilidades.generarAleatorio(0, 4);//2 velerps de cada 3 barcos
        if (opcion < 3) {
            listaBarcos.add(new Velero(getWidth() + imagenVelero.getWidth(this) * 2 + 50, 0, imagenVelero, this, puenteControlador));
        } else if (opcion == 3) {
            listaBarcos.add(new Crucero(getWidth() + imagenCrucero.getWidth(this) * 2 + 50, 0, imagenCrucero, this, puenteControlador));
        }
        imprimirDatosBarcos(listaBarcos.get(listaBarcos.size() - 1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!puenteControlador.getPararCrearCoches()) {
            crearCoches();
        }
        crearBarcos();
        temporizador.stop();
        temporizador.setInitialDelay(Utilidades.generarAleatorio(3000, 5000));
        temporizador.start();
    }

    private void dibujarHistoricoUsoPuente(Graphics2D pantalla) {
        datosUsoPuente[0] = puente.getDatosPuente();
        pantalla.setColor(Color.BLACK);
        pantalla.drawImage(imagenHistoricoUsoPuente, getWidth() - imagenHistoricoUsoPuente.getWidth(this), getHeight() - imagenHistoricoUsoPuente.getHeight(this), this);
        for (int i = 0; i < datosUsoPuente.length; i++) {
            pantalla.drawString(String.valueOf(datosUsoPuente[i]), getWidth() - 60, getHeight() - (70 + (i * 30)));
        }

    }

    private void imprimirDatosCoches(Coche coche) {
        switch (coche.getTipoCoche()) {
            case POLICIA:
                Policia policia = (Policia) coche;
                System.out.println("----------------------------------------");
                System.out.println("########### Coche de policía ###########");
                System.out.println(" Matrícula..........: " + policia.getMatricula());
                System.out.println(" Nº de patrulla.....: " + policia.getNumPatrulla());
                System.out.println(" Nombre de la unidad: " + policia.getNomUnidad());
                System.out.println("----------------------------------------\n");
                break;
            case AMBULANCIA:
                Ambulancia ambulancia = (Ambulancia) coche;
                System.out.println("----------------------------------------");
                System.out.println("####### Ambulancia de emergencia #######");
                System.out.println(" Matrícula..........: " + ambulancia.getMatricula());
                System.out.println(" Nº de ambulancia...: " + ambulancia.getNumAmbulancia());
                System.out.println(" Nombre del hospital: " + ambulancia.getHospital());
                System.out.println("----------------------------------------\n");
                break;
            case PARTICULAR:
                Particular particular = (Particular) coche;
                System.out.println("----------------------------------------");
                System.out.println("########### Coche particular ###########");
                System.out.println(" Matrícula: " + particular.getMatricula());
                System.out.println(" Marca....: " + particular.getMarca());
                System.out.println(" Modelo...: " + particular.getModelo());
                System.out.println(" Color....: " + particular.getColor());
                System.out.println("----------------------------------------\n");
        }

    }

    private void imprimirDatosBarcos(Barco barco) {
        switch (barco.getTipoBarco()) {
            case CRUCERO:
                Crucero crucero = (Crucero) barco;
                System.out.println("----------------------------------------");
                System.out.println("############### Crucero ################");
                System.out.println(" Matrícula...........: " + crucero.getMatricula());
                System.out.println(" Nombre crucero......: " + crucero.getNombre());
                System.out.println(" Naviera.............: " + crucero.getNaviera());
                System.out.println(" País de origen......: " + crucero.getPais());
                System.out.println(" Número de pasajeros.: " + crucero.getNumPasajero());
                System.out.println("----------------------------------------\n");
                break;
            case VELERO:
                Velero velero = (Velero) barco;
                System.out.println("----------------------------------------");
                System.out.println("############### Velero #################");
                System.out.println(" Matrícula........: " + velero.getMatricula());
                System.out.println(" Nombre velero....: " + velero.getNombre());
                System.out.println(" País de origen...: " + velero.getPais());
                System.out.println(" Metros de eslora.: " + velero.getMetrosEslora());
                System.out.println("----------------------------------------\n");
                break;
        }
    }
}
