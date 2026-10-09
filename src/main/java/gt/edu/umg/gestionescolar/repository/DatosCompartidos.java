package gt.edu.umg.gestionescolar.repository;

public final class DatosCompartidos {

    private static final EstudianteRepository ESTUDIANTES =
            new EstudianteRepository();

    private static final DocenteRepository DOCENTES =
            new DocenteRepository();

    private static final EncargadoRepository ENCARGADOS =
            new EncargadoRepository();

    private static final GradoSeccionRepository GRADOS =
            new GradoSeccionRepository();

    private static final CursoRepository CURSOS =
            new CursoRepository();

    private static final CicloEscolarRepository CICLOS =
            new CicloEscolarRepository();

    private static final InscripcionRepository INSCRIPCIONES =
            new InscripcionRepository();

    private static final AsistenciaRepository ASISTENCIAS =
            new AsistenciaRepository();

    private static final BoletaRepository BOLETAS =
            new BoletaRepository();

    private DatosCompartidos() {
    }

    public static EstudianteRepository getEstudiantes() {
        return ESTUDIANTES;
    }

    public static DocenteRepository getDocentes() {
        return DOCENTES;
    }

    public static EncargadoRepository getEncargados() {
        return ENCARGADOS;
    }

    public static GradoSeccionRepository getGrados() {
        return GRADOS;
    }

    public static CursoRepository getCursos() {
        return CURSOS;
    }

    public static CicloEscolarRepository getCiclos() {
        return CICLOS;
    }

    public static InscripcionRepository getInscripciones() {
        return INSCRIPCIONES;
    }

    public static AsistenciaRepository getAsistencias() {
        return ASISTENCIAS;
    }

    public static BoletaRepository getBoletas() {
        return BOLETAS;
    }
}