package Controlador;

import Modelo.Grafo;
import Modelo.Nodo;
import Modelo.TipoNodo;
import Persistencia.GestorDatos;

import java.util.List;


public class RedControlador {

    private final Grafo grafo;

    // Contador para generar IDs únicos automáticamente
    private int contadorEstudiante = 0;
    private int contadorProfesor   = 0;
    private int contadorEmpresa    = 0;

    public RedControlador() {
        grafo = new Grafo();
        GestorDatos.cargarTodo(grafo);
        // Sincronizar contadores con los IDs ya cargados
        sincronizarContadores();
        // Si el grafo está vacío, cargar datos de demostración
        if (grafo.estaVacio()) {
            cargarDatosDemostracion();
        }
    }

    // ─── CRUD Nodos ──────────────────────────────────────────────────────────

    
    public String agregarNodo(String nombre, String tipoStr, String descripcion) {
        if (nombre == null || nombre.trim().isEmpty())
            return "El nombre no puede estar vacío.";

        String id    = generarId(TipoNodo.desdeTexto(tipoStr));
        Nodo   nodo  = new Nodo(id, nombre.trim(), TipoNodo.desdeTexto(tipoStr),
                                descripcion == null ? "" : descripcion.trim());
        if (!grafo.agregarNodo(nodo))
            return "Ya existe un nodo con ese ID.";

        GestorDatos.guardarTodo(grafo);
        return null; // éxito
    }

    
    public String eliminarNodo(String id) {
        if (id == null || id.trim().isEmpty()) return "Seleccione un nodo.";
        if (!grafo.eliminarNodo(id.trim()))    return "Nodo no encontrado: " + id;
        GestorDatos.guardarTodo(grafo);
        return null;
    }

    
    public String editarNodo(String id, String nuevoNombre, String nuevaDesc) {
        Nodo n = grafo.buscarNodoPorId(id);
        if (n == null) return "Nodo no encontrado.";
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty())
            return "El nombre no puede estar vacío.";
        n.setNombre(nuevoNombre.trim());
        n.setDescripcion(nuevaDesc == null ? "" : nuevaDesc.trim());
        GestorDatos.guardarTodo(grafo);
        return null;
    }

    // ─── CRUD Conexiones ─────────────────────────────────────────────────────

    
    public String agregarConexion(String idA, String idB, String etiqueta) {
        if (idA == null || idB == null || idA.trim().isEmpty() || idB.trim().isEmpty())
            return "Seleccione los dos nodos a conectar.";
        if (idA.trim().equalsIgnoreCase(idB.trim()))
            return "Un nodo no puede conectarse a sí mismo.";
        if (etiqueta == null || etiqueta.trim().isEmpty()) etiqueta = "Conoce a";

        if (!grafo.agregarConexion(idA.trim(), idB.trim(), etiqueta.trim()))
            return "Ya existe una conexión entre esos nodos, o alguno no fue encontrado.";

        GestorDatos.guardarTodo(grafo);
        return null;
    }

   
    public String eliminarConexion(String idA, String idB) {
        if (idA == null || idB == null) return "Seleccione los nodos.";
        if (!grafo.eliminarConexion(idA.trim(), idB.trim()))
            return "No existe conexión entre esos nodos.";
        GestorDatos.guardarTodo(grafo);
        return null;
    }

    // ─── Algoritmos ──────────────────────────────────────────────────────────

    
    public String ejecutarBFS(String idOrigen, String idDestino) {
        if (idOrigen == null || idDestino == null)
            return "Seleccione nodo origen y destino.";

        List<Nodo> ruta = grafo.bfs(idOrigen.trim(), idDestino.trim());

        if (ruta.isEmpty())
            return "BFS: No existe camino entre los nodos seleccionados.";

        StringBuilder sb = new StringBuilder();
        sb.append("══════ RESULTADO BFS ══════\n");
        sb.append("Ruta de recomendación más corta\n");
        sb.append("Saltos: ").append(ruta.size() - 1).append("\n\n");
        sb.append(grafo.describirRuta(ruta));
        return sb.toString();
    }

    
    public String ejecutarBFSCompleto(String idOrigen) {
        if (idOrigen == null) return "Seleccione un nodo origen.";
        List<String> resultado = grafo.bfsCompleto(idOrigen.trim());
        if (resultado.isEmpty()) return "Nodo no encontrado.";
        StringBuilder sb = new StringBuilder();
        sb.append("══════ RECORRIDO BFS COMPLETO ══════\n");
        sb.append("Grados de separación desde: ")
          .append(grafo.buscarNodoPorId(idOrigen).getNombre()).append("\n\n");
        resultado.forEach(r -> sb.append(r).append("\n"));
        return sb.toString();
    }

    
    public String ejecutarDFS(String idOrigen, String idDestino) {
        if (idOrigen == null || idDestino == null)
            return "Seleccione nodo origen y destino.";

        List<Nodo> ruta = grafo.dfs(idOrigen.trim(), idDestino.trim());

        if (ruta.isEmpty())
            return "DFS: No existe camino entre los nodos seleccionados.";

        StringBuilder sb = new StringBuilder();
        sb.append("══════ RESULTADO DFS ══════\n");
        sb.append("Ruta encontrada por exploración en profundidad\n");
        sb.append("Saltos: ").append(ruta.size() - 1).append("\n\n");
        sb.append(grafo.describirRuta(ruta));
        return sb.toString();
    }

    
    public String ejecutarDFSCompleto(String idOrigen) {
        if (idOrigen == null) return "Seleccione un nodo origen.";
        List<String> resultado = grafo.dfsCompleto(idOrigen.trim());
        if (resultado.isEmpty()) return "Nodo no encontrado.";
        StringBuilder sb = new StringBuilder();
        sb.append("══════ RECORRIDO DFS COMPLETO ══════\n");
        sb.append("Exploración en profundidad desde: ")
          .append(grafo.buscarNodoPorId(idOrigen).getNombre()).append("\n\n");
        resultado.forEach(r -> sb.append(r).append("\n"));
        return sb.toString();
    }

    
    public String obtenerContactosDe(String id) {
        if (id == null) return "Seleccione un nodo.";
        Nodo nodo = grafo.buscarNodoPorId(id.trim());
        if (nodo == null) return "Nodo no encontrado.";
        List<Nodo> vecinos = grafo.vecinosDe(id.trim());
        StringBuilder sb = new StringBuilder();
        sb.append("Contactos directos de ").append(nodo.getNombre()).append(":\n\n");
        if (vecinos.isEmpty()) {
            sb.append("  (sin conexiones aún)");
        } else {
            for (Nodo v : vecinos) {
                var con = grafo.buscarConexion(nodo, v);
                String et = con != null ? con.getEtiqueta() : "";
                sb.append("  • ").append(v.getNombre())
                  .append(" [").append(v.getTipo()).append("]");
                if (!et.isEmpty()) sb.append(" — ").append(et);
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    // ─── Consultas para la Vista ─────────────────────────────────────────────

    public List<Nodo>     getNodos()      { return grafo.getNodos(); }
    public Grafo          getGrafo()      { return grafo; }
    public int            totalNodos()    { return grafo.totalNodos(); }
    public int            totalAristas()  { return grafo.totalAristas(); }

    public String getResumen() {
        return String.format("Red: %d nodos · %d conexiones",
                             grafo.totalNodos(), grafo.totalAristas());
    }


    
    public String ejecutarSugerencias(String idNodo) {
        if (idNodo == null || idNodo.trim().isEmpty())
            return "Seleccione un nodo para generar sugerencias.";

        Nodo base = grafo.buscarNodoPorId(idNodo.trim());
        if (base == null)
            return "Nodo no encontrado.";
        
        if (base.getTipo() == TipoNodo.ESTUDIANTE) {
            return "══════ FUNCIÓN NO DISPONIBLE ══════\n\n"
                 + "Las sugerencias de networking solo están disponibles\n"
                 + "para nodos de tipo Empresa y Profesor.\n\n"
                 + "Motivo:\n"
                 + "  • Empresa  → descubre estudiantes para reclutar\n"
                 + "            a través de sus profesores contacto.\n"
                 + "  • Profesor → descubre estudiantes de colegas\n"
                 + "            para recomendar o incorporar a proyectos.\n"
                 + "  • Estudiante → esta función no aplica a este tipo de nodo.\n\n"
                 + "Seleccione un nodo de tipo Empresa o Profesor.";
        }

        List<Grafo.ResultadoSugerencia> sugerencias = grafo.sugerirEstudiantes(idNodo.trim());

        StringBuilder sb = new StringBuilder();
        sb.append("══════ SUGERENCIAS DE NETWORKING ══════\n");
        sb.append("Estudiantes recomendados para: ")
          .append(base.getNombre())
          .append(" [").append(base.getTipo()).append("]\n");
        sb.append("Basado en conexiones de 2 saltos (BFS nivel 2)\n\n");

        if (sugerencias.isEmpty()) {
            sb.append("No se encontraron estudiantes sugeridos.\n");
            sb.append("  Posibles causas:\n");
            sb.append("  • El nodo no tiene conexiones con Profesores.\n");
            sb.append("  • Los Profesores conectados no tienen Estudiantes en la red.\n");
            sb.append("  • Todos los Estudiantes cercanos ya están conectados directamente.\n");
        } else {
            sb.append(String.format("  %-28s  %s\n", "Estudiante", "Vía (intermediario)"));
            sb.append("  ").append("─".repeat(54)).append("\n");
            for (Grafo.ResultadoSugerencia r : sugerencias) {
                sb.append(String.format("  %-28s  → %s [%s]\n",
                    r.estudiante.getNombre(),
                    r.intermediario.getNombre(),
                    r.intermediario.getTipo()));
            }
            sb.append("\n  Total de sugerencias: ").append(sugerencias.size());
        }
        return sb.toString();
    }

    // ─── Generación de IDs ───────────────────────────────────────────────────

    private String generarId(TipoNodo tipo) {
        return switch (tipo) {
            case ESTUDIANTE -> "E" + String.format("%03d", ++contadorEstudiante);
            case PROFESOR   -> "P" + String.format("%03d", ++contadorProfesor);
            case EMPRESA    -> "C" + String.format("%03d", ++contadorEmpresa);
        };
    }

    private void sincronizarContadores() {
        for (Nodo n : grafo.getNodos()) {
            String id = n.getId();
            try {
                int num = Integer.parseInt(id.substring(1));
                switch (n.getTipo()) {
                    case ESTUDIANTE -> contadorEstudiante = Math.max(contadorEstudiante, num);
                    case PROFESOR   -> contadorProfesor   = Math.max(contadorProfesor,   num);
                    case EMPRESA    -> contadorEmpresa    = Math.max(contadorEmpresa,    num);
                }
            } catch (Exception ignored) {}
        }
    }

    // ─── Datos de demostración ───────────────────────────────────────────────

    private void cargarDatosDemostracion() {
        // Estudiantes
        agregarNodo("Ana Martínez",    "Estudiante", "Ingeniería en Informática");
        agregarNodo("Luis Pérez",      "Estudiante", "Sistemas de Información");
        agregarNodo("María González",  "Estudiante", "Ingeniería Industrial");
        agregarNodo("Carlos Rojas",    "Estudiante", "Administración");
        // Profesores
        agregarNodo("Prof. Dubraska Roca", "Profesor", "Técnicas de Programación");
        agregarNodo("Prof. Juan Mora",     "Profesor", "Bases de Datos");
        agregarNodo("Prof. Sandra López",  "Profesor", "Redes y Comunicaciones");
        // Empresas
        agregarNodo("Tech Solutions C.A.", "Empresa", "Desarrollo de Software");
        agregarNodo("Banesco",             "Empresa", "Sector Bancario");
        agregarNodo("CVG Ferrominera",     "Empresa", "Minería e Industria");

        // Conexiones
        agregarConexion("E001", "P001", "Estudiante de");
        agregarConexion("E001", "C001", "Pasante en");
        agregarConexion("E002", "P001", "Estudiante de");
        agregarConexion("E002", "P002", "Conoce a");
        agregarConexion("E003", "P003", "Estudiante de");
        agregarConexion("E003", "C003", "Interesada en");
        agregarConexion("E004", "C002", "Practicante en");
        agregarConexion("E004", "E002", "Compañero de");
        agregarConexion("P001", "P002", "Colega");
        agregarConexion("P002", "C001", "Asesora a");
        agregarConexion("P003", "C002", "Colabora con");
        agregarConexion("E001", "E002", "Amigo de");
        agregarConexion("E002", "E003", "Compañera de");
    }
}