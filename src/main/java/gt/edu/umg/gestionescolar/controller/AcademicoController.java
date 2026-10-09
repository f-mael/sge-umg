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
import java.util.ResourceBundle;

public class AcademicoController implements Initializable {

    // Repositorios compartidos entre los módulos.
    private final GradoSeccionRepository gradoRepo =
            DatosCompartidos.getGrados();

    private final CursoRepository cursoRepo =
            DatosCompartidos.getCursos();

    private final CicloEscolarRepository cicloRepo =
            DatosCompartidos.getCiclos();

    private final InscripcionRepository inscripcionRepo =
            DatosCompartidos.getInscripciones();

    private final Repository<Estudiante> estudianteRepo =
            DatosCompartidos.getEstudiantes();

    private final Repository<Docente> docenteRepo =
            DatosCompartidos.getDocentes();

    // CURSOS
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

    // GRADOS Y SECCIONES
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

    // MATRICULACIÓN / INSCRIPCIONES
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

    private final ObservableList<Curso> cursosObservable =
            FXCollections.observableArrayList();

    private final ObservableList<GradoSeccion> gradosObservable =
            FXCollections.observableArrayList();

    private final ObservableList<Inscripcion> inscripcionesObservable =
            FXCollections.observableArrayList();

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
        colCursoId.setCellValueFactory(
                new PropertyValueFactory<>("idCurso"));

        colCursoNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre"));

        colCursoDocente.setCellValueFactory(cellData -> {
            Docente docente = cellData.getValue().getDocente();

            return new SimpleStringProperty(
                    docente != null
                            ? docente.getNombre() + " " + docente.getApellido()
                            : "Sin Docente"
            );
        });

        colCursoGrado.setCellValueFactory(cellData -> {
            GradoSeccion grado = cellData.getValue().getGradoSeccion();

            return new SimpleStringProperty(
                    grado != null
                            ? grado.getNombre() + " - Sección " + grado.getSeccion()
                            : "Sin Grado"
            );
        });

        tblCursos.setItems(cursosObservable);

        // Grados
        colGradoId.setCellValueFactory(
                new PropertyValueFactory<>("idGradoSeccion"));

        colGradoNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre"));

        colGradoNivel.setCellValueFactory(
                new PropertyValueFactory<>("nivel"));

        colGradoSeccion.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        String.valueOf(cellData.getValue().getSeccion())
                )
        );

        tblGrados.setItems(gradosObservable);

        // Inscripciones
        colInsId.setCellValueFactory(
                new PropertyValueFactory<>("idInscripcion"));

        colInsFecha.setCellValueFactory(cellData -> {
            LocalDate fecha = cellData.getValue().getFechaInscripcion();

            return new SimpleStringProperty(
                    fecha != null ? fecha.format(DATE_FORMATTER) : "N/A"
            );
        });

        colInsEstudiante.setCellValueFactory(cellData -> {
            Estudiante estudiante = cellData.getValue().getEstudiante();

            return new SimpleStringProperty(
                    estudiante != null
                            ? estudiante.getCarnet() + " - "
                            + estudiante.getNombre() + " "
                            + estudiante.getApellido()
                            : "N/A"
            );
        });

        colInsGrado.setCellValueFactory(cellData -> {
            GradoSeccion grado = cellData.getValue().getGradoSeccion();

            return new SimpleStringProperty(
                    grado != null
                            ? grado.getNombre() + " (" + grado.getSeccion() + ")"
                            : "N/A"
            );
        });

        colInsCiclo.setCellValueFactory(cellData -> {
            CicloEscolar ciclo = cellData.getValue().getCicloEscolar();

            return new SimpleStringProperty(
                    ciclo != null ? String.valueOf(ciclo.getAnio()) : "N/A"
            );
        });

        tblInscripciones.setItems(inscripcionesObservable);
    }

    private void configurarComboBoxes() {
        cbCursoDocente.setConverter(new StringConverter<Docente>() {
            @Override
            public String toString(Docente docente) {
                if (docente == null) {
                    return "-- Seleccionar Docente --";
                }

                return docente.getNombre() + " " + docente.getApellido()
                        + " (" + docente.getEspecialidad() + ")";
            }

            @Override
            public Docente fromString(String texto) {
                return null;
            }
        });

        StringConverter<GradoSeccion> gradoConverter =
                new StringConverter<GradoSeccion>() {
                    @Override
                    public String toString(GradoSeccion grado) {
                        if (grado == null) {
                            return "-- Seleccionar Grado --";
                        }

                        return grado.getNombre() + " - " + grado.getSeccion()
                                + " (" + grado.getNivel() + ")";
                    }

                    @Override
                    public GradoSeccion fromString(String texto) {
                        return null;
                    }
                };

        cbCursoGrado.setConverter(gradoConverter);
        cbInscripcionGrado.setConverter(gradoConverter);

        cbInscripcionEstudiante.setConverter(
                new StringConverter<Estudiante>() {
                    @Override
                    public String toString(Estudiante estudiante) {
                        if (estudiante == null) {
                            return "-- Seleccionar Estudiante --";
                        }

                        return estudiante.getCarnet() + " - "
                                + estudiante.getNombre() + " "
                                + estudiante.getApellido();
                    }

                    @Override
                    public Estudiante fromString(String texto) {
                        return null;
                    }
                }
        );
    }

    private void configurarFiltros() {
        txtCursoBuscar.textProperty().addListener((obs, anterior, nuevo) -> {
            cursosObservable.setAll(cursoRepo.search(nuevo));
            lblCursoContador.setText(
                    "Total: " + cursosObservable.size() + " cursos"
            );
        });

        txtGradoBuscar.textProperty().addListener((obs, anterior, nuevo) -> {
            gradosObservable.setAll(gradoRepo.search(nuevo));
            lblGradoContador.setText(
                    "Total: " + gradosObservable.size() + " grados"
            );
        });

        txtInscripcionBuscar.textProperty().addListener(
                (obs, anterior, nuevo) -> {
                    inscripcionesObservable.setAll(
                            inscripcionRepo.search(nuevo)
                    );

                    lblInscripcionContador.setText(
                            "Total: " + inscripcionesObservable.size()
                                    + " inscripciones"
                    );
                }
        );
    }

    private void configurarSeleccionFilas() {
        tblCursos.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, curso) -> {
                    if (curso != null) {
                        txtCursoId.setText(
                                String.valueOf(curso.getIdCurso())
                        );
                        txtCursoNombre.setText(curso.getNombre());
                        cbCursoDocente.setValue(curso.getDocente());
                        cbCursoGrado.setValue(curso.getGradoSeccion());
                    }
                });

        tblGrados.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, grado) -> {
                    if (grado != null) {
                        txtGradoId.setText(
                                String.valueOf(grado.getIdGradoSeccion())
                        );
                        txtGradoNombre.setText(grado.getNombre());
                        txtGradoNivel.setText(grado.getNivel());
                        txtGradoSeccion.setText(
                                String.valueOf(grado.getSeccion())
                        );
                    }
                });

        tblInscripciones.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, inscripcion) -> {
                    if (inscripcion != null) {
                        txtInscripcionId.setText(
                                String.valueOf(inscripcion.getIdInscripcion())
                        );

                        cbInscripcionEstudiante.setValue(
                                inscripcion.getEstudiante()
                        );

                        cbInscripcionGrado.setValue(
                                inscripcion.getGradoSeccion()
                        );
                    }
                });
    }

    private void cargarDatosGenerales() {
        cursosObservable.setAll(cursoRepo.findAll());
        lblCursoContador.setText(
                "Total: " + cursosObservable.size() + " cursos"
        );

        gradosObservable.setAll(gradoRepo.findAll());
        lblGradoContador.setText(
                "Total: " + gradosObservable.size() + " grados"
        );

        inscripcionesObservable.setAll(inscripcionRepo.findAll());
        lblInscripcionContador.setText(
                "Total: " + inscripcionesObservable.size() + " inscripciones"
        );

        cbCursoDocente.setItems(
                FXCollections.observableArrayList(docenteRepo.findAll())
        );

        List<GradoSeccion> listaGrados = gradoRepo.findAll();

        cbCursoGrado.setItems(
                FXCollections.observableArrayList(listaGrados)
        );

        cbInscripcionGrado.setItems(
                FXCollections.observableArrayList(listaGrados)
        );

        cbInscripcionEstudiante.setItems(
                FXCollections.observableArrayList(estudianteRepo.findAll())
        );

        CicloEscolar activo = cicloRepo.findAll().stream()
                .filter(CicloEscolar::isActivo)
                .findFirst()
                .orElse(null);

        if (activo != null) {
            lblCicloActivoInfo.setText(
                    "Ciclo Activo Vigente: " + activo.getAnio()
            );
        } else {
            lblCicloActivoInfo.setText("Sin ciclo activo configurado");
        }
    }

    // OPERACIONES: CURSOS
    @FXML
    private void handleGuardarCurso(ActionEvent event) {
        if (txtCursoNombre.getText().trim().isEmpty()
                || cbCursoDocente.getValue() == null
                || cbCursoGrado.getValue() == null) {

            mostrarAlerta(
                    "Campos requeridos",
                    "Complete el nombre, seleccione docente y grado."
            );
            return;
        }

        int nuevoId = cursosObservable.size() + 1;

        cursoRepo.save(new Curso(
                nuevoId,
                txtCursoNombre.getText().trim(),
                cbCursoDocente.getValue(),
                cbCursoGrado.getValue()
        ));

        cursosObservable.setAll(cursoRepo.findAll());
        handleLimpiarCurso(null);
    }

    @FXML
    private void handleEliminarCurso(ActionEvent event) {
        if (txtCursoId.getText().isEmpty()) {
            return;
        }

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

    // OPERACIONES: INSCRIPCIONES
    @FXML
    private void handleGuardarInscripcion(ActionEvent event) {
        if (cbInscripcionEstudiante.getValue() == null
                || cbInscripcionGrado.getValue() == null) {

            mostrarAlerta(
                    "Campos requeridos",
                    "Seleccione un estudiante y un grado para matricular."
            );
            return;
        }

        CicloEscolar cicloActivo = cicloRepo.findAll().stream()
                .filter(CicloEscolar::isActivo)
                .findFirst()
                .orElse(null);

        if (cicloActivo == null) {
            mostrarAlerta(
                    "Error",
                    "No hay ciclo escolar activo disponible."
            );
            return;
        }

        int nuevoId = inscripcionesObservable.size() + 1;

        Inscripcion nueva = new Inscripcion(
                nuevoId,
                LocalDate.now(),
                cbInscripcionEstudiante.getValue(),
                cbInscripcionGrado.getValue(),
                cicloActivo
        );

        inscripcionRepo.save(nueva);
        inscripcionesObservable.setAll(inscripcionRepo.findAll());
        handleLimpiarInscripcion(null);
    }

    @FXML
    private void handleEliminarInscripcion(ActionEvent event) {
        if (txtInscripcionId.getText().isEmpty()) {
            return;
        }

        inscripcionRepo.delete(
                Integer.parseInt(txtInscripcionId.getText())
        );

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

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}