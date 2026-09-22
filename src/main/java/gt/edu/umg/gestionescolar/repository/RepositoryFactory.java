package gt.edu.umg.gestionescolar.repository;

import gt.edu.umg.gestionescolar.model.Docente;
import gt.edu.umg.gestionescolar.model.Encargado;
import gt.edu.umg.gestionescolar.model.Estudiante;

/**
 * Fábrica y proveedor central de repositorios con soporte de doble persistencia.
 * Permite alternar limpiamente entre modo SQLite (Entregable 3) y modo Memoria con ArrayList (Entregable 2).
 */
public class RepositoryFactory {

    public enum RepositoryType {
        SQLITE("Base de Datos SQLite (Entregable 3)"),
        MEMORY("Listas en Memoria - ArrayList (Entregable 2)");

        private final String descripcion;

        RepositoryType(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    private static RepositoryType currentType = RepositoryType.SQLITE;

    // Instancias de persistencia en SQLite (JDBC)
    private static final Repository<Estudiante> estudianteSqliteRepo = new EstudianteRepository();
    private static final Repository<Docente> docenteSqliteRepo = new DocenteRepository();
    private static final Repository<Encargado> encargadoSqliteRepo = new EncargadoRepository();

    // Instancias de persistencia en Memoria (ArrayList)
    private static final Repository<Estudiante> estudianteMemoryRepo = new EstudianteInMemoryRepository();
    private static final Repository<Docente> docenteMemoryRepo = new DocenteInMemoryRepository();
    private static final Repository<Encargado> encargadoMemoryRepo = new EncargadoInMemoryRepository();

    public static synchronized RepositoryType getRepositoryType() {
        return currentType;
    }

    public static synchronized void setRepositoryType(RepositoryType type) {
        if (type != null) {
            currentType = type;
        }
    }

    public static Repository<Estudiante> getEstudianteRepository() {
        return (currentType == RepositoryType.SQLITE) ? estudianteSqliteRepo : estudianteMemoryRepo;
    }

    public static Repository<Docente> getDocenteRepository() {
        return (currentType == RepositoryType.SQLITE) ? docenteSqliteRepo : docenteMemoryRepo;
    }

    public static Repository<Encargado> getEncargadoRepository() {
        return (currentType == RepositoryType.SQLITE) ? encargadoSqliteRepo : encargadoMemoryRepo;
    }
}
