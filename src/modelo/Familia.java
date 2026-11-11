package modelo;

import java.util.ArrayList;

public class Familia {

    private String nombre;
    private ArrayList<Colono> colonos = new ArrayList<Colono>();

    public Familia(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public ArrayList<Colono> getColonos() {
        return colonos;
    }

    public void agregarColono(Colono colono) {
        colonos.add(colono);
    }

    public int getPromedioSalud() {
        if (colonos.isEmpty()) {
            return 0;
        }
        int suma = 0;
        for (Colono c : colonos) {
            suma += c.getSalud();
        }
        return suma / colonos.size();
    }

    public int getPromedioFelicidad() {
        if (colonos.isEmpty()) {
            return 0;
        }
        int suma = 0;
        for (Colono c : colonos) {
            suma += c.getFelicidad();
        }
        return suma / colonos.size();
    }

    public int getPromedioProductividad() {
        if (colonos.isEmpty()) {
            return 0;
        }
        int suma = 0;
        for (Colono c : colonos) {
            suma += c.getProductividad();
        }
        return suma / colonos.size();
    }

    // Bienestar familiar integral: promedio de felicidad, salud y productividad
    public int getBienestar() {
        return (getPromedioFelicidad() + getPromedioSalud() + getPromedioProductividad()) / 3;
    }
}
