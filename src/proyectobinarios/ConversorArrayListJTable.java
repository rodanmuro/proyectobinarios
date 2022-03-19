/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

/**
 * Javier Abellán, 31 Mayo 2006
 */
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Convierte un ResultSet en un DefaultTableModel
 *
 * @author Chuidiang
 *
 */
public class ConversorArrayListJTable {

    /**
     * Rellena el DefaultTableModel con los datos del ResultSet. Vacía el
     * DefaultTableModel completamente y le mete los datos que hay en el
     * ResultSet.
     *
     * @param rs El resultado de lac onsula a base de datos.
     * @param modelo El DefaultTableModel que queremos rellenar
     */
    public static void rellena(ArrayList<ArrayList<Object>> al, DefaultTableModel modelo) {
        configuraColumnas(al, modelo);
        vaciaFilasModelo(modelo);
        anhadeFilasDeDatos(al, modelo);
    }

    /**
     * Añade al DefaultTableModel las filas correspondientes al ResultSet.
     *
     * @param rs El resultado de la consulta a base de datos
     * @param modelo El DefaultTableModel que queremos rellenar.
     */
    private static void anhadeFilasDeDatos(ArrayList<ArrayList<Object>> al,
            DefaultTableModel modelo) {
        try {
            // Para cada registro de resultado en la consulta 
            for (int i = 1; i < al.size(); i++) {
                // Se crea y rellena la fila para el modelo de la tabla.
                Object[] datosFila = new Object[modelo.getColumnCount()];
                for (int j = 0; j < modelo.getColumnCount(); j++) {
                    datosFila[j] = al.get(i).get(j);
                }
                modelo.addRow(datosFila);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Borra todas las filas del modelo.
     *
     * @param modelo El modelo para la tabla.
     */
    private static void vaciaFilasModelo(final DefaultTableModel modelo) {
        // La llamada se hace in un invokeAndWait para que se ejecute en el
        // hilo de refresco de ventanas y evitar que salten excepciones
        // durante dicho refresco.
        try {

            while (modelo.getRowCount() > 0) {
                modelo.removeRow(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Pone en el modelo para la tabla tantas columnas como tiene el resultado
     * de la consulta a base de datos.
     *
     * @param rs Resultado de consulta a base de datos.
     * @param modelo Modelo de la tabla.
     */
    public static void configuraColumnas(final ArrayList<ArrayList<Object>> al,
            final DefaultTableModel modelo) {

        try {
            // Se obtiene los metadatos de la consulta. Con ellos
            // podemos obtener el número de columnas y el nombre
            // de las mismas.

            // Se obtiene el numero de columnas.
            int numeroColumnas = al.get(0).size();

            // Se obtienen las etiquetas para cada columna
            Object[] etiquetas = new Object[numeroColumnas];
            for (int i = 0; i < numeroColumnas; i++) {
                etiquetas[i] = al.get(0).get(i);
            }

            // Se meten las etiquetas en el modelo. El numero
            // de columnas se ajusta automáticamente.
            modelo.setColumnIdentifiers(etiquetas);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

