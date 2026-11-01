package gt.edu.umg.gestionescolar.repository;

import gt.edu.umg.gestionescolar.model.CicloEscolar;
import gt.edu.umg.gestionescolar.model.Estudiante;
import gt.edu.umg.gestionescolar.model.GradoSeccion;
import gt.edu.umg.gestionescolar.model.Inscripcion;
import gt.edu.umg.gestionescolar.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InscripcionRepository implements Repository<Inscripcion> {

    private final GradoSeccionRepository gradoRepo = new GradoSeccionRepository();
    private final CicloEscolarRepository cicloRepo = new CicloEscolarRepository();
    private final EstudianteRepository estudianteRepo = new EstudianteRepository();

    @Override
    public void save(Inscripcion ins) {
        String sql = "INSERT INTO inscripciones (fecha_inscripcion, id_estudiante, id_grado_seccion, id_ciclo) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(ins.getFechaInscripcion()));
            ps.setInt(2, ins.getEstudiante() != null ? ins.getEstudiante().getId() : 0);
            ps.setInt(3, ins.getGradoSeccion() != null ? ins.getGradoSeccion().getIdGradoSeccion() : 0);
            ps.setInt(4, ins.getCicloEscolar() != null ? ins.getCicloEscolar().getIdCiclo() : 0);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    ins.setIdInscripcion(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar Inscripcion: " + e.getMessage());
        }
    }

    @Override
    public void update(Inscripcion ins) {
        String sql = "UPDATE inscripciones SET fecha_inscripcion = ?, id_estudiante = ?, id_grado_seccion = ?, id_ciclo = ? WHERE id_inscripcion = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(ins.getFechaInscripcion()));
            ps.setInt(2, ins.getEstudiante() != null ? ins.getEstudiante().getId() : 0);
            ps.setInt(3, ins.getGradoSeccion() != null ? ins.getGradoSeccion().getIdGradoSeccion() : 0);
            ps.setInt(4, ins.getCicloEscolar() != null ? ins.getCicloEscolar().getIdCiclo() : 0);
            ps.setInt(5, ins.getIdInscripcion());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar Inscripcion: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM inscripciones WHERE id_inscripcion = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar Inscripcion: " + e.getMessage());
        }
    }

    @Override
    public Optional<Inscripcion> findById(int id) {
        String sql = "SELECT id_inscripcion, fecha_inscripcion, id_estudiante, id_grado_seccion, id_ciclo FROM inscripciones WHERE id_inscripcion = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en findById Inscripcion: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Inscripcion> findAll() {
        List<Inscripcion> lista = new ArrayList<>();
        String sql = "SELECT id_inscripcion, fecha_inscripcion, id_estudiante, id_grado_seccion, id_ciclo FROM inscripciones ORDER BY id_inscripcion";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en findAll Inscripcion: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<Inscripcion> search(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return findAll();
        }
        String f = criterio.trim().toLowerCase();
        return findAll().stream()
            .filter(i -> (i.getEstudiante() != null && (i.getEstudiante().getNombreCompleto().toLowerCase().contains(f)
                    || (i.getEstudiante().getCarnet() != null && i.getEstudiante().getCarnet().toLowerCase().contains(f))))
                || (i.getGradoSeccion() != null && i.getGradoSeccion().toString().toLowerCase().contains(f)))
            .collect(Collectors.toList());
    }

    private Inscripcion mapear(ResultSet rs) throws SQLException {
        int idIns = rs.getInt("id_inscripcion");
        Date fecha = rs.getDate("fecha_inscripcion");
        int idEst = rs.getInt("id_estudiante");
        int idGrado = rs.getInt("id_grado_seccion");
        int idCiclo = rs.getInt("id_ciclo");

        Estudiante est = estudianteRepo.findById(idEst).orElse(null);
        GradoSeccion grado = gradoRepo.findById(idGrado).orElse(null);
        CicloEscolar ciclo = cicloRepo.findById(idCiclo).orElse(null);

        return new Inscripcion(idIns, fecha.toLocalDate(), est, grado, ciclo);
    }
}