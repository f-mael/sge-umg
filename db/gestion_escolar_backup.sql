--
-- PostgreSQL database dump
--

\restrict NxudZ6Lbbd6d63x17OHHeboNH0xf3S8Foim6AN1OcrQjJvo0LWVX8ouyM61bsUw

-- Dumped from database version 16.15
-- Dumped by pg_dump version 16.15

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

ALTER TABLE IF EXISTS ONLY public.inscripciones DROP CONSTRAINT IF EXISTS fk_inscripciones_grado;
ALTER TABLE IF EXISTS ONLY public.inscripciones DROP CONSTRAINT IF EXISTS fk_inscripciones_ciclo;
ALTER TABLE IF EXISTS ONLY public.estudiantes DROP CONSTRAINT IF EXISTS fk_estudiantes_encargado;
ALTER TABLE IF EXISTS ONLY public.detalles_boletas DROP CONSTRAINT IF EXISTS fk_detalles_curso;
ALTER TABLE IF EXISTS ONLY public.detalles_boletas DROP CONSTRAINT IF EXISTS fk_detalles_boleta;
ALTER TABLE IF EXISTS ONLY public.boletas_calificaciones DROP CONSTRAINT IF EXISTS fk_boletas_estudiante;
ALTER TABLE IF EXISTS ONLY public.boletas_calificaciones DROP CONSTRAINT IF EXISTS fk_boletas_ciclo;
ALTER TABLE IF EXISTS ONLY public.asistencias DROP CONSTRAINT IF EXISTS fk_asistencias_estudiante;
ALTER TABLE IF EXISTS ONLY public.asistencias DROP CONSTRAINT IF EXISTS fk_asistencias_curso;
ALTER TABLE IF EXISTS ONLY public.cursos DROP CONSTRAINT IF EXISTS cursos_id_grado_seccion_fkey;
ALTER TABLE IF EXISTS ONLY public.detalles_boletas DROP CONSTRAINT IF EXISTS uq_detalle_boleta_curso;
ALTER TABLE IF EXISTS ONLY public.boletas_calificaciones DROP CONSTRAINT IF EXISTS uq_boleta_estudiante_ciclo_periodo;
ALTER TABLE IF EXISTS ONLY public.asistencias DROP CONSTRAINT IF EXISTS uq_asistencia_estudiante_curso_fecha;
ALTER TABLE IF EXISTS ONLY public.inscripciones DROP CONSTRAINT IF EXISTS inscripciones_pkey;
ALTER TABLE IF EXISTS ONLY public.grados_secciones DROP CONSTRAINT IF EXISTS grados_secciones_pkey;
ALTER TABLE IF EXISTS ONLY public.estudiantes DROP CONSTRAINT IF EXISTS estudiantes_pkey;
ALTER TABLE IF EXISTS ONLY public.estudiantes DROP CONSTRAINT IF EXISTS estudiantes_carnet_key;
ALTER TABLE IF EXISTS ONLY public.encargados DROP CONSTRAINT IF EXISTS encargados_pkey;
ALTER TABLE IF EXISTS ONLY public.encargados DROP CONSTRAINT IF EXISTS encargados_cui_key;
ALTER TABLE IF EXISTS ONLY public.docentes DROP CONSTRAINT IF EXISTS docentes_pkey;
ALTER TABLE IF EXISTS ONLY public.docentes DROP CONSTRAINT IF EXISTS docentes_codigo_empleado_key;
ALTER TABLE IF EXISTS ONLY public.detalles_boletas DROP CONSTRAINT IF EXISTS detalles_boletas_pkey;
ALTER TABLE IF EXISTS ONLY public.cursos DROP CONSTRAINT IF EXISTS cursos_pkey;
ALTER TABLE IF EXISTS ONLY public.ciclos_escolares DROP CONSTRAINT IF EXISTS ciclos_escolares_pkey;
ALTER TABLE IF EXISTS ONLY public.boletas_calificaciones DROP CONSTRAINT IF EXISTS boletas_calificaciones_pkey;
ALTER TABLE IF EXISTS ONLY public.asistencias DROP CONSTRAINT IF EXISTS asistencias_pkey;
ALTER TABLE IF EXISTS public.inscripciones ALTER COLUMN id_inscripcion DROP DEFAULT;
ALTER TABLE IF EXISTS public.grados_secciones ALTER COLUMN id_grado_seccion DROP DEFAULT;
ALTER TABLE IF EXISTS public.estudiantes ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.encargados ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.docentes ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.detalles_boletas ALTER COLUMN id_detalle DROP DEFAULT;
ALTER TABLE IF EXISTS public.cursos ALTER COLUMN id_curso DROP DEFAULT;
ALTER TABLE IF EXISTS public.ciclos_escolares ALTER COLUMN id_ciclo DROP DEFAULT;
ALTER TABLE IF EXISTS public.boletas_calificaciones ALTER COLUMN id_boleta DROP DEFAULT;
ALTER TABLE IF EXISTS public.asistencias ALTER COLUMN id_asistencia DROP DEFAULT;
DROP SEQUENCE IF EXISTS public.inscripciones_id_inscripcion_seq;
DROP TABLE IF EXISTS public.inscripciones;
DROP SEQUENCE IF EXISTS public.grados_secciones_id_grado_seccion_seq;
DROP TABLE IF EXISTS public.grados_secciones;
DROP SEQUENCE IF EXISTS public.estudiantes_id_seq;
DROP TABLE IF EXISTS public.estudiantes;
DROP SEQUENCE IF EXISTS public.encargados_id_seq;
DROP TABLE IF EXISTS public.encargados;
DROP SEQUENCE IF EXISTS public.docentes_id_seq;
DROP TABLE IF EXISTS public.docentes;
DROP SEQUENCE IF EXISTS public.detalles_boletas_id_detalle_seq;
DROP TABLE IF EXISTS public.detalles_boletas;
DROP SEQUENCE IF EXISTS public.cursos_id_curso_seq;
DROP TABLE IF EXISTS public.cursos;
DROP SEQUENCE IF EXISTS public.ciclos_escolares_id_ciclo_seq;
DROP TABLE IF EXISTS public.ciclos_escolares;
DROP SEQUENCE IF EXISTS public.boletas_calificaciones_id_boleta_seq;
DROP TABLE IF EXISTS public.boletas_calificaciones;
DROP SEQUENCE IF EXISTS public.asistencias_id_asistencia_seq;
DROP TABLE IF EXISTS public.asistencias;
SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: asistencias; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.asistencias (
    id_asistencia integer NOT NULL,
    id_estudiante integer NOT NULL,
    id_curso integer NOT NULL,
    fecha date NOT NULL,
    estado character varying(10) NOT NULL,
    CONSTRAINT chk_asistencias_estado CHECK (((estado)::text = ANY ((ARRAY['Presente'::character varying, 'Ausente'::character varying])::text[])))
);


--
-- Name: asistencias_id_asistencia_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.asistencias_id_asistencia_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: asistencias_id_asistencia_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.asistencias_id_asistencia_seq OWNED BY public.asistencias.id_asistencia;


--
-- Name: boletas_calificaciones; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.boletas_calificaciones (
    id_boleta integer NOT NULL,
    id_estudiante integer NOT NULL,
    id_ciclo integer NOT NULL,
    periodo character varying(100) NOT NULL,
    fecha_emision date NOT NULL,
    promedio numeric(5,2) DEFAULT 0 NOT NULL,
    CONSTRAINT chk_boletas_promedio CHECK (((promedio >= (0)::numeric) AND (promedio <= (100)::numeric)))
);


--
-- Name: boletas_calificaciones_id_boleta_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.boletas_calificaciones_id_boleta_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: boletas_calificaciones_id_boleta_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.boletas_calificaciones_id_boleta_seq OWNED BY public.boletas_calificaciones.id_boleta;


--
-- Name: ciclos_escolares; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ciclos_escolares (
    id_ciclo integer NOT NULL,
    anio integer NOT NULL,
    fecha_inicio date NOT NULL,
    fecha_fin date NOT NULL,
    activo boolean DEFAULT true NOT NULL
);


--
-- Name: ciclos_escolares_id_ciclo_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.ciclos_escolares_id_ciclo_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ciclos_escolares_id_ciclo_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.ciclos_escolares_id_ciclo_seq OWNED BY public.ciclos_escolares.id_ciclo;


--
-- Name: cursos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cursos (
    id_curso integer NOT NULL,
    nombre character varying(100) NOT NULL,
    id_docente integer NOT NULL,
    id_grado_seccion integer NOT NULL
);


--
-- Name: cursos_id_curso_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cursos_id_curso_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cursos_id_curso_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.cursos_id_curso_seq OWNED BY public.cursos.id_curso;


--
-- Name: detalles_boletas; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.detalles_boletas (
    id_detalle integer NOT NULL,
    id_boleta integer NOT NULL,
    id_curso integer NOT NULL,
    nota numeric(5,2) NOT NULL,
    observacion character varying(255),
    peso numeric(10,2) DEFAULT 1 NOT NULL,
    CONSTRAINT chk_detalles_nota CHECK (((nota >= (0)::numeric) AND (nota <= (100)::numeric))),
    CONSTRAINT chk_detalles_peso CHECK ((peso > (0)::numeric))
);


--
-- Name: detalles_boletas_id_detalle_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.detalles_boletas_id_detalle_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: detalles_boletas_id_detalle_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.detalles_boletas_id_detalle_seq OWNED BY public.detalles_boletas.id_detalle;


--
-- Name: docentes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.docentes (
    id integer NOT NULL,
    nombre character varying(100) NOT NULL,
    apellido character varying(100) NOT NULL,
    telefono character varying(20),
    email character varying(100),
    codigo_empleado character varying(20) NOT NULL,
    especialidad character varying(100) NOT NULL
);


--
-- Name: docentes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.docentes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: docentes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.docentes_id_seq OWNED BY public.docentes.id;


--
-- Name: encargados; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.encargados (
    id integer NOT NULL,
    nombre character varying(100) NOT NULL,
    apellido character varying(100) NOT NULL,
    telefono character varying(20),
    email character varying(100),
    cui character varying(20) NOT NULL,
    parentesco character varying(50) NOT NULL,
    direccion character varying(255) NOT NULL
);


--
-- Name: encargados_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.encargados_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: encargados_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.encargados_id_seq OWNED BY public.encargados.id;


--
-- Name: estudiantes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.estudiantes (
    id integer NOT NULL,
    nombre character varying(100) NOT NULL,
    apellido character varying(100) NOT NULL,
    telefono character varying(20),
    email character varying(100),
    carnet character varying(20) NOT NULL,
    fecha_nacimiento date NOT NULL,
    id_encargado integer
);


--
-- Name: estudiantes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.estudiantes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: estudiantes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.estudiantes_id_seq OWNED BY public.estudiantes.id;


--
-- Name: grados_secciones; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grados_secciones (
    id_grado_seccion integer NOT NULL,
    nombre character varying(100) NOT NULL,
    nivel character varying(50) NOT NULL,
    seccion character(1) NOT NULL
);


--
-- Name: grados_secciones_id_grado_seccion_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grados_secciones_id_grado_seccion_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: grados_secciones_id_grado_seccion_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.grados_secciones_id_grado_seccion_seq OWNED BY public.grados_secciones.id_grado_seccion;


--
-- Name: inscripciones; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.inscripciones (
    id_inscripcion integer NOT NULL,
    fecha_inscripcion date NOT NULL,
    id_estudiante integer NOT NULL,
    id_grado_seccion integer NOT NULL,
    id_ciclo integer NOT NULL
);


--
-- Name: inscripciones_id_inscripcion_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.inscripciones_id_inscripcion_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: inscripciones_id_inscripcion_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.inscripciones_id_inscripcion_seq OWNED BY public.inscripciones.id_inscripcion;


--
-- Name: asistencias id_asistencia; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.asistencias ALTER COLUMN id_asistencia SET DEFAULT nextval('public.asistencias_id_asistencia_seq'::regclass);


--
-- Name: boletas_calificaciones id_boleta; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.boletas_calificaciones ALTER COLUMN id_boleta SET DEFAULT nextval('public.boletas_calificaciones_id_boleta_seq'::regclass);


--
-- Name: ciclos_escolares id_ciclo; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ciclos_escolares ALTER COLUMN id_ciclo SET DEFAULT nextval('public.ciclos_escolares_id_ciclo_seq'::regclass);


--
-- Name: cursos id_curso; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cursos ALTER COLUMN id_curso SET DEFAULT nextval('public.cursos_id_curso_seq'::regclass);


--
-- Name: detalles_boletas id_detalle; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detalles_boletas ALTER COLUMN id_detalle SET DEFAULT nextval('public.detalles_boletas_id_detalle_seq'::regclass);


--
-- Name: docentes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.docentes ALTER COLUMN id SET DEFAULT nextval('public.docentes_id_seq'::regclass);


--
-- Name: encargados id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.encargados ALTER COLUMN id SET DEFAULT nextval('public.encargados_id_seq'::regclass);


--
-- Name: estudiantes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.estudiantes ALTER COLUMN id SET DEFAULT nextval('public.estudiantes_id_seq'::regclass);


--
-- Name: grados_secciones id_grado_seccion; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grados_secciones ALTER COLUMN id_grado_seccion SET DEFAULT nextval('public.grados_secciones_id_grado_seccion_seq'::regclass);


--
-- Name: inscripciones id_inscripcion; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.inscripciones ALTER COLUMN id_inscripcion SET DEFAULT nextval('public.inscripciones_id_inscripcion_seq'::regclass);


--
-- Data for Name: asistencias; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.asistencias (id_asistencia, id_estudiante, id_curso, fecha, estado) FROM stdin;
1	1	1	2026-11-03	Presente
\.


--
-- Data for Name: boletas_calificaciones; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.boletas_calificaciones (id_boleta, id_estudiante, id_ciclo, periodo, fecha_emision, promedio) FROM stdin;
1	1	1	Primer Bimestte	2026-11-03	90.00
\.


--
-- Data for Name: ciclos_escolares; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.ciclos_escolares (id_ciclo, anio, fecha_inicio, fecha_fin, activo) FROM stdin;
1	2026	2026-01-15	2026-10-30	t
\.


--
-- Data for Name: cursos; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.cursos (id_curso, nombre, id_docente, id_grado_seccion) FROM stdin;
1	Matematica	1	1
\.


--
-- Data for Name: detalles_boletas; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.detalles_boletas (id_detalle, id_boleta, id_curso, nota, observacion, peso) FROM stdin;
1	1	1	90.00		1.00
\.


--
-- Data for Name: docentes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.docentes (id, nombre, apellido, telefono, email, codigo_empleado, especialidad) FROM stdin;
1	Ana	Garcia	55556896	ana.garcia@umg.edu.gt	DOC-001	Matematica
\.


--
-- Data for Name: encargados; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.encargados (id, nombre, apellido, telefono, email, cui, parentesco, direccion) FROM stdin;
\.


--
-- Data for Name: estudiantes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.estudiantes (id, nombre, apellido, telefono, email, carnet, fecha_nacimiento, id_encargado) FROM stdin;
1	Carlos	Lopez	55551685	carlos@umg.edu.gt	2026	2005-05-15	\N
\.


--
-- Data for Name: grados_secciones; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.grados_secciones (id_grado_seccion, nombre, nivel, seccion) FROM stdin;
1	Primero Basico	Basico	A
\.


--
-- Data for Name: inscripciones; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.inscripciones (id_inscripcion, fecha_inscripcion, id_estudiante, id_grado_seccion, id_ciclo) FROM stdin;
1	2026-11-03	1	1	1
\.


--
-- Name: asistencias_id_asistencia_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.asistencias_id_asistencia_seq', 1, true);


--
-- Name: boletas_calificaciones_id_boleta_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.boletas_calificaciones_id_boleta_seq', 1, true);


--
-- Name: ciclos_escolares_id_ciclo_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.ciclos_escolares_id_ciclo_seq', 1, true);


--
-- Name: cursos_id_curso_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.cursos_id_curso_seq', 1, true);


--
-- Name: detalles_boletas_id_detalle_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.detalles_boletas_id_detalle_seq', 1, true);


--
-- Name: docentes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.docentes_id_seq', 1, true);


--
-- Name: encargados_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.encargados_id_seq', 1, false);


--
-- Name: estudiantes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.estudiantes_id_seq', 1, true);


--
-- Name: grados_secciones_id_grado_seccion_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grados_secciones_id_grado_seccion_seq', 1, true);


--
-- Name: inscripciones_id_inscripcion_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.inscripciones_id_inscripcion_seq', 1, true);


--
-- Name: asistencias asistencias_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.asistencias
    ADD CONSTRAINT asistencias_pkey PRIMARY KEY (id_asistencia);


--
-- Name: boletas_calificaciones boletas_calificaciones_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.boletas_calificaciones
    ADD CONSTRAINT boletas_calificaciones_pkey PRIMARY KEY (id_boleta);


--
-- Name: ciclos_escolares ciclos_escolares_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ciclos_escolares
    ADD CONSTRAINT ciclos_escolares_pkey PRIMARY KEY (id_ciclo);


--
-- Name: cursos cursos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cursos
    ADD CONSTRAINT cursos_pkey PRIMARY KEY (id_curso);


--
-- Name: detalles_boletas detalles_boletas_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detalles_boletas
    ADD CONSTRAINT detalles_boletas_pkey PRIMARY KEY (id_detalle);


--
-- Name: docentes docentes_codigo_empleado_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.docentes
    ADD CONSTRAINT docentes_codigo_empleado_key UNIQUE (codigo_empleado);


--
-- Name: docentes docentes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.docentes
    ADD CONSTRAINT docentes_pkey PRIMARY KEY (id);


--
-- Name: encargados encargados_cui_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.encargados
    ADD CONSTRAINT encargados_cui_key UNIQUE (cui);


--
-- Name: encargados encargados_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.encargados
    ADD CONSTRAINT encargados_pkey PRIMARY KEY (id);


--
-- Name: estudiantes estudiantes_carnet_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.estudiantes
    ADD CONSTRAINT estudiantes_carnet_key UNIQUE (carnet);


--
-- Name: estudiantes estudiantes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.estudiantes
    ADD CONSTRAINT estudiantes_pkey PRIMARY KEY (id);


--
-- Name: grados_secciones grados_secciones_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grados_secciones
    ADD CONSTRAINT grados_secciones_pkey PRIMARY KEY (id_grado_seccion);


--
-- Name: inscripciones inscripciones_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.inscripciones
    ADD CONSTRAINT inscripciones_pkey PRIMARY KEY (id_inscripcion);


--
-- Name: asistencias uq_asistencia_estudiante_curso_fecha; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.asistencias
    ADD CONSTRAINT uq_asistencia_estudiante_curso_fecha UNIQUE (id_estudiante, id_curso, fecha);


--
-- Name: boletas_calificaciones uq_boleta_estudiante_ciclo_periodo; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.boletas_calificaciones
    ADD CONSTRAINT uq_boleta_estudiante_ciclo_periodo UNIQUE (id_estudiante, id_ciclo, periodo);


--
-- Name: detalles_boletas uq_detalle_boleta_curso; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detalles_boletas
    ADD CONSTRAINT uq_detalle_boleta_curso UNIQUE (id_boleta, id_curso);


--
-- Name: cursos cursos_id_grado_seccion_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cursos
    ADD CONSTRAINT cursos_id_grado_seccion_fkey FOREIGN KEY (id_grado_seccion) REFERENCES public.grados_secciones(id_grado_seccion) ON DELETE CASCADE;


--
-- Name: asistencias fk_asistencias_curso; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.asistencias
    ADD CONSTRAINT fk_asistencias_curso FOREIGN KEY (id_curso) REFERENCES public.cursos(id_curso) ON DELETE CASCADE;


--
-- Name: asistencias fk_asistencias_estudiante; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.asistencias
    ADD CONSTRAINT fk_asistencias_estudiante FOREIGN KEY (id_estudiante) REFERENCES public.estudiantes(id) ON DELETE CASCADE;


--
-- Name: boletas_calificaciones fk_boletas_ciclo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.boletas_calificaciones
    ADD CONSTRAINT fk_boletas_ciclo FOREIGN KEY (id_ciclo) REFERENCES public.ciclos_escolares(id_ciclo) ON DELETE CASCADE;


--
-- Name: boletas_calificaciones fk_boletas_estudiante; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.boletas_calificaciones
    ADD CONSTRAINT fk_boletas_estudiante FOREIGN KEY (id_estudiante) REFERENCES public.estudiantes(id) ON DELETE CASCADE;


--
-- Name: detalles_boletas fk_detalles_boleta; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detalles_boletas
    ADD CONSTRAINT fk_detalles_boleta FOREIGN KEY (id_boleta) REFERENCES public.boletas_calificaciones(id_boleta) ON DELETE CASCADE;


--
-- Name: detalles_boletas fk_detalles_curso; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.detalles_boletas
    ADD CONSTRAINT fk_detalles_curso FOREIGN KEY (id_curso) REFERENCES public.cursos(id_curso) ON DELETE CASCADE;


--
-- Name: estudiantes fk_estudiantes_encargado; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.estudiantes
    ADD CONSTRAINT fk_estudiantes_encargado FOREIGN KEY (id_encargado) REFERENCES public.encargados(id) ON DELETE SET NULL;


--
-- Name: inscripciones fk_inscripciones_ciclo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.inscripciones
    ADD CONSTRAINT fk_inscripciones_ciclo FOREIGN KEY (id_ciclo) REFERENCES public.ciclos_escolares(id_ciclo) ON DELETE CASCADE;


--
-- Name: inscripciones fk_inscripciones_grado; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.inscripciones
    ADD CONSTRAINT fk_inscripciones_grado FOREIGN KEY (id_grado_seccion) REFERENCES public.grados_secciones(id_grado_seccion) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

\unrestrict NxudZ6Lbbd6d63x17OHHeboNH0xf3S8Foim6AN1OcrQjJvo0LWVX8ouyM61bsUw

