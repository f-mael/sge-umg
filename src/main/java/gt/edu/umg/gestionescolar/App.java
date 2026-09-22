package gt.edu.umg.gestionescolar;

import gt.edu.umg.gestionescolar.util.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Aplicación principal del Sistema de Gestión Escolar (UMG).
 * Configura la ventana principal con el Módulo Base y Personas.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // 1. Inicializar la estructura de tablas SQLite
            DatabaseManager.initializeDatabase();

            // 2. Cargar la vista FXML del Módulo Base y Personas
            URL fxmlLocation = getClass().getResource("/gt/edu/umg/gestionescolar/view/PersonasView.fxml");
            if (fxmlLocation == null) {
                fxmlLocation = getClass().getResource("/gt/edu/umg/gestionescolar/views/PersonasView.fxml");
            }

            if (fxmlLocation == null) {
                throw new IllegalStateException("No se encontró el archivo PersonasView.fxml en resources.");
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            // 3. Crear escena y configurar dimensiones
            Scene scene = new Scene(root, 1180, 740);

            stage.setTitle("Sistema de Gestión Escolar - Módulo Base y Personas | UMG");
            stage.setMinWidth(980);
            stage.setMinHeight(620);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.err.println("Error crítico al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
