package gt.edu.umg.gestionescolar.repository;

import gt.edu.umg.gestionescolar.model.Asistencia;
import gt.edu.umg.gestionescolar.model.Curso;
import gt.edu.umg.gestionescolar.model.Estudiante;
import gt.edu.umg.gestionescolar.util.DatabaseManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AsistenciaRepository implements Repository<Asistencia> {

    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;

    public AsistenciaRepository() {
        this.estudianteRepository = new EstudianteRepository();
        this.cursoRepository = new CursoRepository();
    }

    @Override
    public void save(Asistencia asistencia) {
        validar(asistencia);

        if (asistencia.getIdAsistencia() != 0) {
            throw new IllegalArgumentException(
                    "Una asistencia nueva debe tener ID 0."
            );
        }

        validarDuplicado(asistencia, 0);

        String sql = """
                INSERT INTO asistencias (
                    id_estudiante,
                    id_curso,
                    fecha,
                    estado
                )
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            ps.setInt(
                    1,
                    asistencia.getEstudiante().getId()
            );
            ps.setInt(
                    2,
                    asistencia.getCurso().getIdCurso()
            );
            ps.setDate(
                    3,
                    Date.valueOf(asistencia.getFecha())
            );
            ps.setString(
                    4,
                    asistencia.getEstado()
            );

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    asistencia.setIdAsistencia(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw errorBaseDatos(
                    "No se pudo guardar la asistencia.",
                    e
            );
        }
    }

    @Override
    public void update(Asistencia asistencia) {
        validar(asistencia);

        if (asistencia.getIdAsistencia() <= 0) {
            throw new IllegalArgumentException(
                    "La asistencia debe tener un ID válido."
            );
        }

        validarDuplicado(
                asistencia,
                asistencia.getIdAsistencia()
        );

        String sql = """
                UPDATE asistencias
                SET id_estudiante = ?,
                    id_curso = ?,
                    fecha = ?,
                    estado = ?
                WHERE id_asistencia = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    asistencia.getEstudiante().getId()
            );
            ps.setInt(
                    2,
                    asistencia.getCurso().getIdCurso()
            );
            ps.setDate(
                    3,
                    Date.valueOf(asistencia.getFecha())
            );
            ps.setString(
                    4,
                    asistencia.getEstado()
            );
            ps.setInt(
                    5,
                    asistencia.getIdAsistencia()
            );

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new IllegalArgumentException(
                        "No se encontró la asistencia "
                        + "que desea actualizar."
                );
            }

        } catch (SQLException e) {
            throw errorBaseDatos(
                    "No se pudo actualizar la asistencia.",
                    e
            );
        }
    }

    @Override
    public void delete(int id) {
        String sql = """
                DELETE FROM asistencias
                WHERE id_asistencia = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw errorBaseDatos(
                    "No se pudo eliminar la asistencia.",
                    e
            );
        }
    }

    @Override
    public Optional<Asistencia> findById(int id) {
        String sql = """
                SELECT id_asistencia,
                       id_estudiante,
                       id_curso,
                       fecha,
                       estado
                FROM asistencias
                WHERE id_asistencia = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }

        } catch (SQLException e) {
            throw errorBaseDatos(
                    "No se pudo consultar la asistencia.",
                    e
            );
        }

        return Optional.empty();
    }

    @Override
    public List<Asistencia> findAll() {
        String sql = """
                SELECT id_asistencia,
                       id_estudiante,
                       id_curso,
                       fecha,
                       estado
                FROM asistencias
                ORDER BY fecha DESC, id_asistencia DESC
                """;

        List<Asistencia> asistencias = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                asistencias.add(mapear(rs));
            }

        } catch (SQLException e) {
            throw errorBaseDatos(
                    "No se pudieron consultar las asistencias.",
                    e
            );
        }

        return asistencias;
    }

    @Override
    public List<Asistencia> search(String criterio) {
        if (criterio == null || criterio.isBlank()) {
            return findAll();
        }

        String sql = """
                SELECT a.id_asistencia,
                       a.id_estudiante,
                       a.id_curso,
                       a.fecha,
                       a.estado
                FROM asistencias a
                INNER JOIN estudiantes e
                    ON e.id = a.id_estudiante
                INNER JOIN cursos c
                    ON c.id_curso = a.id_curso
                WHERE CAST(a.id_asistencia AS TEXT) ILIKE ?
                   OR e.nombre ILIKE ?
                   OR e.apellido ILIKE ?
                   OR e.carnet ILIKE ?
                   OR c.nombre ILIKE ?
                   OR CAST(a.fecha AS TEXT) ILIKE ?
                   OR a.estado ILIKE ?
                ORDER BY a.fecha DESC, a.id_asistencia DESC
                """;

        String filtro = "%" + criterio.trim() + "%";
        List<Asistencia> resultados = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 1; i <= 7; i++) {
                ps.setString(i, filtro);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapear(rs));
                }
            }

        } catch (SQLException e) {
            throw errorBaseDatos(
                    "No se pudo realizar la búsqueda de asistencias.",
                    e
            );
        }

        return resultados;
    }

    private Asistencia mapear(ResultSet rs) throws SQLException {
        int idEstudiante = rs.getInt("id_estudiante");
        int idCurso = rs.getInt("id_curso");

        Estudiante estudiante = estudianteRepository
                .findById(idEstudiante)
                .orElseThrow(() -> new SQLException(
                        "No existe el estudiante relacionado: "
                        + idEstudiante
                ));

        Curso curso = cursoRepository
                .findById(idCurso)
                .orElseThrow(() -> new SQLException(
                        "No existe el curso relacionado: "
                        + idCurso
                ));

        return new Asistencia(
                rs.getInt("id_asistencia"),
                estudiante,
                curso,
                rs.getDate("fecha").toLocalDate(),
                rs.getString("estado")
        );
    }

    private void validarDuplicado(
            Asistencia asistencia,
            int idExcluido
    ) {
        String sql = """
                SELECT COUNT(*)
                FROM asistencias
                WHERE id_estudiante = ?
                  AND id_curso = ?
                  AND fecha = ?
                  AND id_asistencia <> ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    asistencia.getEstudiante().getId()
            );
            ps.setInt(
                    2,
                    asistencia.getCurso().getIdCurso()
            );
            ps.setDate(
                    3,
                    Date.valueOf(asistencia.getFecha())
            );
            ps.setInt(4, idExcluido);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new IllegalArgumentException(
                            "Ya existe una asistencia para ese "
                            + "estudiante, curso y fecha."
                    );
                }
            }

        } catch (SQLException e) {
            throw errorBaseDatos(
                    "No se pudo validar la asistencia.",
                    e
            );
        }
    }

    private void validar(Asistencia asistencia) {
        if (asistencia == null) {
            throw new IllegalArgumentException(
                    "La asistencia es obligatoria."
            );
        }

        if (asistencia.getEstudiante() == null
                || asistencia.getCurso() == null
                || asistencia.getFecha() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar estudiante, curso y fecha."
            );
        }

        if (asistencia.getEstudiante().getId() <= 0
                || asistencia.getCurso().getIdCurso() <= 0) {
            throw new IllegalArgumentException(
                    "El estudiante y el curso deben estar registrados."
            );
        }

        if (!"Presente".equals(asistencia.getEstado())
                && !"Ausente".equals(asistencia.getEstado())) {
            throw new IllegalArgumentException(
                    "Debe seleccionar Presente o Ausente."
            );
        }
    }

    private IllegalStateException errorBaseDatos(
            String mensaje,
            SQLException causa
    ) {
        return new IllegalStateException(
                mensaje + " " + causa.getMessage(),
                causa
        );
    }
}