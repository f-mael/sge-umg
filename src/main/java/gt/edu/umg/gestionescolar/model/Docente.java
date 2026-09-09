package gt.edu.umg.gestionescolar.model;

public class Docente extends Persona {
    private String codigoEmpleado;
    private String especialidad;

    public Docente() {}

    public Docente(int id, String nombre, String apellido, String telefono, String email, 
                   String codigoEmpleado, String especialidad) {
        super(id, nombre, apellido, telefono, email);
        this.codigoEmpleado = codigoEmpleado;
        this.especialidad = especialidad;
    }

    public String getCodigoEmpleado() { return codigoEmpleado; }
    public void setCodigoEmpleado(String codigoEmpleado) { this.codigoEmpleado = codigoEmpleado; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
}
