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
        String sql = """
                    INSERT INTO estudiantes (nombre, apellido, telefono, email, carnet, fecha_nacimiento, id_encargado)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, estudiante.getNombre());
            stmt.setString(2, estudiante.getApellido());
            stmt.setString(3, estudiante.getTelefono());
            stmt.setString(4, estudiante.getEmail());
            stmt.setString(5, estudiante.getCarnet());
            stmt.setString(6, estudiante.getFechaNacimiento().toString());

            if (estudiante.getIdEncargado() > 0) {
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
        String sql = """
                    UPDATE estudiantes
                    SET nombre = ?, apellido = ?, telefono = ?, email = ?, carnet = ?, fecha_nacimiento = ?, id_encargado = ?
                    WHERE id = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, estudiante.getNombre());
            stmt.setString(2, estudiante.getApellido());
            stmt.setString(3, estudiante.getTelefono());
            stmt.setString(4, estudiante.getEmail());
            stmt.setString(5, estudiante.getCarnet());
            stmt.setString(6, estudiante.getFechaNacimiento().toString());

            if (estudiante.getIdEncargado() > 0) {
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
        String sql = "SELECT * FROM estudiantes WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEstudiante(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar estudiante por ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Estudiante> findAll() {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT * FROM estudiantes ORDER BY apellido, nombre";

        try (Connection conn = DatabaseManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapResultSetToEstudiante(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar estudiantes: " + e.getMessage());
        }
        return lista;
    }

    private Estudiante mapResultSetToEstudiante(ResultSet rs) throws SQLException {
        return new Estudiante(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("carnet"),
                LocalDate.parse(rs.getString("fecha_nacimiento")),
                rs.getInt("id_encargado"));
    }
}