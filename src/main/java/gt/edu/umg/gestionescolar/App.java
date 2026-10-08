package gt.edu.umg.gestionescolar;

import gt.edu.umg.gestionescolar.controller.AsistenciaController;
import gt.edu.umg.gestionescolar.controller.BoletaController;
import gt.edu.umg.gestionescolar.util.DatosPruebaAcademicos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            Tab personas = cargarPestana(
                    "Personas", "PersonasView.fxml");

            Tab asistencia = cargarPestana(
                    "Asistencia", "AsistenciaView.fxml");

            Tab boletas = cargarPestana(
                    "Boletas", "BoletaView.fxml");

            TabPane pestanas = new TabPane(
                    personas, asistencia, boletas);

            pestanas.setTabClosingPolicy(
                    TabPane.TabClosingPolicy.UNAVAILABLE);

            Scene scene = new Scene(pestanas, 1200, 800);

            stage.setTitle(
                    "Sistema de Gestión Escolar - SGE (datos académicos de prueba)");
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error al iniciar");
            alerta.setHeaderText(
                    "No se pudo abrir el Sistema de Gestión Escolar");
            alerta.setContentText(
                    "Revise el error en la terminal.\n" + e.getMessage());
            alerta.showAndWait();
        }
    }

    private Tab cargarPestana(String titulo, String archivo)
            throws IOException {

        URL ubicacion = getClass().getResource(
                "/gt/edu/umg/gestionescolar/view/" + archivo);

        if (ubicacion == null) {
            ubicacion = getClass().getResource(
                    "/gt/edu/umg/gestionescolar/views/" + archivo);
        }

        if (ubicacion == null) {
            throw new IOException(
                    "No se encontró el archivo " + archivo);
        }

        FXMLLoader loader = new FXMLLoader(ubicacion);
        Parent contenido = loader.load();

        Object controlador = loader.getController();

        // Conexión temporal para probar el módulo.
        if (controlador instanceof AsistenciaController asistencia) {
            asistencia.setCursos(DatosPruebaAcademicos.getCursos());
        }

        if (controlador instanceof BoletaController boletas) {
            boletas.setCursos(DatosPruebaAcademicos.getCursos());
            boletas.setCiclos(DatosPruebaAcademicos.getCiclos());
        }

        return new Tab(titulo, contenido);
    }

    public static void main(String[] args) {
        launch(args);
    }
}