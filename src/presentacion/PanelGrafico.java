package presentacion;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import modelo.Aldea;

/**
 * Grafico de barras con los recursos y el estado promedio de la comunidad.
 */
public class PanelGrafico extends JPanel {

    private Aldea aldea;

    public void setAldea(Aldea aldea) {
        this.aldea = aldea;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (aldea == null) {
            return;
        }
        int[] valores = {aldea.getEnergia(), aldea.getRaciones(), aldea.getAgua(),
            aldea.getPromedioFelicidad(), aldea.getPromedioSalud(), aldea.getPromedioProductividad()};
        String[] nombres = {"Energía", "Raciones", "Agua", "Felicidad", "Salud", "Product."};
        Color[] colores = {new Color(240, 190, 20), new Color(200, 130, 50), new Color(60, 140, 220),
            new Color(230, 100, 160), new Color(220, 60, 60), new Color(90, 170, 90)};

        int ancho = getWidth();
        int alto = getHeight();
        int margenArriba = 25;
        int margenAbajo = 25;
        int espacio = ancho / valores.length;
        int anchoBarra = espacio - 16;
        int altoMax = alto - margenArriba - margenAbajo;

        g.setColor(Color.GRAY);
        g.drawLine(5, alto - margenAbajo, ancho - 5, alto - margenAbajo);
        for (int i = 0; i < valores.length; i++) {
            int altoBarra = altoMax * valores[i] / 100;
            int x = i * espacio + 8;
            int y = alto - margenAbajo - altoBarra;
            g.setColor(colores[i]);
            g.fillRect(x, y, anchoBarra, altoBarra);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(valores[i]), x + anchoBarra / 2 - 8, y - 4);
            g.drawString(nombres[i], x, alto - 8);
        }
    }
}
