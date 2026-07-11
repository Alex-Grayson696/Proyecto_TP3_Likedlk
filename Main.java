import Controlador.RedControlador;
import Vista.VentanaPrincipal;

import javax.swing.*;


public class Main {

    public static void main(String[] args) {
        // Ejecutar en el hilo de eventos de Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Look and Feel del sistema operativo
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            // 1. Construir el Controlador (carga el Modelo internamente)
            RedControlador controlador = new RedControlador();

            // 2. Construir la Vista pasando el Controlador
            VentanaPrincipal ventana = new VentanaPrincipal(controlador);
            ventana.setVisible(true);
        });
    }
}