package modelo;

public class Purificador extends Colono {

    public Purificador(String nombre, int salud, int felicidad, int productividad) {
        super(nombre, salud, felicidad, productividad);
    }

    @Override
    public String getTipo() {
        return "Purificador";
    }

    @Override
    public void trabajar(Aldea aldea) {
        aldea.cambiarAgua(cantidadBase());
    }
}
