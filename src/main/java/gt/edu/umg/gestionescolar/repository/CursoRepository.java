/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gt.edu.umg.gestionescolar.repository;

import gt.edu.umg.gestionescolar.model.Curso;
import gt.edu.umg.gestionescolar.model.Docente;
import gt.edu.umg.gestionescolar.model.GradoSeccion;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author HP
 */
public class CursoRepository implements Repository<Curso>{
    
    private List<Curso> cursos;
    
    public CursoRepository(){
        this.cursos = new ArrayList<>();
        
        //instancia Docente 
        Docente docente1 = new Docente(1, "Dania", "Pereira", "12345678", "danipereira@umg", "DOC-01", "Ciencias Exactas"); 
        Docente docente2 = new Docente(1, "Bryan", "Caal", "11111111", "brycaal@umg", "DOC-02", "Lenguaje"); 
        
        //instanci GradoSeccion
        GradoSeccion gradoSeccion1 = new GradoSeccion(1, "Primero Basico", "Basico", 'A');
        GradoSeccion gradoSeccion2 = new GradoSeccion(2, "Segundo Basico", "Basico", 'B');
        
        //datos bases
        save(new Curso(1, "Matematica", docente1, gradoSeccion1));
        save(new Curso(2, "Idioma Español", docente2, gradoSeccion2)); 
        save(new Curso(3, "Fisica", docente1, gradoSeccion2));
    }
    
    //guardar
    @Override 
    public void save(Curso entity){
        cursos.add(entity); 
    }
    
    // actualizar
    @Override
    public void update(Curso entity){ 
        for(int i= 0; i<cursos.size(); i++){
            if(cursos.get(i).getIdCurso()== entity.getIdCurso()){
                cursos.set(i, entity); // aqui se reemplaza el curso en el indice i
                return; 
            }
        }
    }
    
    //Eliminar
    @Override 
    public void delete(int id){
        cursos.removeIf(c -> c.getIdCurso() == id);
    }
    
    // buscar
    @Override
    public Optional<Curso> findById(int id){
        for(Curso c : cursos){ 
            if(c.getIdCurso() == id){
                return Optional.of(c); 
            }
        }
        return Optional.empty(); 
    }
    
    // listar
    @Override 
    public List<Curso> findAll(){
        return cursos; 
    }
    
    @Override 
    public List<Curso> search(String criterio){
        List<Curso> resultados = new ArrayList<>(); 
        String filtro = criterio.toLowerCase().trim(); 
        
        for(Curso c: cursos){
            //validacion para que el nombre no este vacio
            boolean coincideNombre = c.getNombre() != null && 
                                     c.getNombre().toLowerCase().contains(filtro); 
            boolean coincideDocente = false; 
            if(c.getDocente() != null){
                String nombreCompleto= c.getDocente().getNombre() + "" + c.getDocente().getApellido();                 
                coincideDocente = nombreCompleto.toLowerCase().contains(filtro); 
                
            }
            
            // validacion que tenga grado asignado 
            boolean coincideGrado= false; 
            if(c.getGradoSeccion() != null && c.getGradoSeccion().getNombre() != null){
                coincideGrado = c.getGradoSeccion().getNombre().toLowerCase().toLowerCase().contains(filtro);  
            }
            
            // si coincide con cualquiera de los 3 resultados se agrega al resultado
            if(coincideNombre || coincideDocente || coincideGrado){
                resultados.add(c); 
            }
        }
        return resultados; 
    }
}
