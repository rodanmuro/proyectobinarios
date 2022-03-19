/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Usuario
 */
public class FuncionesExcel {

    public FuncionesExcel() {
    }

    public static XSSFWorkbook cargarLibroExcel(String rutaArchivo) {
        XSSFWorkbook libro = null;
        try {
            libro = new XSSFWorkbook(rutaArchivo);
        } catch (Exception e) {
            Validaciones.mostrarVentanaError(e);
        }
        return libro;
    }

    public static XSSFSheet cargarHojaLibro(String rutaArchivo, String nombreHoja) {
        return cargarLibroExcel(rutaArchivo).getSheet(nombreHoja);
    }

    public static XSSFSheet cargarHojaLibro(String rutaArchivo, int indiceHoja) {
        return cargarLibroExcel(rutaArchivo).getSheetAt(indiceHoja);
    }

    public static XSSFRow cargarFilaLibro(String rutaArchivo, String nombreHoja, int indiceFila) {
        return cargarLibroExcel(rutaArchivo).getSheet(nombreHoja).getRow(indiceFila);
    }

    public static XSSFRow cargarFilaLibro(String rutaArchivo, int indiceHoja, int indiceFila) {
        return cargarLibroExcel(rutaArchivo).getSheetAt(indiceHoja).getRow(indiceFila);
    }

    public static XSSFCell cargarCeldaLibro(String rutaArchivo, String nombreHoja, int indiceFila, int indiceCelda) {
        return cargarLibroExcel(rutaArchivo).getSheet(nombreHoja).getRow(indiceFila).getCell(indiceCelda);
    }

    public static XSSFCell cargarCeldaLibro(String rutaArchivo, int indiceHoja, int indiceFila, int indiceCelda) {
        return cargarLibroExcel(rutaArchivo).getSheetAt(indiceHoja).getRow(indiceFila).getCell(indiceCelda);
    }

    public static File convertirHojaExcelAArchivoCSVTemporal(String rutaLibroExcel, String nombreHoja) {
        XSSFSheet hoja = cargarHojaLibro(rutaLibroExcel, nombreHoja);
        int numeroEncabezados = hoja.getRow(0).getLastCellNum();
        File archivoTemporal = null;

        try {
            archivoTemporal = File.createTempFile("archivo", ".csv");
            archivoTemporal.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(archivoTemporal);
            OutputStreamWriter osw = new OutputStreamWriter(fos,"windows-1252");
            BufferedWriter bw = new BufferedWriter(osw);

            for (int i = 0; i < hoja.getLastRowNum(); i++) {
                XSSFRow fila = hoja.getRow(i);
                String cadenaFila = "";
                if (fila != null) {
                    for (int j = 0; j < numeroEncabezados; j++) {
                        XSSFCell cell = fila.getCell(j);
                        String separador = ";";
                        if (j == numeroEncabezados - 1) {
                            separador = "";
                        }

                        if (cell == null) {
                            cadenaFila = cadenaFila + "" + separador;
                        } else {
                            CellType type = cell.getCellTypeEnum();
                            if (type == CellType.BOOLEAN) {
                                cadenaFila = cadenaFila + "" + (cell.getBooleanCellValue()) + separador;
                            } else if (type == CellType.NUMERIC) {
                                cadenaFila = cadenaFila + "" + (cell.getNumericCellValue()) + separador;
                            } else if (type == CellType.STRING) {
                                cadenaFila = cadenaFila + "" + (cell.getStringCellValue()) + separador;
                            } else if (type == CellType.BLANK) {
                                cadenaFila = cadenaFila + "" + separador;
                            } else {
                                cadenaFila = cadenaFila + "" + (cell + "") + separador;
                            }
                        }
                    }
                    bw.write(cadenaFila);
                    bw.newLine();
                }
            }
            bw.close();
            osw.close();
            fos.close();
            hoja.getWorkbook().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return archivoTemporal;
    }
}
