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
        
        String sqlCicloEscolares = ("""
                CREATE TABLE IF NOT EXISTS ciclos_escolares (
                    id_ciclo SERIAL PRIMARY KEY,
                    anio INT NOT NULL,
                    fecha_inicio DATE NOT NULL,
                    fecha_fin DATE NOT NULL,
                    activo BOOLEAN NOT NULL DEFAULT TRUE
                );
            """);
        
        String sqlGradosSecciones = ("""
                CREATE TABLE IF NOT EXISTS grados_secciones (
                    id_grado_seccion SERIAL PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    nivel VARCHAR(50) NOT NULL,
                    seccion CHAR(1) NOT NULL
                );
            """);

            String sqlCursos = ("""
                CREATE TABLE IF NOT EXISTS cursos (
                    id_curso SERIAL PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    id_docente INT NOT NULL,
                    id_grado_seccion INT NOT NULL REFERENCES grados_secciones(id_grado_seccion) ON DELETE CASCADE
                );
            """);

            String sqlInscripciones = """
                CREATE TABLE IF NOT EXISTS inscripciones (
                    id_inscripcion SERIAL PRIMARY KEY,
                    fecha_inscripcion DATE NOT NULL,
                    id_estudiante INTEGER NOT NULL,
                    id_grado_seccion INTEGER NOT NULL,
                    id_ciclo INTEGER NOT NULL,
                    CONSTRAINT fk_inscripciones_grado FOREIGN KEY (id_grado_seccion) REFERENCES grados_secciones(id_grado_seccion) ON DELETE CASCADE,
                    CONSTRAINT fk_inscripciones_ciclo FOREIGN KEY (id_ciclo) REFERENCES ciclos_escolares(id_ciclo) ON DELETE CASCADE
                );
                """;

           

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlEncargados);
            stmt.execute(sqlEstudiantes);
            stmt.execute(sqlDocentes);
            stmt.execute(sqlCicloEscolares); 
            stmt.execute(sqlGradosSecciones); 
            stmt.execute(sqlCursos); 
            stmt.execute(sqlInscripciones); 
        } catch (SQLException e) {
            System.err.println("Error al inicializar modulo personas: " + e.getMessage());
        }
    }
}