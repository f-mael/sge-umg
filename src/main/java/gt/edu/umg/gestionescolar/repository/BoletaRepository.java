package gt.edu.umg.gestionescolar.repository;

import gt.edu.umg.gestionescolar.model.BoletaCalificaciones;
import gt.edu.umg.gestionescolar.model.CicloEscolar;
import gt.edu.umg.gestionescolar.model.Curso;
import gt.edu.umg.gestionescolar.model.DetalleBoleta;
import gt.edu.umg.gestionescolar.model.Estudiante;
import gt.edu.umg.gestionescolar.util.DatabaseManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public class BoletaRepository
        implements Repository<BoletaCalificaciones> {

    private final EstudianteRepository estudianteRepository;
    private final CicloEscolarRepository cicloRepository;
    private final CursoRepository cursoRepository;

    public BoletaRepository() {
        estudianteRepository = new EstudianteRepository();
        cicloRepository = new CicloEscolarRepository();
        cursoRepository = new CursoRepository();
    }

    @Override
    public void save(BoletaCalificaciones boleta) {
        validar(boleta);

        if (boleta.getIdBoleta() != 0) {
            throw new IllegalArgumentException(
                "Una boleta nueva debe tener ID 0."
            );
        }

        validarDuplicado(boleta, 0);
        boleta.calcularPromedio();

        String sqlBoleta = """
            INSERT INTO boletas_calificaciones
                (id_estudiante, id_ciclo, periodo,
                 fecha_emision, promedio)
            VALUES (?, ?, ?, ?, ?)
            """;

        Connection conn = null;

        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(
                    sqlBoleta, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setInt(
                    1, boleta.getEstudiante().getId()
                );
                stmt.setInt(
                    2, boleta.getCicloEscolar().getIdCiclo()
                );
                stmt.setString(
                    3, boleta.getPeriodo().trim()
                );
                stmt.setDate(
                    4, Date.valueOf(boleta.getFechaEmision())
                );
                stmt.setDouble(
                    5, boleta.getPromedio()
                );

                stmt.executeUpdate();

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException(
                            "No se pudo obtener el ID de la boleta."
                        );
                    }

                    boleta.setIdBoleta(keys.getInt(1));
                }
            }

            guardarDetalles(
                conn,
                boleta.getIdBoleta(),
                boleta.getDetalles()
            );

            conn.commit();

        } catch (SQLException e) {
            realizarRollback(conn);
            boleta.setIdBoleta(0);

            throw new IllegalStateException(
                "No se pudo guardar la boleta: "
                    + e.getMessage(),
                e
            );

        } finally {
            cerrarConexion(conn);
        }
    }

    @Override
    public void update(BoletaCalificaciones boleta) {
        validar(boleta);

        if (boleta.getIdBoleta() <= 0) {
            throw new IllegalArgumentException(
                "Debe seleccionar una boleta registrada."
            );
        }

        if (findById(boleta.getIdBoleta()).isEmpty()) {
            throw new IllegalArgumentException(
                "No se encontró la boleta que desea actualizar."
            );
        }

        validarDuplicado(
            boleta,
            boleta.getIdBoleta()
        );

        boleta.calcularPromedio();

        String sqlActualizar = """
            UPDATE boletas_calificaciones
            SET id_estudiante = ?,
                id_ciclo = ?,
                periodo = ?,
                fecha_emision = ?,
                promedio = ?
            WHERE id_boleta = ?
            """;

        String sqlEliminarDetalles = """
            DELETE FROM detalles_boletas
            WHERE id_boleta = ?
            """;

        Connection conn = null;

        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt =
                    conn.prepareStatement(sqlActualizar)) {

                stmt.setInt(
                    1, boleta.getEstudiante().getId()
                );
                stmt.setInt(
                    2, boleta.getCicloEscolar().getIdCiclo()
                );
                stmt.setString(
                    3, boleta.getPeriodo().trim()
                );
                stmt.setDate(
                    4, Date.valueOf(boleta.getFechaEmision())
                );
                stmt.setDouble(
                    5, boleta.getPromedio()
                );
                stmt.setInt(
                    6, boleta.getIdBoleta()
                );

                int filas = stmt.executeUpdate();

                if (filas == 0) {
                    throw new SQLException(
                        "La boleta ya no existe."
                    );
                }
            }

            try (PreparedStatement stmt =
                    conn.prepareStatement(sqlEliminarDetalles)) {

                stmt.setInt(
                    1, boleta.getIdBoleta()
                );
                stmt.executeUpdate();
            }

            guardarDetalles(
                conn,
                boleta.getIdBoleta(),
                boleta.getDetalles()
            );

            conn.commit();

        } catch (SQLException e) {
            realizarRollback(conn);

            throw new IllegalStateException(
                "No se pudo actualizar la boleta: "
                    + e.getMessage(),
                e
            );

        } finally {
            cerrarConexion(conn);
        }
    }

    @Override
    public void delete(int id) {
        String sql = """
            DELETE FROM boletas_calificaciones
            WHERE id_boleta = ?
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(
                "No se pudo eliminar la boleta: "
                    + e.getMessage(),
                e
            );
        }
    }

    @Override
    public Optional<BoletaCalificaciones> findById(int id) {
        String sql = """
            SELECT id_boleta,
                   id_estudiante,
                   id_ciclo,
                   periodo,
                   fecha_emision,
                   promedio
            FROM boletas_calificaciones
            WHERE id_boleta = ?
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(
                        construirBoleta(rs)
                    );
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new IllegalStateException(
                "No se pudo consultar la boleta: "
                    + e.getMessage(),
                e
            );
        }
    }

    @Override
    public List<BoletaCalificaciones> findAll() {
        String sql = """
            SELECT id_boleta,
                   id_estudiante,
                   id_ciclo,
                   periodo,
                   fecha_emision,
                   promedio
            FROM boletas_calificaciones
            ORDER BY id_boleta
            """;

        List<BoletaCalificaciones> boletas =
            new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                boletas.add(
                    construirBoleta(rs)
                );
            }

            return boletas;

        } catch (SQLException e) {
            throw new IllegalStateException(
                "No se pudieron consultar las boletas: "
                    + e.getMessage(),
                e
            );
        }
    }

    @Override
    public List<BoletaCalificaciones> search(String criterio) {
        String texto = criterio == null
            ? ""
            : criterio.trim().toLowerCase(Locale.ROOT);

        if (texto.isEmpty()) {
            return findAll();
        }

        List<BoletaCalificaciones> resultados =
            new ArrayList<>();

        for (BoletaCalificaciones boleta : findAll()) {
            StringBuilder datos = new StringBuilder();

            datos.append(boleta.getIdBoleta()).append(" ")
                .append(
                    boleta.getEstudiante()
                        .getNombreCompleto()
                )
                .append(" ")
                .append(
                    boleta.getEstudiante().getCarnet()
                )
                .append(" ")
                .append(
                    boleta.getCicloEscolar().getAnio()
                )
                .append(" ")
                .append(boleta.getPeriodo())
                .append(" ")
                .append(boleta.getFechaEmision());

            for (DetalleBoleta detalle
                    : boleta.getDetalles()) {

                datos.append(" ")
                    .append(
                        detalle.getCurso().getNombre()
                    );
            }

            if (datos.toString()
                    .toLowerCase(Locale.ROOT)
                    .contains(texto)) {

                resultados.add(boleta);
            }
        }

        return resultados;
    }

    private BoletaCalificaciones construirBoleta(
            ResultSet rs) throws SQLException {

        int idBoleta = rs.getInt("id_boleta");
        int idEstudiante = rs.getInt("id_estudiante");
        int idCiclo = rs.getInt("id_ciclo");

        Estudiante estudiante = estudianteRepository
            .findById(idEstudiante)
            .orElseThrow(() -> new IllegalStateException(
                "No se encontró el estudiante con ID "
                    + idEstudiante + "."
            ));

        CicloEscolar ciclo = cicloRepository
            .findById(idCiclo)
            .orElseThrow(() -> new IllegalStateException(
                "No se encontró el ciclo con ID "
                    + idCiclo + "."
            ));

        BoletaCalificaciones boleta =
            new BoletaCalificaciones();

        boleta.setIdBoleta(idBoleta);
        boleta.setEstudiante(estudiante);
        boleta.setCicloEscolar(ciclo);
        boleta.setPeriodo(
            rs.getString("periodo")
        );
        boleta.setFechaEmision(
            rs.getDate("fecha_emision").toLocalDate()
        );

        boleta.setDetalles(
            cargarDetalles(idBoleta)
        );

        return boleta;
    }

    private List<DetalleBoleta> cargarDetalles(
            int idBoleta) {

        String sql = """
            SELECT id_detalle,
                   id_curso,
                   nota,
                   observacion,
                   peso
            FROM detalles_boletas
            WHERE id_boleta = ?
            ORDER BY id_detalle
            """;

        List<DetalleBoleta> detalles =
            new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idBoleta);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idCurso =
                        rs.getInt("id_curso");

                    Curso curso = cursoRepository
                        .findById(idCurso)
                        .orElseThrow(() ->
                            new IllegalStateException(
                                "No se encontró el curso "
                                    + "con ID "
                                    + idCurso + "."
                            )
                        );

                    DetalleBoleta detalle =
                        new DetalleBoleta();

                    detalle.setIdDetalle(
                        rs.getInt("id_detalle")
                    );
                    detalle.setCurso(curso);
                    detalle.setNota(
                        rs.getDouble("nota")
                    );
                    detalle.setObservacion(
                        rs.getString("observacion")
                    );
                    detalle.setPeso(
                        rs.getDouble("peso")
                    );

                    detalles.add(detalle);
                }
            }

            return detalles;

        } catch (SQLException e) {
            throw new IllegalStateException(
                "No se pudieron consultar los detalles: "
                    + e.getMessage(),
                e
            );
        }
    }

    private void guardarDetalles(
            Connection conn,
            int idBoleta,
            List<DetalleBoleta> detalles)
            throws SQLException {

        String sql = """
            INSERT INTO detalles_boletas
                (id_boleta, id_curso, nota,
                 observacion, peso)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            for (DetalleBoleta detalle : detalles) {
                stmt.setInt(1, idBoleta);
                stmt.setInt(
                    2, detalle.getCurso().getIdCurso()
                );
                stmt.setDouble(
                    3, detalle.getNota()
                );
                stmt.setString(
                    4, detalle.getObservacion()
                );
                stmt.setDouble(
                    5, detalle.getPeso()
                );

                stmt.executeUpdate();

                try (ResultSet keys =
                        stmt.getGeneratedKeys()) {

                    if (keys.next()) {
                        detalle.setIdDetalle(
                            keys.getInt(1)
                        );
                    }
                }
            }
        }
    }

    private void validarDuplicado(
            BoletaCalificaciones boleta,
            int idExcluido) {

        String sql = """
            SELECT COUNT(*)
            FROM boletas_calificaciones
            WHERE id_estudiante = ?
              AND id_ciclo = ?
              AND LOWER(TRIM(periodo))
                    = LOWER(TRIM(?))
              AND id_boleta <> ?
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(
                1, boleta.getEstudiante().getId()
            );
            stmt.setInt(
                2, boleta.getCicloEscolar().getIdCiclo()
            );
            stmt.setString(
                3, boleta.getPeriodo()
            );
            stmt.setInt(
                4, idExcluido
            );

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new IllegalArgumentException(
                        "Ya existe una boleta para ese "
                            + "estudiante, ciclo y período."
                    );
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                "No se pudo validar la boleta: "
                    + e.getMessage(),
                e
            );
        }
    }

    private void validar(BoletaCalificaciones boleta) {
        if (boleta == null) {
            throw new IllegalArgumentException(
                "La boleta es obligatoria."
            );
        }

        if (boleta.getEstudiante() == null
                || boleta.getCicloEscolar() == null
                || boleta.getFechaEmision() == null) {

            throw new IllegalArgumentException(
                "Debe seleccionar estudiante, "
                    + "ciclo escolar y fecha."
            );
        }

        if (boleta.getEstudiante().getId() <= 0
                || boleta.getCicloEscolar()
                    .getIdCiclo() <= 0) {

            throw new IllegalArgumentException(
                "El estudiante y el ciclo deben "
                    + "estar registrados."
            );
        }

        if (boleta.getPeriodo() == null
                || boleta.getPeriodo().isBlank()) {

            throw new IllegalArgumentException(
                "Debe indicar el período de la boleta."
            );
        }

        List<DetalleBoleta> detalles =
            boleta.getDetalles();

        if (detalles.isEmpty()) {
            throw new IllegalArgumentException(
                "La boleta debe contener al menos "
                    + "un curso con nota."
            );
        }

        Set<Integer> cursos = new HashSet<>();

        for (DetalleBoleta detalle : detalles) {
            if (detalle == null
                    || detalle.getCurso() == null
                    || detalle.getCurso()
                        .getIdCurso() <= 0) {

                throw new IllegalArgumentException(
                    "Cada detalle debe tener "
                        + "un curso registrado."
                );
            }

            if (!cursos.add(
                    detalle.getCurso().getIdCurso())) {

                throw new IllegalArgumentException(
                    "No puede repetir un curso "
                        + "dentro de la boleta."
                );
            }

            if (!Double.isFinite(detalle.getNota())
                    || detalle.getNota() < 0
                    || detalle.getNota() > 100) {

                throw new IllegalArgumentException(
                    "Las notas deben estar "
                        + "entre 0 y 100."
                );
            }

            if (!Double.isFinite(detalle.getPeso())
                    || detalle.getPeso() <= 0) {

                throw new IllegalArgumentException(
                    "Los pesos deben ser "
                        + "mayores que cero."
                );
            }
        }
    }

    private void realizarRollback(Connection conn) {
        if (conn == null) {
            return;
        }

        try {
            conn.rollback();
        } catch (SQLException e) {
            System.err.println(
                "No se pudo revertir la transacción: "
                    + e.getMessage()
            );
        }
    }

    private void cerrarConexion(Connection conn) {
        if (conn == null) {
            return;
        }

        try {
            conn.setAutoCommit(true);
            conn.close();
        } catch (SQLException e) {
            System.err.println(
                "No se pudo cerrar la conexión: "
                    + e.getMessage()
            );
        }
    }
}