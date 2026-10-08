package gt.edu.umg.gestionescolar.repository;

import gt.edu.umg.gestionescolar.model.Inscripcion;
import gt.edu.umg.gestionescolar.model.CicloEscolar;
import gt.edu.umg.gestionescolar.model.Estudiante;
import gt.edu.umg.gestionescolar.model.GradoSeccion; 
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; 
import java.time.LocalDate;
import java.time.Month;

public class InscripcionRepository implements Repository<Inscripcion> {
    
    private List<Inscripcion> inscripciones;
    
    public InscripcionRepository() {
        this.inscripciones = new ArrayList<>();
        
        // 1. Instancias de Estudiante
        Estudiante est1 = new Estudiante(1, "Pedro", "Lopez", "11111111", "plopez@correo.com", "EST-001", LocalDate.of(2006, Month.OCTOBER, 8), 1);
        Estudiante est2 = new Estudiante(2, "Dania", "Pereira", "22222222", "danipereira@correo.com", "EST-002", LocalDate.of(2007, Month.DECEMBER, 16), 2);
    
        // 2. Instancias de GradoSeccion 
        GradoSeccion grado1 = new GradoSeccion(1, "Primero Basico", "Basico", 'A');
        GradoSeccion grado2 = new GradoSeccion(2, "Segundo Basico", "Basico", 'B');
        
        // 3. Fechas y Ciclos Escolares
        LocalDate fechaInicio = LocalDate.of(2026, Month.FEBRUARY, 1); 
        LocalDate fechaFin = LocalDate.of(2026, Month.OCTOBER, 15);
        LocalDate fechaInscripcion = LocalDate.of(2026, Month.JANUARY, 30);

        CicloEscolar ciclo1 = new CicloEscolar(1, 2026, fechaInicio, fechaFin, true); 
        CicloEscolar ciclo2 = new CicloEscolar(2, 2025, fechaInicio, fechaFin, false);                   
    
        // 4. Datos base (ajusta los argumentos según tu constructor en Inscripcion.java)
        save(new Inscripcion(1, fechaInscripcion, est1, grado1, ciclo1)); 
        save(new Inscripcion(2, fechaInscripcion, est2, grado2, ciclo2));
    }
    
    // 1. Guardar
    @Override
    public void save(Inscripcion entity) {
        inscripciones.add(entity);
    }

    // 2. Actualizar
    @Override 
    public void update(Inscripcion entity) { 
        for (int i = 0; i < inscripciones.size(); i++) {
            if (inscripciones.get(i).getIdInscripcion() == entity.getIdInscripcion()) {
                inscripciones.set(i, entity);
                return; 
            }
        }
    }
    
    // 3. Eliminar 
    @Override 
    public void delete(int id) {
        inscripciones.removeIf(in -> in.getIdInscripcion() == id);
    }
    
    // 4. Buscar por ID
    @Override
    public Optional<Inscripcion> findById(int id) {
        for (Inscripcion in : inscripciones) {
            if (in.getIdInscripcion() == id) {
                return Optional.of(in); 
            }
        }
        return Optional.empty(); 
    }
    
    // 5. Listar todos
    @Override 
    public List<Inscripcion> findAll() {
        return inscripciones; 
    }
    
    // 6. Buscar con filtros
    @Override 
    public List<Inscripcion> search(String criterio) {
        List<Inscripcion> resultados = new ArrayList<>(); 
        String filtro = criterio.toLowerCase().trim(); 
        
        for (Inscripcion in : inscripciones) {
            boolean coincideCarnet = false;
            boolean coincideNombre = false;
            boolean coincideGrado = false;

            // Búsqueda por datos del estudiante (carnet, nombre o apellido)
            if (in.getEstudiante() != null) {
                if (in.getEstudiante().getCarnet() != null) {
                    coincideCarnet = in.getEstudiante().getCarnet().toLowerCase().contains(filtro);
                }
                String nombreCompleto = in.getEstudiante().getNombre() + " " + in.getEstudiante().getApellido();
                coincideNombre = nombreCompleto.toLowerCase().contains(filtro);
            }

            // Búsqueda por nombre del grado asignado
            if (in.getGradoSeccion() != null && in.getGradoSeccion().getNombre() != null) {
                coincideGrado = in.getGradoSeccion().getNombre().toLowerCase().contains(filtro);
            }

            // Si coincide con carnet, nombre o grado, se añade a resultados
            if (coincideCarnet || coincideNombre || coincideGrado) {
                resultados.add(in); 
            }
        }
        return resultados; 
    }
}