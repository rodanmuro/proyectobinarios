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
public class ensayoTablaTemporalASignaturas {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            Connection conn = ConsultasSQL.conexionBD(ConsultasSQL.NOMBREBDOFERTAEDUCATIVA);
            
            String nombreTAbla = EstudiantesPorPrograma.crearTablaTemporalAsignaturasCarreraCurriculoDado(conn, "AEMD", "AEMD");
        
            ResultSet rs = conn.createStatement().executeQuery("select * from "+nombreTAbla);
            
            while(rs.next()){
                for (int i = 1; i < rs.getMetaData().getColumnCount(); i++) {
                    
                }
            }
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
}