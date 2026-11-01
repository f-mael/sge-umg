/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gt.edu.umg.gestionescolar.repository;

import gt.edu.umg.gestionescolar.model.GradoSeccion;
import gt.edu.umg.gestionescolar.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GradoSeccionRepository implements Repository<GradoSeccion> {

    @Override
    public void save(GradoSeccion gs) {
        String sql = "INSERT INTO grados_secciones (nombre, nivel, seccion) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, gs.getNombre());
            ps.setString(2, gs.getNivel());
            ps.setString(3, String.valueOf(gs.getSeccion()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    gs.setIdGradoSeccion(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar GradoSeccion: " + e.getMessage());
        }
    }

    @Override
    public void update(GradoSeccion gs) {
        String sql = "UPDATE grados_secciones SET nombre = ?, nivel = ?, seccion = ? WHERE id_grado_seccion = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gs.getNombre());
            ps.setString(2, gs.getNivel());
            ps.setString(3, String.valueOf(gs.getSeccion()));
            ps.setInt(4, gs.getIdGradoSeccion());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar GradoSeccion: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM grados_secciones WHERE id_grado_seccion = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar GradoSeccion: " + e.getMessage());
        }
    }

    @Override
    public Optional<GradoSeccion> findById(int id) {
        String sql = "SELECT id_grado_seccion, nombre, nivel, seccion FROM grados_secciones WHERE id_grado_seccion = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en findById GradoSeccion: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<GradoSeccion> findAll() {
        List<GradoSeccion> lista = new ArrayList<>();
        String sql = "SELECT id_grado_seccion, nombre, nivel, seccion FROM grados_secciones ORDER BY id_grado_seccion";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en findAll GradoSeccion: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<GradoSeccion> search(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return findAll();
        }
        List<GradoSeccion> lista = new ArrayList<>();
        String sql = """
            SELECT id_grado_seccion, nombre, nivel, seccion
            FROM grados_secciones
            WHERE LOWER(nombre) LIKE ? OR LOWER(nivel) LIKE ? OR LOWER(seccion) LIKE ?
            ORDER BY id_grado_seccion
        """;
        String filtro = "%" + criterio.trim().toLowerCase() + "%";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            ps.setString(3, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en search GradoSeccion: " + e.getMessage());
        }
        return lista;
    }

    private GradoSeccion mapear(ResultSet rs) throws SQLException {
        String secStr = rs.getString("seccion");
        char seccion = (secStr != null && !secStr.isEmpty()) ? secStr.charAt(0) : 'A';
        return new GradoSeccion(
            rs.getInt("id_grado_seccion"),
            rs.getString("nombre"),
            rs.getString("nivel"),
            seccion
        );
    }
}