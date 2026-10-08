package gt.edu.umg.gestionescolar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Aplicación principal del Sistema de Gestión Escolar (UMG).
 * Integra el Módulo de Personas y el Módulo Académico en una sola interfaz.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // 1. Cargar la vista principal (Módulo de Personas)
            URL fxmlPersonas = getClass().getResource("/gt/edu/umg/gestionescolar/view/PersonasView.fxml");
            if (fxmlPersonas == null) {
                fxmlPersonas = getClass().getResource("/gt/edu/umg/gestionescolar/views/PersonasView.fxml");
            }
            if (fxmlPersonas == null) {
                throw new IllegalStateException("No se encontró PersonasView.fxml");
            }

            FXMLLoader loaderPersonas = new FXMLLoader(fxmlPersonas);
            Parent rootPersonas = loaderPersonas.load();

            // 2. Cargar tu vista (Módulo Académico)
            URL fxmlAcademico = getClass().getResource("/gt/edu/umg/gestionescolar/view/AcademicoView.fxml");
            if (fxmlAcademico == null) {
                fxmlAcademico = getClass().getResource("/gt/edu/umg/gestionescolar/views/AcademicoView.fxml");
            }

            if (fxmlAcademico != null) {
                FXMLLoader loaderAcademico = new FXMLLoader(fxmlAcademico);
                Parent rootAcademico = loaderAcademico.load();

                // Extraer el TabPane de PersonasView y el de AcademicoView
                TabPane tabPanePersonas = (TabPane) ((BorderPane) rootPersonas).getCenter();
                TabPane tabPaneAcademico = (TabPane) ((BorderPane) rootAcademico).getCenter();

                // Transferir todas las pestañas de tu módulo al TabPane principal
                // (Usamos new java.util.ArrayList para evitar conflictos al mover los tabs)
                for (Tab tab : new java.util.ArrayList<>(tabPaneAcademico.getTabs())) {
                    tabPanePersonas.getTabs().add(tab);
                }
            }

            // 3. Crear la escena con la ventana integrada
            Scene scene = new Scene(rootPersonas, 1200, 720);

            stage.setTitle("Sistema de Gestión Escolar - SGE (UMG Cobán)");
            stage.setMinWidth(1000);
            stage.setMinHeight(650);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación integrada: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}