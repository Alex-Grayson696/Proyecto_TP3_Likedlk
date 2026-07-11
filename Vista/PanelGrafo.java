package Vista;

import Modelo.Conexion;
import Modelo.Grafo;
import Modelo.Nodo;
import Modelo.TipoNodo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;


public class PanelGrafo extends JPanel {

    // ─── Colores ─────────────────────────────────────────────────────────────
    private static final Color COLOR_ESTUDIANTE  = new Color(25,  118, 210);
    private static final Color COLOR_PROFESOR    = new Color(27,  94,  32);
    private static final Color COLOR_EMPRESA     = new Color(230, 81,  0);
    private static final Color COLOR_ARISTA      = new Color(120, 120, 120);
    private static final Color COLOR_SELECCION   = new Color(255, 193, 7);
    private static final Color COLOR_FONDO       = new Color(252, 252, 255);
    private static final Color COLOR_ETIQUETA    = new Color(60,  60,  60);
    private static final Font  FUENTE_NODO       = new Font("Segoe UI", Font.BOLD,  11);
    private static final Font  FUENTE_ARISTA     = new Font("Segoe UI", Font.ITALIC, 9);

    private static final int RADIO_NODO = 28;

    // ─── Estado interno ───────────────────────────────────────────────────────
    private Grafo                    grafo;
    private final Map<String, Point> posiciones  = new LinkedHashMap<>();
    private Nodo                     nodoSel     = null;   // nodo seleccionado
    private Nodo                     nodoDrag    = null;   // nodo siendo arrastrado
    private Point                    offsetDrag  = new Point();
    private JTextArea                areaInfo;             // panel info externo (opcional)

    // ─── Constructor ──────────────────────────────────────────────────────────
    public PanelGrafo(Grafo grafo) {
        this.grafo = grafo;
        setBackground(COLOR_FONDO);
        setPreferredSize(new Dimension(700, 480));
        configurarMouse();
    }

    // ─── Actualizar grafo (llamado por la Vista principal) ────────────────────
    public void actualizarGrafo(Grafo grafo) {
        this.grafo = grafo;
        // Añadir posiciones para nodos nuevos, conservar posiciones de los existentes
        Set<String> idsActuales = posiciones.keySet();
        List<Nodo>  sinPos = new ArrayList<>();
        for (Nodo n : grafo.getNodos()) {
            if (!idsActuales.contains(n.getId())) sinPos.add(n);
        }
        if (!sinPos.isEmpty()) distribuirNuevosNodos(sinPos);
        // Eliminar posiciones de nodos ya borrados
        posiciones.keySet().retainAll(
            grafo.getNodos().stream().map(Nodo::getId)
                 .collect(java.util.stream.Collectors.toSet())
        );
        repaint();
    }

    
    private void distribuirCirculo() {
        posiciones.clear();
        List<Nodo> lista = grafo.getNodos();
        if (lista.isEmpty()) return;
        int cx = getWidth()  > 0 ? getWidth()  / 2 : 350;
        int cy = getHeight() > 0 ? getHeight() / 2 : 240;
        int r  = Math.min(cx, cy) - RADIO_NODO - 30;
        for (int i = 0; i < lista.size(); i++) {
            double ang = 2 * Math.PI * i / lista.size() - Math.PI / 2;
            posiciones.put(lista.get(i).getId(),
                new Point((int)(cx + r * Math.cos(ang)),
                          (int)(cy + r * Math.sin(ang))));
        }
    }

    
    private void distribuirNuevosNodos(List<Nodo> nuevos) {
        int cx = getWidth()  > 0 ? getWidth()  / 2 : 350;
        int cy = getHeight() > 0 ? getHeight() / 2 : 240;
        int total = grafo.getNodos().size();
        int r = Math.min(cx, cy) - RADIO_NODO - 30;
        int idx = 0;
        for (Nodo n : grafo.getNodos()) {
            if (!posiciones.containsKey(n.getId())) {
                double ang = 2 * Math.PI * idx / total - Math.PI / 2;
                posiciones.put(n.getId(),
                    new Point((int)(cx + r * Math.cos(ang)),
                              (int)(cy + r * Math.sin(ang))));
            }
            idx++;
        }
    }

    // ─── Pintar ───────────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (posiciones.isEmpty() && !grafo.getNodos().isEmpty()) {
            distribuirCirculo();
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        dibujarAristas(g2);
        dibujarNodos(g2);
        dibujarInfo(g2);
    }

    private void dibujarAristas(Graphics2D g2) {
        g2.setStroke(new BasicStroke(1.6f));
        for (Conexion c : grafo.getConexiones()) {
            Point p1 = posiciones.get(c.getOrigen().getId());
            Point p2 = posiciones.get(c.getDestino().getId());
            if (p1 == null || p2 == null) continue;

            // Color especial si algún nodo está seleccionado
            boolean destacada = nodoSel != null &&
                (c.getOrigen().equals(nodoSel) || c.getDestino().equals(nodoSel));
            g2.setColor(destacada ? COLOR_SELECCION : COLOR_ARISTA);
            g2.setStroke(new BasicStroke(destacada ? 2.5f : 1.4f));
            g2.drawLine(p1.x, p1.y, p2.x, p2.y);

            // Etiqueta de la arista en el punto medio
            int mx = (p1.x + p2.x) / 2;
            int my = (p1.y + p2.y) / 2;
            g2.setFont(FUENTE_ARISTA);
            g2.setColor(destacada ? COLOR_EMPRESA : COLOR_ETIQUETA);
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(c.getEtiqueta());
            // Fondo pequeño para la etiqueta
            g2.setColor(new Color(252, 252, 255, 200));
            g2.fillRoundRect(mx - tw/2 - 3, my - 9, tw + 6, 14, 4, 4);
            g2.setColor(destacada ? COLOR_EMPRESA : COLOR_ETIQUETA);
            g2.drawString(c.getEtiqueta(), mx - tw/2, my + 2);
        }
    }

    private void dibujarNodos(Graphics2D g2) {
        for (Nodo n : grafo.getNodos()) {
            Point p = posiciones.get(n.getId());
            if (p == null) continue;

            Color colorBase = colorPorTipo(n.getTipo());
            boolean sel     = n.equals(nodoSel);

            // Sombra
            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillOval(p.x - RADIO_NODO + 3, p.y - RADIO_NODO + 3,
                        RADIO_NODO * 2, RADIO_NODO * 2);

            // Círculo principal
            g2.setColor(sel ? COLOR_SELECCION : colorBase);
            g2.fillOval(p.x - RADIO_NODO, p.y - RADIO_NODO, RADIO_NODO * 2, RADIO_NODO * 2);

            // Borde
            g2.setColor(sel ? COLOR_SELECCION.darker() : colorBase.darker());
            g2.setStroke(new BasicStroke(sel ? 3f : 1.5f));
            g2.drawOval(p.x - RADIO_NODO, p.y - RADIO_NODO, RADIO_NODO * 2, RADIO_NODO * 2);

            // Icono de tipo
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            String icono = switch (n.getTipo()) {
                case ESTUDIANTE -> "E";
                case PROFESOR   -> "P";
                case EMPRESA    -> "C";
            };
            FontMetrics fmI = g2.getFontMetrics();
            g2.drawString(icono, p.x - fmI.stringWidth(icono)/2, p.y - 6);

            // Nombre debajo del ícono — truncado dinámico según el espacio real disponible
            g2.setFont(FUENTE_NODO);
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            // Espacio horizontal usable dentro del círculo (diámetro menos margen de 8px)
            int anchoDisponible = (RADIO_NODO * 2) - 8;
            String nombreMostrado = truncarNombre(n.getNombre(), fm, anchoDisponible);
            g2.drawString(nombreMostrado, p.x - fm.stringWidth(nombreMostrado)/2, p.y + 9);

            // Etiqueta debajo del nodo
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            g2.setColor(colorBase.darker());
            String id = n.getId();
            g2.drawString(id, p.x - g2.getFontMetrics().stringWidth(id)/2, p.y + RADIO_NODO + 13);
        }
    }

    private void dibujarInfo(Graphics2D g2) {
        if (nodoSel == null) return;
        String info = nodoSel.getNombre() + " — " + nodoSel.getTipo()
                    + "\n" + nodoSel.getDescripcion()
                    + "\nContactos: " + grafo.vecinosDe(nodoSel.getId()).size();
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(new Color(30, 30, 30, 200));
        g2.fillRoundRect(10, 10, 240, 52, 8, 8);
        g2.setColor(Color.WHITE);
        int y = 26;
        for (String linea : info.split("\n")) {
            g2.drawString(linea, 18, y);
            y += 16;
        }
    }

    private Color colorPorTipo(TipoNodo tipo) {
        return switch (tipo) {
            case ESTUDIANTE -> COLOR_ESTUDIANTE;
            case PROFESOR   -> COLOR_PROFESOR;
            case EMPRESA    -> COLOR_EMPRESA;
        };
    }

    // ─── Interacción con el mouse ─────────────────────────────────────────────
    private void configurarMouse() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                nodoDrag  = nodoCercano(e.getPoint(), RADIO_NODO + 5);
                nodoSel   = nodoDrag;
                if (nodoDrag != null) {
                    Point pos = posiciones.get(nodoDrag.getId());
                    offsetDrag.setLocation(e.getX() - pos.x, e.getY() - pos.y);
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                nodoDrag = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (nodoDrag != null) {
                    int nx = e.getX() - offsetDrag.x;
                    int ny = e.getY() - offsetDrag.y;
                    // Limitar dentro del panel
                    nx = Math.max(RADIO_NODO, Math.min(getWidth()  - RADIO_NODO, nx));
                    ny = Math.max(RADIO_NODO, Math.min(getHeight() - RADIO_NODO, ny));
                    posiciones.put(nodoDrag.getId(), new Point(nx, ny));
                    repaint();
                }
            }
        });

        // Tooltip al pasar el mouse
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Nodo cerca = nodoCercano(e.getPoint(), RADIO_NODO + 4);
                if (cerca != null) {
                    setToolTipText("<html><b>" + cerca.getNombre() + "</b><br>"
                        + cerca.getTipo() + "<br>"
                        + cerca.getDescripcion() + "<br>"
                        + "Contactos: " + grafo.vecinosDe(cerca.getId()).size()
                        + "</html>");
                } else {
                    setToolTipText(null);
                }
            }
        });
    }

    private Nodo nodoCercano(Point click, int umbral) {
        for (Nodo n : grafo.getNodos()) {
            Point p = posiciones.get(n.getId());
            if (p != null && click.distance(p) <= umbral) return n;
        }
        return null;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        // Primera distribución cuando el panel ya tiene tamaño real
        if (posiciones.isEmpty() && !grafo.getNodos().isEmpty()) {
            distribuirCirculo();
        }
    }

    
    private String truncarNombre(String nombre, FontMetrics fm, int anchoMax) {
        if (nombre == null || nombre.isEmpty()) return "";
        // Si cabe completo, devolverlo sin cambios
        if (fm.stringWidth(nombre) <= anchoMax) return nombre;
        // Sufijo de truncado
        String sufijo = "...";
        int anchoSufijo = fm.stringWidth(sufijo);
        // Si ni el sufijo cabe, devolver vacío (círculo muy pequeño)
        if (anchoSufijo >= anchoMax) return "";
        // Recortar carácter a carácter hasta que (texto + "...") quepa
        StringBuilder sb = new StringBuilder(nombre);
        while (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            if (fm.stringWidth(sb.toString()) + anchoSufijo <= anchoMax) {
                return sb.toString() + sufijo;
            }
        }
        return sufijo;
    }
}