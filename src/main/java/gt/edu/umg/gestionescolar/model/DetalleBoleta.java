package gt.edu.umg.gestionescolar.model;

public class DetalleBoleta {
    private int idDetalle;
    private Curso curso;
    private double nota;
    private String observacion;

    public DetalleBoleta() {
    }

    public DetalleBoleta(int idDetalle, Curso curso, double nota, String observacion) {
        this.idDetalle = idDetalle;
        this.curso = curso;
        this.nota = nota;
        this.observacion = observacion;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public Curso getCurso() {
        return curso;
    }

    public double getNota() {
        return nota;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}