package gt.edu.umg.gestionescolar.repository;

import gt.edu.umg.gestionescolar.model.Asistencia;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class AsistenciaRepository implements Repository<Asistencia> {
    private final List<Asistencia> asistencias = new ArrayList<>();
    private int siguienteId = 1;

    @Override
    public void save(Asistencia asistencia) {
        validar(asistencia);
        validarDuplicado(asistencia, 0);

        if (asistencia.getIdAsistencia() != 0) {
            throw new IllegalArgumentException(
                "Una asistencia nueva debe tener ID 0."
            );
        }

        asistencia.setIdAsistencia(siguienteId++);
        asistencias.add(asistencia);
    }

    @Override
    public void update(Asistencia asistencia) {
        validar(asistencia);

        for (int i = 0; i < asistencias.size(); i++) {
            if (asistencias.get(i).getIdAsistencia()
                    == asistencia.getIdAsistencia()) {
                validarDuplicado(
                    asistencia, asistencia.getIdAsistencia()
                );
                asistencias.set(i, asistencia);
                return;
            }
        }

        throw new IllegalArgumentException(
            "No se encontró la asistencia que desea actualizar."
        );
    }

    @Override
    public void delete(int id) {
        asistencias.removeIf(a -> a.getIdAsistencia() == id);
    }

    @Override
    public Optional<Asistencia> findById(int id) {
        return asistencias.stream()
            .filter(a -> a.getIdAsistencia() == id)
            .findFirst();
    }

    @Override
    public List<Asistencia> findAll() {
        return new ArrayList<>(asistencias);
    }

    @Override
    public List<Asistencia> search(String criterio) {
        String texto = criterio == null
            ? ""
            : criterio.trim().toLowerCase(Locale.ROOT);

        List<Asistencia> resultados = new ArrayList<>();

        for (Asistencia asistencia : asistencias) {
            String datos = (
                asistencia.getIdAsistencia() + " "
                + asistencia.getEstudiante().getNombreCompleto() + " "
                + asistencia.getEstudiante().getCarnet() + " "
                + asistencia.getCurso().getNombre() + " "
                + asistencia.getFecha() + " "
                + asistencia.getEstado()
            ).toLowerCase(Locale.ROOT);

            if (datos.contains(texto)) {
                resultados.add(asistencia);
            }
        }

        return resultados;
    }

    private void validar(Asistencia asistencia) {
        if (asistencia == null) {
            throw new IllegalArgumentException(
                "La asistencia es obligatoria."
            );
        }

        if (asistencia.getEstudiante() == null
                || asistencia.getCurso() == null
                || asistencia.getFecha() == null) {
            throw new IllegalArgumentException(
                "Debe seleccionar estudiante, curso y fecha."
            );
        }

        if (asistencia.getEstudiante().getId() <= 0
                || asistencia.getCurso().getIdCurso() <= 0) {
            throw new IllegalArgumentException(
                "El estudiante y el curso deben estar registrados."
            );
        }

        if (!"Presente".equals(asistencia.getEstado())
                && !"Ausente".equals(asistencia.getEstado())) {
            throw new IllegalArgumentException(
                "Debe seleccionar Presente o Ausente."
            );
        }
    }

    private void validarDuplicado(Asistencia asistencia,
                                   int idExcluido) {
        for (Asistencia existente : asistencias) {
            boolean mismoEstudiante =
                existente.getEstudiante().getId()
                    == asistencia.getEstudiante().getId();

            boolean mismoCurso =
                existente.getCurso().getIdCurso()
                    == asistencia.getCurso().getIdCurso();

            boolean mismaFecha =
                existente.getFecha().equals(asistencia.getFecha());

            if (existente.getIdAsistencia() != idExcluido
                    && mismoEstudiante && mismoCurso && mismaFecha) {
                throw new IllegalArgumentException(
                    "Ya existe una asistencia para ese estudiante, "
                    + "curso y fecha."
                );
            }
        }
    }
}