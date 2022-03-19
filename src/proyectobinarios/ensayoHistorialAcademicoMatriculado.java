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
public class ensayoHistorialAcademicoMatriculado {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        try {
            Connection conn = ConsultasSQL.conexionBD(ConsultasSQL.NOMBREBDOFERTAEDUCATIVA);
            String sede = "PER";
            String programa = "COPD";
            String curriculo = "COPD";
            String alfaNumericoAEvaluar = "INFO1010";
            //EstudiantesNoHanVistoNRC enhv = new EstudiantesNoHanVistoNRC(conn, sede, programa, curriculo);

            String nombreTablaHistorialMatriculado = EstudiantesPorPrograma.crearTablaTemporalHistorialAcademicoMatriculadoSedeCarreraCuriculoDado(conn, sede, programa);

            ResultSet rsmatriculado = conn.createStatement().executeQuery("select * from " + nombreTablaHistorialMatriculado);

            String nombreTablaHistorialHistorico = EstudiantesPorPrograma.crearTablaTemporalHistorialAcademicoHistoricoSedeCarreraCurriculoDado(conn, sede, programa);

            ResultSet rshistorico = conn.createStatement().executeQuery("select * from " + nombreTablaHistorialHistorico);

            ResultSet rsunion = conn.createStatement().executeQuery("select * from "
                    + nombreTablaHistorialHistorico
                    + " union all "
                    + " select * from " + nombreTablaHistorialMatriculado);

            new JFrameJTable("matriculado", rsmatriculado);

            new JFrameJTable("historico", rshistorico);
            
            new JFrameJTable("union", rsunion);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
