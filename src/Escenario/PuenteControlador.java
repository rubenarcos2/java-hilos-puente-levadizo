package Escenario;

import Vehiculos.Barco;
import Vehiculos.Coche;
import java.util.ArrayList;

/**
 * Examen Servicios y procesos - 1º Evaluación
 * -----------------------------------------
 *
 * @author Rubén
 */
public class PuenteControlador { //PIPE (TUBERÍA)

    private Puente puente;
    private ArrayList<Coche> listaCoches;
    private ArrayList<Barco> listaBarcos;
    private boolean cruceroEsperando, vehiculoPrioritarioEnPuente, apartadoCruzandoPuente;

    public PuenteControlador(Puente puente, ArrayList<Coche> listaCoches, ArrayList<Barco> listaBarcos) {
        this.puente = puente;
        this.listaCoches = listaCoches;
        this.listaBarcos = listaBarcos;
        cruceroEsperando = false;
        vehiculoPrioritarioEnPuente = false;
        apartadoCruzandoPuente = false;
    }

    public synchronized void solicitarPasoPuenteBarco(Barco barco) {

        //0-BARCO) Detector de petición de crucero para pasar el puente.
        if (barco.getTipoBarco() == Barco.TIPO_BARCO.CRUCERO
                //&& barco.getEstadoBarco() != Barco.ESTADO_BARCO.NAVEGANDO_DESPUES) {
                && barco.getEstadoBarco() == Barco.ESTADO_BARCO.NAVEGANDO_ANTES) {
            puente.setSemaforo(false);
            cruceroEsperando = true;
        } else if (barco.getTipoBarco() == Barco.TIPO_BARCO.CRUCERO
                && barco.getEstadoBarco() == Barco.ESTADO_BARCO.NAVEGANDO_DESPUES
                && puente.getEstado() == Puente.ESTADO_PUENTE.BAJADO) {
            cruceroEsperando = false;
        }

        //1-BARCO) Si es crucero, hay coches en el puente, esta bajado y el barco no ha cruzado está delante del puente, se pone semáforo en rojo y se espera.
        if (barco.getTipoBarco() == Barco.TIPO_BARCO.CRUCERO
                && puente.getEstado() != Puente.ESTADO_PUENTE.SUBIDO
                && getCochesCruzandoPuente()
                && barco.getEstadoBarco() == Barco.ESTADO_BARCO.NAVEGANDO_ANTES) {
            puente.setSemaforo(false);
            try {
                wait();
            } catch (InterruptedException ex) {
                System.out.println("ERROR en la parada del hilo: " + ex);
            }
            //2-BARCO) Subir el puente con el semáforo rojo, sin coches cruzando (estan antes del puente) y con el crucero a la espera.
        } else if (barco.getTipoBarco() == Barco.TIPO_BARCO.CRUCERO
                && barco.getEstadoBarco() != Barco.ESTADO_BARCO.NAVEGANDO_DESPUES
                && !getCochesCruzandoPuente()
                && !puente.getSemaforo()
                && cruceroEsperando
                && !vehiculoPrioritarioEnPuente) {
            puente.setEstado(Puente.ESTADO_PUENTE.SUBIENDO);
            //3.0-BARCO) Baja el puente cuando ha pasado el crucero
        } else if (barco.getTipoBarco() == Barco.TIPO_BARCO.CRUCERO
                && barco.getEstadoBarco() == Barco.ESTADO_BARCO.NAVEGANDO_DESPUES
                && puente.getEstado() == Puente.ESTADO_PUENTE.SUBIDO //&& !cruceroEsperando
                ) {
            puente.setEstado(Puente.ESTADO_PUENTE.BAJANDO);
            notifyAll();
            //3.1-BARCO) en caso de bajar el puente tras pasar crucero, reactivo los coches prioritarios que estaban esperando
        } else if (barco.getTipoBarco() == Barco.TIPO_BARCO.CRUCERO
                && puente.getEstado() == Puente.ESTADO_PUENTE.BAJADO
                //&& puente.getSemaforo()
                && barco.getEstadoBarco() == Barco.ESTADO_BARCO.NAVEGANDO_DESPUES) {
            notifyAll();
        }
    }

    public synchronized void solicitarPasoPuenteCoche(Coche coche) {
        //0.1-COCHE) Detector de vehículo prioritario en puente.
        if (coche.getTipoCoche() != Coche.TIPO_COCHE.PARTICULAR) {
            vehiculoPrioritarioEnPuente = true;
        }

        //1-COCHE) Si coche particular está antes del puente y el semáforo cerrado, el coche se para
        if (coche.getTipoCoche() == Coche.TIPO_COCHE.PARTICULAR
                && coche.getEstado() == Coche.ESTADO_COCHE.CIRCULANDO_ANTES
                && !puente.getSemaforo()) {
            try {
                wait();
            } catch (InterruptedException ex) {
                System.out.println("ERROR en la parada del hilo: " + ex);
            }
            //1.1-COCHE) Si coche NO particular está antes del puente y el semáforo cerrado, y el puente levantado, el coche se para
        } else if (coche.getTipoCoche() != Coche.TIPO_COCHE.PARTICULAR
                && coche.getEstado() == Coche.ESTADO_COCHE.CIRCULANDO_ANTES
                && puente.getEstado() != Puente.ESTADO_PUENTE.BAJADO //&& !puente.getSemaforo() -> como es vehículo prioritario, se salta el semáforo, pero no cuando el puente está abierto
                ) {
            try {
                wait(); //No funciona
            } catch (InterruptedException ex) {
                System.out.println("ERROR en la parada del hilo: " + ex);
            }

            //2-COCHE) Si coche está en cualquier parte del puente y entra un vehículo prioritario se para el coche particular
        } else if (coche.getTipoCoche() == Coche.TIPO_COCHE.PARTICULAR
                && vehiculoPrioritarioEnPuente
                && !coche.getDetectadaColision()) { //Si el coche está adelantando, no se para, para que él y el prioritario continuen por el carril izquierdo.
            if (coche.getEstado() == Coche.ESTADO_COCHE.APARTADO) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    System.out.println("ERROR en la parada del hilo: " + ex);
                }
            } else if (coche.getEstado() != Coche.ESTADO_COCHE.APARTANDOSE) {
                //En el caso de que se quede en el medio, no dispongo de doble estado, apartándose y en cruzando el puente.
                if (coche.getEstado() == Coche.ESTADO_COCHE.CRUZANDO_PUENTE) {
                    apartadoCruzandoPuente = true;
                }
                coche.setEstado(Coche.ESTADO_COCHE.APARTANDOSE);
            }
            //2.1-COCHE) Cuando se ha apartado y se ha ido el cochePrioritario, vuelve a la carretera
        } else if (coche.getTipoCoche() == Coche.TIPO_COCHE.PARTICULAR
                && coche.getEstado() == Coche.ESTADO_COCHE.APARTADO
                && !vehiculoPrioritarioEnPuente) {
            //No impora el estado, solo que NO se apartado o apartándose (a la siguiente consulta, detecta posición y se pone en el correcto)
            coche.setEstado(Coche.ESTADO_COCHE.CRUZANDO_PUENTE);
            apartadoCruzandoPuente = false;
            //3-COCHE) Si el vehículo prioritario abandona el puente, se reactivan los coches.
        } else if (coche.getTipoCoche() != Coche.TIPO_COCHE.PARTICULAR
                && vehiculoPrioritarioEnPuente
                && !coche.getSeEncuentraEnPuente()) {
            notifyAll();
            vehiculoPrioritarioEnPuente = false;
            //3.1-COCHE) Si el vehículo particular abandona el puente, se reactiva el crucer que estaba a la espera.
        } else if (coche.getTipoCoche() == Coche.TIPO_COCHE.PARTICULAR
                && !vehiculoPrioritarioEnPuente
                && !coche.getSeEncuentraEnPuente()
                && cruceroEsperando) {
            notifyAll();
            //4-COCHE) Subir el puente con el semáforo rojo (habiendoselo saltado policia) y que este halla salido del puente.
        } else if (puente.getEstado() == Puente.ESTADO_PUENTE.BAJADO //coche.getTipoCoche() != Coche.TIPO_COCHE.PARTICULAR && 
                && cruceroEsperando
                && !getCochesCruzandoPuente()
                && !vehiculoPrioritarioEnPuente) {
            puente.setEstado(Puente.ESTADO_PUENTE.SUBIENDO);
            notifyAll();
        }
    }

    //COMPROBADOR - Comprueba si hay coches cruzando el puente
    private boolean getCochesCruzandoPuente() {//Devuelve verdedadero cuando hay coches
        int numCochesCruzandoPuente = 0;
        for (int i = 0; i < listaCoches.size(); i++) {
            if (listaCoches.get(i).getEstado() == Coche.ESTADO_COCHE.CRUZANDO_PUENTE) {
                numCochesCruzandoPuente++;
            }
        }
        if (apartadoCruzandoPuente) {
            numCochesCruzandoPuente++;
        }
        if (numCochesCruzandoPuente > 0) {
            return true;
        } else {
            return false;
        }
    }

    //COMPROBADOR - Detiene la creación de nuevos hilos, en el caso de que los coches estén parados por un vehículo prioritario o que estén a la espera de pasar un crucero.
    public boolean getPararCrearCoches() {//Devuelve true si hay un vehiculoPrioritario, detiene la creación de coches
        if (vehiculoPrioritarioEnPuente || !puente.getSemaforo()) {
            return true;
        } else {
            return false;
        }
    }

    public void detectorColisionesBarco(Barco barco) {
        //0.1-BARCO) Detector de colisiones, adelante cuando se fuese a producir una colision.
        for (int i = 0; i < listaBarcos.size(); i++) {
            if (listaBarcos.size() > 1) { //No se realiza cuando no halla más de un barco.
                if (!listaBarcos.get(i).equals(barco)) {//ERROR: java.lang.NullPointerException
                    if (barco.getRectanguloColisiones().intersects(listaBarcos.get(i).getRectanguloColisiones())
                            && barco.getDetectadaColision() == false) {
                        barco.setDetectadaColision(true, barco);
                        listaBarcos.get(i).setDetectadaColision(false, listaBarcos.get(i));
                    } else if (barco.getRectanguloColisiones().intersects(listaBarcos.get(i).getRectanguloColisiones())
                            && barco.getDetectadaColision() == true) {
                    }
                }
            }
        }
    }

    public void detectorColisionesCoche(Coche coche) {
        //0-COCHE) Detector de colisiones, adelante cuando se fuese a producir una colision.
        for (int i = 0; i < listaCoches.size(); i++) {
            if (listaCoches.size() > 1) {//Solo se realiza cuando hay más de un coche
                if (!listaCoches.get(i).equals(coche)
                        && listaCoches.get(i).getTipoCoche() == Coche.TIPO_COCHE.PARTICULAR
                        && coche.getTipoCoche() == Coche.TIPO_COCHE.PARTICULAR) {
                    if (coche.getRectanguloColisiones().intersects(listaCoches.get(i).getRectanguloColisiones())
                            && coche.getDetectadaColision() == false) {
                        coche.setDetectadaColision(true, coche);
                        listaCoches.get(i).setDetectadaColision(false, listaCoches.get(i));
                    } else if (coche.getRectanguloColisiones().intersects(listaCoches.get(i).getRectanguloColisiones())
                            && coche.getDetectadaColision() == true) {
                        //coche.setDetectadaColision(false, coche);
                    }
                }
            }
        }
    }
}
