package gt.edu.umg.gestionescolar.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    void save(T entity);

    void update(T entity);

    void delete(int id);

    Optional<T> findById(int id);

    List<T> findAll();

    List<T> search(String criterio);
}