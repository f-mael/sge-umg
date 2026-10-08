/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gt.edu.umg.gestionescolar.repository;
import java.util.ArrayList; 
import java.util.List; 
import java.util.Optional;
import gt.edu.umg.gestionescolar.model.GradoSeccion;
/**
 *
 * @author HP
 */
public class GradoSeccionRepository implements Repository <GradoSeccion>{
    
    //variable Array llamada grados
    private List<GradoSeccion> grados;
    
    public GradoSeccionRepository(){
        //inicia la lista en la RAM 
    this.grados = new ArrayList<>();
    
    save(new GradoSeccion(1, "Primero Basico", "Basico", 'A'));
    save(new GradoSeccion(2, "Segundo Basico", "Basico", 'A'));
    save(new GradoSeccion(3, "Tercero Basico", "Basico", 'B'));
}
    //1. guardar usando .add()
    //Override avisa al compilador cumple con una de las 6 firmas obligatorias en Repository.java 
    @Override
    public void save(GradoSeccion entity){
        grados.add(entity); // entity es una variable de entrada definida en Repository.java
    }//.add inserta un elemento al final de la lista
    
    //2. modificar 
    @Override
    public void update(GradoSeccion entity){
        for(int i=0; i<grados.size(); i++){
            if(grados.get(i).getIdGradoSeccion() == entity.getIdGradoSeccion()){
                grados.set(i, entity); //aqui se reemplaza el objeto en la posicion "i"
            }
        }
    }
    
    // 3.Eliminar
    @Override 
    public void delete(int id){
        grados.removeIf(grado -> grado.getIdGradoSeccion() == id);
    }
    
    //4. Buscar por ID 
    @Override 
    public Optional<GradoSeccion> findById(int id){
        for(GradoSeccion g : grados){
            if(g.getIdGradoSeccion()==id){
                return Optional.of(g); //se busca dentro del contenedor Optional
            }
        }
        return Optional.empty(); // no encontrado
    }
    
    //5. Listar 
    @Override 
    public List<GradoSeccion> findAll(){
        return grados;
    }
    
    //6. Buscar 
    @Override 
    public List<GradoSeccion> search(String criterio){
        List<GradoSeccion> resultados = new ArrayList<>();
        String filtro = criterio.toLowerCase().trim();
        
        //.tolowerCase convierte el texto a minuscula
        //.contains devuelve true si el texto es similar o igual
        for (GradoSeccion g: grados){
            String sec= String.valueOf(g.getSeccion()).toLowerCase();
            if(g.getNombre().toLowerCase().contains(filtro)||
                g.getNivel().toLowerCase().contains(filtro)||
                sec.contains(filtro) ){
                resultados.add(g);
            }
            
        }
        return resultados;
    }
}
