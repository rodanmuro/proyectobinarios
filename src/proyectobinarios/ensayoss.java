/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Usuario
 */
public class ensayoss {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        XSSFSheet hoja = FuncionesExcel.cargarHojaLibro("insumos/mallabase/malla curricular base 2020-1 V6.xlsx", "carreras");
        String rutaLibroExcel = "insumos/mallabase/malla curricular base 2020-1 V6.xlsx";

        File f = FuncionesExcel.convertirHojaExcelAArchivoCSVTemporal(rutaLibroExcel, "asignaturas");
        
        Desktop.getDesktop().open(f);

    }

    public static void convertExcelToCSV(Sheet sheet, String sheetName) throws IOException {
        StringBuilder data = new StringBuilder();
        try {
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    CellType type = cell.getCellTypeEnum();
                    if (type == CellType.BOOLEAN) {
                        data.append(cell.getBooleanCellValue());
                    } else if (type == CellType.NUMERIC) {
                        data.append(cell.getNumericCellValue());
                    } else if (type == CellType.STRING) {
                        data.append(cell.getStringCellValue());
                    } else if (type == CellType.BLANK) {
                    } else {
                        data.append(cell + "");
                    }
                    data.append(",");
                }
                data.append('\n');
            }
            Files.write(Paths.get(sheetName + ".csv"),
                    data.toString().getBytes("UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
