# UNIVERSIDAD MARIANO GÁLVEZ DE GUATEMALA
### FACULTAD DE INGENIERÍA EN SISTEMAS Y CIENCIAS DE LA COMPUTACIÓN
### CAMPUS COBÁN, ALTA VERAPAZ
**Curso:** Programación II  
**Catedrático:** Ing. Erwin Geovani De Leon Chaman  
**Proyecto:** Sistema de Gestión Escolar (SGE) - Variante 3  
**Fase:** Primer Entregable - Arquitectura y Diagrama de Clases UML  
**Fecha:** 17 de septiembre de 2026  

---

## Integrantes del Equipo
* **Franklin Ismael Tilóm Cojoc** - [@f-mael](https://github.com/f-mael)
* **Angel Tot** - [@AngelTot09](https://github.com/AngelTot09)
* **Iván Castro** - [@POGO149149](https://github.com/POGO149149)

**Repositorio del Proyecto:** https://github.com/f-mael/sge-umg

---

## 1. Introducción
El presente documento define la arquitectura inicial y el modelado conceptual del Sistema de Gestión Escolar (SGE). Este sistema está diseñado para solventar los requerimientos de control administrativo y académico en un establecimiento educativo de nivel medio o primario, integrando el registro de personal y estudiantes, matriculación por ciclos escolares, administración de cursos y el cálculo formal del rendimiento académico de los estudiantes mediante una arquitectura modular en Java[span_0](start_span)[span_0](end_span).

---

## 2. Descripción de Módulos Funcionales

### Módulo 1: Estudiantes e Inscripciones
Gestiona los expedientes personales y académicos de los alumnos matriculados[span_1](start_span)[span_1](end_span). Permite realizar el alta, baja, modificación y consulta de alumnos, así como la vinculación obligatoria de su matrícula activa a un ciclo lectivo y grado específico a través del registro de inscripciones[span_2](start_span)[span_2](end_span).

### Módulo 2: Cursos y Grados/Secciones
Administra la oferta académica institucional[span_3](start_span)[span_3](end_span). Organiza la división física y curricular por niveles (Primaria, Básico, Diversificado), nombres de grado y secciones alfabéticas, sirviendo de base para la asignación formal de las asignaturas que componen el pénsum[span_4](start_span)[span_4](end_span).

### Módulo 3: Docentes y Asignación de Cursos
Controla los datos del claustro de profesores, registrando perfiles profesionales, especialidades pedagógicas y el vínculo contractual con las materias que tienen a su cargo en los distintos grados y secciones[span_5](start_span)[span_5](end_span).

### Módulo 4: Calificaciones y Asistencia (Relación Maestro-Detalle)
Supervisa el rendimiento académico y la permanencia del estudiante en las aulas[span_6](start_span)[span_6](end_span). Integra el control de asistencia diaria por curso y gestiona las boletas oficiales de calificaciones mediante una estructura maestro-detalle, donde cada boleta consolida las calificaciones desglosadas por materia y calcula automáticamente el promedio general del estudiante[span_7](start_span)[span_7](end_span).

### Módulo 5: Padres o Encargados
Registra la información legal y de contacto de los tutores o encargados responsables de cada estudiante, asegurando la trazabilidad de la patria potestad y canales directos de notificación escolar[span_8](start_span)[span_8](end_span).

---

## 3. Catálogo de Entidades del Sistema (10 Entidades Base)

| # | Entidad | Tipo | Atributos Principales | Descripción del Rol |
|---|---|---|---|---|
| 1 | **Persona** | Base (Abstracta) | `id`, `nombre`, `apellido`, `telefono`, `email` | Superclase que consolida los atributos comunes y promueve la reutilización de código mediante herencia. |
| 2 | **Estudiante** | Especialización | `carnet`, `fechaNacimiento`, `idEncargado` | Representa al alumno matriculado; hereda de `Persona` y vincula a su responsable legal[span_9](start_span)[span_9](end_span). |
| 3 | **Docente** | Especialización | `codigoEmpleado`, `especialidad` | Representa al catedrático titular; hereda de `Persona` y se vincula como instructor de cursos[span_10](start_span)[span_10](end_span). |
| 4 | **Encargado** | Especialización | `cui`, `parentesco`, `direccion` | Representa al padre de familia o tutor legal del alumno; hereda de `Persona`[span_11](start_span)[span_11](end_span). |
| 5 | **CicloEscolar** | Catálogo Operativo | `idCiclo`, `anio`, `fechaInicio`, `fechaFin`, `activo` | Delimita el período administrativo y calendario lectivo anual sobre el cual operan las inscripciones y boletas[span_12](start_span)[span_12](end_span). |
| 6 | **GradoSeccion** | Catálogo Académico | `idGrado`, `nombre`, `seccion`, `nivel` | Define el aula y nivel académico de agrupación estudiantil (ej. 1ro Básico Sección A)[span_13](start_span)[span_13](end_span). |
| 7 | **Curso** | Núcleo Académico | `idCurso`, `nombre`, `idDocente`, `idGrado` | Asignatura curricular asignada a un grado específico e impartida por un docente titular[span_14](start_span)[span_14](end_span). |
| 8 | **Inscripcion** | Transaccional | `idInscripcion`, `idEstudiante`, `idGrado`, `idCiclo`, `fechaInscripcion`, `estado` | Formaliza la matrícula del alumno en un grado y ciclo escolar determinado[span_15](start_span)[span_15](end_span). |
| 9 | **Asistencia** | Control Diario | `idAsistencia`, `idEstudiante`, `idCurso`, `fecha`, `estado` | Bitácora de asistencia periódica (Presente, Ausente, Justificado) del estudiante por clase[span_16](start_span)[span_16](end_span). |
| 10 | **BoletaCalificaciones** | Transaccional Maestro | `idBoleta`, `idEstudiante`, `idCiclo`, `periodo`, `fechaEmision`, `promedio` | Encabezado maestro que totaliza y promedia las notas de un alumno en una unidad o bimestre[span_17](start_span)[span_17](end_span). |
| 11 | **DetalleBoleta** | Transaccional Detalle | `idDetalle`, `idBoleta`, `idCurso`, `nota`, `observacion` | Línea de detalle vinculada por composición a la boleta; guarda el puntaje específico obtenido en un curso[span_18](start_span)[span_18](end_span). |

---

## 4. Diagrama de Clases UML
*(Diagrama exportado en docs/diagrama-clases-uml.png)*

---

## 5. Reparto de Responsabilidades en el Repositorio
* **Franklin Tilóm:** Arquitectura del proyecto, clases `Persona`, `Estudiante`, `Docente`, `Encargado`, configuración inicial Maven/JavaFX y documentación preliminar[span_19](start_span)[span_19](end_span).
* **Angel Tot:** Clases del modelo académico: `GradoSeccion`, `Curso`, `CicloEscolar` e `Inscripcion`[span_20](start_span)[span_20](end_span).
* **Iván Castro:** Clases transaccionales: `Asistencia`, `BoletaCalificaciones`, `DetalleBoleta` y generación gráfica del Diagrama de Clases UML[span_21](start_span)[span_21](end_span).
