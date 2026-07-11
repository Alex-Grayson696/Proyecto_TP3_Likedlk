package Persistencia;

import Modelo.Grafo;
import Modelo.Nodo;
import Modelo.TipoNodo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class GestorDatos {

    private static final String ARCHIVO_NODOS     = "nodos.csv";
    private static final String ARCHIVO_CONEXIONES = "conexiones.csv";

    // ─── GUARDAR ─────────────────────────────────────────────────────────────

    public static void guardarTodo(Grafo grafo) {
        guardarNodos(grafo);
        guardarConexiones(grafo);
    }

    private static void guardarNodos(Grafo grafo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_NODOS))) {
            pw.println("id,nombre,tipo,descripcion");
            for (Nodo n : grafo.getNodos()) {
                pw.println(
                    esc(n.getId())          + "," +
                    esc(n.getNombre())      + "," +
                    esc(n.getTipo().name()) + "," +
                    esc(n.getDescripcion())
                );
            }
        } catch (IOException e) {
            System.err.println("[GestorDatos] Error guardando nodos: " + e.getMessage());
        }
    }

    private static void guardarConexiones(Grafo grafo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_CONEXIONES))) {
            pw.println("idOrigen,idDestino,etiqueta");
            for (var c : grafo.getConexiones()) {
                pw.println(
                    esc(c.getOrigen().getId())  + "," +
                    esc(c.getDestino().getId()) + "," +
                    esc(c.getEtiqueta())
                );
            }
        } catch (IOException e) {
            System.err.println("[GestorDatos] Error guardando conexiones: " + e.getMessage());
        }
    }

    // ─── CARGAR ──────────────────────────────────────────────────────────────

    public static void cargarTodo(Grafo grafo) {
        cargarNodos(grafo);
        cargarConexiones(grafo);
    }

    private static void cargarNodos(Grafo grafo) {
        File f = new File(ARCHIVO_NODOS);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                if (primera) { primera = false; continue; }
                String[] c = splitCSV(linea);
                if (c.length < 4) continue;
                Nodo n = new Nodo(desc(c[0]), desc(c[1]),
                                  TipoNodo.valueOf(desc(c[2]).toUpperCase()),
                                  desc(c[3]));
                grafo.agregarNodo(n);
            }
        } catch (IOException e) {
            System.err.println("[GestorDatos] Error cargando nodos: " + e.getMessage());
        }
    }

    private static void cargarConexiones(Grafo grafo) {
        File f = new File(ARCHIVO_CONEXIONES);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                if (primera) { primera = false; continue; }
                String[] c = splitCSV(linea);
                if (c.length < 3) continue;
                grafo.agregarConexion(desc(c[0]), desc(c[1]), desc(c[2]));
            }
        } catch (IOException e) {
            System.err.println("[GestorDatos] Error cargando conexiones: " + e.getMessage());
        }
    }

    // ─── Utilidades CSV ──────────────────────────────────────────────────────

    private static String esc(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private static String desc(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length() - 1).replace("\"\"", "\"");
        }
        return s;
    }

    private static String[] splitCSV(String linea) {
        List<String> tokens = new ArrayList<>();
        boolean enComillas = false;
        StringBuilder sb = new StringBuilder();
        for (char ch : linea.toCharArray()) {
            if (ch == '"') {
                enComillas = !enComillas;
            } else if (ch == ',' && !enComillas) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(ch);
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }
}