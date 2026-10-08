package gt.edu.umg.gestionescolar.model;

public class DetalleBoleta {
    private int idDetalle;
    private Curso curso;
    private double nota;
    private String observacion;
    private double peso = 1.0;

    public DetalleBoleta() {
    }

    public DetalleBoleta(int idDetalle, Curso curso,
                         double nota, String observacion) {
        this(idDetalle, curso, nota, observacion, 1.0);
    }

    public DetalleBoleta(int idDetalle, Curso curso,
                         double nota, String observacion, double peso) {
        this.idDetalle = idDetalle;
        this.curso = curso;
        setNota(nota);
        this.observacion = observacion;
        setPeso(peso);
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

    public double getPeso() {
        return peso;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public void setNota(double nota) {
        if (!Double.isFinite(nota) || nota < 0 || nota > 100) {
            throw new IllegalArgumentException(
                "La nota debe estar entre 0 y 100."
            );
        }
        this.nota = nota;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public void setPeso(double peso) {
        if (!Double.isFinite(peso) || peso <= 0) {
            throw new IllegalArgumentException(
                "El peso debe ser un número mayor que cero."
            );
        }
        this.peso = peso;
    }
}