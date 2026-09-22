package gt.edu.umg.gestionescolar.repository;

import gt.edu.umg.gestionescolar.model.Encargado;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación en memoria del repositorio de Encargados utilizando ArrayList.
 * Cumple con los requerimientos del Entregable 2 (Listas en memoria).
 */
public class EncargadoInMemoryRepository implements Repository<Encargado> {

    private final List<Encargado> encargados = new ArrayList<>();
    private int nextId = 1;

    public EncargadoInMemoryRepository() {
        // Datos semilla para demostración inmediata del Entregable 2
        save(new Encargado(0, "Juan", "López", "5555-1111", "juan.lopez@gmail.com", "2456789010101", "Padre", "Zona 1, Ciudad de Guatemala"));
        save(new Encargado(0, "Elena", "Gómez", "5555-2222", "elena.gomez@gmail.com", "3012456780101", "Madre", "Zona 11, Mixco, Guatemala"));
    }

    @Override
    public synchronized void save(Encargado encargado) {
        if (encargado.getId() <= 0) {
            encargado.setId(nextId++);
        } else if (encargado.getId() >= nextId) {
            nextId = encargado.getId() + 1;
        }
        encargados.add(encargado);
    }

    @Override
    public synchronized void update(Encargado encargado) {
        for (int i = 0; i < encargados.size(); i++) {
            if (encargados.get(i).getId() == encargado.getId()) {
                encargados.set(i, encargado);
                return;
            }
        }
    }

    @Override
    public synchronized void delete(int id) {
        encargados.removeIf(e -> e.getId() == id);
    }

    @Override
    public synchronized Optional<Encargado> findById(int id) {
        return encargados.stream()
                .filter(e -> e.getId() == id)
                .findFirst();
    }

    @Override
    public synchronized List<Encargado> findAll() {
        return new ArrayList<>(encargados);
    }

    @Override
    public synchronized List<Encargado> search(String criterio) {
        if (criterio == null || criterio.isBlank()) {
            return findAll();
        }
        String crit = criterio.trim().toLowerCase();
        return encargados.stream()
                .filter(e -> (e.getNombre() != null && e.getNombre().toLowerCase().contains(crit))
                        || (e.getApellido() != null && e.getApellido().toLowerCase().contains(crit))
                        || (e.getCui() != null && e.getCui().toLowerCase().contains(crit))
                        || (e.getParentesco() != null && e.getParentesco().toLowerCase().contains(crit))
                        || (e.getDireccion() != null && e.getDireccion().toLowerCase().contains(crit))
                        || (e.getEmail() != null && e.getEmail().toLowerCase().contains(crit))
                        || (e.getTelefono() != null && e.getTelefono().toLowerCase().contains(crit)))
                .collect(Collectors.toList());
    }
}
