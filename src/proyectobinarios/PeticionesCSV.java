/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.sql.Connection;
import java.sql.Statement;
import org.apache.commons.csv.*;
import com.mysql.jdbc.Driver;
import java.io.File;
import java.io.FileReader;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;
import org.apache.commons.csv.*;

/**
 *
 * @author Usuario
 */
public class PeticionesCSV {

    public PeticionesCSV() {

    }

    public static void readCsvUsingLoad() {
        try {
//            Class.forName("com.mysql.jdbc.Driver").newInstance();
            
            Connection connection = null;
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306?serverTimezone=UTC", "root", "8Ro4512952;");
            
            String loadQuery = "LOAD DATA LOCAL INFILE '" 
                    + "C:\\Users\\Usuario\\Documents\\NetBeansProjects\\ProyectoBinarios\\Histórico final CSV.csv" + 
                    "' INTO TABLE historialacademico.historicoensayo FIELDS TERMINATED BY ','" 
                    + " LINES TERMINATED BY '\n' "
                    +"(ID_ESTUDIANTE,NOMBRE_ESTUDIANTE,RECTORIA,DESCRIPCION_RECTORIA,SEDE,DESCRIPCION_SEDE, FACULTA,DESCRIPCION_FACULTAD, PROGRAMA, DESCRIPCION_PROGRAMA,NIVEL,DESCRIPCION_NIVEL,JORNADA,PERIODO,NRCS,ALFA,NUMERI,DESCRIPION,DEFINITIVA,PROMEDIO_SEM,PROM_ACU,FORMA_CAL,COMENTARIO); ";
            System.out.println(loadQuery);
            Statement stmt = connection.createStatement();
            stmt.execute(loadQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        

    }
    
    public static void readCsv()
    {
 
        try 
        {   
            FileReader fr = new FileReader(new File("Histórico final CSV.csv"));
            CSVParser parserArchivo = CSVParser.parse(fr, CSVFormat.newFormat(';').withFirstRecordAsHeader());
            List<String> listadoCabecerasArchivo = parserArchivo.getHeaderNames(); 
            
            
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306?serverTimezone=UTC&characterEncoding=iso8859-1", "root", "8Ro4512952;");
                String insertQuery = "Insert into historialacademico.historicoensayo "
                        +" (ID_ESTUDIANTE,"
                        + "NOMBRE_ESTUDIANTE,"
                        + "RECTORIA,"
                        + "DESCRIPCION_RECTORIA,"
                        + "SEDE,"
                        + "DESCRIPCION_SEDE, "
                        + "FACULTA,"
                        + "DESCRIPCION_FACULTAD, "
                        + "PROGRAMA, "
                        + "DESCRIPCION_PROGRAMA,"
                        + "NIVEL,"
                        + "DESCRIPCION_NIVEL,"
                        + "JORNADA,"
                        + "PERIODO,"
                        + "NRCS,"
                        + "ALFA,"
                        + "NUMERI,"
                        + "DESCRIPION,"
                        + "DEFINITIVA,"
                        + "PROMEDIO_SEM,"
                        + "PROM_ACU,"
                        + "FORMA_CAL,"
                        + "COMENTARIO) "
                        +"values "
                        +"("
                        +"?,?,?,?,?,?,?,"
                        +"?,?,?,?,?,?,?,"
                        +"?,?,?,?,?,?,?,?,?"
                        +")";
                PreparedStatement pstmt = connection.prepareStatement(insertQuery);
                String[] rowData = null;
                int i = 0;
                
                List<CSVRecord> records = parserArchivo.getRecords();
                long t1 = System.currentTimeMillis();
                for(int j=0;j<20000;j++){
                    CSVRecord record = records.get(j);
                    pstmt.setInt(1, Integer.parseInt(record.get(0)));
                    for (int k = 2; k < 15; k++) {
                        pstmt.setString(k, record.get(k-1));
                    }
                    for (int k = 15; k < 16; k++) {
                        pstmt.setString(k, (record.get(k-1)));
                    }
                    for (int k = 16; k < 17; k++) {
                        pstmt.setString(k, (record.get(k-1)));
                    }
                    for (int k = 17; k < 18; k++) {
                        pstmt.setInt(k, Integer.parseInt(record.get(k-1)));
                    }
                    for (int k = 18; k < 19; k++) {
                        pstmt.setString(k, record.get(k-1));
                    }
                    for (int k = 19; k < 20; k++) {
                        pstmt.setString(k, (record.get(k-1)));
                    }
                    for (int k = 20; k < 24; k++) {
                        pstmt.setString(k, record.get(k-1));
                    }
                    
                        pstmt.addBatch();
                    
                    if(j==19999){
                        pstmt.executeBatch();
                    }
//                    pstmt.executeUpdate();
                }
                long t2 = System.currentTimeMillis();
                System.out.println("tiempo en agregar los registrs "+(t2-t1));
                
                
//                while((rowData = reader.readNext()) != null)
//                {
//                    for (String data : rowData)
//                    {
//                            pstmt.setString((i % 3) + 1, data);
// 
//                            if (++i % 3 == 0)
//                                    pstmt.addBatch();// add batch
// 
//                            if (i % 30 == 0)// insert when the batch size is 10
//                                    pstmt.executeBatch();
//                    }
//                }
                System.out.println("Data Successfully Uploaded");
        }
        catch (Exception e)
        {
                e.printStackTrace();
        }
 
    }

}
