package persistencia;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import modelo.Aldea;
import modelo.Colono;
import modelo.Familia;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * Genera el "Informe de Supervivencia y Estado de las Familias" en PDF
 * usando la libreria PDFBox, con un codigo QR creado con ZXing.
 */
public class InformePDF {

    private static final int ANCHO_LINEA = 95;

    public static void generar(Aldea aldea, String motivo, int turnosMax, String ruta)
            throws Exception {
        ArrayList<String> lineas = armarLineas(aldea, motivo, turnosMax);
        BufferedImage qr = crearQR(textoQR(aldea, motivo, turnosMax));

        PDDocument documento = new PDDocument();
        PDType1Font normal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        PDType1Font negrita = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDImageXObject imagenQR = LosslessFactory.createFromImage(documento, qr);

        // ----- Primera pagina: titulo y QR -----
        PDPage pagina = new PDPage(PDRectangle.A4);
        documento.addPage(pagina);
        PDPageContentStream cs = new PDPageContentStream(documento, pagina);
        escribir(cs, negrita, 16, 50, 800, "Informe de Supervivencia y Estado de las Familias");
        escribir(cs, normal, 11, 50, 780,
                "Simulación de Asentamiento Humano - Aldea " + aldea.getNombre());
        cs.drawImage(imagenQR, 50, 635, 130, 130);
        escribir(cs, normal, 10, 195, 720, "Código QR con los datos clave de la partida.");
        escribir(cs, normal, 10, 195, 705, "Turnos jugados: "
                + Math.min(aldea.getTurno() - 1, turnosMax) + " de " + turnosMax);
        escribir(cs, normal, 10, 195, 690,
                "Bienestar de la comunidad: " + aldea.getBienestarComunidad() + "/100");

        // ----- Resto del contenido, con salto de pagina -----
        int y = 610;
        for (String linea : lineas) {
            if (y < 50) {
                cs.close();
                pagina = new PDPage(PDRectangle.A4);
                documento.addPage(pagina);
                cs = new PDPageContentStream(documento, pagina);
                y = 800;
            }
            boolean esTitulo = linea.startsWith("#");
            String t = esTitulo ? linea.substring(1) : linea;
            if (esTitulo) {
                escribir(cs, negrita, 12, 50, y, t);
                y -= 20;
            } else {
                escribir(cs, normal, 10, 50, y, t);
                y -= 14;
            }
        }
        cs.close();
        documento.save(ruta);
        documento.close();
    }

    // Escribe una linea de texto en la pagina
    private static void escribir(PDPageContentStream cs, PDType1Font fuente, int tam,
            int x, int y, String texto) throws Exception {
        cs.beginText();
        cs.setFont(fuente, tam);
        cs.newLineAtOffset(x, y);
        cs.showText(limpiar(texto));
        cs.endText();
    }

    // La fuente estandar solo admite caracteres latinos (tildes y enie incluidas)
    private static String limpiar(String texto) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c < 256) {
                sb.append(c);
            } else {
                sb.append('?');
            }
        }
        return sb.toString();
    }

    private static String textoQR(Aldea aldea, String motivo, int turnosMax) {
        return "ALDEA:" + aldea.getNombre()
                + "|TURNOS:" + Math.min(aldea.getTurno() - 1, turnosMax) + "/" + turnosMax
                + "|COLONOS:" + aldea.getTotalColonos()
                + "|BIENESTAR:" + aldea.getBienestarComunidad()
                + "|ENERGIA:" + aldea.getEnergia()
                + "|RACIONES:" + aldea.getRaciones()
                + "|AGUA:" + aldea.getAgua()
                + "|EVENTOS:" + aldea.getHistorial().size();
    }

    private static BufferedImage crearQR(String contenido) throws Exception {
        BitMatrix matriz = new QRCodeWriter().encode(contenido, BarcodeFormat.QR_CODE, 300, 300);
        return MatrixToImageWriter.toBufferedImage(matriz);
    }

    private static ArrayList<String> armarLineas(Aldea aldea, String motivo, int turnosMax) {
        ArrayList<String> lineas = new ArrayList<String>();
        lineas.add("#Resultado de la simulación");
        partir(lineas, motivo, "");
        lineas.add("#Recursos críticos");
        lineas.add("Energía: " + aldea.getEnergia() + "/100    Raciones de alimento: "
                + aldea.getRaciones() + "/100    Agua purificada: " + aldea.getAgua() + "/100");
        lineas.add("#Estado de la comunidad");
        lineas.add("Colonos: " + aldea.getTotalColonos() + "    Familias: " + aldea.getFamilias().size());
        lineas.add("Felicidad: " + aldea.getPromedioFelicidad() + "    Salud: " + aldea.getPromedioSalud()
                + "    Productividad: " + aldea.getPromedioProductividad());
        lineas.add("#Estado de las familias");
        for (Familia f : aldea.getFamilias()) {
            lineas.add(f.getNombre() + " - bienestar " + f.getBienestar() + " (salud " + f.getPromedioSalud()
                    + ", felicidad " + f.getPromedioFelicidad() + ", productividad "
                    + f.getPromedioProductividad() + ")");
            for (Colono c : f.getColonos()) {
                lineas.add("      " + c.getTipo() + " " + c.getNombre() + ": salud " + c.getSalud()
                        + ", felicidad " + c.getFelicidad() + ", productividad " + c.getProductividad());
            }
        }
        lineas.add("#Historial de eventos");
        for (String evento : aldea.getHistorial()) {
            partir(lineas, evento, "");
        }
        return lineas;
    }

    // Parte un texto largo en varias lineas para que quepa en la hoja
    private static void partir(ArrayList<String> lineas, String texto, String sangria) {
        String[] palabras = texto.split(" ");
        StringBuilder actual = new StringBuilder(sangria);
        for (String palabra : palabras) {
            if (actual.length() + palabra.length() + 1 > ANCHO_LINEA) {
                lineas.add(actual.toString());
                actual = new StringBuilder(sangria + "   ");
            }
            actual.append(palabra).append(" ");
        }
        lineas.add(actual.toString());
    }
}
