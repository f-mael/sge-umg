package gt.edu.umg.gestionescolar;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("Sistema de Gestión Escolar - UMG");
        Scene scene = new Scene(new StackPane(label), 640, 480);
        stage.setTitle("Gestión Escolar");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
