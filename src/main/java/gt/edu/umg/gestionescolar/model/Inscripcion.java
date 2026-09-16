package gt.edu.umg.gestionescolar.model;

import java.time.LocalDate;

public class Inscripcion {
    private int idInscripcion;
    private LocalDate fechaInscripcion;
    private Estudiante estudiante;     // Relación con Estudiante
    private GradoSeccion gradoSeccion; // Grado asignado
    private CicloEscolar cicloEscolar; // Ciclo lectivo correspondiente

    public Inscripcion() {}

    public Inscripcion(int idInscripcion, LocalDate fechaInscripcion, Estudiante estudiante, GradoSeccion gradoSeccion, CicloEscolar cicloEscolar) {
        this.idInscripcion = idInscripcion;
        this.fechaInscripcion = fechaInscripcion;
        this.estudiante = estudiante;
        this.gradoSeccion = gradoSeccion;
        this.cicloEscolar = cicloEscolar;
    }

    public int getIdInscripcion() { return idInscripcion; }
    public void setIdInscripcion(int idInscripcion) { this.idInscripcion = idInscripcion; }

    public LocalDate getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDate fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public GradoSeccion getGradoSeccion() { return gradoSeccion; }
    public void setGradoSeccion(GradoSeccion gradoSeccion) { this.gradoSeccion = gradoSeccion; }

    public CicloEscolar getCicloEscolar() { return cicloEscolar; }
    public void setCicloEscolar(CicloEscolar cicloEscolar) { this.cicloEscolar = cicloEscolar; }
}