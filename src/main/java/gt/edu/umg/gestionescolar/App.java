package gt.edu.umg.gestionescolar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Aplicación principal del Sistema de Gestión Escolar (UMG).
 * Configura la ventana principal para el Módulo Base y Personas (Entregable 2 - Listas en Memoria).
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Cargar la vista FXML del Módulo Base y Personas
            URL fxmlLocation = getClass().getResource("/gt/edu/umg/gestionescolar/view/PersonasView.fxml");
            if (fxmlLocation == null) {
                fxmlLocation = getClass().getResource("/gt/edu/umg/gestionescolar/views/PersonasView.fxml");
            }

            if (fxmlLocation == null) {
                throw new IllegalStateException("No se encontró el archivo PersonasView.fxml en resources.");
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            // Configurar escena y dimensiones de la ventana principal
            Scene scene = new Scene(root, 1160, 700);

            stage.setTitle("Sistema de Gestión Escolar - Módulo Base y Personas (Entregable 2) | UMG");
            stage.setMinWidth(960);
            stage.setMinHeight(600);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
