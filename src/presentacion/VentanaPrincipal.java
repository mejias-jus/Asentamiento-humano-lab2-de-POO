package presentacion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import logica.MotorSimulacion;
import modelo.Aldea;
import modelo.Colono;
import modelo.Familia;
import persistencia.GestorArchivos;
import persistencia.InformePDF;

/**
 * Capa de presentacion: dashboard de la simulacion.
 */
public class VentanaPrincipal extends JFrame implements ActionListener {

    private static final String[] MAPA = {
        "FCFEF.",
        "C.P.C.",
        "FE.P.F",
        ".CF.E."
    };
    private static final String RUTA_PDF = "Informe_Supervivencia.pdf";

    private MotorSimulacion motor = new MotorSimulacion();
    private int[][] indiceFamilia = new int[MAPA.length][MAPA[0].length()];
    private int familiaSeleccionada = -1;

    private JLabel lblEstado = new JLabel();
    private JButton[][] celdas = new JButton[MAPA.length][MAPA[0].length()];
    private JProgressBar[] barras = new JProgressBar[MotorSimulacion.NUM_FAMILIAS];
    private JTextArea txtFamilia = new JTextArea();
    private JTextArea txtRegistro = new JTextArea();
    private PanelGrafico grafico = new PanelGrafico();
    private JComboBox<String> cmbDecision = new JComboBox<String>(MotorSimulacion.getDecisiones());
    private JButton btnTurno = new JButton("Ejecutar turno");
    private JButton btnGuardar = new JButton("Guardar");
    private JButton btnCargar = new JButton("Cargar");
    private JButton btnNueva = new JButton("Nueva simulación");
    private JButton btnFinalizar = new JButton("Finalizar y exportar PDF");

    public VentanaPrincipal() {
        super("Simulación de Asentamiento Humano");
        setSize(1150, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        construirInterfaz();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                guardarSilencioso();
                System.exit(0);
            }
        });

        // Si hay una partida guardada, preguntar si se quiere continuar
        if (GestorArchivos.existenDatos()) {
            int resp = JOptionPane.showConfirmDialog(this,
                    "Hay una simulación guardada. ¿Desea continuarla?",
                    "Simulación guardada", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                cargarPartida();
            }
        }
        actualizar();
    }

    private void construirInterfaz() {
        // ----- Arriba: estado general -----
        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblEstado.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        add(lblEstado, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(2, 2, 6, 6));
        centro.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

        // ----- Matriz visual de la aldea -----
        int filas = MAPA.length;
        int columnas = MAPA[0].length();
        JPanel panelMapa = new JPanel(new GridLayout(filas, columnas, 2, 2));
        panelMapa.setBorder(BorderFactory.createTitledBorder("Mapa de la aldea (clic en una casa)"));
        int contador = 0;
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                char tipo = MAPA[f].charAt(c);
                celdas[f][c] = new JButton();
                celdas[f][c].addActionListener(this);
                if (tipo == 'F') {
                    indiceFamilia[f][c] = contador;
                    contador++;
                } else {
                    indiceFamilia[f][c] = -1;
                    if (tipo == 'C') {
                        celdas[f][c].setIcon(IconosAldea.cultivo());
                    } else if (tipo == 'P') {
                        celdas[f][c].setIcon(IconosAldea.pozo());
                    } else if (tipo == 'E') {
                        celdas[f][c].setIcon(IconosAldea.torreEnergia());
                    } else {
                        celdas[f][c].setIcon(IconosAldea.terreno());
                    }
                }
                panelMapa.add(celdas[f][c]);
            }
        }
        centro.add(panelMapa);

        // ----- Paneles de las familias -----
        JPanel panelFamilias = new JPanel(new BorderLayout(4, 4));
        panelFamilias.setBorder(BorderFactory.createTitledBorder("Familias (bienestar integral)"));
        JPanel panelBarras = new JPanel(new GridLayout(MotorSimulacion.NUM_FAMILIAS, 1, 2, 2));
        for (int i = 0; i < barras.length; i++) {
            barras[i] = new JProgressBar(0, 100);
            barras[i].setStringPainted(true);
            panelBarras.add(barras[i]);
        }
        panelFamilias.add(panelBarras, BorderLayout.NORTH);
        txtFamilia.setEditable(false);
        txtFamilia.setText("Haga clic en una casa del mapa para ver a sus colonos.");
        txtFamilia.setFont(new Font("Monospaced", Font.PLAIN, 12));
        panelFamilias.add(new JScrollPane(txtFamilia), BorderLayout.CENTER);
        centro.add(panelFamilias);

        // ----- Grafico -----
        grafico.setBorder(BorderFactory.createTitledBorder("Recursos y estado de la comunidad"));
        grafico.setBackground(Color.WHITE);
        centro.add(grafico);

        // ----- Registro de eventos en tiempo real -----
        txtRegistro.setEditable(false);
        txtRegistro.setLineWrap(true);
        txtRegistro.setWrapStyleWord(true);
        JScrollPane scrollRegistro = new JScrollPane(txtRegistro);
        scrollRegistro.setBorder(BorderFactory.createTitledBorder("Registro de eventos"));
        centro.add(scrollRegistro);

        add(centro, BorderLayout.CENTER);

        // ----- Abajo: decisiones y botones -----
        JPanel sur = new JPanel(new GridLayout(2, 1));
        JPanel filaDecision = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaDecision.add(new JLabel("Decisión del administrador:"));
        cmbDecision.setPreferredSize(new Dimension(420, 26));
        filaDecision.add(cmbDecision);
        filaDecision.add(btnTurno);
        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaBotones.add(btnGuardar);
        filaBotones.add(btnCargar);
        filaBotones.add(btnNueva);
        filaBotones.add(btnFinalizar);
        sur.add(filaDecision);
        sur.add(filaBotones);
        add(sur, BorderLayout.SOUTH);

        btnTurno.addActionListener(this);
        btnGuardar.addActionListener(this);
        btnCargar.addActionListener(this);
        btnNueva.addActionListener(this);
        btnFinalizar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == btnTurno) {
            ejecutarTurno();
        } else if (origen == btnGuardar) {
            guardarPartida();
        } else if (origen == btnCargar) {
            cargarPartida();
            actualizar();
        } else if (origen == btnNueva) {
            int resp = JOptionPane.showConfirmDialog(this,
                    "Se perderá la simulación actual. ¿Continuar?", "Nueva simulación",
                    JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                motor.nuevaSimulacion();
                familiaSeleccionada = -1;
                guardarSilencioso();
                actualizar();
            }
        } else if (origen == btnFinalizar) {
            exportarInforme();
        } else {
            // Clic en una celda del mapa
            for (int f = 0; f < celdas.length; f++) {
                for (int c = 0; c < celdas[f].length; c++) {
                    if (origen == celdas[f][c]) {
                        clicEnCelda(f, c);
                    }
                }
            }
        }
    }

    private void clicEnCelda(int f, int c) {
        char tipo = MAPA[f].charAt(c);
        if (tipo == 'F') {
            familiaSeleccionada = indiceFamilia[f][c];
        } else {
            familiaSeleccionada = -1;
            if (tipo == 'C') {
                txtFamilia.setText("Campo de cultivo.\nLos agricultores producen raciones de alimento aquí.");
            } else if (tipo == 'P') {
                txtFamilia.setText("Pozo de agua.\nLos purificadores producen agua purificada aquí.");
            } else if (tipo == 'E') {
                txtFamilia.setText("Torre de energía.\nLos ingenieros generan energía aquí.");
            } else {
                txtFamilia.setText("Terreno libre.");
            }
            return;
        }
        mostrarFamilia();
    }

    private void ejecutarTurno() {
        if (motor.estaTerminada()) {
            JOptionPane.showMessageDialog(this, "La simulación ya terminó. Exporte el informe o inicie una nueva.");
            return;
        }
        boolean ok = motor.ejecutarTurno(cmbDecision.getSelectedIndex());
        if (!ok) {
            JOptionPane.showMessageDialog(this, motor.getMensaje(), "Decisión no posible",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        guardarSilencioso(); // el estado persiste en archivos cada turno
        actualizar();
        if (motor.estaTerminada()) {
            int resp = JOptionPane.showConfirmDialog(this,
                    motor.getMotivoFin() + "\n¿Desea exportar el informe en PDF?",
                    "Fin de la simulación", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                exportarInforme();
            }
        }
    }

    private void guardarPartida() {
        try {
            GestorArchivos.guardar(motor.getAldea());
            JOptionPane.showMessageDialog(this, "Simulación guardada en la carpeta \"datos\".");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarSilencioso() {
        try {
            GestorArchivos.guardar(motor.getAldea());
        } catch (Exception ex) {
            System.out.println("No se pudo guardar: " + ex.getMessage());
        }
    }

    private void cargarPartida() {
        if (!GestorArchivos.existenDatos()) {
            JOptionPane.showMessageDialog(this, "No hay una simulación guardada.");
            return;
        }
        try {
            motor.setAldea(GestorArchivos.cargar());
            familiaSeleccionada = -1;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarInforme() {
        try {
            guardarSilencioso();
            InformePDF.generar(motor.getAldea(), motor.getMotivoFin(), MotorSimulacion.TURNOS_MAX, RUTA_PDF);
            JOptionPane.showMessageDialog(this, "Informe generado:\n" + new File(RUTA_PDF).getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo generar el PDF: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ----- Actualizar toda la pantalla -----
    private void actualizar() {
        Aldea a = motor.getAldea();
        lblEstado.setText("Aldea " + a.getNombre() + "   |   Turno " + Math.min(a.getTurno(), MotorSimulacion.TURNOS_MAX)
                + "/" + MotorSimulacion.TURNOS_MAX + "   |   Energía " + a.getEnergia() + "   Raciones "
                + a.getRaciones() + "   Agua " + a.getAgua() + "   |   Bienestar " + a.getBienestarComunidad());

        for (int f = 0; f < celdas.length; f++) {
            for (int c = 0; c < celdas[f].length; c++) {
                int indice = indiceFamilia[f][c];
                if (indice >= 0) {
                    if (indice < a.getFamilias().size()) {
                        celdas[f][c].setIcon(IconosAldea.casa(a.getFamilias().get(indice).getBienestar()));
                    } else {
                        celdas[f][c].setIcon(IconosAldea.terreno());
                    }
                }
            }
        }

        for (int i = 0; i < barras.length; i++) {
            if (i < a.getFamilias().size()) {
                Familia fam = a.getFamilias().get(i);
                int b = fam.getBienestar();
                barras[i].setValue(b);
                barras[i].setString(fam.getNombre() + ": " + b);
                if (b >= 60) {
                    barras[i].setForeground(new Color(46, 160, 67));
                } else if (b >= 35) {
                    barras[i].setForeground(new Color(230, 160, 20));
                } else {
                    barras[i].setForeground(new Color(200, 50, 50));
                }
            }
        }

        if (familiaSeleccionada >= 0) {
            mostrarFamilia();
        }
        grafico.setAldea(a);

        StringBuilder sb = new StringBuilder();
        for (String linea : a.getHistorial()) {
            sb.append(linea).append("\n");
        }
        txtRegistro.setText(sb.toString());
        txtRegistro.setCaretPosition(txtRegistro.getDocument().getLength());

        boolean terminada = motor.estaTerminada();
        btnTurno.setEnabled(!terminada);
    }

    private void mostrarFamilia() {
        Aldea a = motor.getAldea();
        if (familiaSeleccionada < 0 || familiaSeleccionada >= a.getFamilias().size()) {
            txtFamilia.setText("Seleccione una casa del mapa.");
            return;
        }
        Familia fam = a.getFamilias().get(familiaSeleccionada);
        StringBuilder sb = new StringBuilder();
        sb.append(fam.getNombre()).append("\n");
        sb.append("Bienestar: ").append(fam.getBienestar()).append("\n\n");
        for (Colono c : fam.getColonos()) {
            sb.append(c.getTipo()).append(" ").append(c.getNombre()).append("\n");
            sb.append("  Salud: ").append(c.getSalud()).append("\n");
            sb.append("  Felicidad: ").append(c.getFelicidad()).append("\n");
            sb.append("  Productividad: ").append(c.getProductividad()).append("\n");
        }
        txtFamilia.setText(sb.toString());
        txtFamilia.setCaretPosition(0);
    }
}
