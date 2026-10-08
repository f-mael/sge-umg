package gt.edu.umg.gestionescolar;

import gt.edu.umg.gestionescolar.controller.AsistenciaController;
import gt.edu.umg.gestionescolar.controller.BoletaController;
import gt.edu.umg.gestionescolar.util.DatosPruebaAcademicos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // 1. Cargar vista principal (Personas)
            URL fxmlPersonas = getClass().getResource("/gt/edu/umg/gestionescolar/view/PersonasView.fxml");
            if (fxmlPersonas == null) {
                fxmlPersonas = getClass().getResource("/gt/edu/umg/gestionescolar/views/PersonasView.fxml");
            }
            if (fxmlPersonas == null) {
                throw new IllegalStateException("No se encontró PersonasView.fxml");
            }

            FXMLLoader loaderPersonas = new FXMLLoader(fxmlPersonas);
            Parent rootPersonas = loaderPersonas.load();

            // 2. Cargar vista del Módulo Académico e integrar sus pestañas
            URL fxmlAcademico = getClass().getResource("/gt/edu/umg/gestionescolar/view/AcademicoView.fxml");
            if (fxmlAcademico == null) {
                fxmlAcademico = getClass().getResource("/gt/edu/umg/gestionescolar/views/AcademicoView.fxml");
            }

            if (fxmlAcademico != null) {
                FXMLLoader loaderAcademico = new FXMLLoader(fxmlAcademico);
                Parent rootAcademico = loaderAcademico.load();

                TabPane tabPanePersonas = (TabPane) ((BorderPane) rootPersonas).getCenter();
                TabPane tabPaneAcademico = (TabPane) ((BorderPane) rootAcademico).getCenter();

                for (Tab tab : new ArrayList<>(tabPaneAcademico.getTabs())) {
                    tabPanePersonas.getTabs().add(tab);
                }
            }

            Scene scene = new Scene(rootPersonas, 1200, 720);
            stage.setTitle("Sistema de Gestión Escolar - SGE");
            stage.setMinWidth(1000);
            stage.setMinHeight(650);
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