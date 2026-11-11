package modelo;

/**
 * Clase padre de todos los colonos. Las clases hijas (Agricultor,
 * Purificador, Ingeniero) heredan de ella y redefinen el metodo trabajar().
 */
public class Colono {

    private String nombre;
    private int salud;
    private int felicidad;
    private int productividad;

    public Colono(String nombre, int salud, int felicidad, int productividad) {
        this.nombre = nombre;
        this.salud = limitar(salud);
        this.felicidad = limitar(felicidad);
        this.productividad = limitar(productividad);
    }

    private int limitar(int valor) {
        if (valor < 0) {
            return 0;
        }
        if (valor > 100) {
            return 100;
        }
        return valor;
    }

    public String getNombre() {
        return nombre;
    }

    public int getSalud() {
        return salud;
    }

    public int getFelicidad() {
        return felicidad;
    }

    public int getProductividad() {
        return productividad;
    }

    public void cambiarSalud(int cambio) {
        salud = limitar(salud + cambio);
    }

    public void cambiarFelicidad(int cambio) {
        felicidad = limitar(felicidad + cambio);
    }

    public void cambiarProductividad(int cambio) {
        productividad = limitar(productividad + cambio);
    }

    // Cantidad base que produce un colono segun su productividad y salud
    protected int cantidadBase() {
        int cantidad = 2 + productividad / 25;
        if (salud < 30) {
            cantidad = cantidad / 2;
        }
        return cantidad;
    }

    // Se redefine en las clases hijas (polimorfismo)
    public String getTipo() {
        return "Colono";
    }

    // Se redefine en las clases hijas (polimorfismo)
    public void trabajar(Aldea aldea) {
        // un colono generico no produce nada
    }
}
