package gt.edu.umg.gestionescolar.controller;

import gt.edu.umg.gestionescolar.model.Asistencia;
import gt.edu.umg.gestionescolar.model.Curso;
import gt.edu.umg.gestionescolar.model.Estudiante;
import gt.edu.umg.gestionescolar.repository.AsistenciaRepository;
import gt.edu.umg.gestionescolar.repository.DatosCompartidos;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;

public class AsistenciaController {

    private final AsistenciaRepository repository =
        DatosCompartidos.getAsistencias();

    private int idSeleccionado = 0;

    @FXML private TextField txtBuscar;
    @FXML private Label lblId;
    @FXML private Label lblTotal;

    @FXML private ComboBox<Estudiante> cbEstudiante;
    @FXML private ComboBox<Curso> cbCurso;
    @FXML private ComboBox<String> cbEstado;
    @FXML private DatePicker dpFecha;

    @FXML private TableView<Asistencia> tblAsistencias;
    @FXML private TableColumn<Asistencia, String> colId;
    @FXML private TableColumn<Asistencia, String> colEstudiante;
    @FXML private TableColumn<Asistencia, String> colCurso;
    @FXML private TableColumn<Asistencia, String> colFecha;
    @FXML private TableColumn<Asistencia, String> colEstado;

    @FXML
    private void initialize() {
        cbEstado.setItems(
            FXCollections.observableArrayList("Presente", "Ausente")
        );

        cbEstudiante.setConverter(new StringConverter<Estudiante>() {
            @Override
            public String toString(Estudiante estudiante) {
                return estudiante == null ? ""
                    : estudiante.getCarnet() + " - "
                    + estudiante.getNombreCompleto();
            }

            @Override
            public Estudiante fromString(String texto) {
                return null;
            }
        });

        cbCurso.setConverter(new StringConverter<Curso>() {
            @Override
            public String toString(Curso curso) {
                return curso == null ? ""
                    : curso.getIdCurso() + " - " + curso.getNombre();
            }

            @Override
            public Curso fromString(String texto) {
                return null;
            }
        });

        colId.setCellValueFactory(c ->
            new SimpleStringProperty(
                String.valueOf(c.getValue().getIdAsistencia())
            )
        );

        colEstudiante.setCellValueFactory(c ->
            new SimpleStringProperty(
                c.getValue().getEstudiante().getNombreCompleto()
            )
        );

        colCurso.setCellValueFactory(c ->
            new SimpleStringProperty(
                c.getValue().getCurso().getNombre()
            )
        );

        colFecha.setCellValueFactory(c ->
            new SimpleStringProperty(
                c.getValue().getFecha().toString()
            )
        );

        colEstado.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getEstado())
        );

        tblAsistencias.setPlaceholder(
            new Label("No se encontraron asistencias.")
        );

        txtBuscar.textProperty().addListener(
            (observable, anterior, actual) -> cargarTabla()
        );

        tblAsistencias.getSelectionModel()
            .selectedItemProperty().addListener(
                (observable, anterior, actual) -> {
                    if (actual != null) {
                        seleccionar(actual);
                    }
                }
            );

        // Refrescar estudiantes al abrir el selector.
        cbEstudiante.setOnShowing(event -> cargarEstudiantes());

        actualizarListas();
        limpiar();
    }

    // Recibe los cursos del módulo académico.
    public void setCursos(List<Curso> cursos) {
        Curso seleccionado = cbCurso.getValue();

        cbCurso.setItems(FXCollections.observableArrayList(cursos));

        if (seleccionado != null) {
            cbCurso.setValue(
                cursos.stream()
                    .filter(c -> c.getIdCurso()
                        == seleccionado.getIdCurso())
                    .findFirst()
                    .orElse(null)
            );
        }
    }

    private void cargarEstudiantes() {
        Estudiante seleccionado = cbEstudiante.getValue();

        List<Estudiante> estudiantes =
            DatosCompartidos.getEstudiantes().findAll();

        cbEstudiante.setItems(
            FXCollections.observableArrayList(estudiantes)
        );

        if (seleccionado != null) {
            cbEstudiante.setValue(
                estudiantes.stream()
                    .filter(e -> e.getId() == seleccionado.getId())
                    .findFirst()
                    .orElse(null)
            );
        }
    }

    @FXML
    private void actualizarListas() {
        cargarEstudiantes();
        cargarTabla();
    }

    private void cargarTabla() {
        List<Asistencia> resultados =
            repository.search(txtBuscar.getText());

        tblAsistencias.setItems(
            FXCollections.observableArrayList(resultados)
        );

        lblTotal.setText(
            "Mostrando: " + resultados.size()
            + " de " + repository.findAll().size() + " registros"
        );
    }

    private void seleccionar(Asistencia asistencia) {
        idSeleccionado = asistencia.getIdAsistencia();
        lblId.setText("Registro: " + idSeleccionado);

        cbEstudiante.setValue(asistencia.getEstudiante());
        cbCurso.setValue(asistencia.getCurso());
        dpFecha.setValue(asistencia.getFecha());
        cbEstado.setValue(asistencia.getEstado());
    }

    private Asistencia leerFormulario(int id) {
        Estudiante estudiante = cbEstudiante.getValue();
        Curso curso = cbCurso.getValue();

        if (estudiante == null || curso == null
                || dpFecha.getValue() == null
                || cbEstado.getValue() == null) {
            throw new IllegalArgumentException(
                "Seleccione estudiante, curso, fecha y estado."
            );
        }

        // Obtener la versión actual del estudiante.
        estudiante = DatosCompartidos.getEstudiantes()
            .findById(estudiante.getId())
            .orElseThrow(() -> new IllegalArgumentException(
                "El estudiante ya no está registrado. "
                + "Actualice las listas."
            ));

        // Crear un objeto nuevo evita modificar el registro
        // original antes de que las validaciones terminen.
        return new Asistencia(
            id,
            estudiante,
            curso,
            dpFecha.getValue(),
            cbEstado.getValue()
        );
    }

    @FXML
    private void guardar() {
        if (idSeleccionado != 0) {
            mostrarMensaje(
                Alert.AlertType.WARNING,
                "Está editando un registro. Use Actualizar, "
                + "o Limpiar para crear uno nuevo."
            );
            return;
        }

        try {
            repository.save(leerFormulario(0));
            limpiar();
            cargarTabla();

            mostrarMensaje(
                Alert.AlertType.INFORMATION,
                "Asistencia guardada correctamente."
            );
        } catch (IllegalArgumentException e) {
            mostrarMensaje(Alert.AlertType.WARNING, e.getMessage());
        }
    }

    @FXML
    private void actualizar() {
        if (!haySeleccion()) {
            return;
        }

        try {
            repository.update(leerFormulario(idSeleccionado));
            limpiar();
            cargarTabla();

            mostrarMensaje(
                Alert.AlertType.INFORMATION,
                "Asistencia actualizada correctamente."
            );
        } catch (IllegalArgumentException e) {
            mostrarMensaje(Alert.AlertType.WARNING, e.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        if (!haySeleccion()) {
            return;
        }

        Alert confirmacion = new Alert(
            Alert.AlertType.CONFIRMATION,
            "¿Desea eliminar la asistencia seleccionada?",
            ButtonType.YES,
            ButtonType.NO
        );

        confirmacion.setTitle("Eliminar asistencia");
        confirmacion.setHeaderText(null);

        if (confirmacion.showAndWait().orElse(ButtonType.NO)
                == ButtonType.YES) {
            repository.delete(idSeleccionado);
            limpiar();
            cargarTabla();
        }
    }

    private boolean haySeleccion() {
        if (idSeleccionado == 0) {
            mostrarMensaje(
                Alert.AlertType.WARNING,
                "Seleccione una asistencia de la tabla."
            );
            return false;
        }

        return true;
    }

    @FXML
    private void limpiar() {
        idSeleccionado = 0;
        lblId.setText("Nuevo registro");

        tblAsistencias.getSelectionModel().clearSelection();
        cbEstudiante.getSelectionModel().clearSelection();
        cbCurso.getSelectionModel().clearSelection();
        cbEstado.getSelectionModel().clearSelection();
        dpFecha.setValue(LocalDate.now());
    }

    @FXML
    private void limpiarBusqueda() {
        txtBuscar.clear();
    }

    private void mostrarMensaje(Alert.AlertType tipo, String texto) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Control de asistencia");
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }
}