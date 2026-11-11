package modelo;

public class EscasezRecursos extends Evento {

    public EscasezRecursos() {
        super("Escasez de recursos");
    }

    @Override
    public String aplicar(Aldea aldea) {
        int raciones = 10 + azar.nextInt(11);
        int agua = 5 + azar.nextInt(8);
        aldea.cambiarRaciones(-raciones);
        aldea.cambiarAgua(-agua);
        return "ESCASEZ: se perdieron " + raciones + " raciones y " + agua + " de agua.";
    }
}
