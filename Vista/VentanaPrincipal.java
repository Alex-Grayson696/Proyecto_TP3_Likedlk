package Vista;

import Controlador.RedControlador;
import Modelo.Nodo;
import Modelo.TipoNodo;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;


public class VentanaPrincipal extends JFrame {

    // ─── Colores y Fuentes ──────────────────────────────────────────────────
    private static final Color AZUL_OSCURO   = new Color(13, 71, 161);
    private static final Color AZUL_MEDIO    = new Color(25, 118, 210);
    private static final Color AZUL_CLARO    = new Color(227, 242, 253);
    private static final Color VERDE         = new Color(27, 94, 32);
    private static final Color VERDE_CLARO   = new Color(232, 245, 233);
    private static final Color NARANJA       = new Color(230, 81, 0);
    private static final Color NARANJA_CLARO = new Color(255, 243, 224);
    private static final Color GRIS_FONDO    = new Color(245, 245, 245);
    private static final Color BLANCO        = Color.WHITE;

    private static final Font FUENTE_TITULO  = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FUENTE_NORMAL  = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FUENTE_MONO    = new Font("Consolas", Font.PLAIN, 12);

    // ─── Controlador ────────────────────────────────────────────────────────
    private final RedControlador controlador;

    // ─── Componentes globales ────────────────────────────────────────────────
    private JLabel      labelResumen;
    private JTabbedPane pestanas;

    // Pestaña Nodos
    private DefaultTableModel modeloTablaNodos;
    private JTable            tablaNodos;

    // Pestaña Conexiones
    private DefaultTableModel modeloTablaConexiones;
    private JTable            tablaConexiones;
    private JComboBox<String> comboNodoA, comboNodoB;
    private JTextField        campoEtiqueta;

    // Pestaña Algoritmos
    private JComboBox<String> comboOrigen, comboDestino;
    private JTextArea         areaResultado;

    // Panel de visualización del grafo
    private PanelGrafo panelGrafo;

    // ─── Constructor ─────────────────────────────────────────────────────────
    public VentanaPrincipal(RedControlador controlador) {
        this.controlador = controlador;
        configurarVentana();
        construirUI();
        actualizarTodo();
    }

    // ─── Configuración de la ventana ─────────────────────────────────────────
    private void configurarVentana() {
        setTitle("LinkedIn Académico — Red de Networking Profesional");
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(GRIS_FONDO);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int r = JOptionPane.showConfirmDialog(VentanaPrincipal.this,
                    "¿Desea salir? Los datos están guardados automáticamente.",
                    "Salir", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) System.exit(0);
            }
        });
    }

    // ─── Construcción de la UI ────────────────────────────────────────────────
    private void construirUI() {
        add(crearEncabezado(), BorderLayout.NORTH);
        pestanas = new JTabbedPane(JTabbedPane.TOP);
        pestanas.setFont(FUENTE_TITULO);
        pestanas.setBackground(BLANCO);
        pestanas.addTab("🌐  Vista de Red",    crearPestanaGrafo());
        pestanas.addTab("👤  Nodos",           crearPestanaNodos());
        pestanas.addTab("🔗  Conexiones",      crearPestanaConexiones());
        pestanas.addTab("🔍  Algoritmos",      crearPestanaAlgoritmos());
        add(pestanas, BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);
    }

    // ─── Encabezado ──────────────────────────────────────────────────────────
    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AZUL_OSCURO);
        panel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titulo = new JLabel("LinkedIn Académico");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(BLANCO);

        JLabel subtitulo = new JLabel("Red de Networking Profesional — Teoría de Grafos");
        subtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subtitulo.setForeground(new Color(187, 222, 251));

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subtitulo);
        panel.add(textos, BorderLayout.WEST);

        // Logo textual
        JLabel logo = new JLabel("[ in ]");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logo.setForeground(new Color(255, 255, 255));
        logo.setBorder(new EmptyBorder(0, 0, 0, 10));
        panel.add(logo, BorderLayout.EAST);

        return panel;
    }

    // ─── PESTAÑA 1: Vista del Grafo ───────────────────────────────────────────
    private JPanel crearPestanaGrafo() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(GRIS_FONDO);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panelGrafo = new PanelGrafo(controlador.getGrafo());
        panelGrafo.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        panel.add(panelGrafo, BorderLayout.CENTER);

        // Panel de leyenda
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        leyenda.setBackground(BLANCO);
        leyenda.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220)),
            new EmptyBorder(4, 8, 4, 8)));

        leyenda.add(etiquetaLeyenda("● Estudiante",  AZUL_MEDIO));
        leyenda.add(etiquetaLeyenda("● Profesor",    VERDE));
        leyenda.add(etiquetaLeyenda("● Empresa",     NARANJA));
        leyenda.add(new JSeparator(SwingConstants.VERTICAL));
        leyenda.add(etiquetaLeyenda("Haz clic en un nodo para ver sus conexiones", Color.GRAY));

        JButton btnRefrescar = boton("↺ Refrescar", AZUL_MEDIO);
        btnRefrescar.addActionListener(e -> {
            panelGrafo.actualizarGrafo(controlador.getGrafo());
            panelGrafo.repaint();
        });
        leyenda.add(btnRefrescar);
        panel.add(leyenda, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel etiquetaLeyenda(String texto, Color color) {
        JLabel l = new JLabel(texto);
        l.setFont(FUENTE_NORMAL);
        l.setForeground(color);
        return l;
    }

    // ─── PESTAÑA 2: CRUD Nodos ────────────────────────────────────────────────
    private JSplitPane crearPestanaNodos() {
        // ── Tabla ──
        String[] columnas = {"ID", "Nombre", "Tipo", "Descripción / Especialidad"};
        modeloTablaNodos = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaNodos = new JTable(modeloTablaNodos);
        tablaNodos.setFont(FUENTE_NORMAL);
        tablaNodos.setRowHeight(24);
        tablaNodos.getTableHeader().setFont(FUENTE_TITULO);
        tablaNodos.getTableHeader().setBackground(AZUL_OSCURO);
        tablaNodos.getTableHeader().setForeground(BLANCO);
        tablaNodos.setSelectionBackground(AZUL_CLARO);
        tablaNodos.setGridColor(new Color(220, 220, 220));
        tablaNodos.getColumnModel().getColumn(0).setMaxWidth(70);
        tablaNodos.getColumnModel().getColumn(2).setMaxWidth(110);

        JScrollPane scrollTabla = new JScrollPane(tablaNodos);
        scrollTabla.setBorder(new TitledBorder(new LineBorder(AZUL_MEDIO), " Nodos de la Red "));

        // ── Formulario ──
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(BLANCO);
        formulario.setBorder(new CompoundBorder(
            new TitledBorder(new LineBorder(AZUL_MEDIO), " Agregar / Editar Nodo "),
            new EmptyBorder(8, 12, 8, 12)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        JTextField campoNombre = campoTexto("Nombre completo");
        JComboBox<String> comboTipo = new JComboBox<>(new String[]{"Estudiante", "Profesor", "Empresa"});
        comboTipo.setFont(FUENTE_NORMAL);
        JTextField campoDesc   = campoTexto("Carrera / Especialidad / Rubro");

        gbc.gridx=0; gbc.gridy=0; gbc.weightx=0.3;
        formulario.add(etiqueta("Nombre:"), gbc);
        gbc.gridx=1; gbc.weightx=0.7;
        formulario.add(campoNombre, gbc);

        gbc.gridx=0; gbc.gridy=1; gbc.weightx=0.3;
        formulario.add(etiqueta("Tipo:"), gbc);
        gbc.gridx=1; gbc.weightx=0.7;
        formulario.add(comboTipo, gbc);

        gbc.gridx=0; gbc.gridy=2; gbc.weightx=0.3;
        formulario.add(etiqueta("Descripción:"), gbc);
        gbc.gridx=1; gbc.weightx=0.7;
        formulario.add(campoDesc, gbc);

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        botones.setBackground(BLANCO);

        JButton btnAgregar  = boton("+ Agregar",  AZUL_MEDIO);
        JButton btnEditar   = boton("✎ Editar",   new Color(56, 142, 60));
        JButton btnEliminar = boton("✕ Eliminar", new Color(211, 47, 47));

        btnAgregar.addActionListener(e -> {
            String err = controlador.agregarNodo(
                campoNombre.getText(),
                (String) comboTipo.getSelectedItem(),
                campoDesc.getText()
            );
            if (err != null) { mostrarError(err); return; }
            campoNombre.setText("");
            campoDesc.setText("");
            actualizarTodo();
            mostrarExito("Nodo agregado correctamente.");
        });

        btnEditar.addActionListener(e -> {
            int fila = tablaNodos.getSelectedRow();
            if (fila < 0) { mostrarError("Seleccione un nodo de la tabla para editar."); return; }
            String id = (String) modeloTablaNodos.getValueAt(fila, 0);
            String err = controlador.editarNodo(id, campoNombre.getText(), campoDesc.getText());
            if (err != null) { mostrarError(err); return; }
            actualizarTodo();
            mostrarExito("Nodo actualizado.");
        });

        btnEliminar.addActionListener(e -> {
            int fila = tablaNodos.getSelectedRow();
            if (fila < 0) { mostrarError("Seleccione un nodo de la tabla para eliminar."); return; }
            String id   = (String) modeloTablaNodos.getValueAt(fila, 0);
            String nom  = (String) modeloTablaNodos.getValueAt(fila, 1);
            int    conf = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el nodo \"" + nom + "\" y todas sus conexiones?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (conf != JOptionPane.YES_OPTION) return;
            String err = controlador.eliminarNodo(id);
            if (err != null) { mostrarError(err); return; }
            actualizarTodo();
            mostrarExito("Nodo eliminado.");
        });

        // Rellenar campos al seleccionar fila
        tablaNodos.getSelectionModel().addListSelectionListener(ev -> {
            int fila = tablaNodos.getSelectedRow();
            if (fila < 0) return;
            campoNombre.setText((String) modeloTablaNodos.getValueAt(fila, 1));
            comboTipo.setSelectedItem(modeloTablaNodos.getValueAt(fila, 2));
            campoDesc.setText((String) modeloTablaNodos.getValueAt(fila, 3));
        });

        botones.add(btnAgregar);
        botones.add(btnEditar);
        botones.add(btnEliminar);

        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setBackground(BLANCO);
        panelDerecho.add(formulario, BorderLayout.NORTH);
        panelDerecho.add(botones,    BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTabla, panelDerecho);
        split.setDividerLocation(620);
        split.setBorder(new EmptyBorder(8, 8, 8, 8));
        split.setBackground(GRIS_FONDO);
        return split;
    }

    // ─── PESTAÑA 3: Conexiones ────────────────────────────────────────────────
    private JSplitPane crearPestanaConexiones() {
        // ── Tabla ──
        String[] columnas = {"Nodo A", "Relación", "Nodo B"};
        modeloTablaConexiones = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaConexiones = new JTable(modeloTablaConexiones);
        tablaConexiones.setFont(FUENTE_NORMAL);
        tablaConexiones.setRowHeight(24);
        tablaConexiones.getTableHeader().setFont(FUENTE_TITULO);
        tablaConexiones.getTableHeader().setBackground(AZUL_OSCURO);
        tablaConexiones.getTableHeader().setForeground(BLANCO);
        tablaConexiones.setSelectionBackground(AZUL_CLARO);
        tablaConexiones.setGridColor(new Color(220, 220, 220));

        JScrollPane scrollTabla = new JScrollPane(tablaConexiones);
        scrollTabla.setBorder(new TitledBorder(new LineBorder(AZUL_MEDIO), " Conexiones Existentes "));

        // ── Formulario ──
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(BLANCO);
        formulario.setBorder(new CompoundBorder(
            new TitledBorder(new LineBorder(AZUL_MEDIO), " Nueva Conexión "),
            new EmptyBorder(8, 12, 8, 12)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        comboNodoA   = new JComboBox<>();
        comboNodoB   = new JComboBox<>();
        campoEtiqueta = campoTexto("Ej: Conoce a, Trabaja en, Estudia con...");
        comboNodoA.setFont(FUENTE_NORMAL);
        comboNodoB.setFont(FUENTE_NORMAL);

        // Tipos de relación predefinidos
        String[] relaciones = {"Conoce a", "Trabaja en", "Pasante en", "Estudiante de",
                               "Colega", "Compañero de", "Asesora a", "Colabora con", "Interesado en"};
        JComboBox<String> comboRelacion = new JComboBox<>(relaciones);
        comboRelacion.setEditable(true);
        comboRelacion.setFont(FUENTE_NORMAL);
        comboRelacion.addActionListener(e ->
            campoEtiqueta.setText((String) comboRelacion.getSelectedItem()));

        gbc.gridx=0; gbc.gridy=0; gbc.weightx=0.35;
        formulario.add(etiqueta("Nodo A:"), gbc);
        gbc.gridx=1; gbc.weightx=0.65;
        formulario.add(comboNodoA, gbc);

        gbc.gridx=0; gbc.gridy=1;
        formulario.add(etiqueta("Tipo de relación:"), gbc);
        gbc.gridx=1;
        formulario.add(comboRelacion, gbc);

        gbc.gridx=0; gbc.gridy=2;
        formulario.add(etiqueta("Nodo B:"), gbc);
        gbc.gridx=1;
        formulario.add(comboNodoB, gbc);

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        botones.setBackground(BLANCO);

        JButton btnConectar  = boton("+ Conectar",    AZUL_MEDIO);
        JButton btnEliminar  = boton("✕ Eliminar",    new Color(211, 47, 47));

        btnConectar.addActionListener(e -> {
            String nodA = idDesdeComboCon(comboNodoA);
            String nodB = idDesdeComboCon(comboNodoB);
            String et   = (String) comboRelacion.getSelectedItem();
            String err  = controlador.agregarConexion(nodA, nodB,
                          (et == null || et.trim().isEmpty()) ? "Conoce a" : et.trim());
            if (err != null) { mostrarError(err); return; }
            actualizarTodo();
            mostrarExito("Conexión creada correctamente.");
        });

        btnEliminar.addActionListener(e -> {
            int fila = tablaConexiones.getSelectedRow();
            if (fila < 0) { mostrarError("Seleccione una conexión de la tabla para eliminar."); return; }
            String nomA = (String) modeloTablaConexiones.getValueAt(fila, 0);
            String nomB = (String) modeloTablaConexiones.getValueAt(fila, 2);
            // Buscar IDs por nombre
            String idA = idPorNombre(nomA), idB = idPorNombre(nomB);
            int conf = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la conexión entre \"" + nomA + "\" y \"" + nomB + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
            if (conf != JOptionPane.YES_OPTION) return;
            String err = controlador.eliminarConexion(idA, idB);
            if (err != null) { mostrarError(err); return; }
            actualizarTodo();
            mostrarExito("Conexión eliminada.");
        });

        botones.add(btnConectar);
        botones.add(btnEliminar);

        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setBackground(BLANCO);
        panelDerecho.add(formulario, BorderLayout.NORTH);
        panelDerecho.add(botones,    BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTabla, panelDerecho);
        split.setDividerLocation(560);
        split.setBorder(new EmptyBorder(8, 8, 8, 8));
        return split;
    }

    // ─── PESTAÑA 4: Algoritmos ────────────────────────────────────────────────
    private JPanel crearPestanaAlgoritmos() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(GRIS_FONDO);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // ── Panel de controles ──
        JPanel controles = new JPanel(new GridBagLayout());
        controles.setBackground(BLANCO);
        controles.setBorder(new CompoundBorder(
            new TitledBorder(new LineBorder(AZUL_MEDIO), " Parámetros del Algoritmo "),
            new EmptyBorder(8, 12, 8, 12)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        comboOrigen  = new JComboBox<>();
        comboDestino = new JComboBox<>();
        comboOrigen.setFont(FUENTE_NORMAL);
        comboDestino.setFont(FUENTE_NORMAL);

        // Fila 0: Nodo Origen
        gbc.gridx=0; gbc.gridy=0; gbc.weightx=0.25;
        controles.add(etiqueta("Nodo Origen:"), gbc);
        gbc.gridx=1; gbc.weightx=0.75;
        controles.add(comboOrigen, gbc);

        // Fila 1: Nodo Destino
        gbc.gridx=0; gbc.gridy=1; gbc.weightx=0.25;
        controles.add(etiqueta("Nodo Destino:"), gbc);
        gbc.gridx=1; gbc.weightx=0.75;
        controles.add(comboDestino, gbc);

        // ── Botones de algoritmos ──
        JPanel botAlg = new JPanel(new GridLayout(2, 2, 10, 8));
        botAlg.setBackground(BLANCO);
        botAlg.setBorder(new EmptyBorder(10, 0, 4, 0));

        JButton btnBFS       = boton("🔵 BFS — Ruta más corta",     AZUL_MEDIO);
        JButton btnBFSFull   = boton("🔵 BFS — Exploración completa", new Color(21, 101, 192));
        JButton btnDFS       = boton("🟢 DFS — Primer camino",       VERDE);
        JButton btnDFSFull   = boton("🟢 DFS — Exploración completa", new Color(27, 94, 32));
        JButton btnContactos = boton("👥 Ver contactos directos",    new Color(106, 27, 154));

        btnBFS.addActionListener(e ->
            areaResultado.setText(controlador.ejecutarBFS(
                idDesdeCombo(comboOrigen), idDesdeCombo(comboDestino))));

        btnBFSFull.addActionListener(e ->
            areaResultado.setText(controlador.ejecutarBFSCompleto(
                idDesdeCombo(comboOrigen))));

        btnDFS.addActionListener(e ->
            areaResultado.setText(controlador.ejecutarDFS(
                idDesdeCombo(comboOrigen), idDesdeCombo(comboDestino))));

        btnDFSFull.addActionListener(e ->
            areaResultado.setText(controlador.ejecutarDFSCompleto(
                idDesdeCombo(comboOrigen))));

        btnContactos.addActionListener(e ->
            areaResultado.setText(controlador.obtenerContactosDe(
                idDesdeCombo(comboOrigen))));

        botAlg.add(btnBFS);
        botAlg.add(btnBFSFull);
        botAlg.add(btnDFS);
        botAlg.add(btnDFSFull);

        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=2;
        controles.add(botAlg, gbc);
        gbc.gridy=3;
        controles.add(btnContactos, gbc);

        // ── Área de resultados ──
        areaResultado = new JTextArea();
        areaResultado.setFont(FUENTE_MONO);
        areaResultado.setEditable(false);
        areaResultado.setBackground(new Color(30, 30, 30));
        areaResultado.setForeground(new Color(180, 255, 180));
        areaResultado.setBorder(new EmptyBorder(10, 12, 10, 12));
        areaResultado.setText("Seleccione nodos y ejecute un algoritmo para ver el resultado...");

        JScrollPane scrollResultado = new JScrollPane(areaResultado);
        scrollResultado.setBorder(new TitledBorder(
            new LineBorder(new Color(50, 50, 50), 1), " Resultado del Algoritmo "));

        // Descripción de algoritmos
        JPanel infoAlg = new JPanel(new GridLayout(1, 2, 10, 0));
        infoAlg.setBackground(GRIS_FONDO);
        infoAlg.add(tarjetaInfo("BFS (Búsqueda en Amplitud)",
            "Encuentra la RUTA MÁS CORTA entre dos nodos (menor número de intermediarios). " +
            "Útil para: ¿Cuántos intermediarios necesito para llegar a esta empresa?",
            AZUL_CLARO, AZUL_OSCURO));
        infoAlg.add(tarjetaInfo("DFS (Búsqueda en Profundidad)",
            "Explora la red siguiendo conexiones hasta el fondo antes de retroceder. " +
            "Útil para: ¿Existe algún camino de contactos que llegue a B desde A?",
            VERDE_CLARO, VERDE));

        panel.add(controles,      BorderLayout.NORTH);
        panel.add(scrollResultado, BorderLayout.CENTER);
        panel.add(infoAlg,        BorderLayout.SOUTH);

        return panel;
    }

    // ─── Barra de estado ──────────────────────────────────────────────────────
    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(AZUL_OSCURO);
        barra.setBorder(new EmptyBorder(4, 12, 4, 12));

        labelResumen = new JLabel("Cargando...");
        labelResumen.setFont(FUENTE_NORMAL);
        labelResumen.setForeground(new Color(187, 222, 251));

        JLabel credito = new JLabel("UNEG — Técnicas de Programación III | Sección 2");
        credito.setFont(FUENTE_NORMAL);
        credito.setForeground(new Color(150, 180, 210));

        barra.add(labelResumen, BorderLayout.WEST);
        barra.add(credito,      BorderLayout.EAST);
        return barra;
    }

    // ─── Actualización global de la Vista ────────────────────────────────────
    public void actualizarTodo() {
        actualizarTablaNodos();
        actualizarTablaConexiones();
        actualizarCombos();
        if (panelGrafo != null) {
            panelGrafo.actualizarGrafo(controlador.getGrafo());
            panelGrafo.repaint();
        }
        labelResumen.setText(controlador.getResumen());
    }

    private void actualizarTablaNodos() {
        modeloTablaNodos.setRowCount(0);
        for (Nodo n : controlador.getNodos()) {
            modeloTablaNodos.addRow(new Object[]{
                n.getId(), n.getNombre(), n.getTipo().getEtiqueta(), n.getDescripcion()
            });
        }
    }

    private void actualizarTablaConexiones() {
        modeloTablaConexiones.setRowCount(0);
        for (var c : controlador.getGrafo().getConexiones()) {
            modeloTablaConexiones.addRow(new Object[]{
                c.getOrigen().getNombre(),
                c.getEtiqueta(),
                c.getDestino().getNombre()
            });
        }
    }

    private void actualizarCombos() {
        DefaultComboBoxModel<String> mOrigen  = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> mDestino = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> mNodoA   = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> mNodoB   = new DefaultComboBoxModel<>();

        for (Nodo n : controlador.getNodos()) {
            String item = n.getNombre() + " [" + n.getId() + "]";
            mOrigen.addElement(item);
            mDestino.addElement(item);
            mNodoA.addElement(item);
            mNodoB.addElement(item);
        }

        comboOrigen.setModel(mOrigen);
        comboDestino.setModel(mDestino);
        if (comboNodoA != null) comboNodoA.setModel(mNodoA);
        if (comboNodoB != null) comboNodoB.setModel(mNodoB);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    
    private String idDesdeCombo(JComboBox<String> combo) {
        Object sel = combo.getSelectedItem();
        if (sel == null) return null;
        String s = sel.toString();
        int a = s.lastIndexOf('['), b = s.lastIndexOf(']');
        if (a >= 0 && b > a) return s.substring(a + 1, b);
        return null;
    }

    
    private String idDesdeComboCon(JComboBox<String> combo) {
        return idDesdeCombo(combo);
    }

    private String idPorNombre(String nombre) {
        for (Nodo n : controlador.getNodos()) {
            if (n.getNombre().equals(nombre)) return n.getId();
        }
        return null;
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FUENTE_TITULO);
        return l;
    }

    private JTextField campoTexto(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(FUENTE_NORMAL);
        tf.setToolTipText(placeholder);
        tf.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200)),
            new EmptyBorder(3, 6, 3, 6)));
        return tf;
    }

    private JButton boton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(FUENTE_TITULO);
        btn.setBackground(color);
        btn.setForeground(BLANCO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        // Efecto hover
        btn.addMouseListener(new MouseAdapter() {
            final Color original = color;
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(original.darker());
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(original);
            }
        });
        return btn;
    }

    private JPanel tarjetaInfo(String titulo, String descripcion, Color fondo, Color colorTitulo) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(fondo);
        card.setBorder(new CompoundBorder(
            new LineBorder(colorTitulo, 1),
            new EmptyBorder(8, 10, 8, 10)));
        JLabel lTitulo = new JLabel(titulo);
        lTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lTitulo.setForeground(colorTitulo);
        JTextArea lDesc = new JTextArea(descripcion);
        lDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lDesc.setLineWrap(true);
        lDesc.setWrapStyleWord(true);
        lDesc.setEditable(false);
        lDesc.setOpaque(false);
        card.add(lTitulo, BorderLayout.NORTH);
        card.add(lDesc,   BorderLayout.CENTER);
        return card;
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
}