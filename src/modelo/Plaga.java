package modelo;

public class Plaga extends Evento {

    public Plaga() {
        super("Plaga");
    }

    @Override
    public String aplicar(Aldea aldea) {
        int raciones = 8 + azar.nextInt(8);
        aldea.cambiarRaciones(-raciones);
        aldea.cambiarEstadoTodos(-8, 0, 0);
        return "PLAGA: arruinó " + raciones + " raciones y enfermó a los colonos (-8 salud).";
    }
}
