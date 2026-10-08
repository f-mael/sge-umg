package gt.edu.umg.gestionescolar.controller;

import gt.edu.umg.gestionescolar.model.*;
import gt.edu.umg.gestionescolar.repository.BoletaRepository;
import gt.edu.umg.gestionescolar.repository.DatosCompartidos;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class BoletaController {

    private final BoletaRepository repository =
        DatosCompartidos.getBoletas();

    private final ObservableList<DetalleBoleta> detalles =
        FXCollections.observableArrayList();

    private int idSeleccionado = 0;
    private int siguienteIdDetalle = 1;

    @FXML private Label lblId;
    @FXML private Label lblPromedio;
    @FXML private Label lblTotal;

    @FXML private ComboBox<Estudiante> cbEstudiante;
    @FXML private ComboBox<CicloEscolar> cbCiclo;
    @FXML private ComboBox<Curso> cbCurso;

    @FXML private DatePicker dpFecha;

    @FXML private TextField txtPeriodo;
    @FXML private TextField txtNota;
    @FXML private TextField txtPeso;
    @FXML private TextField txtObservacion;
    @FXML private TextField txtBuscar;

    @FXML private TableView<DetalleBoleta> tblDetalles;
    @FXML private TableColumn<DetalleBoleta, String> colDetalleCurso;
    @FXML private TableColumn<DetalleBoleta, String> colDetalleNota;
    @FXML private TableColumn<DetalleBoleta, String> colDetallePeso;
    @FXML private TableColumn<DetalleBoleta, String> colDetalleObservacion;

    @FXML private TableView<BoletaCalificaciones> tblBoletas;
    @FXML private TableColumn<BoletaCalificaciones, String> colBoletaId;
    @FXML private TableColumn<BoletaCalificaciones, String> colBoletaEstudiante;
    @FXML private TableColumn<BoletaCalificaciones, String> colBoletaCiclo;
    @FXML private TableColumn<BoletaCalificaciones, String> colBoletaPeriodo;
    @FXML private TableColumn<BoletaCalificaciones, String> colBoletaFecha;
    @FXML private TableColumn<BoletaCalificaciones, String> colBoletaPromedio;

    @FXML
    private void initialize() {
        configurarSelector(cbEstudiante,
            e -> e.getCarnet() + " - " + e.getNombreCompleto());

        configurarSelector(cbCurso,
            c -> c.getIdCurso() + " - " + c.getNombre());

        configurarSelector(cbCiclo,
            c -> c.getAnio() + (c.isActivo() ? " (Activo)" : ""));

        configurarColumna(colDetalleCurso,
            d -> d.getCurso().getNombre());
        configurarColumna(colDetalleNota,
            d -> formato(d.getNota()));
        configurarColumna(colDetallePeso,
            d -> formato(d.getPeso()));
        configurarColumna(colDetalleObservacion,
            DetalleBoleta::getObservacion);

        configurarColumna(colBoletaId,
            b -> String.valueOf(b.getIdBoleta()));
        configurarColumna(colBoletaEstudiante,
            b -> b.getEstudiante().getNombreCompleto());
        configurarColumna(colBoletaCiclo,
            b -> String.valueOf(b.getCicloEscolar().getAnio()));
        configurarColumna(colBoletaPeriodo,
            BoletaCalificaciones::getPeriodo);
        configurarColumna(colBoletaFecha,
            b -> b.getFechaEmision().toString());
        configurarColumna(colBoletaPromedio,
            b -> formato(b.getPromedio()));

        tblDetalles.setItems(detalles);
        tblDetalles.setPlaceholder(
            new Label("Agregue los cursos y sus notas.")
        );
        tblBoletas.setPlaceholder(
            new Label("No se encontraron boletas.")
        );

        tblDetalles.getSelectionModel()
            .selectedItemProperty().addListener(
                (observable, anterior, actual) -> {
                    if (actual != null) {
                        cbCurso.setValue(actual.getCurso());
                        txtNota.setText(
                            String.valueOf(actual.getNota())
                        );
                        txtPeso.setText(
                            String.valueOf(actual.getPeso())
                        );
                        txtObservacion.setText(
                            actual.getObservacion()
                        );
                    }
                }
            );

        tblBoletas.getSelectionModel()
            .selectedItemProperty().addListener(
                (observable, anterior, actual) -> {
                    if (actual != null) {
                        seleccionarBoleta(actual);
                    }
                }
            );

        txtBuscar.textProperty().addListener(
            (observable, anterior, actual) -> cargarTabla()
        );

        cbEstudiante.setOnShowing(event -> cargarEstudiantes());

        actualizarListas();
        limpiar();
    }

    private <T> void configurarSelector(
            ComboBox<T> selector, Function<T, String> texto) {
        selector.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T objeto) {
                return objeto == null ? "" : texto.apply(objeto);
            }

            @Override
            public T fromString(String valor) {
                return null;
            }
        });
    }

    private <T> void configurarColumna(
            TableColumn<T, String> columna,
            Function<T, String> texto) {
        columna.setCellValueFactory(c ->
            new SimpleStringProperty(texto.apply(c.getValue()))
        );
    }

    private String formato(double valor) {
        return String.format(Locale.ROOT, "%.2f", valor);
    }

    // Estos datos serán proporcionados por el módulo académico.
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

    public void setCiclos(List<CicloEscolar> ciclos) {
        CicloEscolar seleccionado = cbCiclo.getValue();
        cbCiclo.setItems(FXCollections.observableArrayList(ciclos));

        if (seleccionado != null) {
            cbCiclo.setValue(
                ciclos.stream()
                    .filter(c -> c.getIdCiclo()
                        == seleccionado.getIdCiclo())
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
        List<BoletaCalificaciones> resultados =
            repository.search(txtBuscar.getText());

        tblBoletas.setItems(
            FXCollections.observableArrayList(resultados)
        );

        lblTotal.setText(
            "Mostrando: " + resultados.size()
            + " de " + repository.findAll().size() + " boletas"
        );
    }

    private DetalleBoleta copiarDetalle(DetalleBoleta original) {
        return new DetalleBoleta(
            original.getIdDetalle(),
            original.getCurso(),
            original.getNota(),
            original.getObservacion(),
            original.getPeso()
        );
    }

    private void seleccionarBoleta(BoletaCalificaciones boleta) {
        idSeleccionado = boleta.getIdBoleta();
        lblId.setText("Boleta: " + idSeleccionado);

        cbEstudiante.setValue(boleta.getEstudiante());
        cbCiclo.setValue(boleta.getCicloEscolar());
        txtPeriodo.setText(boleta.getPeriodo());
        dpFecha.setValue(boleta.getFechaEmision());

        limpiarDetalle();
        detalles.clear();
        siguienteIdDetalle = 1;

        for (DetalleBoleta detalle : boleta.getDetalles()) {
            detalles.add(copiarDetalle(detalle));
            siguienteIdDetalle = Math.max(
                siguienteIdDetalle, detalle.getIdDetalle() + 1
            );
        }

        actualizarPromedio();
    }

    private double leerNumero(String texto, String campo) {
        try {
            double valor = Double.parseDouble(
                texto.trim().replace(',', '.')
            );

            if (!Double.isFinite(valor)) {
                throw new NumberFormatException();
            }

            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                campo + " debe ser un número válido."
            );
        }
    }

    private DetalleBoleta leerDetalle(int id) {
        Curso curso = cbCurso.getValue();

        if (curso == null) {
            throw new IllegalArgumentException(
                "Seleccione un curso."
            );
        }

        return new DetalleBoleta(
            id,
            curso,
            leerNumero(txtNota.getText(), "La nota"),
            txtObservacion.getText().trim(),
            leerNumero(txtPeso.getText(), "El peso")
        );
    }

    private void validarCursoRepetido(
            DetalleBoleta nuevo, DetalleBoleta excluido) {
        for (DetalleBoleta detalle : detalles) {
            if (detalle != excluido
                    && detalle.getCurso().getIdCurso()
                        == nuevo.getCurso().getIdCurso()) {
                throw new IllegalArgumentException(
                    "Ese curso ya está incluido en la boleta."
                );
            }
        }
    }

    @FXML
    private void agregarDetalle() {
        try {
            DetalleBoleta nuevo = leerDetalle(siguienteIdDetalle);
            validarCursoRepetido(nuevo, null);

            detalles.add(nuevo);
            siguienteIdDetalle++;

            limpiarDetalle();
            actualizarPromedio();
        } catch (IllegalArgumentException e) {
            mostrarMensaje(Alert.AlertType.WARNING, e.getMessage());
        }
    }

    @FXML
    private void actualizarDetalle() {
        DetalleBoleta seleccionado =
            tblDetalles.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarMensaje(
                Alert.AlertType.WARNING,
                "Seleccione un curso de la tabla de detalles."
            );
            return;
        }

        try {
            DetalleBoleta nuevo =
                leerDetalle(seleccionado.getIdDetalle());

            validarCursoRepetido(nuevo, seleccionado);

            int posicion = detalles.indexOf(seleccionado);
            detalles.set(posicion, nuevo);

            limpiarDetalle();
            actualizarPromedio();
        } catch (IllegalArgumentException e) {
            mostrarMensaje(Alert.AlertType.WARNING, e.getMessage());
        }
    }

    @FXML
    private void eliminarDetalle() {
        DetalleBoleta seleccionado =
            tblDetalles.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarMensaje(
                Alert.AlertType.WARNING,
                "Seleccione el curso que desea quitar."
            );
            return;
        }

        detalles.remove(seleccionado);
        limpiarDetalle();
        actualizarPromedio();
    }

    @FXML
    private void limpiarDetalle() {
        tblDetalles.getSelectionModel().clearSelection();
        cbCurso.getSelectionModel().clearSelection();
        txtNota.clear();
        txtPeso.setText("1");
        txtObservacion.clear();
    }

    private void actualizarPromedio() {
        BoletaCalificaciones temporal = new BoletaCalificaciones();
        temporal.setDetalles(detalles);

        lblPromedio.setText(
            "Promedio ponderado: " + formato(temporal.getPromedio())
        );
    }

    private BoletaCalificaciones leerBoleta(int id) {
        Estudiante estudiante = cbEstudiante.getValue();

        if (estudiante == null || cbCiclo.getValue() == null
                || dpFecha.getValue() == null) {
            throw new IllegalArgumentException(
                "Seleccione estudiante, ciclo escolar y fecha."
            );
        }

        estudiante = DatosCompartidos.getEstudiantes()
            .findById(estudiante.getId())
            .orElseThrow(() -> new IllegalArgumentException(
                "El estudiante ya no está registrado. "
                + "Actualice las listas."
            ));

        BoletaCalificaciones boleta = new BoletaCalificaciones(
            id,
            estudiante,
            cbCiclo.getValue(),
            txtPeriodo.getText().trim(),
            dpFecha.getValue()
        );

        for (DetalleBoleta detalle : detalles) {
            boleta.agregarDetalle(copiarDetalle(detalle));
        }

        return boleta;
    }

    @FXML
    private void guardar() {
        if (idSeleccionado != 0) {
            mostrarMensaje(
                Alert.AlertType.WARNING,
                "Está editando una boleta. Use Actualizar boleta "
                + "o Nueva / Limpiar para crear otra."
            );
            return;
        }

        try {
            repository.save(leerBoleta(0));
            limpiar();
            cargarTabla();

            mostrarMensaje(
                Alert.AlertType.INFORMATION,
                "Boleta guardada correctamente."
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
            repository.update(leerBoleta(idSeleccionado));
            limpiar();
            cargarTabla();

            mostrarMensaje(
                Alert.AlertType.INFORMATION,
                "Boleta actualizada correctamente."
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
            "¿Desea eliminar la boleta y sus detalles?",
            ButtonType.YES,
            ButtonType.NO
        );

        confirmacion.setTitle("Eliminar boleta");
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
                "Seleccione una boleta de la tabla."
            );
            return false;
        }

        return true;
    }

    @FXML
    private void limpiar() {
        idSeleccionado = 0;
        siguienteIdDetalle = 1;
        lblId.setText("Nueva boleta");

        tblBoletas.getSelectionModel().clearSelection();
        cbEstudiante.getSelectionModel().clearSelection();
        cbCiclo.getSelectionModel().clearSelection();
        txtPeriodo.clear();
        dpFecha.setValue(LocalDate.now());

        limpiarDetalle();
        detalles.clear();
        actualizarPromedio();
    }

    @FXML
    private void limpiarBusqueda() {
        txtBuscar.clear();
    }

    private void mostrarMensaje(Alert.AlertType tipo, String texto) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Boletas de calificaciones");
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }
}