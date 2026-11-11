package modelo;

import java.util.Random;

/**
 * Evento inesperado. Cada evento hijo redefine aplicar() (polimorfismo).
 */
public abstract class Evento {

    private String nombre;
    protected Random azar = new Random();

    public Evento(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    // Aplica el efecto a la aldea y devuelve un texto para el registro
    public abstract String aplicar(Aldea aldea);
}
