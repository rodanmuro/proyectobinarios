/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import static proyectobinarios.ConsultasSQL.SEPARADORARCHIVOSCSV;

/**
 *
 * @author Usuario
 */
public class Reportes {

    public Reportes() {
    }

    /**
     * Este reporte devuelve un CSVParser para los camposRequeridos con la
     * condición dada en los camposCondición y que se cumplan uno a uno los
     * valoresBuscados en el archivo rutaArchivoCSV
     *
     * @param camposRequeridos
     * @param camposCondicion
     * @param valoresBuscados
     * @param rutaArchivoCSV
     * @return
     */
    public static CSVParser reporteY(ArrayList<String> camposRequeridos, ArrayList<String> camposCondicion, ArrayList<Object> valoresBuscados, String rutaArchivoCSV) {
        CSVParser reporteCSV = null;

        try {
            CSVParser parser = ConsultasSQL.obtenerCSVParserCSV(rutaArchivoCSV);
            List<CSVRecord> listadoRecords = parser.getRecords();

            int tamanoRecords = listadoRecords.size();

            String cadenaResultante = "";
            String filaResultante = "";
            String filaCondicion = "";

            for (int i = 0; i < camposRequeridos.size(); i++) {
                String separador = ",";
                if (i == camposRequeridos.size() - 1) {
                    separador = "";
                }
                filaResultante = filaResultante + camposRequeridos.get(i) + separador;
            }
            cadenaResultante = cadenaResultante + filaResultante + "\n";

            for (int i = 0; i < tamanoRecords; i++) {
                String separadorLinea = "\n";
                filaResultante = "";
                filaCondicion = "";

                if (i == tamanoRecords - 1) {
                    separadorLinea = "";
                }

                for (int j = 0; j < camposRequeridos.size(); j++) {
                    String separador = ",";
                    if (j == camposRequeridos.size() - 1) {
                        separador = "";
                    }
                    filaResultante = filaResultante + listadoRecords.get(i).get(camposRequeridos.get(j)) + separador;
                }

                for (int j = 0; j < camposCondicion.size(); j++) {
                    String separador = ",";
                    if (j == camposCondicion.size() - 1) {
                        separador = "";
                    }
                    filaCondicion = filaCondicion + listadoRecords.get(i).get(camposCondicion.get(j)) + separador;
                }

                if (csvIguales(filaCondicion, arrayListACSV(valoresBuscados))) {
                    cadenaResultante = cadenaResultante + filaResultante + separadorLinea;
                }
            }

            System.out.println("" + cadenaResultante);

            reporteCSV = CSVParser.parse(cadenaResultante, CSVFormat.newFormat(';').withFirstRecordAsHeader());
        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }

        return reporteCSV;
    }
    
    public static CSVParser reporteY(ArrayList<String> camposRequeridos, ArrayList<String> camposCondicion, ArrayList<Object> valoresBuscados, List<CSVRecord> listadoRecords) {
        CSVParser reporteCSV = null;

        try {

            int tamanoRecords = listadoRecords.size();

            String cadenaResultante = "";
            String filaResultante = "";
            String filaCondicion = "";

            for (int i = 0; i < camposRequeridos.size(); i++) {
                String separador = ",";
                if (i == camposRequeridos.size() - 1) {
                    separador = "";
                }
                filaResultante = filaResultante + camposRequeridos.get(i) + separador;
            }
            cadenaResultante = cadenaResultante + filaResultante + "\n";

            for (int i = 0; i < tamanoRecords; i++) {
                String separadorLinea = "\n";
                filaResultante = "";
                filaCondicion = "";

                if (i == tamanoRecords - 1) {
                    separadorLinea = "";
                }

                for (int j = 0; j < camposRequeridos.size(); j++) {
                    String separador = ",";
                    if (j == camposRequeridos.size() - 1) {
                        separador = "";
                    }
                    filaResultante = filaResultante + listadoRecords.get(i).get(camposRequeridos.get(j)) + separador;
                }

                for (int j = 0; j < camposCondicion.size(); j++) {
                    String separador = ",";
                    if (j == camposCondicion.size() - 1) {
                        separador = "";
                    }
                    filaCondicion = filaCondicion + listadoRecords.get(i).get(camposCondicion.get(j)) + separador;
                }

                if (csvIguales(filaCondicion, arrayListACSV(valoresBuscados))) {
                    cadenaResultante = cadenaResultante + filaResultante + separadorLinea;
                }
            }

            reporteCSV = CSVParser.parse(cadenaResultante, CSVFormat.newFormat(';').withFirstRecordAsHeader());
        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }

        return reporteCSV;
    }

    public static void agregarCabecerasReporteCSV(String cadenaResultante, String filaResultante, ArrayList<String> camposRequeridos) {
        for (int i = 0; i < camposRequeridos.size(); i++) {
            String separador = ",";
            if (i == camposRequeridos.size() - 1) {
                separador = "";
            }
            filaResultante = filaResultante + camposRequeridos.get(i) + separador;
        }
        cadenaResultante = cadenaResultante + filaResultante + "\n";
    }

    public static boolean csvIguales(String csv1, String csv2) {
        try {
            CSVParser parser1 = CSVParser.parse(csv1, CSVFormat.newFormat(';'));
            CSVParser parser2 = CSVParser.parse(csv2, CSVFormat.newFormat(';'));

            List<CSVRecord> listadoRecordsParser1 = parser1.getRecords();
            List<CSVRecord> listadoRecordsParser2 = parser2.getRecords();

            for (int i = 0; i < listadoRecordsParser1.get(0).size(); i++) {
                if (!listadoRecordsParser1.get(0).get(i).equals(listadoRecordsParser2.get(0).get(i))) {
                    return false;
                }
            }

        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static String arrayListACSV(ArrayList<Object> listaObjetos) {
        String cadena = "";
        for (int i = 0; i < listaObjetos.size(); i++) {
            String separador = ",";
            if (i == listaObjetos.size() - 1) {
                separador = "";
            }
            cadena = cadena + listaObjetos.get(i) + separador;
        }
        return cadena;
    }
    
    

}
