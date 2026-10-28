package gt.edu.umg.gestionescolar.repository;

import gt.edu.umg.gestionescolar.model.Estudiante;
import gt.edu.umg.gestionescolar.util.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EstudianteRepository implements Repository<Estudiante> {

    @Override
    public void save(Estudiante estudiante) {
        String sql = "INSERT INTO estudiantes (nombre, apellido, telefono, email, carnet, fecha_nacimiento, id_encargado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, estudiante.getNombre());
            stmt.setString(2, estudiante.getApellido());
            stmt.setString(3, estudiante.getTelefono());
            stmt.setString(4, estudiante.getEmail());
            stmt.setString(5, estudiante.getCarnet());
            if (estudiante.getFechaNacimiento() != null) {
                stmt.setDate(6, Date.valueOf(estudiante.getFechaNacimiento()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            if (estudiante.getIdEncargado() != null && estudiante.getIdEncargado() > 0) {
                stmt.setInt(7, estudiante.getIdEncargado());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    estudiante.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar estudiante: " + e.getMessage());
        }
    }

    @Override
    public void update(Estudiante estudiante) {
        String sql = "UPDATE estudiantes SET nombre = ?, apellido = ?, telefono = ?, email = ?, carnet = ?, fecha_nacimiento = ?, id_encargado = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estudiante.getNombre());
            stmt.setString(2, estudiante.getApellido());
            stmt.setString(3, estudiante.getTelefono());
            stmt.setString(4, estudiante.getEmail());
            stmt.setString(5, estudiante.getCarnet());
            if (estudiante.getFechaNacimiento() != null) {
                stmt.setDate(6, Date.valueOf(estudiante.getFechaNacimiento()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            if (estudiante.getIdEncargado() != null && estudiante.getIdEncargado() > 0) {
                stmt.setInt(7, estudiante.getIdEncargado());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            stmt.setInt(8, estudiante.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar estudiante: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM estudiantes WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar estudiante: " + e.getMessage());
        }
    }

    @Override
    public Optional<Estudiante> findById(int id) {
        String sql = "SELECT id, nombre, apellido, telefono, email, carnet, fecha_nacimiento, id_encargado FROM estudiantes WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearEstudiante(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar estudiante por ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Estudiante> findAll() {
        List<Estudiante> estudiantes = new ArrayList<>();
        String sql = "SELECT id, nombre, apellido, telefono, email, carnet, fecha_nacimiento, id_encargado FROM estudiantes ORDER BY id ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                estudiantes.add(mapearEstudiante(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar estudiantes: " + e.getMessage());
        }
        return estudiantes;
    }

    @Override
    public List<Estudiante> search(String criterio) {
        if (criterio == null || criterio.isBlank()) {
            return findAll();
        }
        List<Estudiante> estudiantes = new ArrayList<>();
        String sql = """
            SELECT id, nombre, apellido, telefono, email, carnet, fecha_nacimiento, id_encargado
            FROM estudiantes
            WHERE LOWER(nombre) LIKE ?
               OR LOWER(apellido) LIKE ?
               OR LOWER(carnet) LIKE ?
               OR LOWER(email) LIKE ?
               OR LOWER(telefono) LIKE ?
            ORDER BY id ASC
        """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String filtro = "%" + criterio.trim().toLowerCase() + "%";
            for (int i = 1; i <= 5; i++) {
                stmt.setString(i, filtro);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    estudiantes.add(mapearEstudiante(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar estudiantes: " + e.getMessage());
        }
        return estudiantes;
    }

    private Estudiante mapearEstudiante(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        String apellido = rs.getString("apellido");
        String telefono = rs.getString("telefono");
        String email = rs.getString("email");
        String carnet = rs.getString("carnet");
        Date fechaDate = rs.getDate("fecha_nacimiento");
        LocalDate fechaNacimiento = fechaDate != null ? fechaDate.toLocalDate() : null;
        int idEnc = rs.getInt("id_encargado");
        Integer idEncargado = rs.wasNull() ? null : idEnc;
        return new Estudiante(id, nombre, apellido, telefono, email, carnet, fechaNacimiento, idEncargado);
    }
}