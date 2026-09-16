package gt.edu.umg.gestionescolar.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BoletaCalificaciones {
    private int idBoleta;
    private Estudiante estudiante;
    private CicloEscolar cicloEscolar;
    private String periodo;
    private LocalDate fechaEmision;
    private List<DetalleBoleta> detalles;
    private double promedio;

    public BoletaCalificaciones() {
        this.detalles = new ArrayList<>();
        this.promedio = 0.0;
    }

    public BoletaCalificaciones(int idBoleta, Estudiante estudiante,
                               CicloEscolar cicloEscolar, String periodo,
                               LocalDate fechaEmision) {
        this.idBoleta = idBoleta;
        this.estudiante = estudiante;
        this.cicloEscolar = cicloEscolar;
        this.periodo = periodo;
        this.fechaEmision = fechaEmision;
        this.detalles = new ArrayList<>();
        this.promedio = 0.0;
    }

    public int getIdBoleta() {
        return idBoleta;
    }

    public void setIdBoleta(int idBoleta) {
        this.idBoleta = idBoleta;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public CicloEscolar getCicloEscolar() {
        return cicloEscolar;
    }

    public void setCicloEscolar(CicloEscolar cicloEscolar) {
        this.cicloEscolar = cicloEscolar;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public List<DetalleBoleta> getDetalles() {
        return new ArrayList<>(detalles);
    }

    public void setDetalles(List<DetalleBoleta> detalles) {
        if (detalles == null) {
            this.detalles = new ArrayList<>();
        } else {
            this.detalles = new ArrayList<>(detalles);
        }

        calcularPromedio();
    }

    public double getPromedio() {
        return calcularPromedio();
    }

    public void agregarDetalle(DetalleBoleta detalle) {
        if (detalle != null) {
            detalles.add(detalle);
            calcularPromedio();
        }
    }

    public boolean eliminarDetalle(DetalleBoleta detalle) {
        boolean eliminado = detalles.remove(detalle);

        if (eliminado) {
            calcularPromedio();
        }

        return eliminado;
    }

    public double calcularPromedio() {
        if (detalles.isEmpty()) {
            promedio = 0.0;
            return promedio;
        }

        double sumaNotas = 0.0;

        for (DetalleBoleta detalle : detalles) {
            sumaNotas += detalle.getNota();
        }

        promedio = sumaNotas / detalles.size();
        return promedio;
    }
}