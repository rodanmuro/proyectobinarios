/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.sql.Connection;
import java.sql.ResultSet;
import static proyectobinarios.ConsultasSQL.TABLAESTUDIANTESACTIVOSNRC;

/**
 *
 * @author Usuario
 */
public class ensayoEstudiantesActivosMasCurriculo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            Connection conn = ConsultasSQL.conexionBD(ConsultasSQL.NOMBREBDOFERTAEDUCATIVA);

            String nombreTablaEstudiantesActivos = "tablaTemporalParaCreacionEstudiantesActivosArgos";

            String sql = "create temporary table IF NOT EXISTS "
                    + nombreTablaEstudiantesActivos
                    + " as "
                    + "select cast(" + ConsultasSQL.COLUMNACODIGOESTUDIANTESACTIVOSARGOS + " as INTEGER)  as CODIGO, "
                    + " " + ConsultasSQL.COLUMNANOMBRESESTUDIANTESACTIVOSARGOS + ", "
                    + " " + ConsultasSQL.COLUMNASEDEESTUDIANTESACTIVOSARGOS + ", "
                    + " " + ConsultasSQL.COLUMNAPROGRAMAESTUDIANTESACTIVOSARGOS
                    + " from "
                    + TABLAESTUDIANTESACTIVOSNRC
                    + " group by " + ConsultasSQL.COLUMNACODIGOESTUDIANTESACTIVOSARGOS
                    + " ORDER BY " + ConsultasSQL.COLUMNANOMBRESESTUDIANTESACTIVOSARGOS;
                    //+ " limit  100 ";

            conn.createStatement().execute(sql);

//            ResultSet rs = conn.createStatement().executeQuery("select * from " + nombreTablaEstudiantesActivos);
//
//            new JFrameJTable("", rs);

            String nombreTablaCodigoCurriculo = ConsultasSQL.NOMBRETABLATEMPORAL_CODIGOCURRICULO;
            String sql2 = "create temporary table IF NOT EXISTS "
                    + nombreTablaCodigoCurriculo
                    + " as "
                    + " select "
                    + "cast(" + ConsultasSQL.COLUMNACODIGOESTUDIANTEACTIVOSGENERAL + " as integer) as CODIGO"
                    + ","
                    + ConsultasSQL.COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL
                    + " from "
                    + ConsultasSQL.TABLAESTUDIANTESACTIVOSGENERAL;
                    //+ " limit 100 ";
            conn.createStatement().execute(sql2);

//            ResultSet rs = conn.createStatement().executeQuery("select * from tablaCodigoCurriculo");
//            
//            new JFrameJTable("", rs);
            String sql3 = "create temporary table IF NOT EXISTS "
                    + ConsultasSQL.NOMBRETABLATEMPORAL_ESTUDIANTESACTIVOS
                    + " as "
                    + "select " + nombreTablaEstudiantesActivos + ".*,"
                    + nombreTablaCodigoCurriculo + "." + ConsultasSQL.COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL
                    + " from "
                    + nombreTablaEstudiantesActivos
                    + " left join "
                    + nombreTablaCodigoCurriculo
                    + " on "
                    + nombreTablaEstudiantesActivos+"."+ConsultasSQL.COLUMNACODIGOESTUDIANTESACTIVOSARGOS 
                    + "=" 
                    + nombreTablaCodigoCurriculo+"."+ConsultasSQL.COLUMNACODIGOESTUDIANTEACTIVOSGENERAL + "";
            System.out.println(sql3);
            conn.createStatement().execute(sql3);

            ResultSet rs = conn.createStatement().executeQuery("select * from "+ConsultasSQL.NOMBRETABLATEMPORAL_ESTUDIANTESACTIVOS);

            new JFrameJTable("", rs);

        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo crear la tabla de estudiantes "
                    + " activos sin repetir desde argos", e);
        }
    }

}
