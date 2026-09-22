package gt.edu.umg.gestionescolar.controller;

import gt.edu.umg.gestionescolar.model.Docente;
import gt.edu.umg.gestionescolar.model.Encargado;
import gt.edu.umg.gestionescolar.model.Estudiante;
import gt.edu.umg.gestionescolar.repository.DocenteRepository;
import gt.edu.umg.gestionescolar.repository.EncargadoRepository;
import gt.edu.umg.gestionescolar.repository.EstudianteRepository;
import gt.edu.umg.gestionescolar.repository.Repository;

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
import java.util.*;
import java.util.regex.Pattern;

/**
 * Controlador principal para el Módulo Base y Personas.
 * Opera 100% con almacenamiento en memoria mediante listas de objetos (ArrayList).
 * Cumple con los requerimientos del Entregable 2 (Programación II).
 */
public class PersonasController implements Initializable {

    // Repositorios en memoria basados en ArrayList
    private final Repository<Estudiante> estudianteRepo = new EstudianteRepository();
    private final Repository<Docente> docenteRepo = new DocenteRepository();
    private final Repository<Encargado> encargadoRepo = new EncargadoRepository();

    // ==========================================
    // CONTROLES DE CABECERA Y ESTADO
    // ==========================================
    @FXML private TabPane tabPanePrincipal;
    @FXML private Label lblEstadoSistema;

    // ==========================================
    // PESTAÑA 1: ESTUDIANTES
    // ==========================================
    @FXML private TextField txtEstudianteBuscar;
    @FXML private Label lblEstudianteContador;
    @FXML private TextField txtEstudianteId;
    @FXML private TextField txtEstudianteCarnet;
    @FXML private TextField txtEstudianteNombre;
    @FXML private TextField txtEstudianteApellido;
    @FXML private TextField txtEstudianteTelefono;
    @FXML private TextField txtEstudianteEmail;
    @FXML private DatePicker dpEstudianteFechaNac;
    @FXML private ComboBox<Encargado> cbEstudianteEncargado;

    @FXML private TableView<Estudiante> tblEstudiantes;
    @FXML private TableColumn<Estudiante, Integer> colEstudianteId;
    @FXML private TableColumn<Estudiante, String> colEstudianteCarnet;
    @FXML private TableColumn<Estudiante, String> colEstudianteNombre;
    @FXML private TableColumn<Estudiante, String> colEstudianteApellido;
    @FXML private TableColumn<Estudiante, String> colEstudianteTelefono;
    @FXML private TableColumn<Estudiante, String> colEstudianteEmail;
    @FXML private TableColumn<Estudiante, String> colEstudianteFechaNac;
    @FXML private TableColumn<Estudiante, String> colEstudianteEncargado;

    // ==========================================
    // PESTAÑA 2: DOCENTES
    // ==========================================
    @FXML private TextField txtDocenteBuscar;
    @FXML private Label lblDocenteContador;
    @FXML private TextField txtDocenteId;
    @FXML private TextField txtDocenteCodigo;
    @FXML private TextField txtDocenteNombre;
    @FXML private TextField txtDocenteApellido;
    @FXML private TextField txtDocenteEspecialidad;
    @FXML private TextField txtDocenteTelefono;
    @FXML private TextField txtDocenteEmail;

    @FXML private TableView<Docente> tblDocentes;
    @FXML private TableColumn<Docente, Integer> colDocenteId;
    @FXML private TableColumn<Docente, String> colDocenteCodigo;
    @FXML private TableColumn<Docente, String> colDocenteNombre;
    @FXML private TableColumn<Docente, String> colDocenteApellido;
    @FXML private TableColumn<Docente, String> colDocenteEspecialidad;
    @FXML private TableColumn<Docente, String> colDocenteTelefono;
    @FXML private TableColumn<Docente, String> colDocenteEmail;

    // ==========================================
    // PESTAÑA 3: ENCARGADOS
    // ==========================================
    @FXML private TextField txtEncargadoBuscar;
    @FXML private Label lblEncargadoContador;
    @FXML private TextField txtEncargadoId;
    @FXML private TextField txtEncargadoCui;
    @FXML private TextField txtEncargadoNombre;
    @FXML private TextField txtEncargadoApellido;
    @FXML private ComboBox<String> cbEncargadoParentesco;
    @FXML private TextField txtEncargadoDireccion;
    @FXML private TextField txtEncargadoTelefono;
    @FXML private TextField txtEncargadoEmail;

    @FXML private TableView<Encargado> tblEncargados;
    @FXML private TableColumn<Encargado, Integer> colEncargadoId;
    @FXML private TableColumn<Encargado, String> colEncargadoCui;
    @FXML private TableColumn<Encargado, String> colEncargadoNombre;
    @FXML private TableColumn<Encargado, String> colEncargadoApellido;
    @FXML private TableColumn<Encargado, String> colEncargadoParentesco;
    @FXML private TableColumn<Encargado, String> colEncargadoTelefono;
    @FXML private TableColumn<Encargado, String> colEncargadoEmail;
    @FXML private TableColumn<Encargado, String> colEncargadoDireccion;

    // Listas observables para las tablas
    private final ObservableList<Estudiante> estudiantesObservable = FXCollections.observableArrayList();
    private final ObservableList<Docente> docentesObservable = FXCollections.observableArrayList();
    private final ObservableList<Encargado> encargadosObservable = FXCollections.observableArrayList();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTablas();
        configurarComboBoxes();
        configurarFiltrosBusqueda();
        configurarSeleccionFilas();
        cargarDatosGenerales();
    }

    // ==========================================
    // CONFIGURACIONES INICIALES
    // ==========================================

    private void configurarTablas() {
        // Tabla Estudiantes
        colEstudianteId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEstudianteCarnet.setCellValueFactory(new PropertyValueFactory<>("carnet"));
        colEstudianteNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEstudianteApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colEstudianteTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEstudianteEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEstudianteFechaNac.setCellValueFactory(cellData -> {
            LocalDate fecha = cellData.getValue().getFechaNacimiento();
            return new SimpleStringProperty(fecha != null ? fecha.format(DATE_FORMATTER) : "N/A");
        });
        colEstudianteEncargado.setCellValueFactory(cellData -> {
            Integer idEnc = cellData.getValue().getIdEncargado();
            if (idEnc == null || idEnc <= 0) {
                return new SimpleStringProperty("Sin Encargado");
            }
            Optional<Encargado> enc = encargadoRepo.findById(idEnc);
            return new SimpleStringProperty(enc.map(e -> e.getNombreCompleto() + " (" + e.getParentesco() + ")").orElse("ID: " + idEnc));
        });
        tblEstudiantes.setItems(estudiantesObservable);
        tblEstudiantes.setPlaceholder(new Label("No hay estudiantes registrados en memoria."));

        // Tabla Docentes
        colDocenteId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDocenteCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoEmpleado"));
        colDocenteNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDocenteApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colDocenteEspecialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        colDocenteTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colDocenteEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tblDocentes.setItems(docentesObservable);
        tblDocentes.setPlaceholder(new Label("No hay docentes registrados en memoria."));

        // Tabla Encargados
        colEncargadoId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEncargadoCui.setCellValueFactory(new PropertyValueFactory<>("cui"));
        colEncargadoNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEncargadoApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colEncargadoParentesco.setCellValueFactory(new PropertyValueFactory<>("parentesco"));
        colEncargadoTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEncargadoEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEncargadoDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        tblEncargados.setItems(encargadosObservable);
        tblEncargados.setPlaceholder(new Label("No hay encargados registrados en memoria."));
    }

    private void configurarComboBoxes() {
        cbEncargadoParentesco.setItems(FXCollections.observableArrayList(
                "Padre", "Madre", "Tutor Legal", "Abuelo/a", "Tío/a", "Hermano/a Mayor", "Otro"
        ));
        cbEncargadoParentesco.setEditable(true);

        cbEstudianteEncargado.setConverter(new StringConverter<>() {
            @Override
            public String toString(Encargado enc) {
                if (enc == null || enc.getId() == 0) {
                    return "-- Ninguno (Sin Encargado) --";
                }
                return enc.getNombreCompleto() + " (" + enc.getParentesco() + ") - DPI: " + enc.getCui();
            }

            @Override
            public Encargado fromString(String string) {
                return null;
            }
        });
    }

    private void configurarFiltrosBusqueda() {
        txtEstudianteBuscar.textProperty().addListener((obs, oldV, newV) -> filtrarEstudiantes(newV));
        txtDocenteBuscar.textProperty().addListener((obs, oldV, newV) -> filtrarDocentes(newV));
        txtEncargadoBuscar.textProperty().addListener((obs, oldV, newV) -> filtrarEncargados(newV));
    }

    private void configurarSeleccionFilas() {
        tblEstudiantes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, est) -> {
            if (est != null) {
                cargarEstudianteAlFormulario(est);
            }
        });

        tblDocentes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, doc) -> {
            if (doc != null) {
                cargarDocenteAlFormulario(doc);
            }
        });

        tblEncargados.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, enc) -> {
            if (enc != null) {
                cargarEncargadoAlFormulario(enc);
            }
        });
    }

    // ==========================================
    // CARGA Y REFRESH DE DATOS EN MEMORIA
    // ==========================================

    private void cargarDatosGenerales() {
        cargarEncargados();
        cargarEstudiantes();
        cargarDocentes();
        actualizarEstado("Datos en memoria sincronizados.");
    }

    private void cargarEstudiantes() {
        List<Estudiante> lista = estudianteRepo.findAll();
        estudiantesObservable.setAll(lista);
        lblEstudianteContador.setText("Total: " + lista.size() + " estudiantes");
    }

    private void cargarDocentes() {
        List<Docente> lista = docenteRepo.findAll();
        docentesObservable.setAll(lista);
        lblDocenteContador.setText("Total: " + lista.size() + " docentes");
    }

    private void cargarEncargados() {
        List<Encargado> lista = encargadoRepo.findAll();
        encargadosObservable.setAll(lista);
        lblEncargadoContador.setText("Total: " + lista.size() + " encargados");

        List<Encargado> comboList = new ArrayList<>();
        Encargado opcionVacia = new Encargado(0, "", "", "", "", "", "", "");
        comboList.add(opcionVacia);
        comboList.addAll(lista);
        cbEstudianteEncargado.setItems(FXCollections.observableArrayList(comboList));
    }

    private void actualizarEstado(String mensaje) {
        lblEstadoSistema.setText(mensaje + " | Almacenamiento: ArrayList");
    }

    // ==========================================
    // CRUD: ESTUDIANTES (MEMORIA)
    // ==========================================

    @FXML
    private void handleGuardarEstudiante(ActionEvent event) {
        if (!validarFormularioEstudiante()) return;

        Estudiante estudiante = construirEstudianteDesdeFormulario(0);
        estudianteRepo.save(estudiante);

        cargarEstudiantes();
        limpiarFormularioEstudiante();
        actualizarEstado("Estudiante agregado a la lista en memoria.");
        mostrarAlerta(Alert.AlertType.INFORMATION, "Estudiante Agregado", "Estudiante agregado con éxito a la lista en memoria.");
    }

    @FXML
    private void handleActualizarEstudiante(ActionEvent event) {
        String idStr = txtEstudianteId.getText();
        if (idStr == null || idStr.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Seleccione un estudiante de la tabla para modificar.");
            return;
        }

        if (!validarFormularioEstudiante()) return;

        int id = Integer.parseInt(idStr.trim());
        Estudiante estudiante = construirEstudianteDesdeFormulario(id);
        estudianteRepo.update(estudiante);

        cargarEstudiantes();
        limpiarFormularioEstudiante();
        actualizarEstado("Estudiante modificado en memoria.");
        mostrarAlerta(Alert.AlertType.INFORMATION, "Estudiante Modificado", "Los datos del estudiante han sido modificados.");
    }

    @FXML
    private void handleEliminarEstudiante(ActionEvent event) {
        String idStr = txtEstudianteId.getText();
        if (idStr == null || idStr.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Seleccione un estudiante de la tabla para eliminar.");
            return;
        }

        int id = Integer.parseInt(idStr.trim());
        String nombre = txtEstudianteNombre.getText() + " " + txtEstudianteApellido.getText();

        if (confirmarAccion("Confirmar Eliminación", "¿Desea eliminar al estudiante '" + nombre + "' de la lista?")) {
            estudianteRepo.delete(id);
            cargarEstudiantes();
            limpiarFormularioEstudiante();
            actualizarEstado("Estudiante eliminado de la lista.");
            mostrarAlerta(Alert.AlertType.INFORMATION, "Estudiante Eliminado", "Estudiante eliminado exitosamente.");
        }
    }

    @FXML
    private void handleLimpiarEstudiante(ActionEvent event) {
        limpiarFormularioEstudiante();
    }

    @FXML
    private void handleLimpiarBusquedaEstudiante(ActionEvent event) {
        txtEstudianteBuscar.clear();
        filtrarEstudiantes("");
    }

    private void filtrarEstudiantes(String criterio) {
        List<Estudiante> resultados = estudianteRepo.search(criterio);
        estudiantesObservable.setAll(resultados);
        lblEstudianteContador.setText("Resultados: " + resultados.size() + " estudiantes");
    }

    private void cargarEstudianteAlFormulario(Estudiante est) {
        txtEstudianteId.setText(String.valueOf(est.getId()));
        txtEstudianteCarnet.setText(est.getCarnet());
        txtEstudianteNombre.setText(est.getNombre());
        txtEstudianteApellido.setText(est.getApellido());
        txtEstudianteTelefono.setText(est.getTelefono() != null ? est.getTelefono() : "");
        txtEstudianteEmail.setText(est.getEmail() != null ? est.getEmail() : "");
        dpEstudianteFechaNac.setValue(est.getFechaNacimiento());

        if (est.getIdEncargado() != null && est.getIdEncargado() > 0) {
            cbEstudianteEncargado.getItems().stream()
                    .filter(enc -> enc.getId() == est.getIdEncargado())
                    .findFirst()
                    .ifPresent(cbEstudianteEncargado::setValue);
        } else {
            cbEstudianteEncargado.setValue(cbEstudianteEncargado.getItems().isEmpty() ? null : cbEstudianteEncargado.getItems().get(0));
        }
    }

    private void limpiarFormularioEstudiante() {
        txtEstudianteId.clear();
        txtEstudianteCarnet.clear();
        txtEstudianteNombre.clear();
        txtEstudianteApellido.clear();
        txtEstudianteTelefono.clear();
        txtEstudianteEmail.clear();
        dpEstudianteFechaNac.setValue(null);
        if (!cbEstudianteEncargado.getItems().isEmpty()) {
            cbEstudianteEncargado.setValue(cbEstudianteEncargado.getItems().get(0));
        }
        tblEstudiantes.getSelectionModel().clearSelection();
    }

    private boolean validarFormularioEstudiante() {
        if (esVacio(txtEstudianteCarnet.getText())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", "El carné del estudiante es obligatorio.");
            txtEstudianteCarnet.requestFocus();
            return false;
        }
        if (esVacio(txtEstudianteNombre.getText())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", "Los nombres del estudiante son obligatorios.");
            txtEstudianteNombre.requestFocus();
            return false;
        }
        if (esVacio(txtEstudianteApellido.getText())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", "Los apellidos del estudiante son obligatorios.");
            txtEstudianteApellido.requestFocus();
            return false;
        }
        if (dpEstudianteFechaNac.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", "Debe seleccionar la fecha de nacimiento.");
            dpEstudianteFechaNac.requestFocus();
            return false;
        }
        if (dpEstudianteFechaNac.getValue().isAfter(LocalDate.now())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Fecha Inválida", "La fecha de nacimiento no puede ser futura.");
            dpEstudianteFechaNac.requestFocus();
            return false;
        }
        if (!esVacio(txtEstudianteEmail.getText()) && !EMAIL_PATTERN.matcher(txtEstudianteEmail.getText().trim()).matches()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Correo Inválido", "Formato de correo inválido (ej. alumno@miumg.edu.gt).");
            txtEstudianteEmail.requestFocus();
            return false;
        }
        return true;
    }

    private Estudiante construirEstudianteDesdeFormulario(int id) {
        Encargado encSeleccionado = cbEstudianteEncargado.getValue();
        Integer idEncargado = (encSeleccionado != null && encSeleccionado.getId() > 0) ? encSeleccionado.getId() : null;

        return new Estudiante(
                id,
                txtEstudianteNombre.getText().trim(),
                txtEstudianteApellido.getText().trim(),
                txtEstudianteTelefono.getText() != null ? txtEstudianteTelefono.getText().trim() : "",
                txtEstudianteEmail.getText() != null ? txtEstudianteEmail.getText().trim() : "",
                txtEstudianteCarnet.getText().trim(),
                dpEstudianteFechaNac.getValue(),
                idEncargado
        );
    }

    // ==========================================
    // CRUD: DOCENTES (MEMORIA)
    // ==========================================

    @FXML
    private void handleGuardarDocente(ActionEvent event) {
        if (!validarFormularioDocente()) return;

        Docente docente = construirDocenteDesdeFormulario(0);
        docenteRepo.save(docente);

        cargarDocentes();
        limpiarFormularioDocente();
        actualizarEstado("Docente agregado a la lista en memoria.");
        mostrarAlerta(Alert.AlertType.INFORMATION, "Docente Agregado", "Docente agregado con éxito a la lista en memoria.");
    }

    @FXML
    private void handleActualizarDocente(ActionEvent event) {
        String idStr = txtDocenteId.getText();
        if (idStr == null || idStr.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Seleccione un docente de la tabla para modificar.");
            return;
        }

        if (!validarFormularioDocente()) return;

        int id = Integer.parseInt(idStr.trim());
        Docente docente = construirDocenteDesdeFormulario(id);
        docenteRepo.update(docente);

        cargarDocentes();
        limpiarFormularioDocente();
        actualizarEstado("Docente modificado en memoria.");
        mostrarAlerta(Alert.AlertType.INFORMATION, "Docente Modificado", "Los datos del docente han sido modificados.");
    }

    @FXML
    private void handleEliminarDocente(ActionEvent event) {
        String idStr = txtDocenteId.getText();
        if (idStr == null || idStr.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Seleccione un docente de la tabla para eliminar.");
            return;
        }

        int id = Integer.parseInt(idStr.trim());
        String nombre = txtDocenteNombre.getText() + " " + txtDocenteApellido.getText();

        if (confirmarAccion("Confirmar Eliminación", "¿Desea eliminar al docente '" + nombre + "' de la lista?")) {
            docenteRepo.delete(id);
            cargarDocentes();
            limpiarFormularioDocente();
            actualizarEstado("Docente eliminado de la lista.");
            mostrarAlerta(Alert.AlertType.INFORMATION, "Docente Eliminado", "Docente eliminado exitosamente.");
        }
    }

    @FXML
    private void handleLimpiarDocente(ActionEvent event) {
        limpiarFormularioDocente();
    }

    @FXML
    private void handleLimpiarBusquedaDocente(ActionEvent event) {
        txtDocenteBuscar.clear();
        filtrarDocentes("");
    }

    private void filtrarDocentes(String criterio) {
        List<Docente> resultados = docenteRepo.search(criterio);
        docentesObservable.setAll(resultados);
        lblDocenteContador.setText("Resultados: " + resultados.size() + " docentes");
    }

    private void cargarDocenteAlFormulario(Docente doc) {
        txtDocenteId.setText(String.valueOf(doc.getId()));
        txtDocenteCodigo.setText(doc.getCodigoEmpleado());
        txtDocenteNombre.setText(doc.getNombre());
        txtDocenteApellido.setText(doc.getApellido());
        txtDocenteEspecialidad.setText(doc.getEspecialidad());
        txtDocenteTelefono.setText(doc.getTelefono() != null ? doc.getTelefono() : "");
        txtDocenteEmail.setText(doc.getEmail() != null ? doc.getEmail() : "");
    }

    private void limpiarFormularioDocente() {
        txtDocenteId.clear();
        txtDocenteCodigo.clear();
        txtDocenteNombre.clear();
        txtDocenteApellido.clear();
        txtDocenteEspecialidad.clear();
        txtDocenteTelefono.clear();
        txtDocenteEmail.clear();
        tblDocentes.getSelectionModel().clearSelection();
    }

    private boolean validarFormularioDocente() {
        if (esVacio(txtDocenteCodigo.getText())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", "El código de empleado es obligatorio.");
            txtDocenteCodigo.requestFocus();
            return false;
        }
        if (esVacio(txtDocenteNombre.getText())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", "Los nombres del docente son obligatorios.");
            txtDocenteNombre.requestFocus();
            return false;
        }
        if (esVacio(txtDocenteApellido.getText())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", "Los apellidos del docente son obligatorios.");
            txtDocenteApellido.requestFocus();
            return false;
        }
        if (esVacio(txtDocenteEspecialidad.getText())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", "La especialidad del docente es obligatoria.");
            txtDocenteEspecialidad.requestFocus();
            return false;
        }
        if (!esVacio(txtDocenteEmail.getText()) && !EMAIL_PATTERN.matcher(txtDocenteEmail.getText().trim()).matches()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Correo Inválido", "Formato de correo electrónico inválido.");
            txtDocenteEmail.requestFocus();
            return false;
        }
        return true;
    }

    private Docente construirDocenteDesdeFormulario(int id) {
        return new Docente(
                id,
                txtDocenteNombre.getText().trim(),
                txtDocenteApellido.getText().trim(),
                txtDocenteTelefono.getText() != null ? txtDocenteTelefono.getText().trim() : "",
                txtDocenteEmail.getText() != null ? txtDocenteEmail.getText().trim() : "",
                txtDocenteCodigo.getText().trim(),
                txtDocenteEspecialidad.getText().trim()
        );
    }

    // ==========================================
    // CRUD: ENCARGADOS (MEMORIA)
    // ==========================================

    @FXML
    private void handleGuardarEncargado(ActionEvent event) {
        if (!validarFormularioEncargado()) return;

        Encargado encargado = construirEncargadoDesdeFormulario(0);
        encargadoRepo.save(encargado);

        cargarEncargados();
        limpiarFormularioEncargado();
        actualizarEstado("Encargado agregado a la lista en memoria.");
        mostrarAlerta(Alert.AlertType.INFORMATION, "Encargado Agregado", "Encargado agregado con éxito a la lista en memoria.");
    }

    @FXML
    private void handleActualizarEncargado(ActionEvent event) {
        String idStr = txtEncargadoId.getText();
        if (idStr == null || idStr.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Seleccione un encargado de la tabla para modificar.");
            return;
        }

        if (!validarFormularioEncargado()) return;

        int id = Integer.parseInt(idStr.trim());
        Encargado encargado = construirEncargadoDesdeFormulario(id);
        encargadoRepo.update(encargado);

        cargarEncargados();
        cargarEstudiantes(); // Refresca nombres de encargados en la tabla de estudiantes
        limpiarFormularioEncargado();
        actualizarEstado("Encargado modificado en memoria.");
        mostrarAlerta(Alert.AlertType.INFORMATION, "Encargado Modificado", "Los datos del encargado han sido modificados.");
    }

    @FXML
    private void handleEliminarEncargado(ActionEvent event) {
        String idStr = txtEncargadoId.getText();
        if (idStr == null || idStr.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Seleccione un encargado de la tabla para eliminar.");
            return;
        }

        int id = Integer.parseInt(idStr.trim());
        String nombre = txtEncargadoNombre.getText() + " " + txtEncargadoApellido.getText();

        if (confirmarAccion("Confirmar Eliminación", "¿Desea eliminar al encargado '" + nombre + "' de la lista?")) {
            encargadoRepo.delete(id);
            cargarEncargados();
            cargarEstudiantes();
            limpiarFormularioEncargado();
            actualizarEstado("Encargado eliminado de la lista.");
            mostrarAlerta(Alert.AlertType.INFORMATION, "Encargado Eliminado", "Encargado eliminado exitosamente.");
        }
    }

    @FXML
    private void handleLimpiarEncargado(ActionEvent event) {
        limpiarFormularioEncargado();
    }

    @FXML
    private void handleLimpiarBusquedaEncargado(ActionEvent event) {
        txtEncargadoBuscar.clear();
        filtrarEncargados("");
    }

    private void filtrarEncargados(String criterio) {
        List<Encargado> resultados = encargadoRepo.search(criterio);
        encargadosObservable.setAll(resultados);
        lblEncargadoContador.setText("Resultados: " + resultados.size() + " encargados");
    }

    private void cargarEncargadoAlFormulario(Encargado enc) {
        txtEncargadoId.setText(String.valueOf(enc.getId()));
        txtEncargadoCui.setText(enc.getCui());
        txtEncargadoNombre.setText(enc.getNombre());
        txtEncargadoApellido.setText(enc.getApellido());
        cbEncargadoParentesco.setValue(enc.getParentesco());
        txtEncargadoDireccion.setText(enc.getDireccion());
        txtEncargadoTelefono.setText(enc.getTelefono() != null ? enc.getTelefono() : "");
        txtEncargadoEmail.setText(enc.getEmail() != null ? enc.getEmail() : "");
    }

    private void limpiarFormularioEncargado() {
        txtEncargadoId.clear();
        txtEncargadoCui.clear();
        txtEncargadoNombre.clear();
        txtEncargadoApellido.clear();
        cbEncargadoParentesco.setValue(null);
        txtEncargadoDireccion.clear();
        txtEncargadoTelefono.clear();
        txtEncargadoEmail.clear();
        tblEncargados.getSelectionModel().clearSelection();
    }

    private boolean validarFormularioEncargado() {
        String cui = txtEncargadoCui.getText();
        if (esVacio(cui)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", "El CUI (DPI) del encargado es obligatorio.");
            txtEncargadoCui.requestFocus();
            return false;
        }
        if (cui.trim().length() < 13) {
            mostrarAlerta(Alert.AlertType.WARNING, "CUI Inválido", "El CUI debe contener al menos 13 dígitos.");
            txtEncargadoCui.requestFocus();
            return false;
        }
        if (esVacio(txtEncargadoNombre.getText())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", "Los nombres del encargado son obligatorios.");
            txtEncargadoNombre.requestFocus();
            return false;
        }
        if (esVacio(txtEncargadoApellido.getText())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", "Los apellidos del encargado son obligatorios.");
            txtEncargadoApellido.requestFocus();
            return false;
        }
        String parentesco = cbEncargadoParentesco.getValue();
        if (esVacio(parentesco)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", "Debe indicar el parentesco.");
            cbEncargadoParentesco.requestFocus();
            return false;
        }
        if (esVacio(txtEncargadoDireccion.getText())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", "La dirección es obligatoria.");
            txtEncargadoDireccion.requestFocus();
            return false;
        }
        if (!esVacio(txtEncargadoEmail.getText()) && !EMAIL_PATTERN.matcher(txtEncargadoEmail.getText().trim()).matches()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Correo Inválido", "Formato de correo electrónico inválido.");
            txtEncargadoEmail.requestFocus();
            return false;
        }
        return true;
    }

    private Encargado construirEncargadoDesdeFormulario(int id) {
        return new Encargado(
                id,
                txtEncargadoNombre.getText().trim(),
                txtEncargadoApellido.getText().trim(),
                txtEncargadoTelefono.getText() != null ? txtEncargadoTelefono.getText().trim() : "",
                txtEncargadoEmail.getText() != null ? txtEncargadoEmail.getText().trim() : "",
                txtEncargadoCui.getText().trim(),
                cbEncargadoParentesco.getValue() != null ? cbEncargadoParentesco.getValue().trim() : "Encargado",
                txtEncargadoDireccion.getText().trim()
        );
    }

    // ==========================================
    // MÉTODOS DE UTILIDAD
    // ==========================================

    private boolean esVacio(String str) {
        return str == null || str.trim().isEmpty();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private boolean confirmarAccion(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }
}
