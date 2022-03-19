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
public class ensayoTablaTemporalAsignaturasCreditosNecesarios {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            Connection CONN = ConsultasSQL.conexionBD(ConsultasSQL.NOMBREBDOFERTAEDUCATIVA);
            String SEDE = "PER";
            String PROGRAMA = "ASST";
            String CURRICULO = "UVASST20182";
            
            ConsultasSQL.crearTablaTemporalEstudiantesActivosArgosSinRepetir(CONN);
            
            EstudiantesNoHanVistoNRC enhv = new EstudiantesNoHanVistoNRC(CONN, SEDE, PROGRAMA, CURRICULO);
            
            String tabla = enhv.crearTablaTemporalAsignaturasTotalCreditosNecesarios();
            
            String sql = " select * from "+tabla;
            
            ResultSet rs = CONN.createStatement().executeQuery(sql);
            
            new JFrameJTable("ASST", rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
