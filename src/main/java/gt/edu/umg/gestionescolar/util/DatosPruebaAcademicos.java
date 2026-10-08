package gt.edu.umg.gestionescolar.util;

import gt.edu.umg.gestionescolar.model.CicloEscolar;
import gt.edu.umg.gestionescolar.model.Curso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Datos temporales para probar asistencia y boletas.
 * Se sustituirán por los datos del módulo académico.
 */
public final class DatosPruebaAcademicos {

    private static final List<Curso> CURSOS = new ArrayList<>();
    private static final List<CicloEscolar> CICLOS = new ArrayList<>();

    static {
        CURSOS.add(crearCurso(1, "Matemática (prueba)"));
        CURSOS.add(crearCurso(2, "Programación (prueba)"));
        CURSOS.add(crearCurso(3, "Física (prueba)"));

        CICLOS.add(new CicloEscolar(
                1,
                2026,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                true
        ));
    }

    private DatosPruebaAcademicos() {
    }

    private static Curso crearCurso(int id, String nombre) {
        Curso curso = new Curso();
        curso.setIdCurso(id);
        curso.setNombre(nombre);
        return curso;
    }

    public static List<Curso> getCursos() {
        return new ArrayList<>(CURSOS);
    }

    public static List<CicloEscolar> getCiclos() {
        return new ArrayList<>(CICLOS);
    }
}