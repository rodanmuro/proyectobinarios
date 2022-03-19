/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import com.mysql.jdbc.Driver;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Usuario
 */
public class ensayosloadfilamysqllocal {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            String url = "jdbc:mysql://localhost:3306/historialacademico?serverTimezone=UTC&local_infile=1&allowLoadLocalInfile=true&Local=true";
            Connection conn = DriverManager.getConnection(url, "root", "8Ro4512952");
            
            
//            String tableName = "historialacademico";
//            
//            String localInfile = "SET GLOBAL local_infile=1";
//            conn.createStatement().execute(localInfile);
//            
//            String sql = " LOAD DATA LOCAL INFILE '" + "Histórico final CSV.csv" + "' into table " + tableName
//                    + " COLUMNS TERMINATED BY '" + ";" + "' ";

String ssql = "SELECT * FROM historialacademico.historialacademico where ID_ESTUDIANTE=82421";
long t1 = System.currentTimeMillis();
            conn.createStatement().execute(ssql);
            long t2 = System.currentTimeMillis();
            System.out.println("tiempo "+(t2-t1));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
