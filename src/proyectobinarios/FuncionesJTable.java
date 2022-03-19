/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author Usuario
 */
public class FuncionesJTable {

    public static void agregarFilaJTable(JTable jtable) {

        DefaultTableModel dtm = (DefaultTableModel) jtable.getModel();
        Vector<Object> data = new Vector<Object>();
        dtm.addRow(data);

    }

    public static int obtenerIndiceColumnaPorNombreJTable(JTable jtable, String nombreColumna) {
        int indice = -1;
        DefaultTableModel dtm = (DefaultTableModel) jtable.getModel();

        for (int i = 0; i < dtm.getColumnCount(); i++) {
            if (dtm.getColumnName(i).trim().equals(nombreColumna.trim())) {
                return i;
            }
        }
        return indice;
    }

    public static DefaultTableModel obtenerDefaultTableModel(JTable jtable) {
        return (DefaultTableModel) jtable.getModel();
    }

    public static String obtenerValorCeldaJTable(JTable jtable, int fila, int columna) {
        String valor = "";
        DefaultTableModel dtm = obtenerDefaultTableModel(jtable);
        if (dtm.getValueAt(fila, columna) != null) {
            return ((String) dtm.getValueAt(fila, columna)).trim();
        }
        return valor;
    }

    public static String obtenerCSVColumnaJTable(JTable jtable, int indiceColumna) {
        String csv = "";
        DefaultTableModel dtm = obtenerDefaultTableModel(jtable);

        for (int i = 0; i < dtm.getRowCount(); i++) {

            String valorCelda = obtenerValorCeldaJTable(jtable, i, indiceColumna);

            if (!valorCelda.equals("")) {
                if (i != dtm.getRowCount() - 1) {
                    csv = csv + valorCelda + ",";
                } else {
                    csv = csv + valorCelda;
                }
            }
        }
        return csv;
    }

    public static String obtenerCSVColumnaJTable(JTable jtable, String nombreColumna) {
        String csv = "";

        int indiceColumna = obtenerIndiceColumnaPorNombreJTable(jtable, nombreColumna);

        DefaultTableModel dtm = obtenerDefaultTableModel(jtable);

        for (int i = 0; i < dtm.getRowCount(); i++) {

            String valorCelda = obtenerValorCeldaJTable(jtable, i, indiceColumna);

            if (!valorCelda.equals("")) {
                if (i != dtm.getRowCount() - 1) {
                    csv = csv + valorCelda + ",";
                } else {
                    csv = csv + valorCelda;
                }
            }
        }

        return csv;
    }

    public static String obtenerCSVFilaJTable(JTable jtable, int indiceFila) {
        String csv = "";

        DefaultTableModel dtm = obtenerDefaultTableModel(jtable);

        for (int i = 0; i < dtm.getColumnCount(); i++) {
            String valorCelda = obtenerValorCeldaJTable(jtable, indiceFila, i);
            if (i != dtm.getColumnCount() - 1) {
                csv = csv + valorCelda + ",";
            } else {
                csv = csv + valorCelda;
            }
        }
        return csv;
    }

    public static void agregarListaColumnaJTable(JTable jtable, int indiceColumna, ArrayList<String> opciones) {

        JComboBox<String> jcombobox = new JComboBox<String>();
        for (String item : opciones) {
            jcombobox.addItem(item);
        }
        jtable.getColumnModel().getColumn(indiceColumna).setCellEditor(new DefaultCellEditor(jcombobox));
    }

    public static void cargarDatosColumnaAJTableDesdeCSV(JTable jtable, int indiceColumna, String csv) {

        try {
            CSVParser parser = CSVParser.parse(csv, CSVFormat.newFormat(','));
            List<CSVRecord> record = parser.getRecords();
            DefaultTableModel dtm = obtenerDefaultTableModel(jtable);

            if (record.size() > 0) {
                for (int i = 0; i < record.get(0).size(); i++) {
                    if (i >= dtm.getRowCount()) {
                        dtm.addRow(new Vector<Object>());
                    }
                    dtm.setValueAt(record.get(0).get(i).trim(), i, indiceColumna);
                }
            }
        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
        }
    }

    public static void cargarDatosColumnaAJTableDesdeCSV(JTable jtable, String nombreColumna, String csv) {

        int indiceColumna = obtenerIndiceColumnaPorNombreJTable(jtable, nombreColumna);

        try {
            CSVParser parser = CSVParser.parse(csv, CSVFormat.newFormat(','));
            List<CSVRecord> record = parser.getRecords();
            DefaultTableModel dtm = obtenerDefaultTableModel(jtable);

            if (record.size() > 0) {
                for (int i = 0; i < record.get(0).size(); i++) {
                    if (i >= dtm.getRowCount()) {
                        dtm.addRow(new Vector<Object>());
                    }
                    dtm.setValueAt(record.get(0).get(i).trim(), i, indiceColumna);
                }
            }
        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
        }
    }

    public static String depurarCadenaComasFinales(String csv) {
        String cadena = csv.trim();

        while (cadena.substring(cadena.length() - 1).equals(",")) {
            cadena = cadena.substring(0, cadena.length() - 1).trim();
        }

        return cadena;
    }

    public static boolean celdaEnBlancoJTable(JTable jtable) {
        DefaultTableModel dtm = obtenerDefaultTableModel(jtable);
        for (int i = 0; i < dtm.getColumnCount(); i++) {
            for (int j = 0; j < dtm.getRowCount(); j++) {
                if (obtenerValorCeldaJTable(jtable, j, i).equals("")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void eventoCeldaEnBlanco(JTable jtable) {
        if (FuncionesJTable.celdaEnBlancoJTable(jtable)) {
            JOptionPane.showMessageDialog(null, "No pueden existir celdas en blanco en este tabla. Llénela o seleccione la fila y elimínela con click derecho");
        };
    }

    public static void agregarFilaPresionandoEnterJTable(JTable jtable, KeyEvent evt) {
        if (evt.getKeyCode() == evt.VK_ENTER) {
            if (jtable.getSelectedRow() == jtable.getRowCount() - 1) {
                FuncionesJTable.agregarFilaJTable(jtable);
            }
        }
    }

    public static void agregarColumnaValidadaTipoDatosJTable(JTable jtable, int indiceColumna) {

        ArrayList<String> listadoTiposDatosComboBox = new ArrayList<String>();
        listadoTiposDatosComboBox.add("texto");
        listadoTiposDatosComboBox.add("texto-novacio");
        listadoTiposDatosComboBox.add("numero");
        listadoTiposDatosComboBox.add("numero-novacio");
        FuncionesJTable.agregarListaColumnaJTable(jtable, 1, listadoTiposDatosComboBox);

    }

    public static void agregarEventoAgregarFilaPresionandoEnter(JTable jtable) {
        jtable.addKeyListener(new KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                agregarFilaPresionandoEnterJTable(jtable, evt);
            }
        });
    }

    public static void agregarEventoEliminarFilaClickDerecho(JTable jtable) {
        jtable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (SwingUtilities.isRightMouseButton(evt)) {
                    int indiceSelecionado = jtable.getSelectedRow();
                    if (indiceSelecionado == -1) {
                        JOptionPane.showMessageDialog(null, "Seleccione una fila");
                    } else {
                        int eleccion = mensajeConfirmacionCancelarPorDefecto("Desea eliminar la fila seleccionada");
                        if (eleccion == 0) {
                            FuncionesJTable.obtenerDefaultTableModel(jtable).removeRow(indiceSelecionado);
                        }
                    }
                }
            }
        });

    }

    public static void agregarEventosJTable(JTable jtable) {
        agregarEventoAgregarFilaPresionandoEnter(jtable);
        agregarEventoEliminarFilaClickDerecho(jtable);
    }

    public static int mensajeConfirmacionCancelarPorDefecto(String mensaje) {
        //Custom button text
        Object[] options = {"Si", "No", "Cancelar"};
        return JOptionPane.showOptionDialog(null,
                mensaje,
                "Confirmar",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[2]);
    }
    
    

}
