package modelo;

public class Tormenta extends Evento {

    public Tormenta() {
        super("Tormenta");
    }

    @Override
    public String aplicar(Aldea aldea) {
        int perdida = 10 + azar.nextInt(11);
        aldea.cambiarEnergia(-perdida);
        aldea.cambiarEstadoTodos(0, -5, 0);
        return "TORMENTA: danó los generadores (-" + perdida + " energía) y bajó el ánimo (-5 felicidad).";
    }
}
