package gt.edu.umg.gestionescolar.repository;

public final class DatosCompartidos {
    private static final EstudianteRepository ESTUDIANTES =
        new EstudianteRepository();

    private static final DocenteRepository DOCENTES =
        new DocenteRepository();

    private static final EncargadoRepository ENCARGADOS =
        new EncargadoRepository();

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

    public static AsistenciaRepository getAsistencias() {
        return ASISTENCIAS;
    }

    public static BoletaRepository getBoletas() {
        return BOLETAS;
    }
}