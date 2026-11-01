/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gt.edu.umg.gestionescolar.repository;

import gt.edu.umg.gestionescolar.model.CicloEscolar;
import gt.edu.umg.gestionescolar.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CicloEscolarRepository implements Repository<CicloEscolar> {

    @Override
    public void save(CicloEscolar c) {
        String sql = "INSERT INTO ciclos_escolares (anio, fecha_inicio, fecha_fin, activo) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getAnio());
            ps.setDate(2, Date.valueOf(c.getFechaInicio()));
            ps.setDate(3, Date.valueOf(c.getFechaFin()));
            ps.setBoolean(4, c.isActivo());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setIdCiclo(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar CicloEscolar: " + e.getMessage());
        }
    }

    @Override
    public void update(CicloEscolar c) {
        String sql = "UPDATE ciclos_escolares SET anio = ?, fecha_inicio = ?, fecha_fin = ?, activo = ? WHERE id_ciclo = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getAnio());
            ps.setDate(2, Date.valueOf(c.getFechaInicio()));
            ps.setDate(3, Date.valueOf(c.getFechaFin()));
            ps.setBoolean(4, c.isActivo());
            ps.setInt(5, c.getIdCiclo());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar CicloEscolar: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM ciclos_escolares WHERE id_ciclo = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar CicloEscolar: " + e.getMessage());
        }
    }

    @Override
    public Optional<CicloEscolar> findById(int id) {
        String sql = "SELECT id_ciclo, anio, fecha_inicio, fecha_fin, activo FROM ciclos_escolares WHERE id_ciclo = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en findById CicloEscolar: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<CicloEscolar> findAll() {
        List<CicloEscolar> lista = new ArrayList<>();
        String sql = "SELECT id_ciclo, anio, fecha_inicio, fecha_fin, activo FROM ciclos_escolares ORDER BY anio DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en findAll CicloEscolar: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<CicloEscolar> search(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return findAll();
        }
        List<CicloEscolar> lista = new ArrayList<>();
        String sql = "SELECT id_ciclo, anio, fecha_inicio, fecha_fin, activo FROM ciclos_escolares WHERE CAST(anio AS TEXT) LIKE ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + criterio.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en search CicloEscolar: " + e.getMessage());
        }
        return lista;
    }

    public Optional<CicloEscolar> findCicloActivo() {
        String sql = "SELECT id_ciclo, anio, fecha_inicio, fecha_fin, activo FROM ciclos_escolares WHERE activo = TRUE LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en findCicloActivo: " + e.getMessage());
        }
        return findAll().stream().findFirst();
    }

    private CicloEscolar mapear(ResultSet rs) throws SQLException {
        return new CicloEscolar(
            rs.getInt("id_ciclo"),
            rs.getInt("anio"),
            rs.getDate("fecha_inicio").toLocalDate(),
            rs.getDate("fecha_fin").toLocalDate(),
            rs.getBoolean("activo")
        );
    }
}

