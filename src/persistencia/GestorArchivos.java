package persistencia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import modelo.Agricultor;
import modelo.Aldea;
import modelo.Colono;
import modelo.Familia;
import modelo.Ingeniero;
import modelo.Purificador;

/**
 * Capa de persistencia: guarda y carga el estado de la simulacion
 * en tres archivos .txt dentro de la carpeta "datos".
 *   recursos.txt -> nombre;turno;energia;raciones;agua
 *   colonos.txt  -> F;nombreFamilia  y  C;tipo;nombre;salud;felicidad;productividad
 *   eventos.txt  -> una linea por evento del historial
 */
public class GestorArchivos {

    public static final String CARPETA = "datos";
    public static final String ARCHIVO_RECURSOS = CARPETA + "/recursos.txt";
    public static final String ARCHIVO_COLONOS = CARPETA + "/colonos.txt";
    public static final String ARCHIVO_EVENTOS = CARPETA + "/eventos.txt";

    public static boolean existenDatos() {
        return new File(ARCHIVO_RECURSOS).exists()
                && new File(ARCHIVO_COLONOS).exists()
                && new File(ARCHIVO_EVENTOS).exists();
    }

    public static void guardar(Aldea aldea) throws IOException {
        File carpeta = new File(CARPETA);
        if (!carpeta.exists()) {
            carpeta.mkdir();
        }

        PrintWriter recursos = new PrintWriter(new FileWriter(ARCHIVO_RECURSOS));
        recursos.println(aldea.getNombre() + ";" + aldea.getTurno() + ";" + aldea.getEnergia()
                + ";" + aldea.getRaciones() + ";" + aldea.getAgua());
        recursos.close();

        PrintWriter colonos = new PrintWriter(new FileWriter(ARCHIVO_COLONOS));
        for (Familia f : aldea.getFamilias()) {
            colonos.println("F;" + f.getNombre());
            for (Colono c : f.getColonos()) {
                colonos.println("C;" + c.getTipo() + ";" + c.getNombre() + ";" + c.getSalud()
                        + ";" + c.getFelicidad() + ";" + c.getProductividad());
            }
        }
        colonos.close();

        PrintWriter eventos = new PrintWriter(new FileWriter(ARCHIVO_EVENTOS));
        for (String linea : aldea.getHistorial()) {
            eventos.println(linea);
        }
        eventos.close();
    }

    public static Aldea cargar() throws IOException {
        // Recursos
        BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_RECURSOS));
        String linea = br.readLine();
        br.close();
        String[] datos = linea.split(";");
        Aldea aldea = new Aldea(datos[0]);
        aldea.setTurno(Integer.parseInt(datos[1]));
        aldea.setRecursos(Integer.parseInt(datos[2]), Integer.parseInt(datos[3]),
                Integer.parseInt(datos[4]));

        // Familias y colonos
        br = new BufferedReader(new FileReader(ARCHIVO_COLONOS));
        Familia actual = null;
        while ((linea = br.readLine()) != null) {
            if (linea.trim().isEmpty()) {
                continue;
            }
            String[] p = linea.split(";");
            if (p[0].equals("F")) {
                actual = new Familia(p[1]);
                aldea.agregarFamilia(actual);
            } else if (p[0].equals("C") && actual != null) {
                actual.agregarColono(crearColono(p[1], p[2], Integer.parseInt(p[3]),
                        Integer.parseInt(p[4]), Integer.parseInt(p[5])));
            }
        }
        br.close();

        // Historial de eventos
        br = new BufferedReader(new FileReader(ARCHIVO_EVENTOS));
        while ((linea = br.readLine()) != null) {
            if (!linea.trim().isEmpty()) {
                aldea.getHistorial().add(linea);
            }
        }
        br.close();
        return aldea;
    }

    private static Colono crearColono(String tipo, String nombre, int salud, int felicidad,
            int productividad) {
        if (tipo.equals("Agricultor")) {
            return new Agricultor(nombre, salud, felicidad, productividad);
        } else if (tipo.equals("Purificador")) {
            return new Purificador(nombre, salud, felicidad, productividad);
        } else if (tipo.equals("Ingeniero")) {
            return new Ingeniero(nombre, salud, felicidad, productividad);
        }
        return new Colono(nombre, salud, felicidad, productividad);
    }
}
