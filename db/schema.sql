-- ============================================================================
-- Sistema de Gestión Escolar (SGE)
-- Esquema de base de datos - PostgreSQL
--
-- Uso:
--   createdb -U postgres gestion_escolar
--   psql -U postgres -d gestion_escolar -f db/schema.sql
-- ============================================================================

CREATE TABLE IF NOT EXISTS encargados (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    cui VARCHAR(20) UNIQUE NOT NULL,
    parentesco VARCHAR(50) NOT NULL,
    direccion VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS estudiantes (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    carnet VARCHAR(20) UNIQUE NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    id_encargado INTEGER,
    CONSTRAINT fk_estudiantes_encargado
        FOREIGN KEY (id_encargado)
        REFERENCES encargados(id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS docentes (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    codigo_empleado VARCHAR(20) UNIQUE NOT NULL,
    especialidad VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS ciclos_escolares (
    id_ciclo SERIAL PRIMARY KEY,
    anio INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS grados_secciones (
    id_grado_seccion SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    nivel VARCHAR(50) NOT NULL,
    seccion CHAR(1) NOT NULL
);

CREATE TABLE IF NOT EXISTS cursos (
    id_curso SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    id_docente INT NOT NULL,
    id_grado_seccion INT NOT NULL,
    CONSTRAINT fk_cursos_docente
        FOREIGN KEY (id_docente)
        REFERENCES docentes(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_cursos_grado
        FOREIGN KEY (id_grado_seccion)
        REFERENCES grados_secciones(id_grado_seccion)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS inscripciones (
    id_inscripcion SERIAL PRIMARY KEY,
    fecha_inscripcion DATE NOT NULL,
    id_estudiante INTEGER NOT NULL,
    id_grado_seccion INTEGER NOT NULL,
    id_ciclo INTEGER NOT NULL,
    CONSTRAINT fk_inscripciones_estudiante
        FOREIGN KEY (id_estudiante)
        REFERENCES estudiantes(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_inscripciones_grado
        FOREIGN KEY (id_grado_seccion)
        REFERENCES grados_secciones(id_grado_seccion)
        ON DELETE CASCADE,
    CONSTRAINT fk_inscripciones_ciclo
        FOREIGN KEY (id_ciclo)
        REFERENCES ciclos_escolares(id_ciclo)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS asistencias (
    id_asistencia SERIAL PRIMARY KEY,
    id_estudiante INTEGER NOT NULL,
    id_curso INTEGER NOT NULL,
    fecha DATE NOT NULL,
    estado VARCHAR(10) NOT NULL,
    CONSTRAINT fk_asistencias_estudiante
        FOREIGN KEY (id_estudiante)
        REFERENCES estudiantes(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_asistencias_curso
        FOREIGN KEY (id_curso)
        REFERENCES cursos(id_curso)
        ON DELETE CASCADE,
    CONSTRAINT chk_asistencias_estado
        CHECK (estado IN ('Presente', 'Ausente')),
    CONSTRAINT uq_asistencia_estudiante_curso_fecha
        UNIQUE (id_estudiante, id_curso, fecha)
);

CREATE TABLE IF NOT EXISTS boletas_calificaciones (
    id_boleta SERIAL PRIMARY KEY,
    id_estudiante INTEGER NOT NULL,
    id_ciclo INTEGER NOT NULL,
    periodo VARCHAR(100) NOT NULL,
    fecha_emision DATE NOT NULL,
    promedio NUMERIC(5,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_boletas_estudiante
        FOREIGN KEY (id_estudiante)
        REFERENCES estudiantes(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_boletas_ciclo
        FOREIGN KEY (id_ciclo)
        REFERENCES ciclos_escolares(id_ciclo)
        ON DELETE CASCADE,
    CONSTRAINT chk_boletas_promedio
        CHECK (promedio >= 0 AND promedio <= 100),
    CONSTRAINT uq_boleta_estudiante_ciclo_periodo
        UNIQUE (id_estudiante, id_ciclo, periodo)
);

CREATE TABLE IF NOT EXISTS detalles_boletas (
    id_detalle SERIAL PRIMARY KEY,
    id_boleta INTEGER NOT NULL,
    id_curso INTEGER NOT NULL,
    nota NUMERIC(5,2) NOT NULL,
    observacion VARCHAR(255),
    peso NUMERIC(10,2) NOT NULL DEFAULT 1,
    CONSTRAINT fk_detalles_boleta
        FOREIGN KEY (id_boleta)
        REFERENCES boletas_calificaciones(id_boleta)
        ON DELETE CASCADE,
    CONSTRAINT fk_detalles_curso
        FOREIGN KEY (id_curso)
        REFERENCES cursos(id_curso)
        ON DELETE CASCADE,
    CONSTRAINT chk_detalles_nota
        CHECK (nota >= 0 AND nota <= 100),
    CONSTRAINT chk_detalles_peso
        CHECK (peso > 0),
    CONSTRAINT uq_detalle_boleta_curso
        UNIQUE (id_boleta, id_curso)
);
