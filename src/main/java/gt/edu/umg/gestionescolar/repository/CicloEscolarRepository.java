/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gt.edu.umg.gestionescolar.repository;
import gt.edu.umg.gestionescolar.model.CicloEscolar;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; 
import java.time.LocalDate;
import java.time.Month;

/**
 *
 * @author HP
 */
public class CicloEscolarRepository implements Repository<CicloEscolar>{
    
   private List<CicloEscolar> cicloEscolar; 
   
   public CicloEscolarRepository(){
       this.cicloEscolar = new ArrayList<>();
       
       //fechas 
       LocalDate fecha_inicio = LocalDate.of(2026, Month.FEBRUARY, 1); 
       LocalDate fecha_fin = LocalDate.of(2026, Month.OCTOBER, 15);
       
       //datos base
       save(new CicloEscolar(1, 2026, fecha_inicio, fecha_fin, true)); 
       save(new CicloEscolar(2, 2025, fecha_inicio, fecha_fin, false)); 
   }   
       //guardar
       @Override
      public void save(CicloEscolar entity){
       cicloEscolar.add(entity); 
   }
      
      //actualizar
      @Override 
      public void update(CicloEscolar entity){
       for(int i= 0; i<cicloEscolar.size(); i++){
            if(cicloEscolar.get(i).getIdCiclo()== entity.getIdCiclo()){
                cicloEscolar.set(i, entity); // aqui se reemplaza el curso en el indice i
                return; 
            }
        }  
   }
      
      //Eliminar
      @Override 
        public void delete(int id){
        cicloEscolar.removeIf(cE -> cE.getIdCiclo()== id);
    }
        // buscar
    @Override
    public Optional<CicloEscolar> findById(int id){
        for(CicloEscolar cE : cicloEscolar){ 
            if(cE.getIdCiclo()== id){
                return Optional.of(cE); 
            }
        }
        return Optional.empty(); 
    }
    
    // listar
    @Override 
    public List<CicloEscolar> findAll(){
        return cicloEscolar; 
    }
    
    //6. Buscar 
    @Override 
    public List<CicloEscolar> search(String criterio){
        List<CicloEscolar> resultados = new ArrayList<>();
        String filtro = criterio.toLowerCase().trim();
        
        //.tolowerCase convierte el texto a minuscula
        //.contains devuelve true si el texto es similar o igual
        for (CicloEscolar cE: cicloEscolar){
            String anioText = String.valueOf(cE.getAnio());
            if (anioText.contains(filtro)) {
                resultados.add(cE);
            }
            
        }
        return resultados;
    }
    
    
      
   }


