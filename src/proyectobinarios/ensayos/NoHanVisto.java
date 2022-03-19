/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios.ensayos;

import java.sql.Connection;
import java.sql.ResultSet;
import proyectobinarios.ConsultasSQL;
import proyectobinarios.ConsultasSQL;
import proyectobinarios.EstudiantesNoHanVistoNRC;
import proyectobinarios.EstudiantesNoHanVistoNRC;
import proyectobinarios.JFrameJTable;
import proyectobinarios.JFrameJTable;

/**
 *
 * @author Usuario
 */
public class NoHanVisto {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            Connection conn =  ConsultasSQL.conexionBD(ConsultasSQL.NOMBREBDOFERTAEDUCATIVA);

            String sede = "PER";
            String programa = "PSID";
            String curriculo = "PSID";
            String anev = "INFO1010";

            EstudiantesNoHanVistoNRC enohv = new EstudiantesNoHanVistoNRC(conn, sede, programa, curriculo);
            String nombretabla = enohv.crearTablaTemporalEstudiantesNoHanVistoAN(anev);

            ResultSet rs = conn.createStatement().executeQuery("select * "
//                    + enohv.NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES
//                    + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO
                    + " from "
                    + enohv.NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES
//                    + " where "
//                    + enohv.NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES + ".alfanumerico='" + anev + "'"
//                    + " or "
//                    + enohv.NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES + ".alfanumericoequivalente='" + anev + "'"
                            + " order by "+ enohv.NOMBRETABLAHISTORIALALFANUMERICOSEQUIVALENTES
                    + "." + ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO+", alfanumerico");
            
            new JFrameJTable("no han visto "+anev+" en programa "+ programa, rs);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
