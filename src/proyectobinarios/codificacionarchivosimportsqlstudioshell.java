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
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 *
 * @author Usuario
 */
public class codificacionarchivosimportsqlstudioshell {

    public static File codificarArchivoAEncoding(String rutaOrigen,
            String encodingOrigen, String encodingDestino) {
        
        File archivoOrigen, archivoDestino = null;
        
        try {
            archivoOrigen = new File(rutaOrigen);
            archivoDestino = File.createTempFile("archivocodificado", ".csv");//new File("archivoCodificado.csv");
            archivoDestino.deleteOnExit();

            FileInputStream fis = new FileInputStream(archivoOrigen);
            FileOutputStream fos = new FileOutputStream(archivoDestino);

            InputStreamReader isr = new InputStreamReader(fis, encodingOrigen);
            OutputStreamWriter osw = new OutputStreamWriter(fos, encodingDestino);
            
            BufferedReader br = new BufferedReader(isr);
            

            int caracter = 0;
            
            System.out.println(""+br.readLine());;
            
            while ((caracter = br.read()) != -1) {
                osw.write(caracter);
            }
            osw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return archivoDestino;
    }
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            
            ConsultasSQL.importarBaseDatosSqlite("nueva", "historialacademico", "Histórico final CSV.csv","windows-1252","UTF-8",true);
            Connection conn = ConsultasSQL.conexionBD("nueva");
            ResultSet rs = conn.createStatement().executeQuery("select * from historialacademico where columna1='a' ");
            
            while (rs.next()) {
                System.out.println("" + rs.getString("columna1") + " " + rs.getString("columna2"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
