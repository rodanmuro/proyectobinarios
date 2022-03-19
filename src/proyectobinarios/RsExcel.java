/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Usuario
 */
public class RsExcel {

    public static void exportarExcel(ResultSet rs, String nombreArchivo, String rutaSalida) {

        try {
            XSSFWorkbook libro = new XSSFWorkbook();

            XSSFSheet hoja = libro.createSheet();

            XSSFRow filaEncabezados = hoja.createRow(0);

            ResultSetMetaData rsmd = rs.getMetaData();

            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                filaEncabezados.createCell(i - 1).setCellValue(rsmd.getColumnName(i));
            }

            int j = 1;
            while (rs.next()) {
                XSSFRow filaDatos = hoja.createRow(j);
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    filaDatos.createCell(i - 1).setCellValue((String)rs.getObject(i));
                }
                j++;
                
                if(j%1000==0){
                    System.out.println(j);
                }
            }

            FileOutputStream fos = new FileOutputStream(new File(rutaSalida + "" + nombreArchivo + ".xlsx"));
            libro.write(fos);
            fos.close();
            libro.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
