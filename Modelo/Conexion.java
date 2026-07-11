package Modelo;

import java.util.Objects;


public class Conexion {

    private final Nodo origen;
    private final Nodo destino;
    private String etiqueta;   // Tipo de relación: "Conoce a", "Trabaja en", etc.

    public Conexion(Nodo origen, Nodo destino, String etiqueta) {
        this.origen   = origen;
        this.destino  = destino;
        this.etiqueta = etiqueta;
    }

    // ─── Getters ────────────────────────────────────────────────────────────
    public Nodo   getOrigen()   { return origen; }
    public Nodo   getDestino()  { return destino; }
    public String getEtiqueta() { return etiqueta; }
    public void   setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }

    
    public boolean conecta(Nodo a, Nodo b) {
        return (origen.equals(a) && destino.equals(b))
            || (origen.equals(b) && destino.equals(a));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conexion)) return false;
        Conexion c = (Conexion) o;
        return (origen.equals(c.origen)  && destino.equals(c.destino))
            || (origen.equals(c.destino) && destino.equals(c.origen));
    }

    @Override
    public int hashCode() {
        // Hash simétrico para que A-B == B-A
        return Objects.hash(origen.getId()) + Objects.hash(destino.getId());
    }

    @Override
    public String toString() {
        return origen.getNombre() + " ──[" + etiqueta + "]──► " + destino.getNombre();
        }
}
