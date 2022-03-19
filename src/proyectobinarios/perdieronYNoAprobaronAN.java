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
public class perdieronYNoAprobaronAN {

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

        

        try {
            ResultSet rsasignaturas = conn.createStatement().executeQuery("select  alfanumerico from " + enhv.NOMBRETABLATEMPORALASIGNATURAS);

//            while (rsasignaturas.next()) {
                long t = System.currentTimeMillis();
                alfaNumericoAEvaluar = rsasignaturas.getString(1);
                String tabla = enhv.crearTablaPerdieronYNoHanAprobadoAlfaNumerico(alfaNumericoAEvaluar);
                ResultSet rs = conn.createStatement().executeQuery("select * from " + tabla);
                rs.next();
                System.out.println("tiempo = " + (System.currentTimeMillis() - t) + " totalestt " + rs.getInt(1) + " an " + alfaNumericoAEvaluar);
                new JFrameJTable("Vieron y no han aprobado "+alfaNumericoAEvaluar, rs);
//            }

            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
