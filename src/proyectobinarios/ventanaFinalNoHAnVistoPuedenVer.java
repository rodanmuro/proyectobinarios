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
public class ventanaFinalNoHAnVistoPuedenVer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Connection conn = ConsultasSQL.conexionBD(ConsultasSQL.NOMBREBDOFERTAEDUCATIVA);
        String sede = "PER";
        String programa = "PSID";
        String curriculo = "PSID";
        String alfaNumericoAEvaluar = "INFO1010";
        EstudiantesNoHanVistoNRC enhv = new EstudiantesNoHanVistoNRC(conn, sede, programa, curriculo);
        
        long t1 = System.currentTimeMillis();
        String nombreTabla = enhv.crearTablaTemporalFinalNoLaHanVistoLaPuedenVer();
        System.out.println(System.currentTimeMillis()-t1);
        try {
            new JFrameJTable("Tabla Final", conn.createStatement().executeQuery("select "
                    + "asignatura as Asignatura, "
                    + "semestre as Semestre, "
                    + "creditos as Créditos, "
                    + "alfanumerico as AlfaNumerico, "
                    + "creditosnecesarios as 'Créditos Necesarios', "
                    + "NoLoHanVisto, "
                    + "LaPuedenVer, "
                    + "LaReprobaronYNoHanAprobado"
                    + " from "+nombreTabla));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
