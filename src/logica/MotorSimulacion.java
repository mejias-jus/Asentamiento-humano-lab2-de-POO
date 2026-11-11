package logica;

import java.util.ArrayList;
import java.util.Random;
import modelo.Agricultor;
import modelo.Aldea;
import modelo.Colono;
import modelo.EscasezRecursos;
import modelo.Evento;
import modelo.Familia;
import modelo.Ingeniero;
import modelo.Plaga;
import modelo.Purificador;
import modelo.Tormenta;

/**
 * Capa de logica de negocio: controla los turnos, los recursos criticos,
 * los eventos aleatorios y el bienestar familiar.
 */
public class MotorSimulacion {

    public static final int TURNOS_MAX = 15;
    public static final int NUM_FAMILIAS = 6;

    private Aldea aldea;
    private Random azar = new Random();
    private Evento[] eventos = {new Tormenta(), new EscasezRecursos(), new Plaga()};
    private String mensaje = "";

    public MotorSimulacion() {
        aldea = crearAldeaNueva();
    }

    public Aldea getAldea() {
        return aldea;
    }

    public void setAldea(Aldea aldea) {
        this.aldea = aldea;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void nuevaSimulacion() {
        aldea = crearAldeaNueva();
    }

    public static String[] getDecisiones() {
        return new String[]{
            "Seguir la rutina (sin costo)",
            "Jornada intensiva (+productividad, -felicidad)",
            "Raciones extra (-8 raciones; +salud, +felicidad)",
            "Organizar festival (-5 raciones, -5 agua; +felicidad)",
            "Campaña de salud (-5 agua, -5 energía; +salud)",
            "Capacitación (-6 energía; +productividad)"
        };
    }

    private Aldea crearAldeaNueva() {
        Aldea nueva = new Aldea("Nueva Esperanza");
        String[] apellidos = {"Rojas", "Vargas", "Mora", "Castro", "Solano", "Jiménez"};
        String[] nombres = {"Ana", "Luis", "Marta", "Pedro", "Sofía", "Diego",
            "Lucía", "Mario", "Elena", "Andrés", "Rosa", "Tomás"};
        int posicion = 0;
        for (int i = 0; i < NUM_FAMILIAS; i++) {
            Familia familia = new Familia("Familia " + apellidos[i]);
            for (int j = 0; j < 2; j++) {
                int salud = 60 + azar.nextInt(21);
                int felicidad = 50 + azar.nextInt(21);
                int productividad = 40 + azar.nextInt(21);
                String nombre = nombres[posicion];
                Colono colono;
                if (posicion % 3 == 0) {
                    colono = new Agricultor(nombre, salud, felicidad, productividad);
                } else if (posicion % 3 == 1) {
                    colono = new Purificador(nombre, salud, felicidad, productividad);
                } else {
                    colono = new Ingeniero(nombre, salud, felicidad, productividad);
                }
                familia.agregarColono(colono);
                posicion++;
            }
            nueva.agregarFamilia(familia);
        }
        nueva.agregarEvento("La aldea " + nueva.getNombre() + " fue fundada con 6 familias.");
        return nueva;
    }

    public boolean estaTerminada() {
        return aldea.getTurno() > TURNOS_MAX || aldea.getBienestarComunidad() <= 15;
    }

    public String getMotivoFin() {
        if (aldea.getTurno() > TURNOS_MAX) {
            return "Se completaron los " + TURNOS_MAX + " turnos: la aldea sobrevivió.";
        }
        if (aldea.getBienestarComunidad() <= 15) {
            return "La aldea colapsó: el bienestar de la comunidad es demasiado bajo.";
        }
        return "Simulación finalizada por el administrador en el turno " + aldea.getTurno() + ".";
    }

    // Ejecuta un turno completo. Devuelve false si no se pudo (mira getMensaje()).
    public boolean ejecutarTurno(int decision) {
        if (estaTerminada()) {
            mensaje = "La simulación ya terminó.";
            return false;
        }
        if (!aplicarDecision(decision)) {
            return false;
        }
        producir();
        consumir();
        eventoAleatorio();
        aldea.setTurno(aldea.getTurno() + 1);
        return true;
    }

    private boolean aplicarDecision(int decision) {
        mensaje = "";
        if (decision == 1) {
            aldea.cambiarEstadoTodos(0, -5, 8);
            aldea.agregarEvento("Decisión: jornada intensiva.");
        } else if (decision == 2) {
            if (aldea.getRaciones() < 8) {
                mensaje = "No hay suficientes raciones para esa decisión.";
                return false;
            }
            aldea.cambiarRaciones(-8);
            aldea.cambiarEstadoTodos(6, 4, 0);
            aldea.agregarEvento("Decisión: se repartieron raciones extra.");
        } else if (decision == 3) {
            if (aldea.getRaciones() < 5 || aldea.getAgua() < 5) {
                mensaje = "No hay suficientes raciones o agua para el festival.";
                return false;
            }
            aldea.cambiarRaciones(-5);
            aldea.cambiarAgua(-5);
            aldea.cambiarEstadoTodos(0, 10, 0);
            aldea.agregarEvento("Decisión: se organizó un festival.");
        } else if (decision == 4) {
            if (aldea.getAgua() < 5 || aldea.getEnergia() < 5) {
                mensaje = "No hay suficiente agua o energía para la campaña de salud.";
                return false;
            }
            aldea.cambiarAgua(-5);
            aldea.cambiarEnergia(-5);
            aldea.cambiarEstadoTodos(10, 0, 0);
            aldea.agregarEvento("Decisión: campaña de salud.");
        } else if (decision == 5) {
            if (aldea.getEnergia() < 6) {
                mensaje = "No hay suficiente energía para la capacitación.";
                return false;
            }
            aldea.cambiarEnergia(-6);
            aldea.cambiarEstadoTodos(0, 0, 8);
            aldea.agregarEvento("Decisión: capacitación de los colonos.");
        } else {
            aldea.agregarEvento("Decisión: seguir la rutina.");
        }
        return true;
    }

    private void producir() {
        int energiaAntes = aldea.getEnergia();
        int racionesAntes = aldea.getRaciones();
        int aguaAntes = aldea.getAgua();
        for (Colono c : aldea.getTodosLosColonos()) {
            c.trabajar(aldea); // polimorfismo: cada tipo produce algo distinto
        }
        aldea.agregarEvento("Producción: energía +" + (aldea.getEnergia() - energiaAntes)
                + ", raciones +" + (aldea.getRaciones() - racionesAntes)
                + ", agua +" + (aldea.getAgua() - aguaAntes) + ".");
    }

    private void consumir() {
        int necesario = aldea.getTotalColonos();
        boolean falta = false;
        if (aldea.getEnergia() < necesario) {
            aldea.cambiarEnergia(-aldea.getEnergia());
            aldea.cambiarEstadoTodos(-8, -6, 0);
            aldea.agregarEvento("¡Falta de energía! La salud y la felicidad bajan.");
            falta = true;
        } else {
            aldea.cambiarEnergia(-necesario);
        }
        if (aldea.getRaciones() < necesario) {
            aldea.cambiarRaciones(-aldea.getRaciones());
            aldea.cambiarEstadoTodos(-8, -6, 0);
            aldea.agregarEvento("¡Hambre! No alcanzaron las raciones.");
            falta = true;
        } else {
            aldea.cambiarRaciones(-necesario);
        }
        if (aldea.getAgua() < necesario) {
            aldea.cambiarAgua(-aldea.getAgua());
            aldea.cambiarEstadoTodos(-8, -6, 0);
            aldea.agregarEvento("¡Sed! No alcanzó el agua purificada.");
            falta = true;
        } else {
            aldea.cambiarAgua(-necesario);
        }
        if (!falta) {
            aldea.cambiarEstadoTodos(1, 0, 0);
        }
    }

    private void eventoAleatorio() {
        if (azar.nextInt(100) < 60) {
            Evento evento = eventos[azar.nextInt(eventos.length)];
            aldea.agregarEvento(evento.aplicar(aldea)); // polimorfismo
        } else {
            aldea.agregarEvento("Turno tranquilo, sin eventos inesperados.");
        }
    }
}
