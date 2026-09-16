package gt.edu.umg.gestionescolar.model;

import java.time.LocalDate;

public class Asistencia {
    private int idAsistencia;
    private Estudiante estudiante;
    private Curso curso;
    private LocalDate fecha;
    private String estado;

    public Asistencia() {
    }

    public Asistencia(int idAsistencia, Estudiante estudiante, Curso curso,
            LocalDate fecha, String estado) {
        this.idAsistencia = idAsistencia;
        this.estudiante = estudiante;
        this.curso = curso;
        this.fecha = fecha;
        this.estado = estado;
    }

    public int getIdAsistencia() {
        return idAsistencia;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public Curso getCurso() {
        return curso;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setIdAsistencia(int idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}