/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 *
 * @author Usuario
 */
public class ensayoRsExcel {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Connection conn = ConsultasSQL.conexionBD("ofertaeducativa");
        
        try {
            String consulta = "select * from historialacademico";
            ResultSet rs = conn.createStatement().executeQuery(consulta);
            RsExcel.exportarExcel(rs, "salidaExcel", "");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
}
