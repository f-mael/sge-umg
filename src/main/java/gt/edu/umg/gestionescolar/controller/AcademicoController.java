/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gt.edu.umg.gestionescolar.controller;

import gt.edu.umg.gestionescolar.model.*;
import gt.edu.umg.gestionescolar.repository.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AcademicoController implements Initializable {

    // 1. Repositorios de tu módulo
    private final GradoSeccionRepository gradoRepo = new GradoSeccionRepository();
    private final CursoRepository cursoRepo = new CursoRepository();
    private final CicloEscolarRepository cicloRepo = new CicloEscolarRepository();
    private final InscripcionRepository inscripcionRepo = new InscripcionRepository();

    // Repositorios externos para vincular Estudiante y Docente en formularios
    private final Repository<Estudiante> estudianteRepo = new EstudianteRepository();
    private final Repository<Docente> docenteRepo = new DocenteRepository();

    // ==========================================
    // PESTAÑA: CURSOS
    // ==========================================
    @FXML private TextField txtCursoBuscar;
    @FXML private Label lblCursoContador;
    @FXML private TextField txtCursoId;
    @FXML private TextField txtCursoNombre;
    @FXML private ComboBox<Docente> cbCursoDocente;
    @FXML private ComboBox<GradoSeccion> cbCursoGrado;

    @FXML private TableView<Curso> tblCursos;
    @FXML private TableColumn<Curso, Integer> colCursoId;
    @FXML private TableColumn<Curso, String> colCursoNombre;
    @FXML private TableColumn<Curso, String> colCursoDocente;
    @FXML private TableColumn<Curso, String> colCursoGrado;

    // ==========================================
    // PESTAÑA: GRADOS Y SECCIONES
    // ==========================================
    @FXML private TextField txtGradoBuscar;
    @FXML private Label lblGradoContador;
    @FXML private TextField txtGradoId;
    @FXML private TextField txtGradoNombre;
    @FXML private TextField txtGradoNivel;
    @FXML private TextField txtGradoSeccion;

    @FXML private TableView<GradoSeccion> tblGrados;
    @FXML private TableColumn<GradoSeccion, Integer> colGradoId;
    @FXML private TableColumn<GradoSeccion, String> colGradoNombre;
    @FXML private TableColumn<GradoSeccion, String> colGradoNivel;
    @FXML private TableColumn<GradoSeccion, String> colGradoSeccion;

    // ==========================================
    // PESTAÑA: MATRICULACIÓN / INSCRIPCIONES
    // ==========================================
    @FXML private TextField txtInscripcionBuscar;
    @FXML private Label lblInscripcionContador;
    @FXML private TextField txtInscripcionId;
    @FXML private ComboBox<Estudiante> cbInscripcionEstudiante;
    @FXML private ComboBox<GradoSeccion> cbInscripcionGrado;
    @FXML private Label lblCicloActivoInfo;

    @FXML private TableView<Inscripcion> tblInscripciones;
    @FXML private TableColumn<Inscripcion, Integer> colInsId;
    @FXML private TableColumn<Inscripcion, String> colInsFecha;
    @FXML private TableColumn<Inscripcion, String> colInsEstudiante;
    @FXML private TableColumn<Inscripcion, String> colInsGrado;
    @FXML private TableColumn<Inscripcion, String> colInsCiclo;

    // Listas observables en memoria para las tablas
    private final ObservableList<Curso> cursosObservable = FXCollections.observableArrayList();
    private final ObservableList<GradoSeccion> gradosObservable = FXCollections.observableArrayList();
    private final ObservableList<Inscripcion> inscripcionesObservable = FXCollections.observableArrayList();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTablas();
        configurarComboBoxes();
        configurarFiltros();
        configurarSeleccionFilas();
        cargarDatosGenerales();
    }

    private void configurarTablas() {
        // Cursos
        colCursoId.setCellValueFactory(new PropertyValueFactory<>("idCurso"));
        colCursoNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCursoDocente.setCellValueFactory(cellData -> {
            Docente d = cellData.getValue().getDocente();
            return new SimpleStringProperty(d != null ? d.getNombre() + " " + d.getApellido() : "Sin Docente");
        });
        colCursoGrado.setCellValueFactory(cellData -> {
            GradoSeccion g = cellData.getValue().getGradoSeccion();
            return new SimpleStringProperty(g != null ? g.getNombre() + " - Sección " + g.getSeccion() : "Sin Grado");
        });
        tblCursos.setItems(cursosObservable);

        // Grados
        colGradoId.setCellValueFactory(new PropertyValueFactory<>("idGradoSeccion"));
        colGradoNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colGradoNivel.setCellValueFactory(new PropertyValueFactory<>("nivel"));
        colGradoSeccion.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(cellData.getValue().getSeccion())));
        tblGrados.setItems(gradosObservable);

        // Inscripciones
        colInsId.setCellValueFactory(new PropertyValueFactory<>("idInscripcion"));
        colInsFecha.setCellValueFactory(cellData -> {
            LocalDate f = cellData.getValue().getFechaInscripcion();
            return new SimpleStringProperty(f != null ? f.format(DATE_FORMATTER) : "N/A");
        });
        colInsEstudiante.setCellValueFactory(cellData -> {
            Estudiante e = cellData.getValue().getEstudiante();
            return new SimpleStringProperty(e != null ? e.getCarnet() + " - " + e.getNombre() + " " + e.getApellido() : "N/A");
        });
        colInsGrado.setCellValueFactory(cellData -> {
            GradoSeccion g = cellData.getValue().getGradoSeccion();
            return new SimpleStringProperty(g != null ? g.getNombre() + " (" + g.getSeccion() + ")" : "N/A");
        });
        colInsCiclo.setCellValueFactory(cellData -> {
            CicloEscolar c = cellData.getValue().getCicloEscolar();
            return new SimpleStringProperty(c != null ? String.valueOf(c.getAnio()) : "N/A");
        });
        tblInscripciones.setItems(inscripcionesObservable);
    }

    private void configurarComboBoxes() {
        // Convertidor para mostrar Docentes en ComboBox
        cbCursoDocente.setConverter(new StringConverter<>() {
            @Override public String toString(Docente d) {
                return (d == null) ? "-- Seleccionar Docente --" : d.getNombre() + " " + d.getApellido() + " (" + d.getEspecialidad() + ")";
            }
            @Override public Docente fromString(String s) { return null; }
        });

        // Convertidor para mostrar Grados en ComboBox
        StringConverter<GradoSeccion> gradoConverter = new StringConverter<>() {
            @Override public String toString(GradoSeccion g) {
                return (g == null) ? "-- Seleccionar Grado --" : g.getNombre() + " - " + g.getSeccion() + " (" + g.getNivel() + ")";
            }
            @Override public GradoSeccion fromString(String s) { return null; }
        };
        cbCursoGrado.setConverter(gradoConverter);
        cbInscripcionGrado.setConverter(gradoConverter);

        // Convertidor para mostrar Estudiantes en ComboBox
        cbInscripcionEstudiante.setConverter(new StringConverter<>() {
            @Override public String toString(Estudiante e) {
                return (e == null) ? "-- Seleccionar Estudiante --" : e.getCarnet() + " - " + e.getNombre() + " " + e.getApellido();
            }
            @Override public Estudiante fromString(String s) { return null; }
        });
    }

    private void configurarFiltros() {
        txtCursoBuscar.textProperty().addListener((obs, oldV, newV) -> {
            cursosObservable.setAll(cursoRepo.search(newV));
            lblCursoContador.setText("Total: " + cursosObservable.size() + " cursos");
        });

        txtGradoBuscar.textProperty().addListener((obs, oldV, newV) -> {
            gradosObservable.setAll(gradoRepo.search(newV));
            lblGradoContador.setText("Total: " + gradosObservable.size() + " grados");
        });

        txtInscripcionBuscar.textProperty().addListener((obs, oldV, newV) -> {
            inscripcionesObservable.setAll(inscripcionRepo.search(newV));
            lblInscripcionContador.setText("Total: " + inscripcionesObservable.size() + " inscripciones");
        });
    }

    private void configurarSeleccionFilas() {
        tblCursos.getSelectionModel().selectedItemProperty().addListener((obs, o, c) -> {
            if (c != null) {
                txtCursoId.setText(String.valueOf(c.getIdCurso()));
                txtCursoNombre.setText(c.getNombre());
                cbCursoDocente.setValue(c.getDocente());
                cbCursoGrado.setValue(c.getGradoSeccion());
            }
        });

        tblGrados.getSelectionModel().selectedItemProperty().addListener((obs, o, g) -> {
            if (g != null) {
                txtGradoId.setText(String.valueOf(g.getIdGradoSeccion()));
                txtGradoNombre.setText(g.getNombre());
                txtGradoNivel.setText(g.getNivel());
                txtGradoSeccion.setText(String.valueOf(g.getSeccion()));
            }
        });

        tblInscripciones.getSelectionModel().selectedItemProperty().addListener((obs, o, ins) -> {
            if (ins != null) {
                txtInscripcionId.setText(String.valueOf(ins.getIdInscripcion()));
                cbInscripcionEstudiante.setValue(ins.getEstudiante());
                cbInscripcionGrado.setValue(ins.getGradoSeccion());
            }
        });
    }

    private void cargarDatosGenerales() {
        // Cargar listas en tablas
        cursosObservable.setAll(cursoRepo.findAll());
        lblCursoContador.setText("Total: " + cursosObservable.size() + " cursos");

        gradosObservable.setAll(gradoRepo.findAll());
        lblGradoContador.setText("Total: " + gradosObservable.size() + " grados");

        inscripcionesObservable.setAll(inscripcionRepo.findAll());
        lblInscripcionContador.setText("Total: " + inscripcionesObservable.size() + " inscripciones");

        // Cargar combos
        cbCursoDocente.setItems(FXCollections.observableArrayList(docenteRepo.findAll()));
        List<GradoSeccion> listaGrados = gradoRepo.findAll();
        cbCursoGrado.setItems(FXCollections.observableArrayList(listaGrados));
        cbInscripcionGrado.setItems(FXCollections.observableArrayList(listaGrados));
        cbInscripcionEstudiante.setItems(FXCollections.observableArrayList(estudianteRepo.findAll()));

        // Obtener ciclo escolar activo
        CicloEscolar activo = cicloRepo.findAll().stream().filter(CicloEscolar::isActivo).findFirst().orElse(null);
        if (activo != null) {
            lblCicloActivoInfo.setText("Ciclo Activo Vigente: " + activo.getAnio());
        } else {
            lblCicloActivoInfo.setText("Sin ciclo activo configurado");
        }
    }

    // ==========================================
    // OPERACIONES CRUD: CURSOS
    // ==========================================
    @FXML
    private void handleGuardarCurso(ActionEvent event) {
        if (txtCursoNombre.getText().trim().isEmpty() || cbCursoDocente.getValue() == null || cbCursoGrado.getValue() == null) {
            mostrarAlerta("Campos requeridos", "Complete el nombre, seleccione docente y grado.");
            return;
        }
        int nuevoId = cursosObservable.size() + 1;
        cursoRepo.save(new Curso(nuevoId, txtCursoNombre.getText().trim(), cbCursoDocente.getValue(), cbCursoGrado.getValue()));
        cursosObservable.setAll(cursoRepo.findAll());
        handleLimpiarCurso(null);
    }

    @FXML
    private void handleEliminarCurso(ActionEvent event) {
        if (txtCursoId.getText().isEmpty()) return;
        cursoRepo.delete(Integer.parseInt(txtCursoId.getText()));
        cursosObservable.setAll(cursoRepo.findAll());
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

    // ==========================================
    // OPERACIONES CRUD: INSCRIPCIONES
    // ==========================================
    @FXML
    private void handleGuardarInscripcion(ActionEvent event) {
        if (cbInscripcionEstudiante.getValue() == null || cbInscripcionGrado.getValue() == null) {
            mostrarAlerta("Campos requeridos", "Seleccione un estudiante y un grado para matricular.");
            return;
        }
        CicloEscolar cicloActivo = cicloRepo.findAll().stream().filter(CicloEscolar::isActivo).findFirst().orElse(null);
        if (cicloActivo == null) {
            mostrarAlerta("Error", "No hay ciclo escolar activo disponible.");
            return;
        }

        int nuevoId = inscripcionesObservable.size() + 1;
        Inscripcion nueva = new Inscripcion(nuevoId, LocalDate.now(), cbInscripcionEstudiante.getValue(), cbInscripcionGrado.getValue(), cicloActivo);
        inscripcionRepo.save(nueva);
        inscripcionesObservable.setAll(inscripcionRepo.findAll());
        handleLimpiarInscripcion(null);
    }

    @FXML
    private void handleEliminarInscripcion(ActionEvent event) {
        if (txtInscripcionId.getText().isEmpty()) return;
        inscripcionRepo.delete(Integer.parseInt(txtInscripcionId.getText()));
        inscripcionesObservable.setAll(inscripcionRepo.findAll());
        handleLimpiarInscripcion(null);
    }

    @FXML
    private void handleLimpiarInscripcion(ActionEvent event) {
        txtInscripcionId.clear();
        cbInscripcionEstudiante.setValue(null);
        cbInscripcionGrado.setValue(null);
        tblInscripciones.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}