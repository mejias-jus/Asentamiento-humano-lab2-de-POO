package modelo;

import java.util.ArrayList;

public class Aldea {

    public static final int MAX_RECURSO = 100;

    private String nombre;
    private int turno;
    private int energia;
    private int raciones;
    private int agua;
    private ArrayList<Familia> familias = new ArrayList<Familia>();
    private ArrayList<String> historial = new ArrayList<String>();

    public Aldea(String nombre) {
        this.nombre = nombre;
        this.turno = 1;
        this.energia = 60;
        this.raciones = 60;
        this.agua = 60;
    }

    private int limitar(int valor) {
        if (valor < 0) {
            return 0;
        }
        if (valor > MAX_RECURSO) {
            return MAX_RECURSO;
        }
        return valor;
    }

    public String getNombre() {
        return nombre;
    }

    public int getTurno() {
        return turno;
    }

    public void setTurno(int turno) {
        this.turno = turno;
    }

    public int getEnergia() {
        return energia;
    }

    public int getRaciones() {
        return raciones;
    }

    public int getAgua() {
        return agua;
    }

    public void cambiarEnergia(int cambio) {
        energia = limitar(energia + cambio);
    }

    public void cambiarRaciones(int cambio) {
        raciones = limitar(raciones + cambio);
    }

    public void cambiarAgua(int cambio) {
        agua = limitar(agua + cambio);
    }

    // Se usa al cargar desde archivo
    public void setRecursos(int energia, int raciones, int agua) {
        this.energia = limitar(energia);
        this.raciones = limitar(raciones);
        this.agua = limitar(agua);
    }

    public ArrayList<Familia> getFamilias() {
        return familias;
    }

    public ArrayList<String> getHistorial() {
        return historial;
    }

    public void agregarFamilia(Familia familia) {
        familias.add(familia);
    }

    public void agregarEvento(String texto) {
        historial.add("Turno " + turno + ": " + texto);
    }

    public ArrayList<Colono> getTodosLosColonos() {
        ArrayList<Colono> todos = new ArrayList<Colono>();
        for (Familia f : familias) {
            todos.addAll(f.getColonos());
        }
        return todos;
    }

    public int getTotalColonos() {
        return getTodosLosColonos().size();
    }

    // Cambia el estado de todos los colonos de la aldea
    public void cambiarEstadoTodos(int salud, int felicidad, int productividad) {
        for (Colono c : getTodosLosColonos()) {
            c.cambiarSalud(salud);
            c.cambiarFelicidad(felicidad);
            c.cambiarProductividad(productividad);
        }
    }

    public int getPromedioSalud() {
        ArrayList<Colono> todos = getTodosLosColonos();
        if (todos.isEmpty()) {
            return 0;
        }
        int suma = 0;
        for (Colono c : todos) {
            suma += c.getSalud();
        }
        return suma / todos.size();
    }

    public int getPromedioFelicidad() {
        ArrayList<Colono> todos = getTodosLosColonos();
        if (todos.isEmpty()) {
            return 0;
        }
        int suma = 0;
        for (Colono c : todos) {
            suma += c.getFelicidad();
        }
        return suma / todos.size();
    }

    public int getPromedioProductividad() {
        ArrayList<Colono> todos = getTodosLosColonos();
        if (todos.isEmpty()) {
            return 0;
        }
        int suma = 0;
        for (Colono c : todos) {
            suma += c.getProductividad();
        }
        return suma / todos.size();
    }

    public int getBienestarComunidad() {
        return (getPromedioFelicidad() + getPromedioSalud() + getPromedioProductividad()) / 3;
    }
}
