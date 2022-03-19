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
public class VentanaInformeTotalLoPuedenVer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        try {
            Connection conn = ConsultasSQL.conexionBD(ConsultasSQL.NOMBREBDOFERTAEDUCATIVA);
            String sede = "PER";
            String programa = "AEMD";
            String curriculo = "AEMD";

            ConsultasSQL.crearTablaTemporalEstudiantesActivosArgosSinRepetir(conn);
            EstudiantesNoHanVistoNRC esnhv = new EstudiantesNoHanVistoNRC(conn, sede, programa, curriculo);
            esnhv.crearTablaTemporalFinalNoLaHanVistoLaPuedenVer();
            

            //EstudiantesPorPrograma.crearTablaInformeTotalProgramasLoPuedenVer(conn, esnhv, sede, programa, curriculo);
            EstudiantesPorPrograma.crearTablaInformeTodosProgramasLoPuedenVer(conn, sede);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
}
