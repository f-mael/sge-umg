package gt.edu.umg.gestionescolar.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:gestion_escolar.db";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static void initializeDatabase() {
        String sqlEncargados = """
            CREATE TABLE IF NOT EXISTS encargados (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                telefono TEXT,
                email TEXT,
                cui TEXT UNIQUE NOT NULL,
                parentesco TEXT NOT NULL,
                direccion TEXT NOT NULL
            );
        """;

        String sqlEstudiantes = """
            CREATE TABLE IF NOT EXISTS estudiantes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                telefono TEXT,
                email TEXT,
                carnet TEXT UNIQUE NOT NULL,
                fecha_nacimiento TEXT NOT NULL,
                id_encargado INTEGER,
                FOREIGN KEY (id_encargado) REFERENCES encargados(id) ON DELETE SET NULL
            );
        """;

        String sqlDocentes = """
            CREATE TABLE IF NOT EXISTS docentes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                telefono TEXT,
                email TEXT,
                codigo_empleado TEXT UNIQUE NOT NULL,
                especialidad TEXT NOT NULL
            );
        """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlEncargados);
            stmt.execute(sqlEstudiantes);
            stmt.execute(sqlDocentes);
        } catch (SQLException e) {
            System.err.println("Error al inicializar modulo personas: " + e.getMessage());
        }
    }
}