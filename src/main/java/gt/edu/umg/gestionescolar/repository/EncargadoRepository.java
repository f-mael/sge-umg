package gt.edu.umg.gestionescolar.repository;

import gt.edu.umg.gestionescolar.model.Encargado;
import gt.edu.umg.gestionescolar.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EncargadoRepository implements Repository<Encargado> {

    @Override
    public void save(Encargado encargado) {
        String sql = "INSERT INTO encargados (nombre, apellido, telefono, email, cui, parentesco, direccion) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
        String sql = "UPDATE encargados SET nombre = ?, apellido = ?, telefono = ?, email = ?, cui = ?, parentesco = ?, direccion = ? WHERE id = ?";
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
        String sql = "SELECT id, nombre, apellido, telefono, email, cui, parentesco, direccion FROM encargados WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearEncargado(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar encargado por ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Encargado> findAll() {
        List<Encargado> encargados = new ArrayList<>();
        String sql = "SELECT id, nombre, apellido, telefono, email, cui, parentesco, direccion FROM encargados ORDER BY id ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                encargados.add(mapearEncargado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar encargados: " + e.getMessage());
        }
        return encargados;
    }

    @Override
    public List<Encargado> search(String criterio) {
        if (criterio == null || criterio.isBlank()) {
            return findAll();
        }
        List<Encargado> encargados = new ArrayList<>();
        String sql = """
            SELECT id, nombre, apellido, telefono, email, cui, parentesco, direccion
            FROM encargados
            WHERE LOWER(nombre) LIKE ?
               OR LOWER(apellido) LIKE ?
               OR LOWER(cui) LIKE ?
               OR LOWER(parentesco) LIKE ?
               OR LOWER(direccion) LIKE ?
               OR LOWER(email) LIKE ?
               OR LOWER(telefono) LIKE ?
            ORDER BY id ASC
        """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String filtro = "%" + criterio.trim().toLowerCase() + "%";
            for (int i = 1; i <= 7; i++) {
                stmt.setString(i, filtro);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    encargados.add(mapearEncargado(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar encargados: " + e.getMessage());
        }
        return encargados;
    }

    private Encargado mapearEncargado(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        String apellido = rs.getString("apellido");
        String telefono = rs.getString("telefono");
        String email = rs.getString("email");
        String cui = rs.getString("cui");
        String parentesco = rs.getString("parentesco");
        String direccion = rs.getString("direccion");
        return new Encargado(id, nombre, apellido, telefono, email, cui, parentesco, direccion);
    }
}
