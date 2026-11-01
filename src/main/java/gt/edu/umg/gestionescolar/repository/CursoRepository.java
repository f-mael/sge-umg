package gt.edu.umg.gestionescolar.repository;

import gt.edu.umg.gestionescolar.model.Curso;
import gt.edu.umg.gestionescolar.model.Docente;
import gt.edu.umg.gestionescolar.model.GradoSeccion;
import gt.edu.umg.gestionescolar.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CursoRepository implements Repository<Curso> {

    private final GradoSeccionRepository gradoRepo = new GradoSeccionRepository();
    private final DocenteRepository docenteRepo = new DocenteRepository();

    @Override
    public void save(Curso c) {
        String sql = "INSERT INTO cursos (nombre, id_docente, id_grado_seccion) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setInt(2, c.getDocente() != null ? c.getDocente().getId() : 0);
            ps.setInt(3, c.getGradoSeccion() != null ? c.getGradoSeccion().getIdGradoSeccion() : 0);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setIdCurso(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar Curso: " + e.getMessage());
        }
    }

    @Override
    public void update(Curso c) {
        String sql = "UPDATE cursos SET nombre = ?, id_docente = ?, id_grado_seccion = ? WHERE id_curso = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setInt(2, c.getDocente() != null ? c.getDocente().getId() : 0);
            ps.setInt(3, c.getGradoSeccion() != null ? c.getGradoSeccion().getIdGradoSeccion() : 0);
            ps.setInt(4, c.getIdCurso());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar Curso: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM cursos WHERE id_curso = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar Curso: " + e.getMessage());
        }
    }

    @Override
    public Optional<Curso> findById(int id) {
        String sql = "SELECT id_curso, nombre, id_docente, id_grado_seccion FROM cursos WHERE id_curso = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en findById Curso: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Curso> findAll() {
        List<Curso> lista = new ArrayList<>();
        String sql = "SELECT id_curso, nombre, id_docente, id_grado_seccion FROM cursos ORDER BY id_curso";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en findAll Curso: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<Curso> search(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return findAll();
        }
        String f = criterio.trim().toLowerCase();
        return findAll().stream()
            .filter(c -> (c.getNombre() != null && c.getNombre().toLowerCase().contains(f))
                || (c.getDocente() != null && c.getDocente().getNombreCompleto().toLowerCase().contains(f))
                || (c.getGradoSeccion() != null && c.getGradoSeccion().toString().toLowerCase().contains(f)))
            .collect(Collectors.toList());
    }

    private Curso mapear(ResultSet rs) throws SQLException {
        int idCurso = rs.getInt("id_curso");
        String nombre = rs.getString("nombre");
        int idDocente = rs.getInt("id_docente");
        int idGrado = rs.getInt("id_grado_seccion");

        Docente docente = docenteRepo.findById(idDocente).orElse(null);
        GradoSeccion grado = gradoRepo.findById(idGrado).orElse(null);

        return new Curso(idCurso, nombre, docente, grado);
    }
}