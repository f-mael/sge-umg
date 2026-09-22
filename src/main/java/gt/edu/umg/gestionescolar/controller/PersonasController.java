package gt.edu.umg.gestionescolar.controller;

import gt.edu.umg.gestionescolar.model.Docente;
import gt.edu.umg.gestionescolar.model.Encargado;
import gt.edu.umg.gestionescolar.model.Estudiante;
import gt.edu.umg.gestionescolar.repository.Repository;
import gt.edu.umg.gestionescolar.repository.RepositoryFactory;
import gt.edu.umg.gestionescolar.repository.RepositoryFactory.RepositoryType;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Controlador principal para el Módulo Base y Personas.
 * Gestiona el ciclo CRUD, enlace de datos, validaciones y reportes para
 * Estudiantes, Docentes y Encargados, con soporte de doble persistencia.
 */
public class PersonasController implements Initializable {

    // ==========================================
    // CONTROLES DE CABECERA Y ESTADO
    // ==========================================
    @FXML private ComboBox<RepositoryType> cbPersistencia;
    @FXML private TabPane tabPanePrincipal;
    @FXML private Label lblEstadoSistema;
    @FXML private Label lblPersistenciaActiva;

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

    // Listas observables enlazadas a las tablas
    private final ObservableList<Estudiante> estudiantesObservable = FXCollections.observableArrayList();
    private final ObservableList<Docente> docentesObservable = FXCollections.observableArrayList();
    private final ObservableList<Encargado> encargadosObservable = FXCollections.observableArrayList();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarSelectorPersistencia();
        configurarTablas();
        configurarComboBoxes();
        configurarFiltrosBusqueda();
        configurarSeleccionFilas();
        cargarDatosGenerales();
    }

    // ==========================================
    // CONFIGURACIONES INICIALES
    // ==========================================

    private void configurarSelectorPersistencia() {
        cbPersistencia.setItems(FXCollections.observableArrayList(RepositoryType.values()));
        cbPersistencia.setValue(RepositoryFactory.getRepositoryType());
        cbPersistencia.setConverter(new StringConverter<>() {
            @Override
            public String toString(RepositoryType object) {
                return object != null ? object.getDescripcion() : "";
            }

            @Override
            public RepositoryType fromString(String string) {
                return null;
            }
        });
        actualizarTextoEstadoPersistencia();
    }

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
            Optional<Encargado> enc = RepositoryFactory.getEncargadoRepository().findById(idEnc);
            return new SimpleStringProperty(enc.map(e -> e.getNombreCompleto() + " (" + e.getParentesco() + ")").orElse("ID: " + idEnc));
        });
        tblEstudiantes.setItems(estudiantesObservable);
        tblEstudiantes.setPlaceholder(new Label("No hay estudiantes registrados."));

        // Tabla Docentes
        colDocenteId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDocenteCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoEmpleado"));
        colDocenteNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDocenteApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colDocenteEspecialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        colDocenteTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colDocenteEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tblDocentes.setItems(docentesObservable);
        tblDocentes.setPlaceholder(new Label("No hay docentes registrados."));

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
        tblEncargados.setPlaceholder(new Label("No hay encargados registrados."));
    }

    private void configurarComboBoxes() {
        // Parentescos comunes
        cbEncargadoParentesco.setItems(FXCollections.observableArrayList(
                "Padre", "Madre", "Tutor Legal", "Abuelo/a", "Tío/a", "Hermano/a Mayor", "Otro"
        ));
        cbEncargadoParentesco.setEditable(true);

        // StringConverter para ComboBox de Encargados
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
    // CARGA Y REFRESH DE DATOS
    // ==========================================

    private void cargarDatosGenerales() {
        cargarEncargados();
        cargarEstudiantes();
        cargarDocentes();
        actualizarEstadoGlobal("Datos actualizados correctamente.");
    }

    private void cargarEstudiantes() {
        try {
            List<Estudiante> lista = RepositoryFactory.getEstudianteRepository().findAll();
            estudiantesObservable.setAll(lista);
            lblEstudianteContador.setText("Total: " + lista.size() + " estudiantes");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Datos", "No se pudieron listar los estudiantes:\n" + e.getMessage());
        }
    }

    private void cargarDocentes() {
        try {
            List<Docente> lista = RepositoryFactory.getDocenteRepository().findAll();
            docentesObservable.setAll(lista);
            lblDocenteContador.setText("Total: " + lista.size() + " docentes");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Datos", "No se pudieron listar los docentes:\n" + e.getMessage());
        }
    }

    private void cargarEncargados() {
        try {
            List<Encargado> lista = RepositoryFactory.getEncargadoRepository().findAll();
            encargadosObservable.setAll(lista);
            lblEncargadoContador.setText("Total: " + lista.size() + " encargados");

            // Actualizar ComboBox de Estudiantes
            List<Encargado> comboList = new ArrayList<>();
            Encargado opcionVacia = new Encargado(0, "", "", "", "", "", "", "");
            comboList.add(opcionVacia);
            comboList.addAll(lista);
            cbEstudianteEncargado.setItems(FXCollections.observableArrayList(comboList));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Datos", "No se pudieron listar los encargados:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleCambioPersistencia(ActionEvent event) {
        RepositoryType seleccionado = cbPersistencia.getValue();
        if (seleccionado != null && seleccionado != RepositoryFactory.getRepositoryType()) {
            RepositoryFactory.setRepositoryType(seleccionado);
            actualizarTextoEstadoPersistencia();
            limpiarFormularioEstudiante();
            limpiarFormularioDocente();
            limpiarFormularioEncargado();
            cargarDatosGenerales();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Persistencia Cambiada",
                    "El modo activo ahora es: " + seleccionado.getDescripcion() + ".\nLas vistas se han sincronizado.");
        }
    }

    private void actualizarTextoEstadoPersistencia() {
        RepositoryType tipo = RepositoryFactory.getRepositoryType();
        lblPersistenciaActiva.setText("Persistencia Activa: " + tipo.getDescripcion());
    }

    private void actualizarEstadoGlobal(String mensaje) {
        lblEstadoSistema.setText(mensaje + " (Última acción: " + LocalDate.now() + ")");
    }

    // ==========================================
    // CRUD: ESTUDIANTES
    // ==========================================

    @FXML
    private void handleGuardarEstudiante(ActionEvent event) {
        if (!validarFormularioEstudiante()) return;

        try {
            Estudiante estudiante = construirEstudianteDesdeFormulario(0);
            RepositoryFactory.getEstudianteRepository().save(estudiante);

            cargarEstudiantes();
            limpiarFormularioEstudiante();
            actualizarEstadoGlobal("Estudiante guardado exitosamente.");
            mostrarAlerta(Alert.AlertType.INFORMATION, "Operación Exitosa", "El estudiante ha sido registrado correctamente.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Guardar", "Ocurrió un problema al guardar el estudiante:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleActualizarEstudiante(ActionEvent event) {
        String idStr = txtEstudianteId.getText();
        if (idStr == null || idStr.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Seleccione un estudiante de la tabla para actualizar.");
            return;
        }

        if (!validarFormularioEstudiante()) return;

        try {
            int id = Integer.parseInt(idStr.trim());
            Estudiante estudiante = construirEstudianteDesdeFormulario(id);
            RepositoryFactory.getEstudianteRepository().update(estudiante);

            cargarEstudiantes();
            limpiarFormularioEstudiante();
            actualizarEstadoGlobal("Estudiante ID " + id + " actualizado exitosamente.");
            mostrarAlerta(Alert.AlertType.INFORMATION, "Operación Exitosa", "Los datos del estudiante han sido actualizados.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Actualizar", "Ocurrió un problema al actualizar:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleEliminarEstudiante(ActionEvent event) {
        String idStr = txtEstudianteId.getText();
        if (idStr == null || idStr.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Seleccione el estudiante que desea eliminar de la tabla.");
            return;
        }

        int id = Integer.parseInt(idStr.trim());
        String nombre = txtEstudianteNombre.getText() + " " + txtEstudianteApellido.getText();

        if (confirmarAccion("Confirmar Eliminación", "¿Está seguro de eliminar al estudiante: " + nombre + "?")) {
            try {
                RepositoryFactory.getEstudianteRepository().delete(id);
                cargarEstudiantes();
                limpiarFormularioEstudiante();
                actualizarEstadoGlobal("Estudiante ID " + id + " eliminado.");
                mostrarAlerta(Alert.AlertType.INFORMATION, "Registro Eliminado", "El estudiante ha sido eliminado del sistema.");
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al Eliminar", "No se pudo eliminar el estudiante:\n" + e.getMessage());
            }
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
        try {
            List<Estudiante> resultados = RepositoryFactory.getEstudianteRepository().search(criterio);
            estudiantesObservable.setAll(resultados);
            lblEstudianteContador.setText("Resultados: " + resultados.size() + " estudiantes");
        } catch (Exception e) {
            System.err.println("Error filtrando estudiantes: " + e.getMessage());
        }
    }

    private void cargarEstudianteAlFormulario(Estudiante est) {
        txtEstudianteId.setText(String.valueOf(est.getId()));
        txtEstudianteCarnet.setText(est.getCarnet());
        txtEstudianteNombre.setText(est.getNombre());
        txtEstudianteApellido.setText(est.getApellido());
        txtEstudianteTelefono.setText(est.getTelefono() != null ? est.getTelefono() : "");
        txtEstudianteEmail.setText(est.getEmail() != null ? est.getEmail() : "");
        dpEstudianteFechaNac.setValue(est.getFechaNacimiento());

        // Seleccionar encargado correspondiente
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
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", "Debe seleccionar una fecha de nacimiento válida.");
            dpEstudianteFechaNac.requestFocus();
            return false;
        }
        if (dpEstudianteFechaNac.getValue().isAfter(LocalDate.now())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Fecha Inválida", "La fecha de nacimiento no puede ser futura.");
            dpEstudianteFechaNac.requestFocus();
            return false;
        }
        if (!esVacio(txtEstudianteEmail.getText()) && !EMAIL_PATTERN.matcher(txtEstudianteEmail.getText().trim()).matches()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Correo Inválido", "El formato del correo electrónico no es válido (ej. usuario@dominio.com).");
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
    // CRUD: DOCENTES
    // ==========================================

    @FXML
    private void handleGuardarDocente(ActionEvent event) {
        if (!validarFormularioDocente()) return;

        try {
            Docente docente = construirDocenteDesdeFormulario(0);
            RepositoryFactory.getDocenteRepository().save(docente);

            cargarDocentes();
            limpiarFormularioDocente();
            actualizarEstadoGlobal("Docente guardado exitosamente.");
            mostrarAlerta(Alert.AlertType.INFORMATION, "Operación Exitosa", "El docente ha sido registrado correctamente.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Guardar", "Ocurrió un problema al guardar el docente:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleActualizarDocente(ActionEvent event) {
        String idStr = txtDocenteId.getText();
        if (idStr == null || idStr.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Seleccione un docente de la tabla para actualizar.");
            return;
        }

        if (!validarFormularioDocente()) return;

        try {
            int id = Integer.parseInt(idStr.trim());
            Docente docente = construirDocenteDesdeFormulario(id);
            RepositoryFactory.getDocenteRepository().update(docente);

            cargarDocentes();
            limpiarFormularioDocente();
            actualizarEstadoGlobal("Docente ID " + id + " actualizado exitosamente.");
            mostrarAlerta(Alert.AlertType.INFORMATION, "Operación Exitosa", "Los datos del docente han sido actualizados.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Actualizar", "Ocurrió un problema al actualizar el docente:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleEliminarDocente(ActionEvent event) {
        String idStr = txtDocenteId.getText();
        if (idStr == null || idStr.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Seleccione el docente que desea eliminar de la tabla.");
            return;
        }

        int id = Integer.parseInt(idStr.trim());
        String nombre = txtDocenteNombre.getText() + " " + txtDocenteApellido.getText();

        if (confirmarAccion("Confirmar Eliminación", "¿Está seguro de eliminar al docente: " + nombre + "?")) {
            try {
                RepositoryFactory.getDocenteRepository().delete(id);
                cargarDocentes();
                limpiarFormularioDocente();
                actualizarEstadoGlobal("Docente ID " + id + " eliminado.");
                mostrarAlerta(Alert.AlertType.INFORMATION, "Registro Eliminado", "El docente ha sido eliminado del sistema.");
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al Eliminar", "No se pudo eliminar el docente:\n" + e.getMessage());
            }
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
        try {
            List<Docente> resultados = RepositoryFactory.getDocenteRepository().search(criterio);
            docentesObservable.setAll(resultados);
            lblDocenteContador.setText("Resultados: " + resultados.size() + " docentes");
        } catch (Exception e) {
            System.err.println("Error filtrando docentes: " + e.getMessage());
        }
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
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", "El código de empleado del docente es obligatorio.");
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
            mostrarAlerta(Alert.AlertType.WARNING, "Correo Inválido", "El formato del correo electrónico no es válido.");
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
    // CRUD: ENCARGADOS
    // ==========================================

    @FXML
    private void handleGuardarEncargado(ActionEvent event) {
        if (!validarFormularioEncargado()) return;

        try {
            Encargado encargado = construirEncargadoDesdeFormulario(0);
            RepositoryFactory.getEncargadoRepository().save(encargado);

            cargarEncargados();
            limpiarFormularioEncargado();
            actualizarEstadoGlobal("Encargado guardado exitosamente.");
            mostrarAlerta(Alert.AlertType.INFORMATION, "Operación Exitosa", "El encargado ha sido registrado correctamente.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Guardar", "Ocurrió un problema al guardar el encargado:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleActualizarEncargado(ActionEvent event) {
        String idStr = txtEncargadoId.getText();
        if (idStr == null || idStr.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Seleccione un encargado de la tabla para actualizar.");
            return;
        }

        if (!validarFormularioEncargado()) return;

        try {
            int id = Integer.parseInt(idStr.trim());
            Encargado encargado = construirEncargadoDesdeFormulario(id);
            RepositoryFactory.getEncargadoRepository().update(encargado);

            cargarEncargados();
            cargarEstudiantes(); // Refresca nombres de encargados en estudiantes si cambiaron
            limpiarFormularioEncargado();
            actualizarEstadoGlobal("Encargado ID " + id + " actualizado exitosamente.");
            mostrarAlerta(Alert.AlertType.INFORMATION, "Operación Exitosa", "Los datos del encargado han sido actualizados.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Actualizar", "Ocurrió un problema al actualizar el encargado:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleEliminarEncargado(ActionEvent event) {
        String idStr = txtEncargadoId.getText();
        if (idStr == null || idStr.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Seleccione el encargado que desea eliminar de la tabla.");
            return;
        }

        int id = Integer.parseInt(idStr.trim());
        String nombre = txtEncargadoNombre.getText() + " " + txtEncargadoApellido.getText();

        if (confirmarAccion("Confirmar Eliminación", "¿Está seguro de eliminar al encargado: " + nombre + "?")) {
            try {
                RepositoryFactory.getEncargadoRepository().delete(id);
                cargarEncargados();
                cargarEstudiantes(); // Refresca asignaciones de estudiantes
                limpiarFormularioEncargado();
                actualizarEstadoGlobal("Encargado ID " + id + " eliminado.");
                mostrarAlerta(Alert.AlertType.INFORMATION, "Registro Eliminado", "El encargado ha sido eliminado del sistema.");
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al Eliminar", "No se pudo eliminar el encargado:\n" + e.getMessage());
            }
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
        try {
            List<Encargado> resultados = RepositoryFactory.getEncargadoRepository().search(criterio);
            encargadosObservable.setAll(resultados);
            lblEncargadoContador.setText("Resultados: " + resultados.size() + " encargados");
        } catch (Exception e) {
            System.err.println("Error filtrando encargados: " + e.getMessage());
        }
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
            mostrarAlerta(Alert.AlertType.WARNING, "CUI Inválido", "El CUI debe tener 13 dígitos numéricos.");
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
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", "Debe indicar el parentesco del encargado.");
            cbEncargadoParentesco.requestFocus();
            return false;
        }
        if (esVacio(txtEncargadoDireccion.getText())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", "La dirección del encargado es obligatoria.");
            txtEncargadoDireccion.requestFocus();
            return false;
        }
        if (!esVacio(txtEncargadoEmail.getText()) && !EMAIL_PATTERN.matcher(txtEncargadoEmail.getText().trim()).matches()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Correo Inválido", "El formato del correo electrónico no es válido.");
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
    // GENERACIÓN DE REPORTES (REQUISITO 2 & 3)
    // ==========================================

    @FXML
    private void handleReporteEstudiante(ActionEvent event) {
        List<Estudiante> lista = estudiantesObservable;
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append("                  REPORTE OFICIAL DE ESTUDIANTES REGISTRADOS                           \n");
        sb.append("                       Sistema de Gestión Escolar - UMG                                \n");
        sb.append("========================================================================================\n");
        sb.append("Fecha de Emisión: ").append(LocalDate.now().format(DATE_FORMATTER)).append("\n");
        sb.append("Total de Estudiantes: ").append(lista.size()).append("\n");
        sb.append("Modo Persistencia: ").append(RepositoryFactory.getRepositoryType().getDescripcion()).append("\n");
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("%-5s | %-14s | %-24s | %-12s | %-20s\n", "ID", "CARNÉ", "NOMBRE COMPLETO", "TELÉFONO", "FECHA NAC."));
        sb.append("----------------------------------------------------------------------------------------\n");

        for (Estudiante e : lista) {
            String fecha = e.getFechaNacimiento() != null ? e.getFechaNacimiento().format(DATE_FORMATTER) : "N/A";
            sb.append(String.format("%-5d | %-14s | %-24s | %-12s | %-20s\n",
                    e.getId(),
                    truncar(e.getCarnet(), 14),
                    truncar(e.getNombreCompleto(), 24),
                    truncar(e.getTelefono(), 12),
                    fecha));
        }
        sb.append("========================================================================================\n");

        mostrarVentanaReporte("Reporte de Estudiantes", "Resumen Consolidado de Estudiantes", sb.toString());
    }

    @FXML
    private void handleReporteDocente(ActionEvent event) {
        List<Docente> lista = docentesObservable;
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append("                   REPORTE OFICIAL DE DOCENTES REGISTRADOS                             \n");
        sb.append("                       Sistema de Gestión Escolar - UMG                                \n");
        sb.append("========================================================================================\n");
        sb.append("Fecha de Emisión: ").append(LocalDate.now().format(DATE_FORMATTER)).append("\n");
        sb.append("Total de Docentes: ").append(lista.size()).append("\n");
        sb.append("Modo Persistencia: ").append(RepositoryFactory.getRepositoryType().getDescripcion()).append("\n");
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("%-5s | %-12s | %-24s | %-24s | %-12s\n", "ID", "CÓDIGO", "NOMBRE COMPLETO", "ESPECIALIDAD", "TELÉFONO"));
        sb.append("----------------------------------------------------------------------------------------\n");

        for (Docente d : lista) {
            sb.append(String.format("%-5d | %-12s | %-24s | %-24s | %-12s\n",
                    d.getId(),
                    truncar(d.getCodigoEmpleado(), 12),
                    truncar(d.getNombreCompleto(), 24),
                    truncar(d.getEspecialidad(), 24),
                    truncar(d.getTelefono(), 12)));
        }
        sb.append("========================================================================================\n");

        mostrarVentanaReporte("Reporte de Docentes", "Resumen Consolidado de Docentes", sb.toString());
    }

    @FXML
    private void handleReporteEncargado(ActionEvent event) {
        List<Encargado> lista = encargadosObservable;
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append("                   REPORTE OFICIAL DE ENCARGADOS REGISTRADOS                           \n");
        sb.append("                       Sistema de Gestión Escolar - UMG                                \n");
        sb.append("========================================================================================\n");
        sb.append("Fecha de Emisión: ").append(LocalDate.now().format(DATE_FORMATTER)).append("\n");
        sb.append("Total de Encargados: ").append(lista.size()).append("\n");
        sb.append("Modo Persistencia: ").append(RepositoryFactory.getRepositoryType().getDescripcion()).append("\n");
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("%-5s | %-16s | %-22s | %-12s | %-20s\n", "ID", "CUI (DPI)", "NOMBRE COMPLETO", "PARENTESCO", "DIRECCIÓN"));
        sb.append("----------------------------------------------------------------------------------------\n");

        for (Encargado enc : lista) {
            sb.append(String.format("%-5d | %-16s | %-22s | %-12s | %-20s\n",
                    enc.getId(),
                    truncar(enc.getCui(), 16),
                    truncar(enc.getNombreCompleto(), 22),
                    truncar(enc.getParentesco(), 12),
                    truncar(enc.getDireccion(), 20)));
        }
        sb.append("========================================================================================\n");

        mostrarVentanaReporte("Reporte de Encargados", "Resumen Consolidado de Encargados", sb.toString());
    }

    private void mostrarVentanaReporte(String titulo, String encabezado, String contenido) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(encabezado);

        TextArea txtReporte = new TextArea(contenido);
        txtReporte.setEditable(false);
        txtReporte.setStyle("-fx-font-family: 'Courier New', Monospaced; -fx-font-size: 12px; -fx-background-color: #f8fafc;");
        txtReporte.setPrefSize(720, 420);

        Button btnCopiar = new Button("📋 Copiar Reporte");
        btnCopiar.getStyleClass().addAll("btn", "btn-reporte");
        btnCopiar.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(contenido);
            clipboard.setContent(content);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Copiado", "El reporte ha sido copiado al portapapeles.");
        });

        HBox buttonBar = new HBox(10, btnCopiar);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        VBox layout = new VBox(10, txtReporte, buttonBar);
        layout.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(layout);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }

    // ==========================================
    // MÉTODOS DE UTILIDAD
    // ==========================================

    private boolean esVacio(String str) {
        return str == null || str.trim().isEmpty();
    }

    private String truncar(String str, int maxLen) {
        if (str == null) return "";
        if (str.length() <= maxLen) return str;
        return str.substring(0, maxLen - 3) + "...";
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
