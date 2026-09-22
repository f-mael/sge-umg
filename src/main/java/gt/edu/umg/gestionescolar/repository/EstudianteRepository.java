package gt.edu.umg.gestionescolar.repository;

import gt.edu.umg.gestionescolar.model.Estudiante;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repositorio de Estudiantes con almacenamiento en memoria utilizando listas de objetos (ArrayList).
 * Cumple estrictamente con el Entregable 2 de Programación II.
 */
public class EstudianteRepository implements Repository<Estudiante> {

    private final List<Estudiante> estudiantes = new ArrayList<>();
    private int nextId = 1;

    public EstudianteRepository() {
        // Datos iniciales en memoria para demostración del Entregable 2
        save(new Estudiante(0, "Carlos", "López", "5555-1234", "clopez@miumg.edu.gt", "0905-22-1001", LocalDate.of(2004, 5, 14), 1));
        save(new Estudiante(0, "María", "Gómez", "5555-5678", "mgomez@miumg.edu.gt", "0905-22-1002", LocalDate.of(2005, 9, 21), 2));
        save(new Estudiante(0, "Andrés", "Morales", "5555-9012", "amorales@miumg.edu.gt", "0905-22-1003", LocalDate.of(2003, 11, 3), 1));
    }

    @Override
    public synchronized void save(Estudiante estudiante) {
        if (estudiante.getId() <= 0) {
            estudiante.setId(nextId++);
        } else if (estudiante.getId() >= nextId) {
            nextId = estudiante.getId() + 1;
        }
        estudiantes.add(estudiante);
    }

    @Override
    public synchronized void update(Estudiante estudiante) {
        for (int i = 0; i < estudiantes.size(); i++) {
            if (estudiantes.get(i).getId() == estudiante.getId()) {
                estudiantes.set(i, estudiante);
                return;
            }
        }
    }

    @Override
    public synchronized void delete(int id) {
        estudiantes.removeIf(e -> e.getId() == id);
    }

    @Override
    public synchronized Optional<Estudiante> findById(int id) {
        return estudiantes.stream()
                .filter(e -> e.getId() == id)
                .findFirst();
    }

    @Override
    public synchronized List<Estudiante> findAll() {
        return new ArrayList<>(estudiantes);
    }

    @Override
    public synchronized List<Estudiante> search(String criterio) {
        if (criterio == null || criterio.isBlank()) {
            return findAll();
        }
        String crit = criterio.trim().toLowerCase();
        return estudiantes.stream()
                .filter(e -> (e.getNombre() != null && e.getNombre().toLowerCase().contains(crit))
                        || (e.getApellido() != null && e.getApellido().toLowerCase().contains(crit))
                        || (e.getCarnet() != null && e.getCarnet().toLowerCase().contains(crit))
                        || (e.getEmail() != null && e.getEmail().toLowerCase().contains(crit))
                        || (e.getTelefono() != null && e.getTelefono().toLowerCase().contains(crit)))
                .collect(Collectors.toList());
    }
}