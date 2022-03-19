/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.sql.Connection;
import java.sql.ResultSet;
import static proyectobinarios.EstudiantesPorPrograma.obtenerIdCarreraNomenclaturaCurriculoDados;

/**
 *
 * @author Usuario
 */
public class EstudiantesNoHanVistoNRC {

    Connection CONN;
    String SEDE;
    String PROGRAMA;
    String CURRICULO;

    String NOMBRETABLATEMPORALIHSTORIALACADEMICO;
    String NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES;
    String NOMBRETABLAAPROBADOS;
    String NOMBRETABLAAPROBADOSCREDITOS;
    String NOMBRETABLATEMPORALASIGNATURAS;
    String NOMBRETABLATEMPORALHISTORIALACADEMICOEQUIVALENTESCREDITOS;
    String NOMBRETABLATEMPORALAPROBADOSTOTALCREDITOS;
    /**
     * NOMBRETABLAFINAL es el nombre de la tabla que se muestra en la ventana
     * estudiantes por programa. Tiene los campos, asignatura, idcarrera,
     * semestre, creditos alfanumerico, creditosnecesarios, NoLoHanVisto,
     * LaPuedenVer, LaReprobaronYNoloHanVisto. La estructura de su nombre es
     * "tablaFinalEstudiantesPorPrograma" + SEDE + PROGRAMA + CURRICULO;
     */
    String NOMBRETABLAFINAL;
    String NOMBRETABLAPERDIERONYNOHANAPROBADOALFANUMERICO;
    String NOMBRETABLANOHANVISTOYPUEDENVER;
    String NOMBRETABLANOHANVISTOAN;
    String NOMBRETABLANOHANVISTOANCREDITOSAPROBADOS;

    public EstudiantesNoHanVistoNRC(Connection CONN, String SEDE, String PROGRAMA, String CURRICULO) {
        this.SEDE = SEDE;
        this.PROGRAMA = PROGRAMA;
        this.CURRICULO = CURRICULO;
        this.CONN = CONN;
        inicializarTablasTemporales();

    }

    private void inicializarTablasTemporales() {
        //consulta de quienes han visto ese alfanumerico
        NOMBRETABLAFINAL = "tablaFinalEstudiantesPorPrograma" + SEDE + PROGRAMA + CURRICULO.replaceAll("-", "_");
        NOMBRETABLAPERDIERONYNOHANAPROBADOALFANUMERICO = "tablaTemporalPerdieronYNoHanAprobadoAN" + SEDE + PROGRAMA + CURRICULO.replaceAll("-", "_");
        NOMBRETABLANOHANVISTOAN = "tablaNoHanVisto" + SEDE + PROGRAMA + CURRICULO.replaceAll("-", "_");
        NOMBRETABLANOHANVISTOANCREDITOSAPROBADOS = "tablaNoHanVistoCreitosAprbados" + SEDE + PROGRAMA + CURRICULO.replaceAll("-", "_");
        NOMBRETABLANOHANVISTOYPUEDENVER = "tablaNoHanVistoPuedenVer" + SEDE + PROGRAMA + CURRICULO.replaceAll("-", "_");

        NOMBRETABLATEMPORALIHSTORIALACADEMICO
                = EstudiantesPorPrograma.
                        crearTablaTemporalHistorialAcademicoSedeCarreraCurriculoDado(CONN, SEDE, PROGRAMA, CURRICULO);

        NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES = EstudiantesPorPrograma.
                crearTablaTemporalHistorialAcademicoAlfaNumericosEquivalentes(CONN, NOMBRETABLATEMPORALIHSTORIALACADEMICO);

        NOMBRETABLATEMPORALASIGNATURAS = crearTablaTemporalAsignaturasTotalCreditosNecesarios();

        NOMBRETABLATEMPORALHISTORIALACADEMICOEQUIVALENTESCREDITOS
                = EstudiantesPorPrograma.crearTablaTemporalHistorialAcademicoCreditos(CONN, NOMBRETABLATEMPORALASIGNATURAS, NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES);

        NOMBRETABLAAPROBADOS = crearTablaTemporalAprobados();

        NOMBRETABLATEMPORALAPROBADOSTOTALCREDITOS = crearTablaTemporalIdEstudianteCreditosAprobados();

    }

    public ResultSet noHanVistoAN(String alfaNumericoAEvaluar) {
        try {
            alfaNumericoAEvaluar = alfaNumericoAEvaluar.replaceAll(" ", "");
            String sqlANE = "select "
                    + NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES
                    + ".ID_ESTUDIANTE "
                    + " from "
                    + NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES
                    + " where "
                    + NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES + ".alfanumerico='" + alfaNumericoAEvaluar + "'"
                    + " or "
                    + NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES + ".alfanumericoequivalente='" + alfaNumericoAEvaluar + "'";

            String sqlNoHanVisto = " select * from "
                    + NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES
                    + " where "
                    + NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES
                    + ".ID_ESTUDIANTE "
                    + " not in "
                    + "("
                    + sqlANE
                    + ") "
                    + " AND "
                    + " " + ConsultasSQL.COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL + " IS NOT NULL "
                    + "group by "
                    + NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES
                    + ".ID_ESTUDIANTE ";

            ResultSet rsnolohanvisto = CONN.createStatement().executeQuery(sqlNoHanVisto);

            return rsnolohanvisto;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int creditosNecesarios(String nombreTablaCreditosNecesarios, String alfaNumericoAEvaluar) {
        try {
            alfaNumericoAEvaluar = alfaNumericoAEvaluar.replaceAll(" ", "");
            String sqlCreditosNecesariosAlfaNumericoEvaluar = "select "
                    + "creditosnecesarios "
                    + " from "
                    + nombreTablaCreditosNecesarios
                    + " where "
                    + "alfanumerico='" + alfaNumericoAEvaluar + "'";

            ResultSet rs = CONN.createStatement().executeQuery(sqlCreditosNecesariosAlfaNumericoEvaluar);
            rs.next();

            return rs.getInt(1);

        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo evaluar el número de créditos necesarios para el "
                    + " alfanumerico " + alfaNumericoAEvaluar, e);
        }

        return -1;
    }

    /**
     * Esta función genera una tabla temporal que muestra los estudiantes que
     * NUNCA han visto el alfanumérico a evaluar. Si un estudiante vio el
     * alfanumerico pero lo perdió, se descarta de esta tabla. Los campos de
     * esta tabla son: ID_ESTUDIANTE NOMBRE_ESTUDIANTE RECTORIA
     * DESCRIPCION_RECTORIA SEDE DESCRIPCION_SEDE FACULTA DESCRIPCION_FACULTAD
     * PROGRAMA DESCRIPCION_PROGRAMA NIVEL DESCRIPCION_NIVEL JORNADA PERIODO
     * NRCS ALFA NUMERI DESCRIPION DEFINITIVA PROMEDIO_SEM PROM_ACU FORMA_CAL
     * COMENTARIO
     *
     * alfaNumericoEquivalente
     *
     * @param alfaNumericoAEvaluar
     * @return String nombreTablaTemporal
     */
    public String crearTablaTemporalEstudiantesNoHanVistoAN(String alfaNumericoAEvaluar) {

        try {

            alfaNumericoAEvaluar = alfaNumericoAEvaluar.replaceAll(" ", "");

            String nombreTablaTemporalEstudiantesNoHanVisto
                    = NOMBRETABLANOHANVISTOAN + alfaNumericoAEvaluar;

            String sqlANE = "select "
                    + NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES
                    + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO
                    + " from "
                    + NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES
                    + " where "
                    + NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES + ".alfanumerico='" + alfaNumericoAEvaluar + "'"
                    + " or "
                    + NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES + ".alfanumericoequivalente='" + alfaNumericoAEvaluar + "'";

            String sqlNoHanVisto = " select * from "
                    + NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES
                    + " where "
                    + NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES
                    + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO
                    + " not in "
                    + "("
                    + sqlANE
                    + ") "
                    + " AND "
                    + " " + ConsultasSQL.COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL + " IS NOT NULL "
                    + " group by "
                    + NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES
                    + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO;

            CONN.createStatement().execute("create temporary table if not exists "
                    + nombreTablaTemporalEstudiantesNoHanVisto + " as " + sqlNoHanVisto);

            return nombreTablaTemporalEstudiantesNoHanVisto;
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo crear la tabla temporal de estudiantes que no han visto "
                    + " el alfanumerico " + alfaNumericoAEvaluar
                    + " para el programa " + PROGRAMA
                    + " curriculo " + CURRICULO, e);
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Depende de la tabla nombreTablaNoHanVistoAN, que es creada a través de la
     * función crearTablaTemporalEstudiantesNoHanVistoAN(alfaNumericoAEvaluar)
     * Depende de la tabla NOMBRETABLATEMPORALAPROBADOSTOTALCREDITOS
     *
     * @param alfaNumericoAEvaluar
     * @return
     */
    public String crearTablaTemporalEstudiantesNoHanVistoANCreditosAprobados(String alfaNumericoAEvaluar) {

        try {
            alfaNumericoAEvaluar = alfaNumericoAEvaluar.replaceAll(" ", "");

            String nombreTablaNoHanVistoAN = crearTablaTemporalEstudiantesNoHanVistoAN(alfaNumericoAEvaluar);
            String nombreTablaNoHanVistoANCreditosTotal
                    = NOMBRETABLANOHANVISTOANCREDITOSAPROBADOS + alfaNumericoAEvaluar;

            String sql = " create temporary table if not exists " + nombreTablaNoHanVistoANCreditosTotal + " as select "
                    + nombreTablaNoHanVistoAN + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO + ","
                    + NOMBRETABLATEMPORALAPROBADOSTOTALCREDITOS + ".creditosaprobados"
                    + " from " + nombreTablaNoHanVistoAN
                    + " left join "
                    + NOMBRETABLATEMPORALAPROBADOSTOTALCREDITOS
                    + " on "
                    + NOMBRETABLATEMPORALAPROBADOSTOTALCREDITOS + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO
                    + "="
                    + nombreTablaNoHanVistoAN + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO + " "
                    + " group by "+ nombreTablaNoHanVistoAN + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO;

            CONN.createStatement().execute(sql);

            return nombreTablaNoHanVistoANCreditosTotal;

        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo crear la tabla de quienes no han visto el alfanumerico "
                    + alfaNumericoAEvaluar
                    + " con los créditos aprobados ",
                    e);
        }
        return null;
    }

    /**
     * Depende de la tabla nombreTablaNoHanVistoAN
     *
     * @param nombreTablaNoHanVistoAN
     * @param alfaNumericoAEvaluar
     * @return String con el nombre de la tabla nombreTablaNoHanVistoYPuedenVer
     */
    public String crearTablaTemporalEStudiantesNoHanVistoYPuedenVer(String nombreTablaNoHanVistoCreditosAprbados, String alfaNumericoAEvaluar) {
        try {

//            String nombreTablaNoHanVistoAN
//                    = crearTablaTemporalEstudiantesNoHanVistoANCreditosAprobados(alfaNumericoAEvaluar);
            alfaNumericoAEvaluar = alfaNumericoAEvaluar.replaceAll(" ", "");
            String nombreTablaCreditosNecesarios = NOMBRETABLATEMPORALASIGNATURAS;

            int creditosNecesarios = creditosNecesarios(nombreTablaCreditosNecesarios, alfaNumericoAEvaluar);

            String nombreTablaNoHanVistoYPuedenVer = NOMBRETABLANOHANVISTOYPUEDENVER
                    + alfaNumericoAEvaluar;
            if (!tablaExiste(nombreTablaNoHanVistoYPuedenVer)) {
                String sql = "create temporary table "
                        + nombreTablaNoHanVistoYPuedenVer + " as select * from "
                        + nombreTablaNoHanVistoCreditosAprbados
                        + " where "
                        + " creditosaprobados>="
                        + (creditosNecesarios) + " "
                        + "group by "
                        +ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO;

                CONN.createStatement().execute(sql);
            }
            return nombreTablaNoHanVistoYPuedenVer;
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo crear la tabla de estudiantes que no han visto "
                    + "y pueden ver el alfanumerico " + alfaNumericoAEvaluar + "", e);
        }
        return null;
    }
    
    public int totalEstudiantesNoHanVistoPuedenVer(String nombreTablaNoHanVistoPuedenVer) {
        try {

            String sql = "select count(*) from "
                    + nombreTablaNoHanVistoPuedenVer;

            ResultSet rs = CONN.createStatement().executeQuery(sql);
            rs.next();

            return rs.getInt(1);
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo realizar el conteo de quienes pueden ver el "
                    + "alfanumerico ", e);
        }
        return -1;
    }
    
    /**
     * Esta función creo una tabla con el historial de sólo aprobados.
     * @return 
     */
    public String crearTablaTemporalAprobados() {

        try {
            String nombreTablaAprobados
                    = EstudiantesPorPrograma.crearTablaTemporalObtenerHistorialTemporalSegunNota(CONN,
                            NOMBRETABLATEMPORALHISTORIALACADEMICOEQUIVALENTESCREDITOS, ">=", 3);

            return nombreTablaAprobados;
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo crear la tabla temporal de aprobados para"
                    + " programa " + PROGRAMA
                    + " curriculo " + CURRICULO, e);
        }

        return null;
    }
    /**
     * Esta tabla toma todos y cada uno de los estudiantes activos y le calcula cuantos créditos
     * tiene aprobados. La tabla que acá se devuelve es indiferente si el estudiante ha perdido 
     * materias. Crea una tabla con los campos ID_ESTUDIANTE, CreditosAprobados
     * @return nombreTabla
     */
    public String crearTablaTemporalIdEstudianteCreditosAprobados() {
        try {
//            String nombreTabla = "tablaTemporalAprobadosCreditosTotal" + System.currentTimeMillis();
//            String crearTabla = " create temporary table " 
//                    + nombreTabla 
//                    + " as select *, sum(creditos) as creditosaprobados from "
//                    + NOMBRETABLAAPROBADOS
//                    + " group by " + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO;
//            CONN.createStatement().execute(crearTabla);

            String nombreTablaEstudiantesActivos = ConsultasSQL.NOMBRETABLATEMPORAL_ESTUDIANTESACTIVOS;
            String nombreTablaIDCreditos = "tablaTemporalAprobadosCreditosTotal" + this.SEDE + this.PROGRAMA + this.CURRICULO.replaceAll("-", "_");

            if (!ConsultasSQL.tablaTemporalExiste(CONN, nombreTablaIDCreditos)) {
                String sqlCrearTabla = "create temporary table  if not exists "
                        + nombreTablaIDCreditos
                        + "("
                        + "ID_ESTUDIANTE integer,"
                        + "creditosaprobados integer"
                        + ")";
                CONN.createStatement().execute(sqlCrearTabla);

//                String sql = "select CODIGO from " + nombreTablaEstudiantesActivos;
                String sql = "select CODIGO from "
                        + nombreTablaEstudiantesActivos
                        + " where "
                        + ConsultasSQL.COLUMNASEDEESTUDIANTESACTIVOSARGOS + "=" + "'" + SEDE + "' "
                        + " and "
                        + ConsultasSQL.COLUMNAPROGRAMAESTUDIANTESACTIVOSARGOS + "=" + "'" + PROGRAMA + "' ";
                ResultSet rs = CONN.createStatement().executeQuery(sql);

                while (rs.next()) {
                    int codigo = rs.getInt("CODIGO");
                    int creditosAprobados = creditosAprobadosEstudiante(codigo);

                    String insertar = "insert into "
                            + nombreTablaIDCreditos
                            + "(ID_ESTUDIANTE, "
                            + "creditosaprobados)"
                            + "values("
                            + codigo
                            + ","
                            + creditosAprobados
                            + ")"
                            + "";//agregar acá los valores

                    CONN.createStatement().execute(insertar);
                }
            }

            return nombreTablaIDCreditos;
//            return nombreTabla;
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo crear la tabla id_estudiante y créditos aprobados", e);
        }
        return null;
    }

    public int creditosAprobadosEstudiante(int idEstudiante) {

        String historialEstudiante = "select * from "
                + NOMBRETABLAAPROBADOS
                + " where "
                + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO
                + "="
                + "'" + idEstudiante + "'";

        String materiasActuales = " select "
                + NOMBRETABLATEMPORALASIGNATURAS + ".alfanumerico as alfanumericoactual,"
                + "historialestudianteaprobados.* "
                + " from "
                + NOMBRETABLATEMPORALASIGNATURAS
                + " left join "
                + "(" + historialEstudiante + ") as historialestudianteaprobados "
                + " on "
                + NOMBRETABLATEMPORALASIGNATURAS + ".alfanumerico=historialestudianteaprobados.alfanumerico "
                + "or "
                + NOMBRETABLATEMPORALASIGNATURAS + ".alfanumerico=historialestudianteaprobados.alfanumericoEquivalente ";

        String agrupadosPorAlfaNumerico = "select * from ("
                + materiasActuales
                + ") as materiasactuales "
                + " where materiasactuales." + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO + " is not null "
                + " group by materiasactuales.alfanumericoactual";

        String sumaCreditos = "select "
                + "sum(sumacreditos.creditos) as totalcreditosaprobados "
                + " from "
                + "(" + agrupadosPorAlfaNumerico + ") as sumacreditos";

        try {
            ResultSet rs = CONN.createStatement().executeQuery(sumaCreditos);
            rs.next();
            return rs.getInt(1);
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo calcular el total de créditos aprobados para "
                    + " el estudiante de id " + idEstudiante, e);
        }

        return 0;
    }

    public int totalEstudiantesNoHanVistoAN(String alfaNumericoAEvaluar) {
        int total = 0;
        try {
            alfaNumericoAEvaluar = alfaNumericoAEvaluar.replaceAll(" ", "");
            ResultSet rs = noHanVistoAN(alfaNumericoAEvaluar);
            while (rs.next()) {
                total++;
            }
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se puedo contar el total de estudiantes que no han visto el alfanumerico " + alfaNumericoAEvaluar, e);
        }

        return total;
    }

    public String crearTablaTemporalAsignaturasTotalCreditosNecesarios() {

        String tablaTemporalAsignaturas = EstudiantesPorPrograma.
                crearTablaTemporalAsignaturasCarreraCurriculoDado(CONN, PROGRAMA, CURRICULO);
        try {
            
            String sqlComprobarColumnaCreditosAprobados = 
                    " select count (*) from pragma_table_info('"
                    +tablaTemporalAsignaturas+"')"
                    + " where "
                    + "name='creditosnecesarios'";
            
            ResultSet rscomprobar = CONN.createStatement().executeQuery(sqlComprobarColumnaCreditosAprobados);
            
            rscomprobar.next();
            
            if(rscomprobar.getInt(1)==0){
                CONN.createStatement().execute("alter table "
                    + tablaTemporalAsignaturas
                    + " add column "
                    + " creditosnecesarios integer");
            }

            ResultSet rs = CONN.createStatement().executeQuery("select * from " + tablaTemporalAsignaturas);

            while (rs.next()) {

                String alfanumerico = rs.getString("alfanumerico");

                String sqlTotalCreditos = "select sum(cast(asignaturas.creditos as real)), "
                        + "asignaturas.asignatura "
                        + "from asignaturas "
                        + "where CAST(semestre AS REAL)<"
                        + "CAST((select asignaturas.semestre from asignaturas where replace(alfa,' ','') || replace(replace(numerico,'.0',''),' ','')  = "
                        + "'" + alfanumerico + "' "
                        + "and cast(asignaturas.id_carrera as real)="
                        + obtenerIdCarreraNomenclaturaCurriculoDados(CONN, PROGRAMA, CURRICULO)
                        + " limit 1) AS REAL) "
                        + "AND "
                        + "cast(asignaturas.id_carrera as real)=" + obtenerIdCarreraNomenclaturaCurriculoDados(CONN, PROGRAMA, CURRICULO);

                ResultSet rstotalcreditos = CONN.createStatement().executeQuery(sqlTotalCreditos);

                rstotalcreditos.next();
                int totalCreditos = rstotalcreditos.getInt(1);

                String sqlAgregarTotalCreditos = "update "
                        + tablaTemporalAsignaturas
                        + " set creditosNecesarios=" + totalCreditos
                        + " where "
                        + " alfanumerico ="
                        + "'" + alfanumerico + "'";

                CONN.createStatement().execute(sqlAgregarTotalCreditos);

            }
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo seleccionar toda la tabla de asignatuas", e);
        }

        return tablaTemporalAsignaturas;
    }

    public static int totalRegistrosEnTabla(Connection conn, String nombreTabla) {
        try {
            String sql = "select count(*) from " + nombreTabla;

            ResultSet rs = conn.createStatement().executeQuery(sql);
            rs.next();
            return rs.getInt(1);

        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo hacer el conteo de registros de la tabla " + nombreTabla, e);
        }
        return -1;
    }

    public static void agregarColumnaInteger(Connection conn, String nombreTabla, String nombreColumna) {
        try {
            String agregarColumnaNoLoHanVisto = " alter table "
                    + nombreTabla
                    + " add column "
                    + nombreColumna + " integer ";

            conn.createStatement().execute(agregarColumnaNoLoHanVisto);
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo agregar la columna "
                    + nombreColumna + " a la tabla " + nombreTabla, e);
        }
    }

    public boolean tablaExiste(String nombreTabla) {
        try {
            String sql = "select count(*) from sqlite_temp_master where type='table' and name='" + nombreTabla + "'";
            ResultSet rs = CONN.createStatement().executeQuery(sql);
            rs.next();
            if (rs.getInt(1) > 0) {
                return true;
            }
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo determinar si la tabla " + nombreTabla + " existe ", e);
        }
        return false;
    }

    public String crearTablaTemporalFinalNoLaHanVistoLaPuedenVer() {
        try {

            String nombreTablaTemporal = NOMBRETABLAFINAL;

            if (!tablaExiste(nombreTablaTemporal)) {
                String copiarTabla = "create temporary table if not exists "
                        + nombreTablaTemporal
                        + " as  select * from " + NOMBRETABLATEMPORALASIGNATURAS;

                CONN.createStatement().execute(copiarTabla);

                agregarColumnaInteger(CONN, nombreTablaTemporal, "NoLoHanVisto");
                agregarColumnaInteger(CONN, nombreTablaTemporal, "LaPuedenVer");
                agregarColumnaInteger(CONN, nombreTablaTemporal, "LaReprobaronYNoHanAprobado");

                ResultSet rsasignaturas = CONN
                        .createStatement()
                        .executeQuery("select * from " + NOMBRETABLATEMPORALASIGNATURAS);

                while (rsasignaturas.next()) {
                    String alfanumerico = rsasignaturas.getString("alfanumerico");

                    String nombreTablaNoHanVistoCreditosAprbados
                            = crearTablaTemporalEstudiantesNoHanVistoANCreditosAprobados(alfanumerico);
                    int numeroNoHanVisto = totalRegistrosEnTabla(CONN, nombreTablaNoHanVistoCreditosAprbados);

                    String nombreTablaNoHanVistoYpuedenVer
                            = crearTablaTemporalEStudiantesNoHanVistoYPuedenVer(nombreTablaNoHanVistoCreditosAprbados, alfanumerico);
                    int numeroNoHanVistoYPuedenVer = totalRegistrosEnTabla(CONN, nombreTablaNoHanVistoYpuedenVer);

                    String nombreTablaLaReprobaronYNoHanGanado
                            = crearTablaPerdieronYNoHanAprobadoAlfaNumerico(alfanumerico);
                    int numeroReprobadosYNoHanGanado = totalRegistrosEnTabla(CONN, nombreTablaLaReprobaronYNoHanGanado);

                    String update = " update "
                            + nombreTablaTemporal
                            + " set "
                            + " NoLoHanVisto=" + numeroNoHanVisto + ","
                            + " LaPuedenVer=" + numeroNoHanVistoYPuedenVer + ","
                            + " LaReprobaronYNoHanAprobado=" + numeroReprobadosYNoHanGanado
                            + " where "
                            + " alfanumerico='" + alfanumerico + "'";
                    CONN.createStatement().execute(update);
                }
                return nombreTablaTemporal;
            } else {
                return nombreTablaTemporal;
            }
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo crear la tabla no la han visto la pueden ver", e);
        }
        return null;
    }

    public String crearTablaPerdieronYNoHanAprobadoAlfaNumerico(String alfaNumericoAEvaluar) {
        
        alfaNumericoAEvaluar = alfaNumericoAEvaluar.replaceAll(" ", "");
        
        String nombreTablaPerdieronANYNoHAnGanado = NOMBRETABLAPERDIERONYNOHANAPROBADOALFANUMERICO + alfaNumericoAEvaluar;

        if (!tablaExiste(nombreTablaPerdieronANYNoHAnGanado)) {
            String nombreTablaHanVistoAN = "tablaTemporalHanVisto" + alfaNumericoAEvaluar + PROGRAMA + SEDE + CURRICULO.replaceAll("-", "_");;

            String sqlANE = "create temporary table  if not exists " + nombreTablaHanVistoAN + " as select "
                    + NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES
                    + ".* " //+ ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO
                    + " from "
                    + NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES
                    + " where "
                    + NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES + ".alfanumerico='" + alfaNumericoAEvaluar + "'"
                    + " or "
                    + NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES + ".alfanumericoequivalente='" + alfaNumericoAEvaluar + "'";

            try {
                CONN.createStatement().execute(sqlANE);

                String consultaTablaReprobados
                        = EstudiantesPorPrograma.consultaTablaTemporalObtenerHistorialTemporalSegunNota(CONN,
                                nombreTablaHanVistoAN, "<", 3);

                String consultaTablaAprobados
                        = EstudiantesPorPrograma.consultaTablaTemporalObtenerHistorialTemporalSegunNota(CONN,
                                nombreTablaHanVistoAN, ">=", 3);

                String sqlLeftJoinGanadoresPerdedores = "create temporary table if not exists "
                        + "leftjoin" + nombreTablaPerdieronANYNoHAnGanado
                        + " as "
                        + " select "
                        + " tablaReprobados " + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO + ", "
                        + " tablaReprobados " + "." + ConsultasSQL.COLUMNANOMBREESTUDIANTEHISTORIALACADEMICO + ", "
                        + " tablaAprobados " + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO + " as alfanumericoaprobados "
                        + " from "
                        + " (" + consultaTablaReprobados + ") as tablaReprobados "
                        + " left join "
                        + "(" + consultaTablaAprobados + ") as tablaAprobados "
                        + " on "
                        + "tablaReprobados." + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO
                        + "="
                        + "tablaAprobados." + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO;
                CONN.createStatement().execute(sqlLeftJoinGanadoresPerdedores);

                String sqlListadoNoHanGanado = "create temporary table if not exists "
                        + nombreTablaPerdieronANYNoHAnGanado
                        + " as "
                        + " select "
                        + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO + ","
                        + ConsultasSQL.COLUMNANOMBREESTUDIANTEHISTORIALACADEMICO
                        + " from "
                        + "leftjoin" + nombreTablaPerdieronANYNoHAnGanado
                        + " where "
                        + " alfanumericoaprobados is null "
                        + "group by " + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO;

                CONN.createStatement().execute(sqlListadoNoHanGanado);

            } catch (Exception e) {
                Validaciones.mostrarErroresTotal("No se pudo crear la tabla de aprobados en " + alfaNumericoAEvaluar, e);
            }
        }

        return nombreTablaPerdieronANYNoHAnGanado;

    }

}
