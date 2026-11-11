package modelo;

public class Ingeniero extends Colono {

    public Ingeniero(String nombre, int salud, int felicidad, int productividad) {
        super(nombre, salud, felicidad, productividad);
    }

    @Override
    public String getTipo() {
        return "Ingeniero";
    }

    @Override
    public void trabajar(Aldea aldea) {
        aldea.cambiarEnergia(cantidadBase());
    }
}
