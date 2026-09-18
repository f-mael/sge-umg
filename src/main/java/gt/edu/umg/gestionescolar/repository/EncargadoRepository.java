package gt.edu.umg.gestionescolar.repository;

import gt.edu.umg.gestionescolar.model.Encargado;
import gt.edu.umg.gestionescolar.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EncargadoRepository implements Repository<Encargado> {

    @Override
    public void save(Encargado encargado) {
        String sql = """
            INSERT INTO encargados (nombre, apellido, telefono, email, cui, parentesco, direccion)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, encargado.getNombre());
            stmt.setString(2, encargado.getApellido());
            stmt.setString(3, encargado.getTelefono());
            stmt.setString(4, encargado.getEmail());
            stmt.setString(5, encargado.getCui());
            stmt.setString(6, encargado.getParentesco());
            stmt.setString(7, encargado.getDireccion());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    encargado.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar encargado: " + e.getMessage());
        }
    }

    @Override
    public void update(Encargado encargado) {
        String sql = """
            UPDATE encargados
            SET nombre = ?, apellido = ?, telefono = ?, email = ?, cui = ?, parentesco = ?, direccion = ?
            WHERE id = ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, encargado.getNombre());
            stmt.setString(2, encargado.getApellido());
            stmt.setString(3, encargado.getTelefono());
            stmt.setString(4, encargado.getEmail());
            stmt.setString(5, encargado.getCui());
            stmt.setString(6, encargado.getParentesco());
            stmt.setString(7, encargado.getDireccion());
            stmt.setInt(8, encargado.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar encargado: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM encargados WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar encargado: " + e.getMessage());
        }
    }

    @Override
    public Optional<Encargado> findById(int id) {
        String sql = "SELECT * FROM encargados WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEncargado(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar encargado por ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Encargado> findAll() {
        List<Encargado> lista = new ArrayList<>();
        String sql = "SELECT * FROM encargados ORDER BY apellido, nombre";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapResultSetToEncargado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar encargados: " + e.getMessage());
        }
        return lista;
    }

    private Encargado mapResultSetToEncargado(ResultSet rs) throws SQLException {
        return new Encargado(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("apellido"),
            rs.getString("telefono"),
            rs.getString("email"),
            rs.getString("cui"),
            rs.getString("parentesco"),
            rs.getString("direccion")
        );
    }
}
