package gt.edu.umg.gestionescolar.repository;

import gt.edu.umg.gestionescolar.model.BoletaCalificaciones;
import gt.edu.umg.gestionescolar.model.DetalleBoleta;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public class BoletaRepository implements Repository<BoletaCalificaciones> {
    private final List<BoletaCalificaciones> boletas = new ArrayList<>();
    private int siguienteId = 1;

    @Override
    public void save(BoletaCalificaciones boleta) {
        validar(boleta);

        if (boleta.getIdBoleta() != 0) {
            throw new IllegalArgumentException(
                "Una boleta nueva debe tener ID 0."
            );
        }

        validarDuplicado(boleta, 0);
        boleta.calcularPromedio();
        boleta.setIdBoleta(siguienteId++);
        boletas.add(boleta);
    }

    @Override
    public void update(BoletaCalificaciones boleta) {
        validar(boleta);

        for (int i = 0; i < boletas.size(); i++) {
            if (boletas.get(i).getIdBoleta() == boleta.getIdBoleta()) {
                validarDuplicado(boleta, boleta.getIdBoleta());
                boleta.calcularPromedio();
                boletas.set(i, boleta);
                return;
            }
        }

        throw new IllegalArgumentException(
            "No se encontró la boleta que desea actualizar."
        );
    }

    @Override
    public void delete(int id) {
        boletas.removeIf(b -> b.getIdBoleta() == id);
    }

    @Override
    public Optional<BoletaCalificaciones> findById(int id) {
        return boletas.stream()
            .filter(b -> b.getIdBoleta() == id)
            .findFirst();
    }

    @Override
    public List<BoletaCalificaciones> findAll() {
        return new ArrayList<>(boletas);
    }

    @Override
    public List<BoletaCalificaciones> search(String criterio) {
        String texto = criterio == null
            ? ""
            : criterio.trim().toLowerCase(Locale.ROOT);

        List<BoletaCalificaciones> resultados = new ArrayList<>();

        for (BoletaCalificaciones boleta : boletas) {
            StringBuilder datos = new StringBuilder();

            datos.append(boleta.getIdBoleta()).append(" ")
                .append(boleta.getEstudiante().getNombreCompleto())
                .append(" ")
                .append(boleta.getEstudiante().getCarnet()).append(" ")
                .append(boleta.getCicloEscolar().getAnio()).append(" ")
                .append(boleta.getPeriodo()).append(" ")
                .append(boleta.getFechaEmision());

            for (DetalleBoleta detalle : boleta.getDetalles()) {
                datos.append(" ").append(detalle.getCurso().getNombre());
            }

            if (datos.toString().toLowerCase(Locale.ROOT)
                    .contains(texto)) {
                resultados.add(boleta);
            }
        }

        return resultados;
    }

    private void validar(BoletaCalificaciones boleta) {
        if (boleta == null) {
            throw new IllegalArgumentException(
                "La boleta es obligatoria."
            );
        }

        if (boleta.getEstudiante() == null
                || boleta.getCicloEscolar() == null
                || boleta.getFechaEmision() == null) {
            throw new IllegalArgumentException(
                "Debe seleccionar estudiante, ciclo escolar y fecha."
            );
        }

        if (boleta.getEstudiante().getId() <= 0
                || boleta.getCicloEscolar().getIdCiclo() <= 0) {
            throw new IllegalArgumentException(
                "El estudiante y el ciclo deben estar registrados."
            );
        }

        if (boleta.getPeriodo() == null
                || boleta.getPeriodo().isBlank()) {
            throw new IllegalArgumentException(
                "Debe indicar el período de la boleta."
            );
        }

        List<DetalleBoleta> detalles = boleta.getDetalles();

        if (detalles.isEmpty()) {
            throw new IllegalArgumentException(
                "La boleta debe contener al menos un curso con nota."
            );
        }

        Set<Integer> cursos = new HashSet<>();

        for (DetalleBoleta detalle : detalles) {
            if (detalle == null || detalle.getCurso() == null
                    || detalle.getCurso().getIdCurso() <= 0) {
                throw new IllegalArgumentException(
                    "Cada detalle debe tener un curso registrado."
                );
            }

            if (!cursos.add(detalle.getCurso().getIdCurso())) {
                throw new IllegalArgumentException(
                    "No puede repetir un curso dentro de la boleta."
                );
            }

            if (!Double.isFinite(detalle.getNota())
                    || detalle.getNota() < 0
                    || detalle.getNota() > 100) {
                throw new IllegalArgumentException(
                    "Las notas deben estar entre 0 y 100."
                );
            }

            if (!Double.isFinite(detalle.getPeso())
                    || detalle.getPeso() <= 0) {
                throw new IllegalArgumentException(
                    "Los pesos deben ser mayores que cero."
                );
            }
        }
    }

    private void validarDuplicado(BoletaCalificaciones boleta,
                                   int idExcluido) {
        for (BoletaCalificaciones existente : boletas) {
            boolean mismoEstudiante =
                existente.getEstudiante().getId()
                    == boleta.getEstudiante().getId();

            boolean mismoCiclo =
                existente.getCicloEscolar().getIdCiclo()
                    == boleta.getCicloEscolar().getIdCiclo();

            boolean mismoPeriodo =
                existente.getPeriodo().trim().equalsIgnoreCase(
                    boleta.getPeriodo().trim()
                );

            if (existente.getIdBoleta() != idExcluido
                    && mismoEstudiante && mismoCiclo && mismoPeriodo) {
                throw new IllegalArgumentException(
                    "Ya existe una boleta para ese estudiante, "
                    + "ciclo y período."
                );
            }
        }
    }
}