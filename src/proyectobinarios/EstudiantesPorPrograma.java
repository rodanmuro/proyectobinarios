/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;

/**
 *
 * @author Usuario
 */
public class EstudiantesPorPrograma {

    public static ResultSet listadoCarreras(Connection conn) {
        try {
            String sql = "select "
                    + ConsultasSQL.COLUMNANOMENCLATURACARRERA
                    + " from " + ConsultasSQL.TABLACARRERAS
                    + " where trim(" + ConsultasSQL.COLUMNANOMENCLATURACARRERA + ")<>''"
                    + " group by " + ConsultasSQL.COLUMNANOMENCLATURACARRERA;

            return conn.createStatement().executeQuery(sql);

        } catch (Exception e) {
            Validaciones.mostrarVentanaError("Error al cargar el listado de las carreras");
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }
        return null;
    }

    public static ResultSet listadoCurriculos(Connection conn, String nomenclatura) {
        try {
            String sql = "select "
                    + ConsultasSQL.COLUMNACURRICULOCARRERA
                    + " from " + ConsultasSQL.TABLACARRERAS
                    + " where "
                    + " trim(" + ConsultasSQL.COLUMNANOMENCLATURACARRERA + ")='" + nomenclatura.trim() + "'";

            return conn.createStatement().executeQuery(sql);

        } catch (Exception e) {
            Validaciones.mostrarVentanaError("Error al cargar el listado de curriculos");
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String descripcionCarreraNomenclaturaDada(Connection conn, String nomenclatura) {
        String descripcion = "";
        try {
            String sql = "select "
                    + ConsultasSQL.COLUMNADESCRIPCIONCARRERA
                    + " from " + ConsultasSQL.TABLACARRERAS
                    + " where "
                    + " trim(" + ConsultasSQL.COLUMNANOMENCLATURACARRERA + ")='" + nomenclatura.trim() + "' limit 1";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                descripcion = rs.getString(1);
            }
        } catch (Exception e) {
            Validaciones.mostrarVentanaError("No se pudo cargar la descripción de carrera");
            Validaciones.mostrarVentanaError(e);
        }
        return descripcion;
    }

    public static int totalMatriculadosEstudiantesActivosGeneral(Connection conn, String sede, String nomenclatura, String curriculo) {
        try {
            String nombreTablaTemporalTotalEstudiantes = "totalEstudiantes" + sede + nomenclatura + curriculo.replace("-", "_");
            String sqlTablaTotalEstudiantes = "create temporary table if not exists "
                    + nombreTablaTemporalTotalEstudiantes
                    + " as "
                    + "select * from " + ConsultasSQL.TABLAESTUDIANTESACTIVOSGENERAL
                    + " where "
                    + "trim(" + ConsultasSQL.COLUMNASEDEESTUDIANTESACTIVOSGENERAL + ")='" + sede.trim() + "'"
                    + " and "
                    + "trim(" + ConsultasSQL.COLUMNAPROGRAMAESTUDIANTESACTIVOSGENERAL + ")='" + nomenclatura.trim() + "'"
                    + " and "
                    + "trim(" + ConsultasSQL.COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL + ")='" + curriculo.trim() + "'";

            conn.createStatement().execute(sqlTablaTotalEstudiantes);

            String sql = "select count(*) from " + nombreTablaTemporalTotalEstudiantes;

            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            Validaciones.mostrarVentanaError("No se pudo cargar el total de matriculados general");
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }
        return 0;
    }

    public static String crearTablaTemporalArgosActivos(Connection conn, String sede, String nomenclatura, String curriculo) {
        String nombreTablaArgosActivos = "";
        try {

            //Se crea una tabla temporal de estudiantes activos que pertenezcan a un programa
            //tomando los estudiantes de los activos en argos (acá aún no de los activos general)
            nombreTablaArgosActivos = "tablaArgosActivos_" + sede + nomenclatura + curriculo.replaceAll("-", "_");
            String sqlCrearTablaTemporalArgosActivos = "create temporary table if not exists "
                    + nombreTablaArgosActivos + " as "
                    + "select "
                    + " cast(" + ConsultasSQL.COLUMNACODIGOESTUDIANTESACTIVOSARGOS + " as real) as CODIGO, "
                    + " " + ConsultasSQL.COLUMNANOMBRESESTUDIANTESACTIVOSARGOS + ", "
                    + " " + ConsultasSQL.COLUMNAPROGRAMAESTUDIANTESACTIVOSARGOS + ", "
                    + " " + ConsultasSQL.COLUMNASEDEESTUDIANTESACTIVOSARGOS + " "
                    + " FROM " + ConsultasSQL.TABLAESTUDIANTESACTIVOSNRC
                    + " WHERE trim(" + ConsultasSQL.COLUMNASEDEESTUDIANTESACTIVOSARGOS + ")='" + sede.trim() + "'"
                    + " and "
                    + " trim(" + ConsultasSQL.COLUMNAPROGRAMAESTUDIANTESACTIVOSARGOS + ")='" + nomenclatura.trim() + "' "
                    + " group by CODIGO order by " + ConsultasSQL.COLUMNANOMBRESESTUDIANTESACTIVOSARGOS;
            conn.createStatement().execute(sqlCrearTablaTemporalArgosActivos);

            //De la tabla de matriculados general, se toman sólo el código y el currículo_1
            //y que pertenezcan al programa dado
            String nombreTablaCodigoCurriculo = "activosGeneralCodigoCurriculo" + sede + nomenclatura + curriculo.replaceAll("-", "_");
            String sqlActivosGeneralCodigoCurriculo = "create temporary table if not exists "
                    + nombreTablaCodigoCurriculo
                    + " as  "
                    + "select "
                    + " cast(" + ConsultasSQL.TABLAESTUDIANTESACTIVOSGENERAL + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEACTIVOSGENERAL + " as real) as CODIGO, "
                    + ConsultasSQL.TABLAESTUDIANTESACTIVOSGENERAL + "." + ConsultasSQL.COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL
                    + " from "
                    + ConsultasSQL.TABLAESTUDIANTESACTIVOSGENERAL
                    + " where "
                    + ConsultasSQL.TABLAESTUDIANTESACTIVOSGENERAL + "." + ConsultasSQL.COLUMNAPROGRAMAESTUDIANTESACTIVOSGENERAL + "='" + nomenclatura + "'"
                    + " and "
                    + ConsultasSQL.TABLAESTUDIANTESACTIVOSGENERAL + "." + ConsultasSQL.COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL + "='" + curriculo + "'";
            conn.createStatement().execute(sqlActivosGeneralCodigoCurriculo);

            //Se una la primera tabla de activos de argos, con la de estudiantes general
            //teniendo en común el código, con el fin de agregar el currículo que se saca de los activos general
            String sqlActivosMasCurriculo = "create temporary table if not exists "
                    + nombreTablaArgosActivos + "curriculo as "
                    + " select "
                    + nombreTablaArgosActivos + ".*, "
                    + nombreTablaCodigoCurriculo + "." + ConsultasSQL.COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL
                    + " from "
                    + nombreTablaArgosActivos
                    + " left join "
                    + nombreTablaCodigoCurriculo
                    + " on "
                    + nombreTablaCodigoCurriculo + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEACTIVOSGENERAL
                    + "="
                    + nombreTablaArgosActivos + ".CODIGO";
            conn.createStatement().execute(sqlActivosMasCurriculo);

            //como se hace un left join
            //algunos activos que están en el argos no aparecen en el general
            //por lo cual aparecen en nulo
            //acá se quita el nulo
            String sqlDepurarCurriculoBlanco = "create temporary table if not exists "
                    + nombreTablaArgosActivos + "curriculoNoNulo as "
                    + " select * from "
                    + nombreTablaArgosActivos + "curriculo "
                    + " where "
                    + nombreTablaArgosActivos + "curriculo." + ConsultasSQL.COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL
                    + " is not null";
            conn.createStatement().execute(sqlDepurarCurriculoBlanco);

            return nombreTablaArgosActivos + "curriculoNoNulo";

        } catch (Exception e) {
            Validaciones.mostrarVentanaError("No se pudo crear la tabla temporal argos activos");
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }
        return nombreTablaArgosActivos;
    }

    /**
     * Esta tabla toma la tabla general de estudiantes activos (la tabla a nivel
     * nacional) y de la misma toma aquellos estudiantes que sean UG o LI Crea
     * la tabla con los campos CODIGO y CURRICULO, que en la tabla general se
     * llama CURRICULO_1
     *
     * @param conn
     * @return
     */
    public static String crearTablaTemporalEstudiantesActivosGeneralCodigoCurriculo(Connection conn) {
        String nombreTablaTemporal = "";
        try {
            nombreTablaTemporal = "tablaTemporalEstudiantesGeneralCodigoCurriculo";
            String sqlCrearTablaTemporal = "create temporary table if not exists "
                    + nombreTablaTemporal + " as "
                    + " select cast(" + ConsultasSQL.COLUMNACODIGOESTUDIANTEACTIVOSGENERAL + " as real) as CODIGO,"
                    + " " + ConsultasSQL.COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL + " "
                    + " from " + ConsultasSQL.TABLAESTUDIANTESACTIVOSGENERAL
                    + " where "
                    + " " + ConsultasSQL.COLUMNANIVELESTUDIANTESACTIVOSGENERAL + "='UG'"
                    + " or "
                    + " " + ConsultasSQL.COLUMNANIVELESTUDIANTESACTIVOSGENERAL + "='LI'";

            conn.createStatement().execute(sqlCrearTablaTemporal);
        } catch (Exception e) {
            Validaciones.mostrarVentanaError("No se pudo crear la tabla temporal estudiantes activos general codigo curriculo");
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }
        return nombreTablaTemporal;
    }

    public static int obtenerIdCarreraNomenclaturaCurriculoDados(Connection conn, String programa, String curriculo) {
        int id = -1;
        try {
            String sql = "select cast(id_carrera as real) from carreras where "
                    + " trim(nomenclatura)='" + programa.trim() + "' "
                    + " and "
                    + " trim(curriculo1banner)='" + curriculo.trim() + "'";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
            Validaciones.mostrarVentanaError("No se pudo tener el id de la carrera");
            e.printStackTrace();
        }
        return id;
    }

    public static String crearTablaTemporalAsignaturasCarreraCurriculoDado(Connection conn, String programa, String curriculo) {
        String nombreTabla = null;
        try {
            nombreTabla = "tablaTemporalAsignaturasCarreraCurriculoDado_" + programa + "_" + curriculo.replaceAll("-", "_");

            String sqlAsignaturas = "create TEMPORARY table if not exists "
                    + nombreTabla + " as "
                    + "select asignatura, "
                    + "cast(id_carrera as integer) as id_carrera, "
                    + "cast(semestre as integer) as semestre, "
                    + " cast(creditos as integer) as creditos,"
                    + "replace(alfa,' ','') || replace(replace(numerico,'.0',''),' ','') as alfanumerico "
                    + "from "
                    + "asignaturas "
                    + "where "
                    + "cast(asignaturas.id_carrera as integer)="
                    + obtenerIdCarreraNomenclaturaCurriculoDados(conn, programa, curriculo)
                    + " order by semestre";

            conn.createStatement().execute(sqlAsignaturas);
            return nombreTabla;
        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
            Validaciones.mostrarVentanaError("No se pudo crear la tabla temporal asignaturas carrera curriculo dado");
            e.printStackTrace();
        }

        return nombreTabla;
    }

    /**
     * Esta función crea la tabla temporal para el historial académico. Para
     * ello toma la tabla original de historial académico, llamada
     * historialacademico y que debió ser previamente creada al subir los
     * archivos csv. De dicha tabla se toman los siguientes campos (LOS NOMBRES
     * DE LOS CAMPOS NO NECESARIAMENTE VAN ASÍ. PARA OBSERVAR LOS NOMBRES
     * EXACTOS IR A CONSULTASQL.JAVA) CODIGOESTUDIANTE NOMBREESTUDIANTE SEDE
     * PROGRAMA ALFANUMERICO (este se coloca como alias as alfanumerico y sale
     * de la concatenación del alfa y numeri del original) DEFINITIVA (también
     * con alias definitiva pues, en el archivo original existen algunas notas
     * cualitativas que se deben pasar a número) Recordar que sólo se tomará el
     * historial académico de los estudiantes que estén activos (dentro de la
     * consulta sql, entendido por la palabra "in") en el archivo de estudiantes
     * activos para el periodo, tomado esto último de la tabla llamada
     * estudiantesactivosnrc
     *
     * @param conn
     * @param sede
     * @param programa
     * @return String con el nombre de la tabla de historial académico
     */
    public static String crearTablaTemporalHistorialAcademicoHistoricoSedeCarreraCurriculoDado(Connection conn, String sede, String programa) {
        try {
            String nombreTablaTemporal = "tablaTemporalHistorialAcademicoHistorico" + sede + "_" + programa;
            String sqlCrearTablaTemporal = " create temporary table if not exists   "
                    + nombreTablaTemporal + " as "
                    + " select "
                    + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO + ","
                    + ConsultasSQL.COLUMNANOMBREESTUDIANTEHISTORIALACADEMICO + ","
                    + ConsultasSQL.COLUMNASEDEHISTORIALACADEMICO + ","
                    + ConsultasSQL.COLUMNAPROGRAMAHISTORIALACADEMICO + ","
                    + ConsultasSQL.COLUMNAALFAHISTORIALACADEMICO + " || REPLACE(" + ConsultasSQL.COLUMNANUMERICOHISTORIALACADEMICO + ",'.0','') AS alfanumerico, "
                    + " cast( case " + ConsultasSQL.COLUMNADEFINITIVAHISTORIALACADEMICO + " when '" + ConsultasSQL.NOTACUALITATIVAAPROBADO + "' then 4 else " + ConsultasSQL.COLUMNADEFINITIVAHISTORIALACADEMICO + " end as real) as DEFINITIVA "
                    + " from "
                    + ConsultasSQL.TABLAHISTORIALACADEMICO
                    + " where "
                    + "trim(" + ConsultasSQL.COLUMNASEDEHISTORIALACADEMICO + ")='" + sede.trim() + "'"
                    + " and "
                    + "trim(" + ConsultasSQL.COLUMNAPROGRAMAHISTORIALACADEMICO + ")='" + programa.trim() + "' "
                    + " and " + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO + " in (select CODIGO from estudiantesactivosnrc )";
            conn.createStatement().execute(sqlCrearTablaTemporal);
            return nombreTablaTemporal;

        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo crear la tabla temporal de historial académicos para sede, carrera y curriculo dados", e);
        }
        return null;
    }

    /**
     * Esta función general un historial de las materias que cada estudiante
     * tiene matriculadas en el momento Se genera a partir de la tabla
     * estudiantesactivosnrc, que a su vez se genera del excel descargado de
     * argos con estudiantes activos. Esta tabla se genera con el fin de unirla
     * posteriormente a la tabla de historial academico historico Los campos de
     * esta tabla son codigo, nombre, sede, programa, materia, definitiva
     *
     * @param conn
     * @param sede
     * @param programa
     * @return String nombreTablaEstudiantesMatriculados
     */
    public static String crearTablaTemporalHistorialAcademicoMatriculadoSedeCarreraCuriculoDado(Connection conn, String sede, String programa) {
        try {
            String nombreTablaTemporal = "tablaHistorialAcademicoMatriculado" + sede + "_" + programa;

            String sql = " create temporary table if not exists "
                    + nombreTablaTemporal + " as "
                    + " select "
                    + ConsultasSQL.COLUMNACODIGOESTUDIANTESACTIVOSARGOS + " as codigoestudiante, "
                    + ConsultasSQL.COLUMNANOMBRESESTUDIANTESACTIVOSARGOS + " as nombreestudiante,"
                    + ConsultasSQL.COLUMNASEDEESTUDIANTESACTIVOSARGOS + ","
                    + ConsultasSQL.COLUMNAPROGRAMAESTUDIANTESACTIVOSARGOS + " as programa, "
                    + ConsultasSQL.COLUMNAALFANUMERICOESTUDIANTESACTIVOSARGOS + " as alfanumerico,"
                    + "3 as definitiva "
                    + " from "
                    + ConsultasSQL.TABLAESTUDIANTESACTIVOSNRC
                    + " where "
                    + "trim(" + ConsultasSQL.COLUMNAPROGRAMAESTUDIANTESACTIVOSARGOS + ")='" + programa + "'"
                    + " and "
                    + "trim(" + ConsultasSQL.COLUMNASEDEESTUDIANTESACTIVOSARGOS + ")='" + sede + "'";

            conn.createStatement().execute(sql);

            return nombreTablaTemporal;
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo crear la tabla temporal de historial academico matriculado para " + sede + " " + programa, e);
        }
        return null;
    }

    /**
     * Esta función crea la tabla temporal para el historial académico. Para
     * ello toma la tabla original de historial académico, llamada
     * historialacademico y que debió ser previamente creada al subir los
     * archivos csv. De dicha tabla se toman los siguientes campos (LOS NOMBRES
     * DE LOS CAMPOS NO NECESARIAMENTE VAN ASÍ. PARA OBSERVAR LOS NOMBRES
     * EXACTOS IR A CONSULTASQL.JAVA) CODIGOESTUDIANTE NOMBREESTUDIANTE SEDE
     * PROGRAMA ALFANUMERICO (este se coloca como alias as alfanumerico y sale
     * de la concatenación del alfa y numeri del original) DEFINITIVA (también
     * con alias definitiva pues, en el archivo original existen algunas notas
     * cualitativas que se deben pasar a número) Recordar que sólo se tomará el
     * historial académico de los estudiantes que estén activos (dentro de la
     * consulta sql, entendido por la palabra "in") en el archivo de estudiantes
     * activos para el periodo, tomado esto último de la tabla llamada
     * estudiantesactivosnrc
     *
     * @param conn
     * @param sede
     * @param programa
     * @return String con el nombre de la tabla de historial académico
     */
    public static String crearTablaTemporalHistorialAcademicoSedeCarreraCurriculoDado(Connection conn, String sede, String programa) {
        try {
            String nombreTablaTemporal = "tablaTemporalHistorialAcademico" + sede + "_" + programa;
            String nombreTablaHistorialMatriculado = crearTablaTemporalHistorialAcademicoMatriculadoSedeCarreraCuriculoDado(conn, sede, programa);
            String nombreTablaHistorialHistorico = crearTablaTemporalHistorialAcademicoHistoricoSedeCarreraCurriculoDado(conn, sede, programa);

            ResultSet rsunion = conn.createStatement().executeQuery("select * from "
                    + nombreTablaHistorialHistorico
                    + " union all "
                    + " select * from " + nombreTablaHistorialMatriculado);

            String sqlCrearTablaTemporal = " create temporary table if not exists "
                    + nombreTablaTemporal + " as "
                    + " select * from "
                    + nombreTablaHistorialHistorico
                    + " union all "
                    + " select * from " + nombreTablaHistorialMatriculado;

            conn.createStatement().execute(sqlCrearTablaTemporal);
            return nombreTablaTemporal;

        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo crear la tabla temporal de historial académicos para sede, carrera y curriculo dados", e);
        }
        return null;
    }

    /**
     * Esta función crea la tabla temporal para el historial académico. Para
     * ello toma la tabla original de historial académico, llamada
     * historialacademico y que debió ser previamente creada al subir los
     * archivos csv. De dicha tabla se toman los siguientes campos (LOS NOMBRES
     * DE LOS CAMPOS NO NECESARIAMENTE VAN ASÍ. PARA OBSERVAR LOS NOMBRES
     * EXACTOS IR A CONSULTASQL.JAVA) CODIGOESTUDIANTE NOMBREESTUDIANTE SEDE
     * PROGRAMA ALFANUMERICO (este se coloca como alias as alfanumerico y sale
     * de la concatenación del alfa y numeri del original) DEFINITIVA (también
     * con alias definitiva pues, en el archivo original existen algunas notas
     * cualitativas que se deben pasar a número) Recordar que sólo se tomará el
     * historial académico de los estudiantes que estén activos (dentro de la
     * consulta sql, entendido por la palabra "in") en el archivo de estudiantes
     * activos para el periodo, tomado esto último de la tabla llamada
     * estudiantesactivosnrc
     *
     * @param conn
     * @param sede
     * @param programa
     * @param curriculo
     * @return String con el nombre de la tabla de historial académico
     */
    public static String crearTablaTemporalHistorialAcademicoSedeCarreraCurriculoDado(Connection conn, String sede, String programa, String curriculo) {
        try {

            String nombreTablaTemporalEstudianteCurriculo = crearTablaTemporalEstudiantesActivosGeneralCodigoCurriculo(conn);

            String nombreTablaTemporalHistorial = "tablaTemporalHistorialAcademico" + sede + "_" + programa;
            String nombreTablaHistorialMatriculado = crearTablaTemporalHistorialAcademicoMatriculadoSedeCarreraCuriculoDado(conn, sede, programa);
            String nombreTablaHistorialHistorico = crearTablaTemporalHistorialAcademicoHistoricoSedeCarreraCurriculoDado(conn, sede, programa);

            String sqlCrearTablaTemporal = " create temporary table if not exists "
                    + nombreTablaTemporalHistorial + " as "
                    + " select * from "
                    + nombreTablaHistorialHistorico
                    + " union all "
                    + " select * from " + nombreTablaHistorialMatriculado;

            conn.createStatement().execute(sqlCrearTablaTemporal);

            String nombreTablaTemporalHistorialCurriculo = "tablaTemporalHistorialCurriculo" + sede + "_" + programa + "_" + curriculo.replaceAll("-", "_");

            String sqlCrearTablaHistorialCurriculo = "create temporary table IF NOT EXISTS "
                    + nombreTablaTemporalHistorialCurriculo
                    + " as select " + nombreTablaTemporalHistorial + ".*"
                    + ", " + nombreTablaTemporalEstudianteCurriculo + "." + ConsultasSQL.COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL
                    + " from "
                    + nombreTablaTemporalHistorial
                    + " left join "
                    + " " + nombreTablaTemporalEstudianteCurriculo
                    + " on "
                    + "" + nombreTablaTemporalEstudianteCurriculo + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEACTIVOSGENERAL + ""
                    + "="
                    + "" + nombreTablaTemporalHistorial + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO + "";

            conn.createStatement().execute(sqlCrearTablaHistorialCurriculo);

            String nombreTablaTemporalHistorialCurriculoDado = "tablaTemporalHistorialCurriculoDado" + sede + "_" + programa + "_" + curriculo.replaceAll("-", "_");
            String sqlCrearTablaHistorialCurriculoDado = "create temporary table if not exists "
                    + nombreTablaTemporalHistorialCurriculoDado
                    + " as "
                    + " select * from "
                    + nombreTablaTemporalHistorialCurriculo
                    + " where "
                    + ConsultasSQL.COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL + "='" + curriculo + "'";
            conn.createStatement().execute(sqlCrearTablaHistorialCurriculoDado);

            return nombreTablaTemporalHistorialCurriculoDado;

        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo crear la tabla temporal de historial académicos para sede, carrera y curriculo dados", e);
        }
        return null;
    }

    /**
     * Esta función crea la tabla temporal de alfanumericos equivalentes Dicha
     * tabla se crea ya que los alfanuméricos que ven historicamente los
     * estudiantes pueden cambiar Es así como, por dar un ejemplo, en algún
     * momento un estudiante vio "Informática" con el alfanumérico INFO1010,
     * pero luego por cambios de currículo, dicho alfanumérico se actualizó a
     * INFO5050 de allí que los dos alfanumericos son equivalentes, y sirve como
     * proceso interno de homologación. Esta tabla se crea apoyándose en otra
     * tabla llamada alfanumericoshomologados, haciendo un left join con la
     * misma a través del alfanumérico. Dicha homologación no tiene en cuenta el
     * programa, por lo cual el estudiante pudo haber visto la materia en un
     * programa distinto y de igual forma lo va a homologar para el programa.
     * Los campos que tiene esta tabla son, los que trae del historial académico
     * codigo nombre sede programa alfanumerico definitiva y se agrega el
     * siguiente campo alfaNumericoEquivalente
     *
     * @param conn
     * @param nombreTablaTemporalHistorialAcademico
     * @return
     */
    public static String crearTablaTemporalHistorialAcademicoAlfaNumericosEquivalentes(Connection conn, String nombreTablaTemporalHistorialAcademico) {
        try {
            String nombreTablaTemporal = "tablaTemporalHistorialAcademicoAlfaNumericosEquivalentes_" + nombreTablaTemporalHistorialAcademico;
            String sqlTablaTemporalHistorialHomologados = "create temporary table IF NOT EXISTS "
                    + nombreTablaTemporal + " as"
                    + " select "
                    + nombreTablaTemporalHistorialAcademico + ".*,  "
                    + " alfaNumericoReciente as alfaNumericoEquivalente "
                    + " from "
                    + nombreTablaTemporalHistorialAcademico + " "
                    + " left join "
                    + " alfanumericoshomologados "
                    + " on "
                    + nombreTablaTemporalHistorialAcademico + ".alfanumerico=alfanumericoshomologados.alfaNumericoAntiguo ";
//                    + " and "
//                    + nombreTablaTemporalHistorialAcademico+"."+ConsultasSQL.COLUMNAPROGRAMAHISTORIALACADEMICO
//                    +"="
//                    +"alfanumericoshomologados.nomenclatura";
            conn.createStatement().execute(sqlTablaTemporalHistorialHomologados);
            return nombreTablaTemporal;
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo crear la tabla temporal Historial Academico con Alfanumericos equivalentes", e);
        }
        return null;
    }

    public static String crearTablaTemporalHistorialAcademicoCreditos(Connection conn, String nombreTablaAsignaturasCarreraCurriculoDado, String nombreTablaHistorialAlfaNumericosEquivalentes) {
        try {
            String nombreTablaTemporal = "tablaTemporalHistorialAlfaNumericosCreditos_" + nombreTablaAsignaturasCarreraCurriculoDado + "_" + nombreTablaHistorialAlfaNumericosEquivalentes;
            String sql = "create temporary table if not exists "
                    + nombreTablaTemporal + " as "
                    + "select " + nombreTablaHistorialAlfaNumericosEquivalentes + ".* "
                    + ", "
                    + nombreTablaAsignaturasCarreraCurriculoDado + ".creditos "
                    + " from "
                    + nombreTablaAsignaturasCarreraCurriculoDado
                    + " left join "
                    + nombreTablaHistorialAlfaNumericosEquivalentes
                    + " on "
                    + nombreTablaHistorialAlfaNumericosEquivalentes + ".alfanumerico="
                    + nombreTablaAsignaturasCarreraCurriculoDado + ".alfanumerico "
                    + " or "
                    + nombreTablaHistorialAlfaNumericosEquivalentes + ".alfanumericoEquivalente="
                    + nombreTablaAsignaturasCarreraCurriculoDado + ".alfanumerico ";
            conn.createStatement().execute(sql);
            return nombreTablaTemporal;
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se puedo crear la tabla historial académico y créditos", e);
        }

        return null;
    }

    public static String convertirComparacionAPalabras(String comparacion) {
        comparacion = comparacion.trim();
        if (comparacion.equals(">")) {
            return "mayorque";
        }
        if (comparacion.equals(">=")) {
            return "mayorigualque";
        }
        if (comparacion.equals("<")) {
            return "menorque";
        }
        if (comparacion.equals("<=")) {
            return "menorigualque";
        }
        if (comparacion.equals("=")) {
            return "igualque";
        }
        return null;
    }

    public static String crearTablaTemporalObtenerHistorialTemporalSegunNota(Connection conn, String nombreTablaTemporalHistorialAcademico, String comparacion, double nota) {
        try {
            String nombreTablaTemporal = "tablaTemporalHistorialSegunNota_" + nombreTablaTemporalHistorialAcademico + convertirComparacionAPalabras(comparacion) + ("" + nota).replaceAll(".", "");
            String sql = " create temporary table if not exists "
                    + nombreTablaTemporal
                    + " as select * from "
                    + nombreTablaTemporalHistorialAcademico
                    + " where "
                    + " cast(DEFINITIVA as real)"
                    + comparacion + ""
                    + nota;
            conn.createStatement().execute(sql);
            return nombreTablaTemporal;
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo realizar la consulta de obtener historial segun nota", e);
        }
        return null;
    }

    public static String consultaTablaTemporalObtenerHistorialTemporalSegunNota(Connection conn, String nombreTablaTemporalHistorialAcademico, String comparacion, double nota) {
        try {
            String sql = "select * from "
                    + nombreTablaTemporalHistorialAcademico
                    + " where "
                    + " cast(DEFINITIVA as real)"
                    + comparacion + ""
                    + nota;
            return sql;
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo realizar la consulta de obtener historial segun nota", e);
        }
        return null;
    }

    public static String crearTablaTemporalAprobadosPendientes(Connection conn, String sede, String programa, String curriculo) {
        try {
            String nombreTAblaAsignaturasCarreraCurriculoDado = EstudiantesPorPrograma.crearTablaTemporalAsignaturasCarreraCurriculoDado(conn, programa, curriculo);
            String nombreTAblaHistorialAcademico = EstudiantesPorPrograma.crearTablaTemporalHistorialAcademicoSedeCarreraCurriculoDado(conn, sede, programa);

            String t1 = nombreTAblaAsignaturasCarreraCurriculoDado;
            String t2 = nombreTAblaHistorialAcademico;

            String thh = EstudiantesPorPrograma.crearTablaTemporalHistorialAcademicoAlfaNumericosEquivalentes(conn, nombreTAblaHistorialAcademico);

            t2 = EstudiantesPorPrograma.crearTablaTemporalObtenerHistorialTemporalSegunNota(conn,
                    thh,
                    ">=",
                    3);

            int totalMatriculados = EstudiantesPorPrograma.totalMatriculadosEstudiantesArgos(conn, sede, curriculo, curriculo);
            String nombreTablaTemporal = "tablatemporalaprobadospendientes" + System.currentTimeMillis();
            String sql = "create temporary table " + nombreTablaTemporal + " as select  asignatura, "
                    + "semestre, "
                    + "alfanumerico as alfanumericoactual, "
                    + "sede, "
                    + "programa, "
                    + " case when " + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO + " is null then 0 else count(*) end AS APROBADOS,"
                    + totalMatriculados + "- case when " + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO + " is null then 0 else count(*) end as pendientes "
                    + " from"
                    + " (select "
                    + t1 + ".*,"
                    + t2 + ".* "
                    + " from "
                    + t1 + "  "
                    + " left join "
                    + t2 + "  "
                    + " on "
                    + t1 + ".alfanumerico=" + t2 + ".alfanumericoEquivalente"
                    + " or "
                    + t1 + ".alfanumerico=" + t2 + ".alfanumerico "
                    + "group by " + t2 + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO + ", " + t1 + ".alfanumerico "
                    + " order by " + t1 + ".semestre, " + t1 + ".asignatura, " + t2 + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO
                    + ") "
                    + " group by alfanumerico "
                    + " order by semestre ";
            conn.createStatement().execute(sql);
            return nombreTablaTemporal;

        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo crear la tabla temporal aprobados y pendientes", e);
        }

        return null;
    }

    static String crearTablaTemporalAsignaturasCarreraCurriculoSemestre(Connection conn,
            String programa, String curriculo, int semestre) {

        String nombreTablaSemestreN = EstudiantesPorPrograma.crearTablaTemporalAsignaturasCarreraCurriculoDado(conn, programa, curriculo);
        String selectSemestreN = "select alfanumerico "
                + " from "
                + nombreTablaSemestreN
                + " where "
                + " semestre=" + semestre;
        String nombreTablaTemporal = " tablaAsignaturasSemestre" + System.currentTimeMillis();

        String sql = " create temporary table "
                + nombreTablaTemporal
                + " as "
                + " " + selectSemestreN;

        try {
            conn.createStatement().execute(sql);
            return nombreTablaTemporal;
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo crear la tabla temporal de asignaturas por semestre", e);
        }
        return null;
    }

    static String crearTablaTemporalAsignaturasCarreraCurriculoHastaSemestre(Connection conn,
            String programa, String curriculo, int semestre) {

        String nombreTablaSemestreN = EstudiantesPorPrograma.crearTablaTemporalAsignaturasCarreraCurriculoDado(conn, programa, curriculo);
        String selectSemestreN = "select alfanumerico "
                + " from "
                + nombreTablaSemestreN
                + " where "
                + " cast(semestre as integer)<=" + semestre;
        String nombreTablaTemporal = " tablaAsignaturasSemestre" + System.currentTimeMillis();

        String sql = " create temporary table "
                + nombreTablaTemporal
                + " as "
                + " " + selectSemestreN;

        try {
            conn.createStatement().execute(sql);
            return nombreTablaTemporal;
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo crear la tabla temporal de asignaturas por semestre", e);
        }
        return null;
    }

    /**
     * Método que crea una tabla temporal con los siguientes campos
     * id_estudiante, <br>
     * nombre_estudiante <br>
     * sede, <br>
     * programa, <br>
     * alfanumerico (cursado por el estudiante), <br>
     * definitiva, <br>
     * alfanumericoequivalente (si el estudiante cursó con otro alfanumerico que
     * no está en la lista de materias actuales, ese sería el equivalente, si es
     * nulo, quiere decir que lo cursó con el alfanumerico actual)<br>
     *
     * La tabla está agrupada por id_estudiante y alfanumerico cursado. Muestra
     * el historial académico de un carrera para un semestre dado, con los
     * estudiantes activos que han aprobado las materias de dicho semestre
     *
     * @param conn
     * @param programa
     * @param curriculo
     * @param sede
     * @param semestre
     * @return String con nombre de la tabla temporal creada
     */
    static String crearTablaTemporalHistorialAcademicoAprobadosSemestreN(Connection conn,
            String programa, String curriculo, String sede, int semestre) {

        String tablaTemporalASignaturasSemestre
                = crearTablaTemporalAsignaturasCarreraCurriculoSemestre(conn, programa, curriculo, semestre);

        String selectSemestreN = "select * from " + tablaTemporalASignaturasSemestre;

        String nombreTablaTemporalHistorialAcademico = EstudiantesPorPrograma.crearTablaTemporalHistorialAcademicoSedeCarreraCurriculoDado(conn, sede, programa);
        String nombreTablaHistorialAnEquivalentes = EstudiantesPorPrograma.crearTablaTemporalHistorialAcademicoAlfaNumericosEquivalentes(conn, nombreTablaTemporalHistorialAcademico);
        String nTHistorialAprobados = EstudiantesPorPrograma.crearTablaTemporalObtenerHistorialTemporalSegunNota(conn, nombreTablaHistorialAnEquivalentes, ">=", 3);

        String nombreTablaTemporal = "tablaTemporalHistorialAcademicoAprobadosSemestreN" + System.currentTimeMillis();
        String sqlHistorialQueTengaAlasSemestreN
                = "select * from " + nTHistorialAprobados
                + " where "
                + " alfanumerico "
                + "in("
                + selectSemestreN
                + ")"
                + " or alfaNumericoEquivalente in "
                + "(" + selectSemestreN + " ) "
                + " group by ID_ESTUDIANTE, alfanumerico";

        String sqlCrearTablaTemporal = " create temporary table "
                + nombreTablaTemporal
                + " as "
                + sqlHistorialQueTengaAlasSemestreN;

        try {
            conn.createStatement().execute(sqlCrearTablaTemporal);
            return nombreTablaTemporal;
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se puedo crear la tabla de historial de aprobados para materias de una semestre dado", e);
        }

        return null;
    }

    static String crearTablaTemporalHistorialAcademicoAprobadosHastaSemestreN(Connection conn,
            String programa, String curriculo, String sede, int semestre) {

        String tablaTemporalASignaturasSemestre
                = crearTablaTemporalAsignaturasCarreraCurriculoHastaSemestre(conn, programa, curriculo, semestre);

        String selectSemestreN = "select * from " + tablaTemporalASignaturasSemestre;

        String nombreTablaTemporalHistorialAcademico = EstudiantesPorPrograma.crearTablaTemporalHistorialAcademicoSedeCarreraCurriculoDado(conn, sede, programa);
        String nombreTablaHistorialAnEquivalentes = EstudiantesPorPrograma.crearTablaTemporalHistorialAcademicoAlfaNumericosEquivalentes(conn, nombreTablaTemporalHistorialAcademico);
        String nTHistorialAprobados = EstudiantesPorPrograma.crearTablaTemporalObtenerHistorialTemporalSegunNota(conn, nombreTablaHistorialAnEquivalentes, ">=", 3);

        String nombreTablaTemporal = "tablaTemporalHistorialAcademicoAprobadosHastaSemestreN" + System.currentTimeMillis();
        String sqlHistorialQueTengaAlasSemestreN
                = "select * from " + nTHistorialAprobados
                + " where "
                + " alfanumerico "
                + "in("
                + selectSemestreN
                + ")"
                + " or alfaNumericoEquivalente in "
                + "(" + selectSemestreN + " ) "
                + " group by ID_ESTUDIANTE, alfanumerico";

        String sqlCrearTablaTemporal = " create temporary table "
                + nombreTablaTemporal
                + " as "
                + sqlHistorialQueTengaAlasSemestreN;

        try {
            conn.createStatement().execute(sqlCrearTablaTemporal);
            return nombreTablaTemporal;
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se puedo crear la tabla de historial de aprobados para materias de una semestre dado", e);
        }

        return null;
    }

    /**
     * Método que genera una tabla temporal con el historial académico del
     * estudiante con las materias aprobadas, en el currículo matriculado para
     * un semestre dado. La tabla tiene los campos ID_ESTUDIANTE <br>
     * NOMBRE_ESTUDIANTE <BR>
     * SEDE <BR>
     * PROGRAMA <BR>
     * alfanumerico (este es el que cursó el estudiante)<br>
     * definitiva<br>
     * alfanumericoequivalente (el alfanumerico equivalente en el curriculo
     * matriculado)<br>
     *
     * @param conn
     * @param idEstudiante
     * @param semestre
     * @param nombreTablaTemporalAprobadosSemestreN generado con el método
     * crearTablaTemporalHistorialAcademicoAprobadosSemestreN
     * @param programa
     * @param curriculo
     * @param sede
     * @return nombre de la tabla temporal
     */
    static String crearTablaTemporalHistorialEstudianteSemestre(Connection conn,
            int idEstudiante,
            int semestre,
            String nombreTablaTemporalAprobadosSemestreN,
            String programa,
            String curriculo,
            String sede) {

        String nombreTablaTemporal = "estudiante"
                + idEstudiante + "semestre" + semestre;

        String sql = "create temporary table " + nombreTablaTemporal
                + " as select * "
                + " from "
                + nombreTablaTemporalAprobadosSemestreN
                + " where " + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO
                + "=" + idEstudiante;
        try {
            conn.createStatement().execute(sql);
            return nombreTablaTemporal;
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo crear la tabla temporal del estudiante en el semestre", e);
        }
        return null;
    }

    static String crearTablaTemporalHistorialEstudianteHastaSemestre(Connection conn,
            int idEstudiante,
            int semestre,
            String nombreTablaTemporalAprobadosHastaSemestreN,
            String programa,
            String curriculo,
            String sede) {

        String nombreTablaTemporal = "estudiante"
                + idEstudiante + "semestre" + semestre;

        String sql = "create temporary table " + nombreTablaTemporal
                + " as select * "
                + " from "
                + nombreTablaTemporalAprobadosHastaSemestreN
                + " where " + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO
                + "=" + idEstudiante;
        try {
            conn.createStatement().execute(sql);
            return nombreTablaTemporal;
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo crear la tabla temporal del estudiante en el semestre", e);
        }
        return null;
    }

    static boolean estudianteEnBloqueSemestreDado(int idEstudiante,
            int semestre,
            Connection conn,
            String nombreTablaAsignaturasSemestre,
            String nombreTablaHistorialEstudianteSemestre
    ) {

        String sqlJoinTabla = "select count(*) from (select tas.*, e.* from "
                + nombreTablaAsignaturasSemestre
                + " as tas "
                + " left join "
                + " " + nombreTablaHistorialEstudianteSemestre + " as e "
                + " on "
                + "tas.alfanumerico=e.alfanumerico "
                + " or "
                + "tas.alfanumerico=e.alfaNumericoEquivalente) as fusionada "
                + " where fusionada.ID_ESTUDIANTE is null";

        try {
            ResultSet rs = conn.createStatement().executeQuery(sqlJoinTabla);
            if (rs.getInt(1) == 0) {
                return true;
            }

        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo determinar si el estudiante va  en bloque ", e);
        }
        return false;
    }

    public static int totalMatriculadosEstudiantesArgos(Connection conn, String sede, String nomenclatura, String curriculo) {
        String sqlTablaTotalEstudiantes = "";
        try {
            String nombreTablaArgosActivos = crearTablaTemporalArgosActivos(conn, sede, nomenclatura, curriculo);
            String nombreTablaEstudiantesActivosGeneralCodigoCurriculo = crearTablaTemporalEstudiantesActivosGeneralCodigoCurriculo(conn);

            sqlTablaTotalEstudiantes = "select count(*) from (select "
                    + nombreTablaArgosActivos + ".*, "
                    + nombreTablaEstudiantesActivosGeneralCodigoCurriculo + "." + ConsultasSQL.COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL + " "
                    + " from " + nombreTablaArgosActivos
                    + " left join "
                    + nombreTablaEstudiantesActivosGeneralCodigoCurriculo
                    + " on "
                    + nombreTablaArgosActivos + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTESACTIVOSARGOS
                    + "="
                    + nombreTablaEstudiantesActivosGeneralCodigoCurriculo + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEACTIVOSGENERAL
                    + ")";
            conn.createStatement().executeQuery(sqlTablaTotalEstudiantes);

            ResultSet rs = conn.createStatement().executeQuery(sqlTablaTotalEstudiantes);

            while (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            Validaciones.mostrarVentanaError("<html>No se pudo cargar el total de matriculados desde estudiantes activos argos: <br>" + sqlTablaTotalEstudiantes + "</html>");
            Validaciones.mostrarVentanaError(e);
            System.out.println(sqlTablaTotalEstudiantes);
            e.printStackTrace();
        }
        return 0;
    }

    public static void inicializarListadoCarrerasEnEstudiantesPorPrograma(Connection conn, JComboBox jComboBoxCarrerasEstudiar) {
        try {
            ResultSet rs = EstudiantesPorPrograma.listadoCarreras(conn);
            if (rs != null) {
                DefaultComboBoxModel cbm = new DefaultComboBoxModel<String>();
                while (rs.next()) {
                    cbm.addElement(rs.getString(1));
                }
                jComboBoxCarrerasEstudiar.setModel(cbm);
            } else {
                Validaciones.mostrarVentanaError("No se puedo cargar el listado de carreras");
            }

        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }
    }

    public static ResultSet listadoSedesReporteArgos(Connection conn) {
        ResultSet rs = null;
        try {
            String sql = "select "
                    + ConsultasSQL.COLUMNASEDEESTUDIANTESACTIVOSARGOS
                    + " from "
                    + ConsultasSQL.TABLAESTUDIANTESACTIVOSNRC
                    + " group by "
                    + ConsultasSQL.COLUMNASEDEESTUDIANTESACTIVOSARGOS;

            return conn.createStatement().executeQuery(sql);
        } catch (Exception e) {
            Validaciones.mostrarVentanaError("No se pudo cargar el listado de sedes desde el reporte de argos");
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }
        return rs;
    }

    public static void cargarSedesComboBox(Connection conn, JComboBox jComboBoxSedes) {
        try {
            ResultSet rs = EstudiantesPorPrograma.listadoSedesReporteArgos(conn);
            if (rs != null) {
                DefaultComboBoxModel cbm = new DefaultComboBoxModel<String>();
                while (rs.next()) {
                    cbm.addElement(rs.getString(1));
                }
                jComboBoxSedes.setModel(cbm);
            } else {
                Validaciones.mostrarVentanaError("No se puedo cargar el listado de sedes");
            }
        } catch (Exception e) {
            Validaciones.mostrarVentanaError("No se pudo cargar el combo box listado de sedes desde el reporte de argos");
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }
    }

    public static void cargarCurriculosComboBoxCarreras(Connection conn, String nomenclatura, JComboBox jComboBoxCurriculos) {
        try {
            ResultSet rs = EstudiantesPorPrograma.listadoCurriculos(conn, nomenclatura);
            if (rs != null) {
                DefaultComboBoxModel cbm = new DefaultComboBoxModel<String>();
                while (rs.next()) {
                    cbm.addElement(rs.getString(1));
                }
                jComboBoxCurriculos.setModel(cbm);
            } else {
                Validaciones.mostrarVentanaError("No se puedo cargar el listado de carreras");
            }
        } catch (Exception e) {
            Validaciones.mostrarVentanaError("No se pudieron cargar los currículos");
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }
    }

    public static void cargarMatriculadosGeneralTextField(Connection conn, JTextField jTextFieldMatriculaGeneral, String sede, String nomenclatura, String curriculo) {
        jTextFieldMatriculaGeneral.setText("" + totalMatriculadosEstudiantesActivosGeneral(conn, sede, nomenclatura, curriculo));
    }

    public static void cargarMatriculadosArgoslTextField(Connection conn, JTextField jTextFieldMatriculaArgos, String sede, String nomenclatura, String curriculo) {
        jTextFieldMatriculaArgos.setText("" + totalMatriculadosEstudiantesArgos(conn, sede, nomenclatura, curriculo));
    }

    public static void inicializarComboBoxCurriculos(Connection conn, JComboBox jComboBoxNomenclaturas, JComboBox jComboBoxCurriculos) {
        cargarCurriculosComboBoxCarreras(conn, (String) jComboBoxNomenclaturas.getSelectedItem(), jComboBoxCurriculos);
    }

    public static void cargarDescripcionCarreraEnTextField(Connection conn, JComboBox jComboBoxCarreras, JTextField jTextField) {
        jTextField.setText(descripcionCarreraNomenclaturaDada(conn, (String) jComboBoxCarreras.getSelectedItem()));
    }

    public static String crearTablaInformeTotalProgramasLoPuedenVer(Connection CONN, EstudiantesNoHanVistoNRC ENHV, String sede, String programa, String curriculo) {
        String nombreTablaInformeTotalProgramaLoPuedenVer
                = "'" + ConsultasSQL.PREFIJOTABLAINFORMETOTALPROGRAMALOPUEDENVER + sede + programa + curriculo + "'";

        String nombreTablaMateriasSemestre = EstudiantesPorPrograma.crearTablaTemporalAsignaturasCarreraCurriculoDado(CONN, programa, curriculo);

        try {
            ResultSet rs = CONN.createStatement().executeQuery("select * from "
                    + nombreTablaMateriasSemestre);

            //                    +" where semestre=1");
            //ConsultasSQL.crearTablaTemporalEstudiantesActivosArgosSinRepetir(CONN);
            //EstudiantesNoHanVistoNRC enhv = new EstudiantesNoHanVistoNRC(CONN, sede, programa, curriculo);
            //ENHV.crearTablaTemporalFinalNoLaHanVistoLaPuedenVer();
            ArrayList<String> materias = new ArrayList<String>();
            ArrayList<String> alfaNumericos = new ArrayList<String>();
            ArrayList<Integer> semestres = new ArrayList<Integer>();
            ArrayList<Integer> creditos = new ArrayList<Integer>();

            while (rs.next()) {
                String anEvaluar = rs.getString("alfanumerico");
                String asignatura = rs.getString("asignatura");
                int semestre = rs.getInt("semestre");
                int credito = rs.getInt("creditos");
                materias.add(asignatura);
                alfaNumericos.add(anEvaluar);
                semestres.add(semestre);
                creditos.add(credito);
            }

            String sqlUnion = "";
            String sql3 = "";
            int tamano = materias.size();
//            tamano = 4;
            for (int i = 0; i < tamano; i++) {
                sql3 = " select "
                        + ConsultasSQL.NOMBRETABLATEMPORAL_ESTUDIANTESACTIVOS + ".*, "
                        + "'" + materias.get(i) + "' as asignatura, '"
                        + alfaNumericos.get(i) + "' as alfaNumerico, "
                        + semestres.get(i) + " as semestreMateria, "
                        + creditos.get(i) + " as creditosMateria, "
                        + ENHV.NOMBRETABLANOHANVISTOYPUEDENVER + alfaNumericos.get(i) + ".creditosaprobados "
                        + " from "
                        + ENHV.NOMBRETABLANOHANVISTOYPUEDENVER + alfaNumericos.get(i)
                        + " LEFT JOIN "
                        + ConsultasSQL.NOMBRETABLATEMPORAL_ESTUDIANTESACTIVOS
                        + " ON "
                        + ENHV.NOMBRETABLANOHANVISTOYPUEDENVER + alfaNumericos.get(i) + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO
                        + "="
                        + ConsultasSQL.NOMBRETABLATEMPORAL_ESTUDIANTESACTIVOS + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTESACTIVOSARGOS;
                if (i != tamano - 1) {
                    sqlUnion = sqlUnion + " " + sql3 + " union ";
                    //sqlUnion = sqlUnion + " select '"+ materias.get(i)+"' as asignatura, '" +alfaNumericos.get(i)+ "' as alfaNumerico, " + nombresTablas.get(i)+".* from " + nombresTablas.get(i) + " union all ";
                } else {
                    sqlUnion = sqlUnion + sql3 + " order by asignatura ";
                    //sqlUnion = sqlUnion + " select '"+ materias.get(i)+"' as asignatura, '" +alfaNumericos.get(i)+ "' as alfaNumerico, " + nombresTablas.get(i)+".* from " + nombresTablas.get(i);
                }
            }
            CONN.createStatement().execute(
                    "create temporary table if not exists "
                    + nombreTablaInformeTotalProgramaLoPuedenVer
                    + " as "
                    + sqlUnion);

        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo cargar la tabla con el total de estudiantes que lo pueden ver para"
                    + "Sede: " + sede + " Programa: " + programa + " Currículo: " + curriculo, e);
        }
        return nombreTablaInformeTotalProgramaLoPuedenVer;
    }

    public static String crearTablaInformeTodosProgramasLoPuedenVer(Connection CONN, String sede) {
        String nombreTablaInformeTodosProgramasLoPuedenVer
                = "'" + ConsultasSQL.PREFIJOTABLAINFORMETODOSPROGRAMASLOPUEDEVENVER + "'";
        ResultSet rsCarreras = listadoCarreras(CONN);

        ArrayList<String> nombresTablas = new ArrayList<String>();

        try {
            if (rsCarreras != null) {
                while (rsCarreras.next()) {
                    String nomenclatura = rsCarreras.getString(1);
                    ResultSet rsCurriculos = listadoCurriculos(CONN, nomenclatura);
                    if (rsCurriculos != null) {
                        while (rsCurriculos.next()) {
                            String curriculo = rsCurriculos.getString(1);
                            EstudiantesNoHanVistoNRC ENHV = new EstudiantesNoHanVistoNRC(CONN, sede, nomenclatura, curriculo);
                            ENHV.crearTablaTemporalFinalNoLaHanVistoLaPuedenVer();
                            String nombreTabla = crearTablaInformeTotalProgramasLoPuedenVer(CONN, ENHV, sede, nomenclatura, curriculo);
                            nombresTablas.add(nombreTabla);
                        }
                    }
                }
            }

            String consultaUnionTodasTablas = "";
            for (int i = 0; i < nombresTablas.size(); i++) {
                if (i == 0) {
                    consultaUnionTodasTablas = " create temporary table if not exists "
                            + nombreTablaInformeTodosProgramasLoPuedenVer
                            + " as "
                            + " select * from " + nombresTablas.get(i)
                            + " union ";
                }
                if (i > 0 && i < nombresTablas.size() - 2) {
                    consultaUnionTodasTablas = consultaUnionTodasTablas
                            + " select * from " + nombresTablas.get(i)
                            + " union ";
                }
                if (i == nombresTablas.size() - 1) {
                    consultaUnionTodasTablas = consultaUnionTodasTablas
                            + " select * from " + nombresTablas.get(i);
                }
            }

            CONN.createStatement().execute(consultaUnionTodasTablas);

        } catch (Exception e) {
            e.printStackTrace();
            Validaciones.mostrarErroresTotal("Error al crear la tabla de informe de todos los programas lo pueden ver", e);
        }
        return nombreTablaInformeTodosProgramasLoPuedenVer;
    }

    public static void mostrarInformeTotalProgramaLoPuedenVer(Connection CONN, EstudiantesNoHanVistoNRC ENHV, String sede, String programa, String curriculo) {

        try {

            String nombreTabla = crearTablaInformeTotalProgramasLoPuedenVer(CONN, ENHV, sede, programa, curriculo);
            ResultSet rs2 = CONN.createStatement().executeQuery("select * from " + nombreTabla);

            new JFrameJTable("Informe Total LoPuedenVer sede: " + sede + " programa: " + programa + " currículo: " + curriculo, rs2);
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo cargar la tabla con el total de estudiantes que lo pueden ver para"
                    + "Sede: " + sede + " Programa: " + programa + " Currículo: " + curriculo, e);
        }

    }

    public static void mostrarInformeTodosProgramasLoPuedenVer(Connection CONN, EstudiantesNoHanVistoNRC ENHV, String sede) {
        try {
            String nombreTabla = crearTablaInformeTodosProgramasLoPuedenVer(CONN, sede);
            ResultSet rs2 = CONN.createStatement().executeQuery("select * from " + nombreTabla);

            new JFrameJTable("Informe Total LoPuedenVer sede: " + sede, rs2);
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo cargar la tabla con el total de estudiantes que lo pueden ver para"
                    + "Sede: " + sede, e);
        }

    }

}
