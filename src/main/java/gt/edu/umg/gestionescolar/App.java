package gt.edu.umg.gestionescolar;

import gt.edu.umg.gestionescolar.controller.AsistenciaController;
import gt.edu.umg.gestionescolar.controller.BoletaController;
import gt.edu.umg.gestionescolar.repository.DatosCompartidos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Cargar el módulo de Personas.
            FXMLLoader loaderPersonas = crearLoader("PersonasView.fxml");
            Parent rootPersonas = loaderPersonas.load();

            TabPane pestanas = obtenerPestanas(
                    rootPersonas, "PersonasView.fxml"
            );

            // Cargar las pestañas del módulo Académico.
            FXMLLoader loaderAcademico = crearLoader("AcademicoView.fxml");
            Parent rootAcademico = loaderAcademico.load();

            TabPane pestanasAcademicas = obtenerPestanas(
                    rootAcademico, "AcademicoView.fxml"
            );

            // Retirar las pestañas del contenedor original antes de moverlas.
            List<Tab> academicas =
                    new ArrayList<>(pestanasAcademicas.getTabs());

            pestanasAcademicas.getTabs().clear();
            pestanas.getTabs().addAll(academicas);

            // Cargar Asistencia.
            FXMLLoader loaderAsistencia = crearLoader("AsistenciaView.fxml");
            Parent rootAsistencia = loaderAsistencia.load();

            AsistenciaController asistenciaController =
                    loaderAsistencia.getController();

            Runnable actualizarCursosAsistencia = () ->
                    asistenciaController.setCursos(
                            DatosCompartidos.getCursos().findAll()
                    );

            actualizarCursosAsistencia.run();

            Tab tabAsistencia = new Tab("Asistencia", rootAsistencia);

            tabAsistencia.selectedProperty().addListener(
                    (obs, anterior, seleccionada) -> {
                        if (seleccionada) {
                            actualizarCursosAsistencia.run();
                        }
                    }
            );

            // Cargar Boletas.
            FXMLLoader loaderBoleta = crearLoader("BoletaView.fxml");
            Parent rootBoleta = loaderBoleta.load();

            BoletaController boletaController =
                    loaderBoleta.getController();

            Runnable actualizarCatalogosBoleta = () -> {
                boletaController.setCursos(
                        DatosCompartidos.getCursos().findAll()
                );

                boletaController.setCiclos(
                        DatosCompartidos.getCiclos().findAll()
                );
            };

            actualizarCatalogosBoleta.run();

            Tab tabBoleta = new Tab("Boletas", rootBoleta);

            tabBoleta.selectedProperty().addListener(
                    (obs, anterior, seleccionada) -> {
                        if (seleccionada) {
                            actualizarCatalogosBoleta.run();
                        }
                    }
            );

            pestanas.getTabs().addAll(tabAsistencia, tabBoleta);

            pestanas.setTabClosingPolicy(
                    TabPane.TabClosingPolicy.UNAVAILABLE
            );

            pestanas.getSelectionModel().selectFirst();

            Scene scene = new Scene(rootPersonas, 1280, 800);

            stage.setTitle("Sistema de Gestión Escolar - SGE");
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error al iniciar");
            alerta.setHeaderText("No se pudo abrir el sistema");
            alerta.setContentText(
                    "Revisá el detalle en la terminal.\n\n" + e.getMessage()
            );
            alerta.showAndWait();
        }
    }

    private FXMLLoader crearLoader(String archivo) throws IOException {
        URL ubicacion = getClass().getResource(
                "/gt/edu/umg/gestionescolar/view/" + archivo
        );

        if (ubicacion == null) {
            ubicacion = getClass().getResource(
                    "/gt/edu/umg/gestionescolar/views/" + archivo
            );
        }

        if (ubicacion == null) {
            throw new IOException("No se encontró el archivo " + archivo);
        }

        return new FXMLLoader(ubicacion);
    }

    private TabPane obtenerPestanas(Parent root, String archivo) {
        if (root instanceof BorderPane panel
                && panel.getCenter() instanceof TabPane pestanas) {
            return pestanas;
        }

        throw new IllegalStateException(
                archivo + " debe tener un BorderPane con un TabPane en el centro."
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}