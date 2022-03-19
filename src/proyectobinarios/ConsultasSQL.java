/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author Usuario
 */
public class ConsultasSQL {

    static String RUTABD = "data/db/";
    static String NOMBREBDOFERTAEDUCATIVA = "ofertaeducativa";

    static String NOMBRETABLATEMPORAL_ESTUDIANTESACTIVOS = "estudiantesActivosArgosSinRepetir";
    static String NOMBRETABLATEMPORAL_CODIGOCURRICULO = "codigoCurriculo";
//    static String NOMBREBDOFERTAEDUCATIVA = "ensayos";
    static char SEPARADORARCHIVOSCSV = ';';
    static String CONEXIONSQLITEOFERTAEDUCATIVA = "jdbc:sqlite:" + RUTABD + "" + NOMBREBDOFERTAEDUCATIVA + ".db";
    static String CODIFICACIONORIGEN = "windows-1252";
    static String CODIFICACIODESTINO = "UTF-8";
    /**
     * TABLAHISTORIALACADEMICO nombre de la tabla historialacademico, es la
     * tabla que se descarga por partes para la sede; en este caso particular,
     * eje cafetero; tiene todo el historial de todos los estudiantes que han
     * pasado por esa sede
     */
    static String TABLAHISTORIALACADEMICO = "historialacademico";
    static String TABLAESTUDIANTESACTIVOSNRC = "estudiantesactivosnrc";
    static String TABLACURRICULOS = "curriculos";
    static String TABLACARRERAS = "carreras";
    static String TABLAASIGNATURAS = "asignaturas";
    static String TABLAESTUDIANTESACTIVOSGENERAL = "estudiantesactivosgeneral";
    static String TABLAPROGRAMACIONACADEMICAARGOS = "programacionacademicaargos";
    static String TABLAALFANUMERICOSHOMOLOGADOS = "alfanumericoshomologados";

    static String PREFIJOTABLAINFORMETOTALPROGRAMALOPUEDENVER = "informeTotalProgramaLoPuedenVer";
    static String PREFIJOTABLAINFORMETODOSPROGRAMASLOPUEDEVENVER = "informeTodosProgramasLoPuedenVer";
    static String PREFIJOTABLAINFORMETODOSPROGRAMASLOPUEDEVENVER_NUMEROS_TOTALES = "informeTodosProgramasLoPuedenVerNumerosTotales";

    //Cabeceras mínimas del archivo de estudiantes activos general, el cual es un archivo
    //entregado por teams a nivel nacional; es un archivo que tiene todos los matriculados
    //a nivel nacional
    static String COLUMNANOMBREESTUDIANTESACTIVOSGENERAL = "APELLIDOS_NOMBRES";
    static String COLUMNAPERIODOADMISIONESTUDIANTESACTIVOSGENERAL = "PERIODO_ADMISION";
    static String COLUMNACODIGOESTUDIANTEACTIVOSGENERAL = "CODIGO";
    static String COLUMNAPROGRAMAESTUDIANTESACTIVOSGENERAL = "PROGRAMA";
    static String COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL = "CURRICULO_1";
    static String COLUMNASEDEESTUDIANTESACTIVOSGENERAL = "SEDE_1";
    static String COLUMNANIVELESTUDIANTESACTIVOSGENERAL = "NIVEL";//Pregrado UG, Licenciatura LI,Diplomado DI

    //Cabeceras mínimas estudiantes activos argos
    static String COLUMNACODIGOESTUDIANTESACTIVOSARGOS = "CODIGO";
    static String COLUMNASEDEESTUDIANTESACTIVOSARGOS = "SEDE";
    static String COLUMNAPROGRAMAESTUDIANTESACTIVOSARGOS = "CODIGO_PROGRAMA_1";
    static String COLUMNANOMBRESESTUDIANTESACTIVOSARGOS = "NOMBRES";
    static String COLUMNAALFANUMERICOESTUDIANTESACTIVOSARGOS = "CODIGO_MATERIA";

    //Cabeceras mínimas para el historial académico
    static String COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO = "ID_ESTUDIANTE";
    static String COLUMNANOMBREESTUDIANTEHISTORIALACADEMICO = "NOMBRE_ESTUDIANTE";
    static String COLUMNASEDEHISTORIALACADEMICO = "SEDE";
    static String COLUMNAPROGRAMAHISTORIALACADEMICO = "PROGRAMA";
    static String COLUMNAALFAHISTORIALACADEMICO = "ALFA";
    static String COLUMNANUMERICOHISTORIALACADEMICO = "NUMERI";
    static String COLUMNADEFINITIVAHISTORIALACADEMICO = "DEFINITIVA";

    //Estos son los nombres de los encabezados que deben existir en la hoja
    //carreras de la malla curricular base, la cual no es un archivo CSV, al menos
    //en la versión del 4 de marzo
    static String COLUMNADESCRIPCIONCARRERA = "carrera";
    static String COLUMNANOMENCLATURACARRERA = "nomenclatura";
    static String COLUMNACURRICULOCARRERA = "curriculo1banner";

    static String NOTACUALITATIVAAPROBADO = "A";

    static double NOTAAPROBAR = 3;

    public ConsultasSQL() {
    }

    /**
     * Crea una base de datos en Sqlite en la ruta guardada en la variable
     * RUTABD Si ya está creada la base de datos, recomienda usar el método
     * conexión
     *
     * @param String nombre es el nombre de la base de datos a crear
     * @return Objeto Connection con la conexión a la base de datos creada
     */
    public static Connection crearBD(String nombre) {
        Connection conn = null;
        try {

            if (new File(RUTABD + "" + nombre + ".db").exists()) {
                JOptionPane.showMessageDialog(null, "La base de datos " + nombre + " ya existe, se recomienda no crearla sino conectarse a la misma");
            } else {
                String url = "jdbc:sqlite:" + RUTABD + "" + nombre + ".db";
                conn = DriverManager.getConnection(url);
            }

        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * Crea una conexión a una base de datos Sqlite, en la ruta guardada en la
     * variable RUTABD
     *
     * @param nombreRutaBD Nombre de la base de datos a la cual se realizará la
     * conexión
     * @return Objeto Connection con la conexión a la base de datos
     */
    public static Connection conexionBD(String nombreRutaBD) {
        Connection connection = null;

        if (new File(RUTABD + "" + nombreRutaBD + ".db").exists()) {
            try {
                String url = "jdbc:sqlite:" + RUTABD + "" + nombreRutaBD + ".db";
                connection = DriverManager.getConnection(url);
                System.out.println("Conexión a la base de datos " + nombreRutaBD + " creada");
            } catch (Exception e) {
                Validaciones.mostrarVentanaError(e);
                e.printStackTrace();
            }
        } else {
            Validaciones.mostrarVentanaError("La base de datos " + nombreRutaBD + " no existe");
        }

        return connection;
    }

    /**
     * Crea la tabla historialacademico en la base de datos para el objeto
     * Connection dada
     *
     * @param conn conexión a la base de datos
     */
    public static void crearTablaHistorialAcademico(Connection conn) {

        String sql = "CREATE TABLE if not exists historialacademico ("
                + "ID_ESTUDIANTE        INT         NOT NULL,"
                + "NOMBRE_ESTUDIANTE    TEXT        NOT NULL,"
                + "RECTORIA             TEXT,"
                + "DESCRIPCION_RECTORIA TEXT,"
                + "SEDE                 TEXT,"
                + "DESCRIPCION_SEDE     TEXT,"
                + "FACULTA              TEXT,"
                + "DESCRIPCION_FACULTAD TEXT,"
                + "PROGRAMA             TEXT,"
                + "DESCRIPCION_PROGRAMA TEXT,"
                + "NIVEL                TEXT,"
                + "DESCRIPCION_NIVEL    TEXT,"
                + "JORNADA              TEXT,"
                + "PERIODO              INT         NOT NULL,"
                + "NRCS                 TEXT        NOT NULL,"
                + "ALFA                 TEXT        NOT NULL,"
                + "NUMERI               TEXT        NOT NULL,"
                + "DESCRIPION           TEXT,"
                + "DEFINITIVA           VARCHAR (5) NOT NULL,"
                + "PROMEDIO_SEM         TEXT,"
                + "PROM_ACU             TEXT,"
                + "FORMA_CAL            TEXT,"
                + "COMENTARIO           TEXT"
                + ");";

        try {
            Statement stm = conn.createStatement();
            stm.execute(sql);
        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }
    }

    /**
     * Crea la parte de una consulta SQL correspondiente al insert de un
     * PreparedStatement,pero sólo con sus campos y values, se complementa con
     * el método sqlInsertarTablaPstm
     *
     * @param cadenaCabeceras nombres de los campos separados por comas
     * @return String con la parte de la consulta SQL con sus campos y values
     */
    public static String crearCadenaCamposValuesInsertPstm(String cadenaCabeceras) {
        String cadenaCampos = "";

        try {
            CSVParser parserCabeceras = CSVParser.parse(cadenaCabeceras, CSVFormat.newFormat(',').withFirstRecordAsHeader());
            List<String> cabeceras = parserCabeceras.getHeaderNames();

            for (int i = 0; i < cabeceras.size(); i++) {
                if (i == 0) {
                    cadenaCampos = "(" + cabeceras.get(i).trim() + ",";
                } else if (i > 0 && i < cabeceras.size() - 1) {
                    cadenaCampos = cadenaCampos + cabeceras.get(i).trim() + ",";
                } else if (i == cabeceras.size() - 1) {
                    cadenaCampos = cadenaCampos + cabeceras.get(i).trim() + ")";
                }
            }

            cadenaCampos = cadenaCampos + "values";

            for (int i = 0; i < cabeceras.size(); i++) {
                if (i == 0) {
                    cadenaCampos = cadenaCampos + "(" + "?" + ",";
                } else if (i > 0 && i < cabeceras.size() - 1) {
                    cadenaCampos = cadenaCampos + "?" + ",";
                } else if (i == cabeceras.size() - 1) {
                    cadenaCampos = cadenaCampos + "?" + ")";
                }
            }
        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }

        return cadenaCampos;
    }

    /**
     * Esta tabla temporal de estudiantes activos sin repetir toma los
     * estudiantes activos en argos no del archivo general de estudiantes Además
     * de eso le agrega el campo CURRICULO_1 que determina el curriculo al cual
     * está matriculado el estudiante
     *
     * @param conn
     * @return
     */
    public static String crearTablaTemporalEstudiantesActivosArgosSinRepetir(Connection conn) {
        try {
            String nombreTablaEstudiantesActivos = "tablaTemporalParaCreacionEstudiantesActivosArgos";

            String sql = "create temporary table IF NOT EXISTS "
                    + nombreTablaEstudiantesActivos
                    + " as "
                    + "select cast(" + ConsultasSQL.COLUMNACODIGOESTUDIANTESACTIVOSARGOS + " as INTEGER)  as CODIGO, "
                    + " " + ConsultasSQL.COLUMNANOMBRESESTUDIANTESACTIVOSARGOS + ", "
                    + " " + ConsultasSQL.COLUMNASEDEESTUDIANTESACTIVOSARGOS + ", "
                    + " " + ConsultasSQL.COLUMNAPROGRAMAESTUDIANTESACTIVOSARGOS
                    + " from "
                    + TABLAESTUDIANTESACTIVOSNRC
                    + " group by " + ConsultasSQL.COLUMNACODIGOESTUDIANTESACTIVOSARGOS
                    + " ORDER BY " + ConsultasSQL.COLUMNANOMBRESESTUDIANTESACTIVOSARGOS;

            conn.createStatement().execute(sql);

            String nombreTablaCodigoCurriculo = ConsultasSQL.NOMBRETABLATEMPORAL_CODIGOCURRICULO;
            String sql2 = "create temporary table IF NOT EXISTS "
                    + nombreTablaCodigoCurriculo
                    + " as "
                    + " select "
                    + "cast(" + ConsultasSQL.COLUMNACODIGOESTUDIANTEACTIVOSGENERAL + " as integer) as CODIGO"
                    + ","
                    + ConsultasSQL.COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL
                    + " from "
                    + ConsultasSQL.TABLAESTUDIANTESACTIVOSGENERAL;
            //+ " limit 100 ";
            conn.createStatement().execute(sql2);

            String sql3 = "create temporary table IF NOT EXISTS "
                    + ConsultasSQL.NOMBRETABLATEMPORAL_ESTUDIANTESACTIVOS
                    + " as "
                    + "select " + nombreTablaEstudiantesActivos + ".*,"
                    + nombreTablaCodigoCurriculo + "." + ConsultasSQL.COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL
                    + " from "
                    + nombreTablaEstudiantesActivos
                    + " left join "
                    + nombreTablaCodigoCurriculo
                    + " on "
                    + nombreTablaEstudiantesActivos + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTESACTIVOSARGOS
                    + "="
                    + nombreTablaCodigoCurriculo + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEACTIVOSGENERAL + "";
            System.out.println(sql3);
            conn.createStatement().execute(sql3);

        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo crear la tabla de estudiantes "
                    + " activos sin repetir desde argos", e);
        }
        return NOMBRETABLATEMPORAL_ESTUDIANTESACTIVOS;
    }

    /**
     * Toma toda la cadena sql generada por el metodo sqlinsertablapstm
     * crearcadenacamposvaluesinsert y la ejecuta
     *
     * @param conn Conexión a la base de datos
     * @param cadenaTipos cadena de tipos de valores, texto o numero, separados
     * por coma
     * @param sql consulta sql insert a ser ejecutada
     * @param rutaArchivoCSV Archivo CSV del cual se tomaran los valores a
     * insertar
     */
    public static void crearEjecutarInsertPreparedStatement(Connection conn, String cadenaTipos, String sql, String rutaArchivoCSV) {

        int filaError = -1;
        String columnaError = "";
        String valorError = "";

        try {
            CSVParser parser = obtenerCSVParserCSV(rutaArchivoCSV);
            List<CSVRecord> listadoRecords = parser.getRecords();
            List<String> cabecerasCSV = parser.getHeaderNames();
            List<String> listadoCadenaTipos = CSVParser.parse(cadenaTipos, CSVFormat.newFormat(',').withFirstRecordAsHeader()).getHeaderNames();

            PreparedStatement pstm = conn.prepareStatement(sql);

            int cantidadRecords = listadoRecords.size();
            int cantidadColumnasRecord = parser.getHeaderNames().size();

            for (int i = 0; i < cantidadRecords; i++) {
                filaError = i;
                columnaError = "";
                valorError = "";

                for (int j = 0; j < cantidadColumnasRecord; j++) {

                    String tipo = listadoCadenaTipos.get(j);
                    String nombreCabecera = cabecerasCSV.get(j);
                    columnaError = nombreCabecera;
                    valorError = listadoRecords.get(i).get(nombreCabecera);

                    if (tipo.trim().equals("texto")) {
                        pstm.setString((j + 1), listadoRecords.get(i).get(nombreCabecera));
                    }
                    if (tipo.trim().equals("numero")) {
                        pstm.setInt((j + 1), Integer.parseInt(listadoRecords.get(i).get(nombreCabecera)));
                    }
                }
                pstm.executeUpdate();
            }
        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
            Validaciones.mostrarVentanaError("Error en los valores del archivo " + rutaArchivoCSV + " fila " + filaError + " columna " + columnaError + " valor inválido " + valorError);
            e.printStackTrace();
        }

    }

    /**
     *
     * @param rutaCSV la ruta al archivo csv que se quiere parsear
     * @return CSVParser
     */
    public static CSVParser obtenerCSVParserCSV(String rutaCSV) {
        CSVParser csvParser = null;
        try {

            InputStream is = new FileInputStream(new File(rutaCSV));
            CSVParser parserArchivo = CSVParser.parse(is, Charset.forName("ISO-8859-1"), CSVFormat.newFormat(SEPARADORARCHIVOSCSV).withFirstRecordAsHeader());

            return parserArchivo;
        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
        }
        return csvParser;
    }

    public static List<CSVRecord> obtenerRecordsCSVParser(CSVParser parser) {
        try {
            return parser.getRecords();
        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Devuelve la cadena completa al concatenar la parte del insert generada
     * por crearCadenaCamposValuesInsertPST
     *
     * @param cadenaSqlPstm cadena que devuelve el método
     * crearCadenaCamposValuesInsertPST
     * @param nombreTabla nombre de la tabla al cual se quiere insertar los
     * valores
     * @return String cadena insert completa para un PreparedStatement
     */
    public static String sqlInsertarTablaPstm(String cadenaSqlPstm, String nombreTabla) {
        return "insert into " + nombreTabla + " " + cadenaSqlPstm;
    }

    public static void insertarDatosCSVaTabla(String rutaArchivo, String nombreTabla, String cadenaCabeceras, String cadenaTipos) {
        try {
            Connection conn = DriverManager.getConnection(CONEXIONSQLITEOFERTAEDUCATIVA);
            String sql = sqlInsertarTablaPstm(crearCadenaCamposValuesInsertPstm(cadenaCabeceras), nombreTabla);
            crearEjecutarInsertPreparedStatement(conn, cadenaTipos, sql, rutaArchivo);
            JOptionPane.showMessageDialog(null, "Se han ingresado los datos desde " + rutaArchivo + " satisfactoriamente");
        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
        }
    }

    public static File codificarArchivoAEncoding(String rutaOrigen,
            String encodingOrigen, String encodingDestino, boolean incluirPrimeraLinea) {

        File archivoOrigen, archivoDestino = null;

        try {
            archivoOrigen = new File(rutaOrigen);
            archivoDestino = File.createTempFile("archivocodificado", ".csv");//new File("archivoCodificado.csv");
            archivoDestino.deleteOnExit();

            FileInputStream fis = new FileInputStream(archivoOrigen);
            FileOutputStream fos = new FileOutputStream(archivoDestino);

            System.out.println("" + Charset.defaultCharset());

            InputStreamReader isr = new InputStreamReader(fis, encodingOrigen);
            OutputStreamWriter osw = new OutputStreamWriter(fos, encodingDestino);

            BufferedReader br = new BufferedReader(isr);

            if (!incluirPrimeraLinea) {
                br.readLine();
            }

            int caracter = 0;
            while ((caracter = br.read()) != -1) {
                osw.write(caracter);
            }
            osw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return archivoDestino;
    }

    public static void importarBaseDatosSqlite(String baseDatos,
            String tabla,
            String rutaCSVOrigen,
            String codificacionOrigen,
            String codificacionDestino,
            boolean primeraLineaComoNombresCampos
    ) {

        rutaCSVOrigen = codificarArchivoAEncoding(rutaCSVOrigen, codificacionOrigen, codificacionDestino, primeraLineaComoNombresCampos).getAbsolutePath();

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("cmd.exe", "/c", "sqlite3/sqlite3 \".open '" + RUTABD + baseDatos + ".db' \" "
                + " \".mode csv\" "
                + " \".separator ,\" "
                + " \".import '" + rutaCSVOrigen + "' " + tabla + "\" "
        );

        try {

            Process process = processBuilder.start();

            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(process.getInputStream()));

//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
            int exitCode = process.waitFor();

//            process.destroy();
            System.out.println("\nExited with error code : " + exitCode);
            if (exitCode != 0) {
                JOptionPane.showMessageDialog(null, "Ocurrió un error durante la importación de las bases de datos");
            }

            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void eliminarTablaSiExiste(Connection conn, String nombreBaseDatos, String nombreTabla) {
        try {
            conn.createStatement().execute("drop table if exists " + nombreTabla);
        } catch (Exception e) {
            Validaciones.mostrarVentanaError("Ocurrió un problema al borrar la tabla " + nombreTabla);
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }
    }

    public static boolean tablaExiste(Connection conn, String nombreTabla) {
        boolean existe = false;

        String sql = "select count(*) from sqlite_master where name='" + nombreTabla + "'";
        int numeroTablas = 0;

        try {
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                numeroTablas = rs.getInt(1);
            }
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo determinar si la tabla " + nombreTabla + " existe ", e);
        }

        if (numeroTablas > 0) {
            existe = true;
        }

        return existe;
    }

    public static boolean tablaTemporalExiste(Connection conn, String nombreTabla) {
        boolean existe = false;

        String sql = "select count(*) from sqlite_temp_master where name='" + nombreTabla + "'";
        int numeroTablas = 0;

        try {
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                numeroTablas = rs.getInt(1);
            }
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo determinar si la tabla " + nombreTabla + " existe ", e);
        }

        if (numeroTablas > 0) {
            existe = true;
        }

        return existe;
    }

    public static void cargarTablaCarrerasBD(Connection conn, String rutaMallaBase) {
        String rutaCSVOrigen = FuncionesExcel.convertirHojaExcelAArchivoCSVTemporal(rutaMallaBase, TABLACARRERAS).getAbsolutePath();
        eliminarTablaSiExiste(conn, NOMBREBDOFERTAEDUCATIVA, TABLACARRERAS);
        importarBaseDatosSqlite(NOMBREBDOFERTAEDUCATIVA, TABLACARRERAS, rutaCSVOrigen, CODIFICACIONORIGEN, CODIFICACIODESTINO, true);
    }

    public static void cargarTablaAsignaturasBD(Connection conn, String rutaMallaBase) {
        String rutaCSVOrigen = FuncionesExcel.convertirHojaExcelAArchivoCSVTemporal(rutaMallaBase, TABLAASIGNATURAS).getAbsolutePath();
        eliminarTablaSiExiste(conn, NOMBREBDOFERTAEDUCATIVA, TABLAASIGNATURAS);
        importarBaseDatosSqlite(NOMBREBDOFERTAEDUCATIVA, TABLAASIGNATURAS, rutaCSVOrigen, CODIFICACIONORIGEN, CODIFICACIODESTINO, true);
    }

    public static ResultSet historialAcademicoEstudianteVsCurriculoMatriculado(Connection conn, int idEstudiante) {
        ResultSet rs = null;

        String sqlIdCarreraParaIdEstudianteDado = "SELECT id_carrera from carreras "
                + "where "
                + "(trim(carreras.nomenclatura),trim(carreras.curriculo1banner))"
                + "in (select trim(PROGRAMA), trim(CURRICULO_1) "
                + "from "
                + "estudiantesactivosgeneral "
                + "where "
                + "cast(CODIGO as real)=" + idEstudiante + ")) ";

        String sqlHistorialAcademicoEstudianteDadoDeHistorialGeneral = "select PROGRAMA, "
                + "DESCRIPION, "
                + "NOMBRE_ESTUDIANTE, "
                + "DEFINITIVA, "
                + "ALFA,"
                + "NUMERI "
                + "from "
                + "historialacademico where cast(ID_ESTUDIANTE as real)=" + idEstudiante;

        String sqlAlfaNumericoHomologado = "(select alfaNumericoReciente from  "
                + "    alfanumericoshomologados "
                + "    where "
                + "    alfaNumericoAntiguo=trim(tabla2.ALFA) || trim(tabla2.NUMERI)  "
                + "    AND "
                + "    alfaNumericoReciente=trim(tabla1.alfa) || trim(replace(tabla1.numerico,\".0\",\"\")) "
                + "    and "
                + "    nomenclatura=tabla2.PROGRAMA)";

        String sqlTablaTemporalMateriasIdCarreraDado = "create TEMPORARY table if not exists  t1" + idEstudiante + " as \n"
                + "select asignatura, id_carrera, semestre, cast(creditos as integer) as creditos, alfa, numerico \n"
                + "from \n"
                + "asignaturas \n"
                + "where \n"
                + "asignaturas.id_carrera=(" + sqlIdCarreraParaIdEstudianteDado;

        String sqlTablaTemporarHistorialAcademicoEstudiante = "create temporary table if not exists t2" + idEstudiante + " as "
                + sqlHistorialAcademicoEstudianteDadoDeHistorialGeneral;

        String sqlJoin = "select "
                + "tabla1.asignatura, "
                + "tabla1.id_carrera, "
                + "tabla1.semestre, "
                + "tabla1.creditos, "
                + "tabla2.PROGRAMA, "
                + "tabla1.alfa || trim(replace(tabla1.numerico,\".0\",\"\")) as alfaNumericoActual, "
                + "trim(tabla2.ALFA) || trim(tabla2.NUMERI) as alfaNumericoCursado,"
                + "tabla2.DESCRIPION,"
                + "tabla2.NOMBRE_ESTUDIANTE, "
                + "tabla2.DEFINITIVA "
                + "from t1" + idEstudiante + " as tabla1 "
                + "left join t2" + idEstudiante + " as tabla2 "
                + "on trim(tabla1.alfa) || trim(replace(tabla1.numerico,\".0\",\"\")) = "
                + sqlAlfaNumericoHomologado
                + " "
                + "OR "
                + "trim(tabla1.alfa) || trim(replace(tabla1.numerico,\".0\",\"\")) = alfaNumericoCursado "
                + "order by cast(tabla1.semestre as real) ASC";

        String sqlConteoRegistros = "select count(*) from t2" + idEstudiante;
        try {

            conn.createStatement().execute(sqlTablaTemporalMateriasIdCarreraDado);
            conn.createStatement().execute(sqlTablaTemporarHistorialAcademicoEstudiante);

            if (conn.createStatement().executeQuery(sqlConteoRegistros).getInt(1) == 0) {
                return null;
            }

            return conn.createStatement().executeQuery(sqlJoin);
        } catch (Exception e) {

            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }

        return rs;
    }

    public static String alfaNumericoHomologado(String nomenclaturaCarrera, String alfaNumericoRecienteCarreraMatriculada, String alfaNumericoCursado) {
        String alfaNumericoHomologado = "";

        ResultSet rs = null;
        Connection conn = ConsultasSQL.conexionBD(ConsultasSQL.NOMBREBDOFERTAEDUCATIVA);
        String sql = "select alfaNumericoReciente from "
                + "    " + ConsultasSQL.TABLAALFANUMERICOSHOMOLOGADOS + " "
                + "    where "
                + "    alfaNumericoAntiguo='" + alfaNumericoCursado + "' "
                + "    AND "
                + "    alfaNumericoReciente='" + alfaNumericoRecienteCarreraMatriculada + "' "
                + "    and "
                + "    nomenclatura='" + nomenclaturaCarrera + "'";
        System.out.println(sql);
        try {
            rs = conn.createStatement().executeQuery(sql);

            ArrayList<String> alfasEncontrados = new ArrayList<String>();
            while (rs.next()) {
                alfasEncontrados.add(rs.getString(1));
            }

            int tamano = alfasEncontrados.size();

            if (tamano == 0) {
                alfaNumericoHomologado = alfaNumericoCursado;
            } else {
                alfaNumericoHomologado = alfasEncontrados.get(0);
            }
        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }

        return alfaNumericoHomologado;
    }

    public static String obtenerNombreEstudiantesActivosGeneral(Connection conn, int idEstudiante) {
        String nombre = "";

        String sql = "select " + COLUMNANOMBREESTUDIANTESACTIVOSGENERAL
                + " from "
                + TABLAESTUDIANTESACTIVOSGENERAL
                + " where cast(" + COLUMNACODIGOESTUDIANTEACTIVOSGENERAL + " as real)=" + idEstudiante + "  limit 1";
        try {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                return rs.getString(1);
            }
        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }
        return nombre;
    }

    public static int obtenerPeriodoAdmisiónEstudiantesActivosGeneral(Connection conn, int idEstudiante) {
        int periodoAdmision = 0;

        String sql = "select "
                + COLUMNAPERIODOADMISIONESTUDIANTESACTIVOSGENERAL
                + " from " + TABLAESTUDIANTESACTIVOSGENERAL
                + " where cast(" + COLUMNACODIGOESTUDIANTEACTIVOSGENERAL + " as real)=" + idEstudiante + "  limit 1";
        try {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                return Integer.parseInt((String) rs.getObject(1));
            }
        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }
        return periodoAdmision;
    }

    public static String obtenerProgramaMatriculadoEstudiantesActivosGeneral(Connection conn, int idEstudiante) {
        String programaMatriculado = "";

        String sql = "select "
                + COLUMNAPROGRAMAESTUDIANTESACTIVOSGENERAL
                + " from " + TABLAESTUDIANTESACTIVOSGENERAL
                + " where cast(" + COLUMNACODIGOESTUDIANTEACTIVOSGENERAL + " as real)=" + idEstudiante + "  limit 1";
        try {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                return rs.getString(1);
            }
        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }
        return programaMatriculado;
    }

    public static String obtenerCurriculoEstudiantesActivosGeneral(Connection conn, int idEstudiante) {
        String nombreCurriculo = "";

        String sql = "select "
                + COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL
                + " from " + TABLAESTUDIANTESACTIVOSGENERAL
                + " where cast(" + COLUMNACODIGOESTUDIANTEACTIVOSGENERAL + " as real)=" + idEstudiante + "  limit 1";
        try {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                return rs.getString(1);
            }
        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }
        return nombreCurriculo;
    }

}
