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
public class ensayoTablaPrincipalActivosSinRepetir {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            Connection conn = ConsultasSQL.conexionBD(ConsultasSQL.NOMBREBDOFERTAEDUCATIVA);
            String nombretabla = ConsultasSQL.crearTablaTemporalEstudiantesActivosArgosSinRepetir(conn);
            ResultSet rs = conn.createStatement().executeQuery("select COUNT(*) from "+nombretabla);
            
            new JFrameJTable("", rs);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
