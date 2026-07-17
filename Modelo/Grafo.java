package Modelo;

import java.util.*;


public class Grafo {

    // ─── Estructura de datos ────────────────────────────────────────────────
    private final Map<Nodo, List<Nodo>> adyacencia = new LinkedHashMap<>();
    private final List<Nodo>            nodos       = new ArrayList<>();
    private final List<Conexion>        conexiones  = new ArrayList<>();

    // ─── CRUD Nodos ─────────────────────────────────────────────────────────

    
    public boolean agregarNodo(Nodo nodo) {
        if (buscarNodoPorId(nodo.getId()) != null) return false;
        nodos.add(nodo);
        adyacencia.put(nodo, new ArrayList<>());
        return true;
    }

    
    public boolean eliminarNodo(String id) {
        Nodo nodo = buscarNodoPorId(id);
        if (nodo == null) return false;

        // Eliminar de la lista de adyacencia de sus vecinos
        for (Nodo vecino : adyacencia.getOrDefault(nodo, new ArrayList<>())) {
            List<Nodo> listaVecino = adyacencia.get(vecino);
            if (listaVecino != null) listaVecino.remove(nodo);
        }

        // Eliminar conexiones que lo involucren
        conexiones.removeIf(c -> c.getOrigen().equals(nodo) || c.getDestino().equals(nodo));

        adyacencia.remove(nodo);
        nodos.remove(nodo);
        return true;
    }

    public Nodo buscarNodoPorId(String id) {
        for (Nodo n : nodos) {
            if (n.getId().equalsIgnoreCase(id.trim())) return n;
        }
        return null;
    }

    // ─── CRUD Conexiones ────────────────────────────────────────────────────

    
    public boolean agregarConexion(String idOrigen, String idDestino, String etiqueta) {
        Nodo o = buscarNodoPorId(idOrigen);
        Nodo d = buscarNodoPorId(idDestino);
        if (o == null || d == null || o.equals(d)) return false;

        // Evitar duplicados
        for (Conexion c : conexiones) {
            if (c.conecta(o, d)) return false;
        }

        Conexion con = new Conexion(o, d, etiqueta);
        conexiones.add(con);
        adyacencia.get(o).add(d);
        adyacencia.get(d).add(o);
        return true;
    }

    
    public boolean eliminarConexion(String idA, String idB) {
        Nodo a = buscarNodoPorId(idA);
        Nodo b = buscarNodoPorId(idB);
        if (a == null || b == null) return false;

        boolean removed = conexiones.removeIf(c -> c.conecta(a, b));
        if (removed) {
            adyacencia.getOrDefault(a, new ArrayList<>()).remove(b);
            adyacencia.getOrDefault(b, new ArrayList<>()).remove(a);
        }
        return removed;
    }

    public Conexion buscarConexion(Nodo a, Nodo b) {
        for (Conexion c : conexiones) {
            if (c.conecta(a, b)) return c;
        }
        return null;
    }

    // ─── BFS — Ruta de recomendación más corta (menos saltos) ───────────────

    
    public List<Nodo> bfs(String idOrigen, String idDestino) {
        Nodo origen  = buscarNodoPorId(idOrigen);
        Nodo destino = buscarNodoPorId(idDestino);
        if (origen == null || destino == null) return Collections.emptyList();
        if (origen.equals(destino))            return List.of(origen);

        Map<Nodo, Nodo> padres   = new LinkedHashMap<>();
        Queue<Nodo>     cola     = new LinkedList<>();
        Set<Nodo>       visitados = new LinkedHashSet<>();

        cola.add(origen);
        visitados.add(origen);
        padres.put(origen, null);

        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();
            if (actual.equals(destino)) break;

            for (Nodo vecino : adyacencia.getOrDefault(actual, Collections.emptyList())) {
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    padres.put(vecino, actual);
                    cola.add(vecino);
                }
            }
        }

        return reconstruirCamino(padres, destino);
    }

    
    public List<String> bfsCompleto(String idOrigen) {
        Nodo origen = buscarNodoPorId(idOrigen);
        if (origen == null) return Collections.emptyList();

        List<String>  resultado = new ArrayList<>();
        Queue<Nodo>   cola      = new LinkedList<>();
        Map<Nodo,Integer> nivel = new LinkedHashMap<>();

        cola.add(origen);
        nivel.put(origen, 0);
        resultado.add("Nivel 0 — " + origen.getNombre() + " [" + origen.getTipo() + "]");

        while (!cola.isEmpty()) {
            Nodo actual    = cola.poll();
            int  nivelAct  = nivel.get(actual);

            for (Nodo vecino : adyacencia.getOrDefault(actual, Collections.emptyList())) {
                if (!nivel.containsKey(vecino)) {
                    nivel.put(vecino, nivelAct + 1);
                    cola.add(vecino);
                    resultado.add("Nivel " + (nivelAct + 1) + " — "
                        + vecino.getNombre() + " [" + vecino.getTipo() + "]"
                        + "  (via " + actual.getNombre() + ")");
                }
            }
        }
        return resultado;
    }

    // ─── DFS — Exploración profunda de la red ────────────────────────────────

    
    public List<Nodo> dfs(String idOrigen, String idDestino) {
        Nodo origen  = buscarNodoPorId(idOrigen);
        Nodo destino = buscarNodoPorId(idDestino);
        if (origen == null || destino == null) return Collections.emptyList();
        if (origen.equals(destino))            return List.of(origen);

        Map<Nodo, Nodo> padres   = new LinkedHashMap<>();
        Set<Nodo>       visitados = new LinkedHashSet<>();
        padres.put(origen, null);

        dfsRec(origen, destino, visitados, padres);

        return reconstruirCamino(padres, destino);
    }

    private boolean dfsRec(Nodo actual, Nodo destino,
                            Set<Nodo> visitados, Map<Nodo, Nodo> padres) {
        visitados.add(actual);
        if (actual.equals(destino)) return true;

        for (Nodo vecino : adyacencia.getOrDefault(actual, Collections.emptyList())) {
            if (!visitados.contains(vecino)) {
                padres.put(vecino, actual);
                if (dfsRec(vecino, destino, visitados, padres)) return true;
            }
        }
        return false;
    }

    
    public List<String> dfsCompleto(String idOrigen) {
        Nodo origen = buscarNodoPorId(idOrigen);
        if (origen == null) return Collections.emptyList();

        List<String> resultado = new ArrayList<>();
        Set<Nodo>    visitados = new LinkedHashSet<>();
        dfsCompletoRec(origen, visitados, resultado, 0);
        return resultado;
    }

    private void dfsCompletoRec(Nodo actual, Set<Nodo> visitados,
                                 List<String> resultado, int profundidad) {
        visitados.add(actual);
        String prefijo = "  ".repeat(profundidad) + (profundidad == 0 ? "►" : "└─");
        resultado.add(prefijo + " " + actual.getNombre() + " [" + actual.getTipo() + "]");

        for (Nodo vecino : adyacencia.getOrDefault(actual, Collections.emptyList())) {
            if (!visitados.contains(vecino)) {
                dfsCompletoRec(vecino, visitados, resultado, profundidad + 1);
            }
        }
    }

    // ─── Utilidades ──────────────────────────────────────────────────────────

    private List<Nodo> reconstruirCamino(Map<Nodo, Nodo> padres, Nodo destino) {
        if (!padres.containsKey(destino)) return Collections.emptyList();
        LinkedList<Nodo> camino = new LinkedList<>();
        Nodo actual = destino;
        while (actual != null) {
            camino.addFirst(actual);
            actual = padres.get(actual);
        }
        return camino;
    }

    
    public String describirRuta(List<Nodo> ruta) {
        if (ruta.isEmpty()) return "No existe camino entre los nodos seleccionados.";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ruta.size(); i++) {
            Nodo n = ruta.get(i);
            sb.append(n.getNombre()).append(" [").append(n.getTipo()).append("]");
            if (i < ruta.size() - 1) {
                Conexion c = buscarConexion(n, ruta.get(i + 1));
                String etiqueta = (c != null) ? c.getEtiqueta() : "conectado con";
                sb.append("\n  ──[").append(etiqueta).append("]──►\n");
            }
        }
        return sb.toString();
    }

    
    public List<Nodo> vecinosDe(String id) {
        Nodo n = buscarNodoPorId(id);
        if (n == null) return Collections.emptyList();
        return Collections.unmodifiableList(adyacencia.getOrDefault(n, Collections.emptyList()));
    }

    // ─── Getters de colecciones ──────────────────────────────────────────────
    public List<Nodo>     getNodos()      { return Collections.unmodifiableList(nodos); }
    public List<Conexion> getConexiones() { return Collections.unmodifiableList(conexiones); }
    public boolean        estaVacio()     { return nodos.isEmpty(); }
    public int            totalNodos()    { return nodos.size(); }
    public int            totalAristas()  { return conexiones.size(); }

    // ─── Sugerencias de Networking ───────────────────────────────────────────

    
    public static class ResultadoSugerencia {
        public final Nodo estudiante;
        public final Nodo intermediario;  // Profesor o nodo que conecta al estudiante con el nodo consultado

        public ResultadoSugerencia(Nodo estudiante, Nodo intermediario) {
            this.estudiante    = estudiante;
            this.intermediario = intermediario;
        }
    }

    
    public List<ResultadoSugerencia> sugerirEstudiantes(String idNodo) {
        Nodo nodoBase = buscarNodoPorId(idNodo);
        if (nodoBase == null) return Collections.emptyList();

        // Vecinos directos del nodo consultado (nivel 1) — ya conectados, se excluyen del resultado
        Set<Nodo> yaConectados = new LinkedHashSet<>(
            adyacencia.getOrDefault(nodoBase, Collections.emptyList())
        );
        yaConectados.add(nodoBase); // excluir el propio nodo

        List<ResultadoSugerencia> sugerencias = new ArrayList<>();
        // Evitar duplicar el mismo estudiante aunque llegue por varios intermediarios
        Set<Nodo> estudiantesYaAgregados = new LinkedHashSet<>();

        // Nivel 1: recorrer vecinos directos del nodo base (intermediarios)
        for (Nodo intermediario : adyacencia.getOrDefault(nodoBase, Collections.emptyList())) {
            // Nivel 2: recorrer vecinos del intermediario
            for (Nodo candidato : adyacencia.getOrDefault(intermediario, Collections.emptyList())) {
            
                if (candidato.getTipo() == TipoNodo.ESTUDIANTE
                        && !yaConectados.contains(candidato)
                        && !estudiantesYaAgregados.contains(candidato)) {
                    sugerencias.add(new ResultadoSugerencia(candidato, intermediario));
                    estudiantesYaAgregados.add(candidato);
                }
            }
        }
        return sugerencias;
    }

}