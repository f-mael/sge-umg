package gt.edu.umg.gestionescolar.repository;

import gt.edu.umg.gestionescolar.model.Docente;
import gt.edu.umg.gestionescolar.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DocenteRepository implements Repository<Docente> {

    @Override
    public void save(Docente docente) {
        String sql = """
            INSERT INTO docentes (nombre, apellido, telefono, email, codigo_empleado, especialidad)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, docente.getNombre());
            stmt.setString(2, docente.getApellido());
            stmt.setString(3, docente.getTelefono());
            stmt.setString(4, docente.getEmail());
            stmt.setString(5, docente.getCodigoEmpleado());
            stmt.setString(6, docente.getEspecialidad());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    docente.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar docente: " + e.getMessage());
        }
    }

    @Override
    public void update(Docente docente) {
        String sql = """
            UPDATE docentes
            SET nombre = ?, apellido = ?, telefono = ?, email = ?, codigo_empleado = ?, especialidad = ?
            WHERE id = ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, docente.getNombre());
            stmt.setString(2, docente.getApellido());
            stmt.setString(3, docente.getTelefono());
            stmt.setString(4, docente.getEmail());
            stmt.setString(5, docente.getCodigoEmpleado());
            stmt.setString(6, docente.getEspecialidad());
            stmt.setInt(7, docente.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar docente: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM docentes WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar docente: " + e.getMessage());
        }
    }

    @Override
    public Optional<Docente> findById(int id) {
        String sql = "SELECT * FROM docentes WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDocente(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar docente por ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Docente> findAll() {
        List<Docente> lista = new ArrayList<>();
        String sql = "SELECT * FROM docentes ORDER BY apellido, nombre";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapResultSetToDocente(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar docentes: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<Docente> search(String criterio) {
        if (criterio == null || criterio.isBlank()) {
            return findAll();
        }
        List<Docente> lista = new ArrayList<>();
        String sql = """
            SELECT * FROM docentes 
            WHERE nombre LIKE ? OR apellido LIKE ? OR codigo_empleado LIKE ? OR especialidad LIKE ? OR email LIKE ? OR telefono LIKE ?
            ORDER BY apellido, nombre
        """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + criterio.trim() + "%";
            for (int i = 1; i <= 6; i++) {
                stmt.setString(i, pattern);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToDocente(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar docentes: " + e.getMessage());
        }
        return lista;
    }

    private Docente mapResultSetToDocente(ResultSet rs) throws SQLException {
        return new Docente(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("apellido"),
            rs.getString("telefono"),
            rs.getString("email"),
            rs.getString("codigo_empleado"),
            rs.getString("especialidad")
        );
    }
}
