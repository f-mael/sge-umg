package gt.edu.umg.gestionescolar.model;

import java.time.LocalDate;

public class Estudiante extends Persona {
    private String carnet;
    private LocalDate fechaNacimiento;
    private int idEncargado;

    public Estudiante() {}

    public Estudiante(int id, String nombre, String apellido, String telefono, String email, 
                      String carnet, LocalDate fechaNacimiento, int idEncargado) {
        super(id, nombre, apellido, telefono, email);
        this.carnet = carnet;
        this.fechaNacimiento = fechaNacimiento;
        this.idEncargado = idEncargado;
    }

    public String getCarnet() { return carnet; }
    public void setCarnet(String carnet) { this.carnet = carnet; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public int getIdEncargado() { return idEncargado; }
    public void setIdEncargado(int idEncargado) { this.idEncargado = idEncargado; }
}
