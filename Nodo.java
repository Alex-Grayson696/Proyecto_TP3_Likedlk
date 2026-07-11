package Modelo;

import java.util.Objects;


public class Nodo {

    private final String id;        // Identificador único (ej. "E001", "P003", "C01")
    private String nombre;          // Nombre del nodo
    private TipoNodo tipo;          // Tipo: ESTUDIANTE, PROFESOR o EMPRESA
    private String descripcion;     // Carrera, especialidad o rubro

    public Nodo(String id, String nombre, TipoNodo tipo, String descripcion) {
        this.id          = id;
        this.nombre      = nombre;
        this.tipo        = tipo;
        this.descripcion = descripcion;
    }

    // ─── Getters ────────────────────────────────────────────────────────────
    public String    getId()          { return id; }
    public String    getNombre()      { return nombre; }
    public TipoNodo  getTipo()        { return tipo; }
    public String    getDescripcion() { return descripcion; }

    // ─── Setters ────────────────────────────────────────────────────────────
    public void setNombre(String nombre)           { this.nombre      = nombre; }
    public void setTipo(TipoNodo tipo)             { this.tipo        = tipo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    // ─── Igualdad por ID ────────────────────────────────────────────────────
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Nodo)) return false;
        Nodo nodo = (Nodo) o;
        return Objects.equals(id, nodo.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return nombre + " [" + tipo.getEtiqueta() + "]"; }
}