package gt.edu.umg.gestionescolar.model;

public class Encargado extends Persona {
    private String cui;
    private String parentesco;
    private String direccion;

    public Encargado() {}

    public Encargado(int id, String nombre, String apellido, String telefono, String email, 
                     String cui, String parentesco, String direccion) {
        super(id, nombre, apellido, telefono, email);
        this.cui = cui;
        this.parentesco = parentesco;
        this.direccion = direccion;
    }

    public String getCui() { return cui; }
    public void setCui(String cui) { this.cui = cui; }

    public String getParentesco() { return parentesco; }
    public void setParentesco(String parentesco) { this.parentesco = parentesco; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
