package Modelo;


public enum TipoNodo {
    ESTUDIANTE("Estudiante"),
    PROFESOR("Profesor"),
    EMPRESA("Empresa");

    private final String etiqueta;

    TipoNodo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }

    public static TipoNodo desdeTexto(String texto) {
        for (TipoNodo t : values()) {
            if (t.etiqueta.equalsIgnoreCase(texto.trim())) return t;
        }
        return ESTUDIANTE;
    }
}