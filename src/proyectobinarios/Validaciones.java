/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.apache.commons.csv.*;

/**
 *
 * @author Usuario
 */
public class Validaciones {

    /**
     * Clase con funciones estáticas que se encargan de validar los valores en
     * los archivos CSV, o bien en algunos textfield del aplicativo
     */
    public Validaciones() {
    }

    /**
     * Muestra un JOption Pane con el mensaje de error de la Excepción recogida
     *
     * @param e
     */
    public static void mostrarVentanaError(Exception e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        JOptionPane.showMessageDialog(null, errors.toString());
    }

    /**
     * Muestra un JOptionPane con el mensaje de error
     *
     * @param error String con el error a mostrar
     */
    public static void mostrarVentanaError(String error) {
        JOptionPane.showMessageDialog(null, error);
    }
    
    public static void mostrarErroresTotal(String textoPersonalizado, Exception e) {
        Validaciones.mostrarVentanaError(e);
        Validaciones.mostrarVentanaError(textoPersonalizado);
        e.printStackTrace();
    }

    /**
     * Cuenta la cantidad de cabeceras existentes en la cadenaCabeceras,
     * separadas por coma y las compara con la cantidad de valores separados por
     * coma en cadenaTipoDatos
     *
     * @param cadenaCabeceras
     * @param cadenaTipoDatos
     * @return ArrayList<String> Listado con los errores encontrados
     */
    public static ArrayList<String> validarCantidadCabecerasTiposDatos(String cadenaCabeceras, String cadenaTipoDatos) {
        ArrayList<String> listaValidaciones = new ArrayList<String>();
        listaValidaciones.add("ok");

        String mensajeCabeceras = " el texto de cabeceras ";
        String mensajeTipos = " el texto de tipos de datos ";

        try {
            CSVParser parserCabeceras = CSVParser.parse(cadenaCabeceras, CSVFormat.newFormat(',').withFirstRecordAsHeader());
            CSVParser parserTipoDatos = CSVParser.parse(cadenaTipoDatos, CSVFormat.newFormat(',').withFirstRecordAsHeader());

            if (parserCabeceras.getHeaderNames().size() > parserTipoDatos.getHeaderNames().size()) {
                listaValidaciones.set(0, "el número de elementos en " + mensajeCabeceras + " es <b>MAYOR</b> que en la " + mensajeTipos);
            }
            if (parserCabeceras.getHeaderNames().size() < parserTipoDatos.getHeaderNames().size()) {
                listaValidaciones.set(0, "el número de elementos en " + mensajeCabeceras + " es <b>MENOR</b> que en la " + mensajeTipos);
            }

        } catch (Exception e) {
            mostrarVentanaError(e);
        }

        return listaValidaciones;
    }

    /**
     * Determina si en la cadena de tipos de datos los valores separados por las
     * comas corresponden a los valores texto, texto-novacio, numero,
     * mumero-novacio
     *
     * @param cadenaTiposDatos
     * @return ArrayList<String> con los valores no válidos
     */
    public static ArrayList<String> validarCadenaTipoDatos(String cadenaTiposDatos) {
        ArrayList<String> listaValidaciones = new ArrayList<String>();
        listaValidaciones.add("ok");

        try {
            CSVParser parserCabeceras = CSVParser.parse(cadenaTiposDatos, CSVFormat.newFormat(',').withFirstRecordAsHeader());
            for (String cabecera : parserCabeceras.getHeaderNames()) {
                if (!cabecera.trim().equals("texto")
                        && !cabecera.trim().equals("numero")) {
                    if (listaValidaciones.get(0).equals("ok")) {
                        listaValidaciones.set(0, "tipo de dato " + cabecera + " no válido");
                    } else {
                        listaValidaciones.add("tipo de dato " + cabecera + " no válido");
                    }
                }
            }
        } catch (Exception e) {
            mostrarVentanaError(e);
        }

        return listaValidaciones;
    }

    /**
     * Valida las cabeceras para un archivo CSV dado, comparándolas con una
     * cadena CSV Se debe tener en cuenta que estas cabeceras tienen en cuenta
     * mayúsculas y minúsculas y las cabeceras en el archivo están en la primera
     * fila
     *
     * @param cabecerasDefinidas
     * @param rutaArchivo
     * @return ArrayList<String> con los errores encontrados
     */
    public static ArrayList<String> validarCabecerasArchivoCSV(String cabecerasDefinidas, String rutaArchivo) {
        ArrayList<String> listaValidaciones = new ArrayList<String>();
        listaValidaciones.add("ok");

        try {
            CSVParser parserCabecerasDefinidas = CSVParser.parse(cabecerasDefinidas, CSVFormat.newFormat(',').withFirstRecordAsHeader());
            List<String> listadoCabecerasDefinidas = parserCabecerasDefinidas.getHeaderNames();

//            FileReader fr = new FileReader(new File(rutaArchivo));
//            CSVParser parserArchivo = CSVParser.parse(fr, CSVFormat.newFormat(';').withFirstRecordAsHeader());
//            List<String> listadoCabecerasArchivo = parserArchivo.getHeaderNames();
            InputStream is = new FileInputStream(new File(rutaArchivo));
            CSVParser parserArchivo = CSVParser.parse(is, Charset.forName("ISO-8859-1"), CSVFormat.newFormat(';').withFirstRecordAsHeader());
            List<String> listadoCabecerasArchivo = parserArchivo.getHeaderNames();

            for (String cabeceraDefinida : listadoCabecerasDefinidas) {
                cabeceraDefinida = cabeceraDefinida.trim().replaceAll(" ", "");

                for (int i = 0; i < listadoCabecerasArchivo.size(); i++) {
                    if (cabeceraDefinida.equals(listadoCabecerasArchivo.get(i).trim())) {
                        break;
                    }
                    if (i == listadoCabecerasArchivo.size() - 1) {
                        if (listaValidaciones.size() == 1 && listaValidaciones.get(0).equals("ok")) {
                            listaValidaciones.set(0, "la cabecera definida " + cabeceraDefinida + " no se encuentra en el archivo " + rutaArchivo);
                        } else {
                            listaValidaciones.add("la cabecera definida " + cabeceraDefinida + " no se encuentra en el archivo " + rutaArchivo);
                        }
                    }
                }
            }

        } catch (Exception e) {
            mostrarVentanaError(e);
            e.printStackTrace();
        }

        return listaValidaciones;
    }

    public static boolean esTexto(String dato) {
        boolean esTexto = true;

        if (dato == null) {
            return false;
        }

        return esTexto;
    }

    public static boolean esTextoNoVacio(String dato) {
        boolean esTextoNoVacio = true;

        if (dato.trim().equals("")) {
            return false;
        }

        return esTextoNoVacio;
    }

    public static boolean esNumero(String dato) {
        try {
            Double.parseDouble(dato);
//            Integer.parseInt(dato);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean esNumeroNoVacio(String dato) {
        if (esNumero(dato) && esTextoNoVacio(dato)) {
            return true;
        }
        return false;
    }

    /**
     * Función auxiliar que inserta la cadena error en un ArrayList<String> dado
     * teniendo en cuenta que si es el primero cambiará el ok del primer
     * registro por el error dado, y luego agregará los otros
     *
     * @param listadoValidaciones
     * @param error
     */
    public static void insertarStringValidacion(ArrayList<String> listadoValidaciones, String error) {
        if (listadoValidaciones.get(0).equals("ok") && listadoValidaciones.size() == 1) {
            listadoValidaciones.set(0, error);
        } else {
            listadoValidaciones.add(error);
        }
    }

    /**
     * Valida si la columna de un CSV cumple con el criterio del tipo de dato
     * válido
     *
     * @param listadoRecords Records que se obtienen desde la función CSVParser
     * parser.getRecords()
     * @param cabecera Nombre de la cabecera en la cual se validarán los datos
     * @param tipoDato toma los valores texto, texto-novacio, numero,
     * numero-novacio
     * @return ArrayList<String> con los errores encontrados o ok
     */
    public static ArrayList<String> validarColumnaCSV(List<CSVRecord> listadoRecords, String cabecera, String tipoDato) {
        ArrayList<String> listaValidaciones = new ArrayList<String>();
        listaValidaciones.add("ok");

        try {
            int i = 0;
            for (CSVRecord record : listadoRecords) {
                String dato = record.get(cabecera);
                if (dato.trim().equals("")) {
                    dato = "valor en blanco";
                }
                if (tipoDato.trim().equals("texto-novacio")) {
                    if (!esTextoNoVacio(dato)) {
                        String error = fraseValidacionColumna(i, cabecera, tipoDato, dato);
                        insertarStringValidacion(listaValidaciones, error);
                    }
                }
                if (tipoDato.trim().equals("numero")) {
                    if (!esNumero(dato)) {
                        String error = fraseValidacionColumna(i, cabecera, tipoDato, dato);
                        insertarStringValidacion(listaValidaciones, error);
                    }
                }
                if (tipoDato.trim().equals("numero-novacio")) {
                    if (!esNumeroNoVacio(dato)) {
                        String error = fraseValidacionColumna(i, cabecera, tipoDato, dato);
                        insertarStringValidacion(listaValidaciones, error);
                    }
                }
                i++;
            }

        } catch (Exception e) {
            mostrarVentanaError(e);
        }
        return listaValidaciones;
    }

    public static String fraseValidacionColumna(int fila, String cabecera, String tipoDato, String dato) {
        return "El valor en la fila " + (fila + 2) + " de la columna " + cabecera + " no corresponde al tipo de dato " + tipoDato + ". Se encontró el valor " + dato;
    }

    /**
     * Valida si la columna de un CSV cumple con el criterio del tipo de dato
     * válido
     *
     * @param listadoRecords Records que se obtienen desde la función CSVParser
     * parser.getRecords()
     * @param indiceCabecera Indice de la cabecera en la cual se validarán los
     * datos
     * @param tipoDato toma los valores texto, texto-novacio, numero,
     * numero-novacio
     * @return ArrayList<String> con los errores encontrados o ok
     */
    public static ArrayList<String> validarColumnaCSV(List<CSVRecord> listadoRecords, int indiceCabecera, String tipoDato) {
        ArrayList<String> listaValidaciones = new ArrayList<String>();
        listaValidaciones.add("ok");

        try {
            int i = 0;
            for (CSVRecord record : listadoRecords) {
                String dato = record.get(indiceCabecera);
                if (dato.trim().equals("")) {
                    dato = "valor en blanco";
                }
                if (tipoDato.trim().equals("texto-novacio")) {
                    if (!esTextoNoVacio(dato)) {
                        String error = "el valor en la fila " + (i + 2) + " no corresponde al tipo de dato " + tipoDato + " se encontró el valor " + dato;
                        insertarStringValidacion(listaValidaciones, error);
                    }
                }
                if (tipoDato.trim().equals("numero")) {
                    if (!esNumero(dato)) {
                        String error = "el valor en la fila " + (i + 2) + " no corresponde al tipo de dato " + tipoDato + " se encontró el valor " + dato;
                        insertarStringValidacion(listaValidaciones, error);
                    }
                }
                if (tipoDato.trim().equals("numero-novacio")) {
                    if (!esNumeroNoVacio(dato)) {
                        String error = "el valor en la fila " + (i + 2) + " no corresponde al tipo de dato " + tipoDato + " se encontró el valor " + dato;
                        insertarStringValidacion(listaValidaciones, error);
                    }
                }
                i++;
            }

        } catch (Exception e) {
            mostrarVentanaError(e);
        }
        return listaValidaciones;
    }

    public static ArrayList<String> validarCabecerasParJTextField(JTextField jtf1, JTextField jtf2) {
        ArrayList<String> listadoValidaciones = new ArrayList<String>();

        String cadenaCabeceras = jtf1.getText();
        String cadenaTipoDatos = jtf2.getText();

        ArrayList<String> listadoValidacionesCantidadCabecerasTiposDatos = validarCantidadCabecerasTiposDatos(cadenaCabeceras, cadenaTipoDatos);
        ArrayList<String> listadoValidacionesTiposDatos = validarCadenaTipoDatos(cadenaTipoDatos);

        listadoValidaciones.addAll(listadoValidacionesCantidadCabecerasTiposDatos);
        listadoValidaciones.addAll(listadoValidacionesTiposDatos);

        return listadoValidaciones;
    }

    public static String deArrayListALineasHTML(ArrayList<String> validaciones) {
        String html = "";

        for (String validacion : validaciones) {

            html = html + " " + validacion + " <br>";

        }

        return html;
    }

    public static ArrayList<String> validarCabecerasArcihvosCSVParJListTextField(JList<String> jlistarchivos, JTextField jtextfield) {

        ArrayList<String> listadoValidaciones = new ArrayList<String>();

        String rutaArchivo = jlistarchivos.getModel().getElementAt(0);
        String cadenaCabeceras = jtextfield.getText();

        listadoValidaciones = Validaciones.validarCabecerasArchivoCSV(cadenaCabeceras, rutaArchivo);

        return listadoValidaciones;
    }

    public static ArrayList<String> validarColumnaCSVParJlistTextField(JList<String> jlistarchivos, JTextField jtextfieldTipoDatos) {
        ArrayList<String> listadoValidacionesTotal = new ArrayList<String>();
        listadoValidacionesTotal.add("ok");

        try {
            String rutaArchivo = jlistarchivos.getModel().getElementAt(0);

            String cadenaTipoDatos = jtextfieldTipoDatos.getText();
            CSVParser parserArchivo = ConsultasSQL.obtenerCSVParserCSV(rutaArchivo);
            List<String> listadoCabeceras = parserArchivo.getHeaderNames();
            List<CSVRecord> listadoRecords = parserArchivo.getRecords();

            CSVParser parserTipoDatos = CSVParser.parse(cadenaTipoDatos, CSVFormat.newFormat(',').withFirstRecordAsHeader());

            int i = 0;
            for (String tipoDato : parserTipoDatos.getHeaderNames()) {

                ArrayList<String> listadoValidaciones = Validaciones.validarColumnaCSV(listadoRecords, listadoCabeceras.get(i), tipoDato);
                if (listadoValidaciones.size() > 0 && !listadoValidaciones.get(0).equals("ok")) {
                    for (int j = 0; j < listadoValidaciones.size(); j++) {
                        insertarStringValidacion(listadoValidacionesTotal, listadoValidaciones.get(j));
                    }
                }
                i++;
            }
        } catch (Exception e) {
            mostrarVentanaError(e);
            e.printStackTrace();
        }
        return listadoValidacionesTotal;
    }

    public static ArrayList<String> validarColumnaCSVArchivo(JList<String> jlistarchivos, String cadenaCSVTiposDatos) {
        ArrayList<String> listadoValidacionesTotal = new ArrayList<String>();
        listadoValidacionesTotal.add("ok");

        try {
            String rutaArchivo = jlistarchivos.getModel().getElementAt(0);

            String cadenaTipoDatos = cadenaCSVTiposDatos;
            CSVParser parserArchivo = ConsultasSQL.obtenerCSVParserCSV(rutaArchivo);
            List<String> listadoCabeceras = parserArchivo.getHeaderNames();
            List<CSVRecord> listadoRecords = parserArchivo.getRecords();

            CSVParser parserTipoDatos = CSVParser.parse(cadenaTipoDatos, CSVFormat.newFormat(',').withFirstRecordAsHeader());

            int i = 0;
            for (String tipoDato : parserTipoDatos.getHeaderNames()) {

                ArrayList<String> listadoValidaciones = Validaciones.validarColumnaCSV(listadoRecords, listadoCabeceras.get(i), tipoDato);
                if (listadoValidaciones.size() > 0 && !listadoValidaciones.get(0).equals("ok")) {
                    for (int j = 0; j < listadoValidaciones.size(); j++) {
                        insertarStringValidacion(listadoValidacionesTotal, listadoValidaciones.get(j));
                    }
                }
                i++;
            }
        } catch (Exception e) {
            mostrarVentanaError(e);
            e.printStackTrace();
        }
        return listadoValidacionesTotal;
    }

    

}
