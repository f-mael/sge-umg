package gt.edu.umg.gestionescolar.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/gestion_escolar";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "postgres");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void initializeDatabase() {
        String sqlEncargados = """
                    CREATE TABLE IF NOT EXISTS encargados (
                        id SERIAL PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL,
                        apellido VARCHAR(100) NOT NULL,
                        telefono VARCHAR(20),
                        email VARCHAR(100),
                        cui VARCHAR(20) UNIQUE NOT NULL,
                        parentesco VARCHAR(50) NOT NULL,
                        direccion VARCHAR(255) NOT NULL
                    );
                """;

        String sqlEstudiantes = """
                    CREATE TABLE IF NOT EXISTS estudiantes (
                        id SERIAL PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL,
                        apellido VARCHAR(100) NOT NULL,
                        telefono VARCHAR(20),
                        email VARCHAR(100),
                        carnet VARCHAR(20) UNIQUE NOT NULL,
                        fecha_nacimiento DATE NOT NULL,
                        id_encargado INTEGER,
                        CONSTRAINT fk_estudiantes_encargado FOREIGN KEY (id_encargado) REFERENCES encargados(id) ON DELETE SET NULL
                    );
                """;

        String sqlDocentes = """
                    CREATE TABLE IF NOT EXISTS docentes (
                        id SERIAL PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL,
                        apellido VARCHAR(100) NOT NULL,
                        telefono VARCHAR(20),
                        email VARCHAR(100),
                        codigo_empleado VARCHAR(20) UNIQUE NOT NULL,
                        especialidad VARCHAR(100) NOT NULL
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