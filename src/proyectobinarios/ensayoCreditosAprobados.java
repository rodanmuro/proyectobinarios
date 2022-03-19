/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.sql.Connection;


/**
 *
 * @author Usuario
 */
public class ensayoCreditosAprobados {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Connection CONN = ConsultasSQL.conexionBD("ofertaeducativa");
        String SEDE = "PER";
        String PROGRAMA = "PSID";
        String CURRICULO = "PSID";
        
        try {
            ConsultasSQL.crearTablaTemporalEstudiantesActivosArgosSinRepetir(CONN);
            
            EstudiantesNoHanVistoNRC enhv = new EstudiantesNoHanVistoNRC(CONN, SEDE, PROGRAMA, CURRICULO);
            System.out.println(""+enhv.creditosAprobadosEstudiante(575138));;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
