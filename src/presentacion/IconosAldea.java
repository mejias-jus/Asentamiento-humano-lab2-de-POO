package presentacion;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * Crea los iconos de la matriz visual dibujandolos con Graphics2D,
 * asi el proyecto no necesita archivos de imagen.
 */
public class IconosAldea {

    private static final int T = 56;

    private static BufferedImage nuevaImagen() {
        return new BufferedImage(T, T, BufferedImage.TYPE_INT_ARGB);
    }

    private static Graphics2D preparar(BufferedImage img) {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return g;
    }

    // Casa de una familia; el color del techo depende de su bienestar
    public static ImageIcon casa(int bienestar) {
        Color techo;
        if (bienestar >= 60) {
            techo = new Color(46, 160, 67);
        } else if (bienestar >= 35) {
            techo = new Color(230, 160, 20);
        } else {
            techo = new Color(200, 50, 50);
        }
        BufferedImage img = nuevaImagen();
        Graphics2D g = preparar(img);
        g.setColor(new Color(235, 220, 190));
        g.fillRect(10, 26, 36, 24);
        g.setColor(techo);
        g.fillPolygon(new Polygon(new int[]{6, 28, 50}, new int[]{28, 6, 28}, 3));
        g.setColor(new Color(110, 70, 40));
        g.fillRect(24, 34, 9, 16);
        g.setColor(new Color(120, 180, 230));
        g.fillRect(14, 32, 7, 7);
        g.dispose();
        return new ImageIcon(img);
    }

    public static ImageIcon cultivo() {
        BufferedImage img = nuevaImagen();
        Graphics2D g = preparar(img);
        g.setColor(new Color(150, 110, 60));
        g.fillRect(6, 10, 44, 36);
        g.setColor(new Color(60, 170, 60));
        g.setStroke(new BasicStroke(3));
        for (int x = 14; x <= 42; x += 14) {
            g.drawLine(x, 40, x, 20);
            g.drawLine(x, 28, x - 5, 22);
            g.drawLine(x, 28, x + 5, 22);
        }
        g.dispose();
        return new ImageIcon(img);
    }

    public static ImageIcon pozo() {
        BufferedImage img = nuevaImagen();
        Graphics2D g = preparar(img);
        g.setColor(new Color(130, 130, 130));
        g.fillOval(8, 14, 40, 32);
        g.setColor(new Color(60, 140, 220));
        g.fillOval(13, 19, 30, 22);
        g.setColor(new Color(200, 230, 255));
        g.fillOval(20, 23, 8, 5);
        g.dispose();
        return new ImageIcon(img);
    }

    public static ImageIcon torreEnergia() {
        BufferedImage img = nuevaImagen();
        Graphics2D g = preparar(img);
        g.setColor(new Color(70, 70, 90));
        g.fillRoundRect(8, 8, 40, 40, 10, 10);
        g.setColor(new Color(255, 215, 0));
        g.fillPolygon(new Polygon(new int[]{32, 20, 28, 24, 38, 29, 34},
                new int[]{10, 30, 30, 46, 24, 24, 10}, 7));
        g.dispose();
        return new ImageIcon(img);
    }

    public static ImageIcon terreno() {
        BufferedImage img = nuevaImagen();
        Graphics2D g = preparar(img);
        g.setColor(new Color(200, 215, 170));
        g.fillRect(0, 0, T, T);
        g.setColor(new Color(150, 180, 120));
        g.fillOval(14, 30, 6, 4);
        g.fillOval(36, 18, 6, 4);
        g.dispose();
        return new ImageIcon(img);
    }
}
