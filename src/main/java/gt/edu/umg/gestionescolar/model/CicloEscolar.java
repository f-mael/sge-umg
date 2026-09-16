package gt.edu.umg.gestionescolar.model;

import java.time.LocalDate;

public class CicloEscolar {
    private int idCiclo;
    private int anio;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activo;

    public CicloEscolar() {}

    public CicloEscolar(int idCiclo, int anio, LocalDate fechaInicio, LocalDate fechaFin, boolean activo) {
        this.idCiclo = idCiclo;
        this.anio = anio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activo = activo;
    }

    public int getIdCiclo() { return idCiclo; }
    public void setIdCiclo(int idCiclo) { this.idCiclo = idCiclo; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}