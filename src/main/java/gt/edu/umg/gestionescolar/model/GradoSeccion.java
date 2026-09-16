package gt.edu.umg.gestionescolar.model;

public class GradoSeccion {
    //declaracion de variables
    private int idGradoSeccion;
    private String nombre;  
    private String nivel;   
    private char seccion;   

    public GradoSeccion() {}

       //constructor
    public GradoSeccion(int idGradoSeccion, String nombre, String nivel, char seccion) {
        this.idGradoSeccion = idGradoSeccion;
        this.nombre = nombre;
        this.nivel = nivel;
        this.seccion = seccion;
    }

    //get y set 
    public int getIdGradoSeccion() { return idGradoSeccion; }
    public void setIdGradoSeccion(int idGradoSeccion) { this.idGradoSeccion = idGradoSeccion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public char getSeccion() { return seccion; }
    public void setSeccion(char seccion) { this.seccion = seccion; }
}