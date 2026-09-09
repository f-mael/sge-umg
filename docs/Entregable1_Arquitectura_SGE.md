# UNIVERSIDAD MARIANO GÁLVEZ DE GUATEMALA
### FACULTAD DE INGENIERÍA EN SISTEMAS Y CIENCIAS DE LA COMPUTACIÓN
### CAMPUS COBÁN, ALTA VERAPAZ
**Curso:** Programación II  
**Catedrático:** Ing. Docente del Curso  
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
El presente documento define la arquitectura inicial y el modelado conceptual del Sistema de Gestión Escolar (SGE). Este sistema está diseñado para solventar los requerimientos de control administrativo y académico en un establecimiento educativo de nivel medio o primario, integrando el registro de personal y estudiantes, matriculación por ciclos escolares, administración de cursos y el cálculo formal del rendimiento académico de los estudiantes mediante una arquitectura modular en Java.

---

## 2. Descripción de Módulos Funcionales

### Módulo 1: Estudiantes e Inscripciones
Gestiona los expedientes personales y académicos de los alumnos matriculados. Permite realizar el alta, baja, modificación y consulta de alumnos, así como la vinculación obligatoria de su matrícula activa a un ciclo lectivo y grado específico a través del registro de inscripciones.

### Módulo 2: Cursos y Grados/Secciones
Administra la oferta académica institucional. Organiza la división física y curricular por niveles (Primaria, Básico, Diversificado), nombres de grado y secciones alfabéticas, sirviendo de base para la asignación formal de las asignaturas que componen el pénsum.

### Módulo 3: Docentes y Asignación de Cursos
Controla los datos del claustro de profesores, registrando perfiles profesionales, especialidades pedagógicas y el vínculo contractual con las materias que tienen a su cargo en los distintos grados y secciones.

### Módulo 4: Calificaciones y Asistencia (Relación Maestro-Detalle)
Supervisa el rendimiento académico y la permanencia del estudiante en las aulas. Integra el control de asistencia diaria por curso y gestiona las boletas oficiales de calificaciones mediante una estructura maestro-detalle, donde cada boleta consolida las calificaciones desglosadas por materia y calcula automáticamente el promedio general del estudiante.

### Módulo 5: Padres o Encargados
Registra la información legal y de contacto de los tutores o encargados responsables de cada estudiante, asegurando la trazabilidad de la patria potestad y canales directos de notificación escolar.

---

## 3. Catálogo de Entidades del Sistema (10 Entidades Base)

| # | Entidad | Tipo | Atributos Principales | Descripción del Rol |
|---|---|---|---|---|
| 1 | **Persona** | Clase Base (Abstracta) | `id`, `nombre`, `apellido`, `telefono`, `email` | Superclase que consolida los atributos comunes y promueve la reutilización de código mediante herencia. |
| 2 | **Estudiante** | Especialización | `carnet`, `fechaNacimiento`, `idEncargado` | Representa al alumno matriculado; hereda de `Persona` y vincula a su responsable legal. |
| 3 | **Docente** | Especialización | `codigoEmpleado`, `especialidad` | Representa al catedrático titular; hereda de `Persona` y se vincula como instructor de cursos. |
| 4 | **Encargado** | Especialización | `cui`, `parentesco`, `direccion` | Representa al padre de familia o tutor legal del alumno; hereda de `Persona`. |
| 5 | **CicloEscolar** | Catálogo Operativo | `idCiclo`, `anio`, `fechaInicio`, `fechaFin`, `activo` | Delimita el período administrativo y calendario lectivo anual sobre el cual operan las inscripciones y boletas. |
| 6 | **GradoSeccion** | Catálogo Académico | `idGrado`, `nombre`, `seccion`, `nivel` | Define el aula y nivel académico de agrupación estudiantil (ej. 1ro Básico Sección A). |
| 7 | **Curso** | Núcleo Académico | `idCurso`, `nombre`, `idDocente`, `idGrado` | Asignatura curricular asignada a un grado específico e impartida por un docente titular. |
| 8 | **Inscripcion** | Transaccional | `idInscripcion`, `idEstudiante`, `idGrado`, `idCiclo`, `fechaInscripcion`, `estado` | Formaliza la matrícula del alumno en un grado y ciclo escolar determinado. |
| 9 | **Asistencia** | Control Diario | `idAsistencia`, `idEstudiante`, `idCurso`, `fecha`, `estado` | Bitácora de asistencia periódica (Presente, Ausente, Justificado) del estudiante por clase. |
| 10 | **BoletaCalificaciones** | Transaccional Maestro | `idBoleta`, `idEstudiante`, `idCiclo`, `periodo`, `fechaEmision`, `promedio` | Encabezado maestro que totaliza y promedia las notas de un alumno en una unidad o bimestre. |
| 11 | **DetalleBoleta** | Transaccional Detalle | `idDetalle`, `idBoleta`, `idCurso`, `nota`, `observacion` | Línea de detalle vinculada por composición a la boleta; guarda el puntaje específico obtenido en un curso. |

---

## 4. Diagrama de Clases UML
*(Diagrama exportado en docs/diagrama-clases-uml.png)*

---

## 5. Reparto de Responsabilidades en el Repositorio
* **Franklin Tilóm:** Arquitectura del proyecto, clases `Persona`, `Estudiante`, `Docente`, `Encargado`, configuración inicial Maven/JavaFX y documentación preliminar.
* **Angel Tot:** Clases del modelo académico: `GradoSeccion`, `Curso`, `CicloEscolar` e `Inscripcion`.
* **Iván Castro:** Clases transaccionales: `Asistencia`, `BoletaCalificaciones`, `DetalleBoleta` y generación gráfica del Diagrama de Clases UML.
