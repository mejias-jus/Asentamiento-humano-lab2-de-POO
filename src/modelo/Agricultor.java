package modelo;

public class Agricultor extends Colono {

    public Agricultor(String nombre, int salud, int felicidad, int productividad) {
        super(nombre, salud, felicidad, productividad);
    }

    @Override
    public String getTipo() {
        return "Agricultor";
    }

    @Override
    public void trabajar(Aldea aldea) {
        aldea.cambiarRaciones(cantidadBase() + 1);
    }
}
