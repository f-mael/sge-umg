package gt.edu.umg.gestionescolar.model;

public class Curso {
    private int idCurso;
    private String nombre;
    private Docente docente;          // Asociación con la clase Docente
    private GradoSeccion gradoSeccion; // Asociación con GradoSeccion

    public Curso() {}

    public Curso(int idCurso, String nombre, Docente docente, GradoSeccion gradoSeccion) {
        this.idCurso = idCurso;
        this.nombre = nombre;
        this.docente = docente;
        this.gradoSeccion = gradoSeccion;
    }

    public int getIdCurso() { return idCurso; }
    public void setIdCurso(int idCurso) { this.idCurso = idCurso; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Docente getDocente() { return docente; }
    public void setDocente(Docente docente) { this.docente = docente; }

    public GradoSeccion getGradoSeccion() { return gradoSeccion; }
    public void setGradoSeccion(GradoSeccion gradoSeccion) { this.gradoSeccion = gradoSeccion; }
}