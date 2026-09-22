package gt.edu.umg.gestionescolar.repository;

import gt.edu.umg.gestionescolar.model.Docente;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación en memoria del repositorio de Docentes utilizando ArrayList.
 * Cumple con los requerimientos del Entregable 2 (Listas en memoria).
 */
public class DocenteInMemoryRepository implements Repository<Docente> {

    private final List<Docente> docentes = new ArrayList<>();
    private int nextId = 1;

    public DocenteInMemoryRepository() {
        // Datos semilla para demostración inmediata del Entregable 2
        save(new Docente(0, "Ing. Roberto", "Morales", "5555-8888", "rmorales@profesor.umg.edu.gt", "DOC-101", "Programación Orientada a Objetos"));
        save(new Docente(0, "Licda. Ana", "Castillo", "5555-9999", "acastillo@profesor.umg.edu.gt", "DOC-102", "Bases de Datos Avanzadas"));
    }

    @Override
    public synchronized void save(Docente docente) {
        if (docente.getId() <= 0) {
            docente.setId(nextId++);
        } else if (docente.getId() >= nextId) {
            nextId = docente.getId() + 1;
        }
        docentes.add(docente);
    }

    @Override
    public synchronized void update(Docente docente) {
        for (int i = 0; i < docentes.size(); i++) {
            if (docentes.get(i).getId() == docente.getId()) {
                docentes.set(i, docente);
                return;
            }
        }
    }

    @Override
    public synchronized void delete(int id) {
        docentes.removeIf(d -> d.getId() == id);
    }

    @Override
    public synchronized Optional<Docente> findById(int id) {
        return docentes.stream()
                .filter(d -> d.getId() == id)
                .findFirst();
    }

    @Override
    public synchronized List<Docente> findAll() {
        return new ArrayList<>(docentes);
    }

    @Override
    public synchronized List<Docente> search(String criterio) {
        if (criterio == null || criterio.isBlank()) {
            return findAll();
        }
        String crit = criterio.trim().toLowerCase();
        return docentes.stream()
                .filter(d -> (d.getNombre() != null && d.getNombre().toLowerCase().contains(crit))
                        || (d.getApellido() != null && d.getApellido().toLowerCase().contains(crit))
                        || (d.getCodigoEmpleado() != null && d.getCodigoEmpleado().toLowerCase().contains(crit))
                        || (d.getEspecialidad() != null && d.getEspecialidad().toLowerCase().contains(crit))
                        || (d.getEmail() != null && d.getEmail().toLowerCase().contains(crit))
                        || (d.getTelefono() != null && d.getTelefono().toLowerCase().contains(crit)))
                .collect(Collectors.toList());
    }
}
