package gt.edu.umg.gestionescolar.controller;

import gt.edu.umg.gestionescolar.model.*;
import gt.edu.umg.gestionescolar.repository.*;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AcademicoController implements Initializable {

    private final GradoSeccionRepository gradoRepo = new GradoSeccionRepository();
    private final CicloEscolarRepository cicloRepo = new CicloEscolarRepository();
    private final CursoRepository cursoRepo = new CursoRepository();
    private final InscripcionRepository inscripcionRepo = new InscripcionRepository();
    private final EstudianteRepository estudianteRepo = new EstudianteRepository();
    private final DocenteRepository docenteRepo = new DocenteRepository();

    private final ObservableList<Inscripcion> inscripcionesObservable = FXCollections.observableArrayList();
    private final ObservableList<Curso> cursosObservable = FXCollections.observableArrayList();
    private final ObservableList<GradoSeccion> gradosObservable = FXCollections.observableArrayList();

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // --- Componentes Inscripción ---
    @FXML private TextField txtInscripcionId;
    @FXML private TextField txtInscripcionBuscar;
    @FXML private Label lblInscripcionContador;
    @FXML private Label lblCicloActivo;
    @FXML private ComboBox<Estudiante> cbInscripcionEstudiante;
    @FXML private ComboBox<GradoSeccion> cbInscripcionGrado;
    @FXML private TableView<Inscripcion> tblInscripciones;
    @FXML private TableColumn<Inscripcion, Integer> colInsId;
    @FXML private TableColumn<Inscripcion, String> colInsFecha;
    @FXML private TableColumn<Inscripcion, String> colInsEstudiante;
    @FXML private TableColumn<Inscripcion, String> colInsGrado;
    @FXML private TableColumn<Inscripcion, Integer> colInsCiclo;

    // --- Componentes Curso ---
    @FXML private TextField txtCursoId;
    @FXML private TextField txtCursoBuscar;
    @FXML private Label lblCursoContador;
    @FXML private TextField txtCursoNombre;
    @FXML private ComboBox<Docente> cbCursoDocente;
    @FXML private ComboBox<GradoSeccion> cbCursoGrado;
    @FXML private TableView<Curso> tblCursos;
    @FXML private TableColumn<Curso, Integer> colCursoId;
    @FXML private TableColumn<Curso, String> colCursoNombre;
    @FXML private TableColumn<Curso, String> colCursoDocente;
    @FXML private TableColumn<Curso, String> colCursoGrado;

    // --- Componentes GradoSeccion ---
    @FXML private TextField txtGradoId;
    @FXML private TextField txtGradoBuscar;
    @FXML private Label lblGradoContador;
    @FXML private TextField txtGradoNombre;
    @FXML private TextField txtGradoNivel;
    @FXML private TextField txtGradoSeccion;
    @FXML private TableView<GradoSeccion> tblGrados;
    @FXML private TableColumn<GradoSeccion, Integer> colGradoId;
    @FXML private TableColumn<GradoSeccion, String> colGradoNombre;
    @FXML private TableColumn<GradoSeccion, String> colGradoNivel;
    @FXML private TableColumn<GradoSeccion, String> colGradoSeccion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarCombos();
        configurarTablas();
        cargarDatosIniciales();
        configurarListeners();
    }

    private void configurarCombos() {
        cbInscripcionEstudiante.setConverter(new StringConverter<>() {
            @Override
            public String toString(Estudiante e) {
                return e != null ? e.getCarnet() + " - " + e.getNombreCompleto() : "";
            }
            @Override
            public Estudiante fromString(String s) { return null; }
        });

        cbCursoDocente.setConverter(new StringConverter<>() {
            @Override
            public String toString(Docente d) {
                return d != null ? d.getNombreCompleto() + " (" + d.getEspecialidad() + ")" : "";
            }
            @Override
            public Docente fromString(String s) { return null; }
        });

        StringConverter<GradoSeccion> gradoConverter = new StringConverter<>() {
            @Override
            public String toString(GradoSeccion g) {
                return g != null ? g.getNombre() + " - Sección " + g.getSeccion() : "";
            }
            @Override
            public GradoSeccion fromString(String s) { return null; }
        };
        cbInscripcionGrado.setConverter(gradoConverter);
        cbCursoGrado.setConverter(gradoConverter);
    }

    private void configurarTablas() {
        colInsId.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getIdInscripcion()));
        colInsFecha.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getFechaInscripcion() != null ? d.getValue().getFechaInscripcion().format(dtf) : ""
        ));
        colInsEstudiante.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getEstudiante() != null
                ? d.getValue().getEstudiante().getCarnet() + " - " + d.getValue().getEstudiante().getNombreCompleto()
                : "Sin asignar"
        ));
        colInsGrado.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getGradoSeccion() != null
                ? d.getValue().getGradoSeccion().getNombre() + " - Sección " + d.getValue().getGradoSeccion().getSeccion()
                : "Sin grado"
        ));
        colInsCiclo.setCellValueFactory(d -> new SimpleObjectProperty<>(
            d.getValue().getCicloEscolar() != null ? d.getValue().getCicloEscolar().getAnio() : 2026
        ));
        tblInscripciones.setItems(inscripcionesObservable);

        colCursoId.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getIdCurso()));
        colCursoNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombre()));
        colCursoDocente.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getDocente() != null ? d.getValue().getDocente().getNombreCompleto() : "Sin docente"
        ));
        colCursoGrado.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getGradoSeccion() != null
                ? d.getValue().getGradoSeccion().getNombre() + " - Sección " + d.getValue().getGradoSeccion().getSeccion()
                : "Sin grado"
        ));
        tblCursos.setItems(cursosObservable);

        colGradoId.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getIdGradoSeccion()));
        colGradoNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombre()));
        colGradoNivel.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNivel()));
        colGradoSeccion.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getSeccion())));
        tblGrados.setItems(gradosObservable);
    }

    private void cargarDatosIniciales() {
        cbInscripcionEstudiante.setItems(FXCollections.observableArrayList(estudianteRepo.findAll()));
        cbCursoDocente.setItems(FXCollections.observableArrayList(docenteRepo.findAll()));

        cbInscripcionEstudiante.setOnShowing(e ->
            cbInscripcionEstudiante.setItems(FXCollections.observableArrayList(estudianteRepo.findAll()))
        );
        cbCursoDocente.setOnShowing(e ->
            cbCursoDocente.setItems(FXCollections.observableArrayList(docenteRepo.findAll()))
        );

        cicloRepo.findCicloActivo().ifPresent(c -> lblCicloActivo.setText("Ciclo Activo Vigente: " + c.getAnio()));

        actualizarListasGrados();
        actualizarListasCursos();
        actualizarListasInscripciones();
    }

    private void configurarListeners() {
        tblInscripciones.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, sel) -> {
            if (sel != null) {
                txtInscripcionId.setText(String.valueOf(sel.getIdInscripcion()));
                cbInscripcionEstudiante.setValue(sel.getEstudiante());
                cbInscripcionGrado.setValue(sel.getGradoSeccion());
            }
        });

        tblCursos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, sel) -> {
            if (sel != null) {
                txtCursoId.setText(String.valueOf(sel.getIdCurso()));
                txtCursoNombre.setText(sel.getNombre());
                cbCursoDocente.setValue(sel.getDocente());
                cbCursoGrado.setValue(sel.getGradoSeccion());
            }
        });

        tblGrados.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, sel) -> {
            if (sel != null) {
                txtGradoId.setText(String.valueOf(sel.getIdGradoSeccion()));
                txtGradoNombre.setText(sel.getNombre());
                txtGradoNivel.setText(sel.getNivel());
                txtGradoSeccion.setText(String.valueOf(sel.getSeccion()));
            }
        });

        txtInscripcionBuscar.textProperty().addListener((obs, oldVal, texto) -> {
            List<Inscripcion> filtrados = inscripcionRepo.search(texto);
            inscripcionesObservable.setAll(filtrados);
            lblInscripcionContador.setText("Total: " + filtrados.size() + " inscripciones");
        });

        txtCursoBuscar.textProperty().addListener((obs, oldVal, texto) -> {
            List<Curso> filtrados = cursoRepo.search(texto);
            cursosObservable.setAll(filtrados);
            lblCursoContador.setText("Total: " + filtrados.size() + " cursos");
        });

        txtGradoBuscar.textProperty().addListener((obs, oldVal, texto) -> {
            List<GradoSeccion> filtrados = gradoRepo.search(texto);
            gradosObservable.setAll(filtrados);
            lblGradoContador.setText("Total: " + filtrados.size() + " grados");
        });
    }

    // ==========================================
    // CRUD INSCRIPCIONES
    // ==========================================
    @FXML
    private void handleGuardarInscripcion(ActionEvent event) {
        Estudiante est = cbInscripcionEstudiante.getValue();
        GradoSeccion grado = cbInscripcionGrado.getValue();
        if (est == null || grado == null) {
            mostrarAlerta("Campos obligatorios", "Seleccione un estudiante y un grado/sección.");
            return;
        }

        CicloEscolar ciclo = cicloRepo.findCicloActivo().orElseGet(() -> {
            CicloEscolar nuevoCiclo = new CicloEscolar(0, 2026, LocalDate.of(2026, 1, 15), LocalDate.of(2026, 10, 30), true);
            cicloRepo.save(nuevoCiclo);
            return nuevoCiclo;
        });

        Inscripcion nueva = new Inscripcion(0, LocalDate.now(), est, grado, ciclo);
        inscripcionRepo.save(nueva);
        actualizarListasInscripciones();
        handleLimpiarInscripcion(null);
    }

    @FXML
    private void handleActualizarInscripcion(ActionEvent event) {
        if (txtInscripcionId.getText().isEmpty()) {
            mostrarAlerta("Selección requerida", "Seleccione una inscripción de la tabla para actualizar.");
            return;
        }
        Estudiante est = cbInscripcionEstudiante.getValue();
        GradoSeccion grado = cbInscripcionGrado.getValue();
        if (est == null || grado == null) {
            mostrarAlerta("Campos obligatorios", "Seleccione un estudiante y un grado/sección.");
            return;
        }
        int id = Integer.parseInt(txtInscripcionId.getText());
        CicloEscolar ciclo = cicloRepo.findCicloActivo()
            .orElseGet(() -> new CicloEscolar(1, 2026, LocalDate.of(2026, 1, 15), LocalDate.of(2026, 10, 30), true));

        inscripcionRepo.update(new Inscripcion(id, LocalDate.now(), est, grado, ciclo));
        actualizarListasInscripciones();
        handleLimpiarInscripcion(null);
    }

    @FXML
    private void handleEliminarInscripcion(ActionEvent event) {
        if (txtInscripcionId.getText().isEmpty()) {
            mostrarAlerta("Selección requerida", "Seleccione una inscripción de la tabla para eliminar.");
            return;
        }
        inscripcionRepo.delete(Integer.parseInt(txtInscripcionId.getText()));
        actualizarListasInscripciones();
        handleLimpiarInscripcion(null);
    }

    @FXML
    private void handleLimpiarInscripcion(ActionEvent event) {
        txtInscripcionId.clear();
        cbInscripcionEstudiante.setValue(null);
        cbInscripcionGrado.setValue(null);
        tblInscripciones.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleLimpiarFiltroInscripcion(ActionEvent event) {
        txtInscripcionBuscar.clear();
    }

    // ==========================================
    // CRUD CURSOS
    // ==========================================
    @FXML
    private void handleGuardarCurso(ActionEvent event) {
        String nombre = txtCursoNombre.getText().trim();
        Docente doc = cbCursoDocente.getValue();
        GradoSeccion grado = cbCursoGrado.getValue();
        if (nombre.isEmpty() || doc == null || grado == null) {
            mostrarAlerta("Campos obligatorios", "Complete el nombre del curso, docente y grado.");
            return;
        }
        cursoRepo.save(new Curso(0, nombre, doc, grado));
        actualizarListasCursos();
        handleLimpiarCurso(null);
    }

    @FXML
    private void handleActualizarCurso(ActionEvent event) {
        if (txtCursoId.getText().isEmpty()) {
            mostrarAlerta("Selección requerida", "Seleccione un curso de la tabla para actualizar.");
            return;
        }
        String nombre = txtCursoNombre.getText().trim();
        Docente doc = cbCursoDocente.getValue();
        GradoSeccion grado = cbCursoGrado.getValue();
        if (nombre.isEmpty() || doc == null || grado == null) {
            mostrarAlerta("Campos obligatorios", "Complete el nombre del curso, docente y grado.");
            return;
        }
        int id = Integer.parseInt(txtCursoId.getText());
        cursoRepo.update(new Curso(id, nombre, doc, grado));
        actualizarListasCursos();
        handleLimpiarCurso(null);
    }

    @FXML
    private void handleEliminarCurso(ActionEvent event) {
        if (txtCursoId.getText().isEmpty()) {
            mostrarAlerta("Selección requerida", "Seleccione un curso de la tabla para eliminar.");
            return;
        }
        cursoRepo.delete(Integer.parseInt(txtCursoId.getText()));
        actualizarListasCursos();
        handleLimpiarCurso(null);
    }

    @FXML
    private void handleLimpiarCurso(ActionEvent event) {
        txtCursoId.clear();
        txtCursoNombre.clear();
        cbCursoDocente.setValue(null);
        cbCursoGrado.setValue(null);
        tblCursos.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleLimpiarFiltroCurso(ActionEvent event) {
        txtCursoBuscar.clear();
    }

    // ==========================================
    // CRUD GRADOS Y SECCIONES
    // ==========================================
    @FXML
    private void handleGuardarGrado(ActionEvent event) {
        if (txtGradoNombre.getText().trim().isEmpty() || txtGradoNivel.getText().trim().isEmpty() || txtGradoSeccion.getText().trim().isEmpty()) {
            mostrarAlerta("Campos obligatorios", "Complete el nombre, nivel y sección.");
            return;
        }
        char seccion = txtGradoSeccion.getText().trim().toUpperCase().charAt(0);
        gradoRepo.save(new GradoSeccion(0, txtGradoNombre.getText().trim(), txtGradoNivel.getText().trim(), seccion));
        actualizarListasGrados();
        handleLimpiarGrado(null);
    }

    @FXML
    private void handleActualizarGrado(ActionEvent event) {
        if (txtGradoId.getText().isEmpty()) {
            mostrarAlerta("Selección requerida", "Seleccione un grado de la tabla para actualizar.");
            return;
        }
        if (txtGradoNombre.getText().trim().isEmpty() || txtGradoNivel.getText().trim().isEmpty() || txtGradoSeccion.getText().trim().isEmpty()) {
            mostrarAlerta("Campos obligatorios", "Complete el nombre, nivel y sección.");
            return;
        }
        int id = Integer.parseInt(txtGradoId.getText());
        char seccion = txtGradoSeccion.getText().trim().toUpperCase().charAt(0);
        gradoRepo.update(new GradoSeccion(id, txtGradoNombre.getText().trim(), txtGradoNivel.getText().trim(), seccion));
        actualizarListasGrados();
        handleLimpiarGrado(null);
    }

    @FXML
    private void handleEliminarGrado(ActionEvent event) {
        if (txtGradoId.getText().isEmpty()) {
            mostrarAlerta("Selección requerida", "Seleccione un grado de la tabla para eliminar.");
            return;
        }
        gradoRepo.delete(Integer.parseInt(txtGradoId.getText()));
        actualizarListasGrados();
        handleLimpiarGrado(null);
    }

    @FXML
    private void handleLimpiarGrado(ActionEvent event) {
        txtGradoId.clear();
        txtGradoNombre.clear();
        txtGradoNivel.clear();
        txtGradoSeccion.clear();
        tblGrados.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleLimpiarFiltroGrado(ActionEvent event) {
        txtGradoBuscar.clear();
    }

    // ==========================================
    // REFRESCO DE TABLAS Y COMBOS
    // ==========================================
    private void actualizarListasGrados() {
        List<GradoSeccion> lista = gradoRepo.findAll();
        gradosObservable.setAll(lista);
        lblGradoContador.setText("Total: " + lista.size() + " grados");
        cbCursoGrado.setItems(FXCollections.observableArrayList(lista));
        cbInscripcionGrado.setItems(FXCollections.observableArrayList(lista));
    }

    private void actualizarListasCursos() {
        List<Curso> lista = cursoRepo.findAll();
        cursosObservable.setAll(lista);
        lblCursoContador.setText("Total: " + lista.size() + " cursos");
    }

    private void actualizarListasInscripciones() {
        List<Inscripcion> lista = inscripcionRepo.findAll();
        inscripcionesObservable.setAll(lista);
        lblInscripcionContador.setText("Total: " + lista.size() + " inscripciones");
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}