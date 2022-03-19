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
public class ensayolArgosActivosMasCurriculo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        
        String nombreTablaArgosActivos = "";
        try {
            //String curriculo="UVCOPD20182";
            String curriculo="LPID";
            String nomenclatura="LPID";
            String sede="PER";
            
            Connection conn = ConsultasSQL.conexionBD(ConsultasSQL.NOMBREBDOFERTAEDUCATIVA);
            
            nombreTablaArgosActivos = "tablaArgosActivos_" + sede + nomenclatura + curriculo;
            
            String sqlCrearTablaTemporalArgosActivos = "create temporary table if not exists "
                    + nombreTablaArgosActivos + " as "
                    + "select "
                    + " cast(" + ConsultasSQL.COLUMNACODIGOESTUDIANTESACTIVOSARGOS + " as real) as CODIGO, "
                    + " " + ConsultasSQL.COLUMNANOMBRESESTUDIANTESACTIVOSARGOS + ", "
                    + " " + ConsultasSQL.COLUMNAPROGRAMAESTUDIANTESACTIVOSARGOS + ", "
                    + " " + ConsultasSQL.COLUMNASEDEESTUDIANTESACTIVOSARGOS + " "
                    + " FROM " + ConsultasSQL.TABLAESTUDIANTESACTIVOSNRC
                    + " WHERE trim(" + ConsultasSQL.COLUMNASEDEESTUDIANTESACTIVOSARGOS + ")='" + sede.trim() + "'"
                    + " and "
                    + " trim(" + ConsultasSQL.COLUMNAPROGRAMAESTUDIANTESACTIVOSARGOS + ")='" + nomenclatura.trim() + "' "
                    + " group by CODIGO order by " + ConsultasSQL.COLUMNANOMBRESESTUDIANTESACTIVOSARGOS;
            conn.createStatement().execute(sqlCrearTablaTemporalArgosActivos);
            
            String nombreTablaCodigoCurriculo = "activosGeneralCodigoCurriculo";
            
            String sqlActivosGeneralCodigoCurriculo = "create temporary table if not exists "
                    +nombreTablaCodigoCurriculo
                    + " as  "
                    + "select "
                    + " cast("+ConsultasSQL.TABLAESTUDIANTESACTIVOSGENERAL+"."+ConsultasSQL.COLUMNACODIGOESTUDIANTEACTIVOSGENERAL+" as real) as CODIGO, "
                    + ConsultasSQL.TABLAESTUDIANTESACTIVOSGENERAL+"."+ConsultasSQL.COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL
                    + " from "
                    + ConsultasSQL.TABLAESTUDIANTESACTIVOSGENERAL
                    +" where "
                    + ConsultasSQL.TABLAESTUDIANTESACTIVOSGENERAL+"."+ConsultasSQL.COLUMNAPROGRAMAESTUDIANTESACTIVOSGENERAL+"='"+nomenclatura+"'"
                    +" and "
                    + ConsultasSQL.TABLAESTUDIANTESACTIVOSGENERAL+"."+ConsultasSQL.COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL+"='"+curriculo+"'";
            conn.createStatement().execute(sqlActivosGeneralCodigoCurriculo);
            
            String sqlActivosMasCurriculo = "create temporary table if not exists "
                    + nombreTablaArgosActivos+"curriculo as "
                    + " select "
                    + nombreTablaArgosActivos+".*, "
                    + nombreTablaCodigoCurriculo+"."+ConsultasSQL.COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL
                    + " from "
                    +nombreTablaArgosActivos
                    +" left join "
                    + nombreTablaCodigoCurriculo
                    +" on "
                    + nombreTablaCodigoCurriculo+"."+ConsultasSQL.COLUMNACODIGOESTUDIANTEACTIVOSGENERAL
                    + "="
                    + nombreTablaArgosActivos+".CODIGO";
            conn.createStatement().execute(sqlActivosMasCurriculo);
            
            String sqlDepurarCurriculoBlanco = "create temporary table if not exists "
                    + nombreTablaArgosActivos+"curriculoNoNulo as "
                    +" select * from "
                    + nombreTablaArgosActivos+"curriculo "
                    + " where "
                    + nombreTablaArgosActivos+"curriculo."+ConsultasSQL.COLUMNANOMBRECURRICULOESTUDIANTESACTIVOSGENERAL
                    +" is not null";
            conn.createStatement().execute(sqlDepurarCurriculoBlanco);
            
            String sqlSeleccionar = " select count(*) from "
                    +nombreTablaArgosActivos+"curriculoNoNulo";
            
            ResultSet rs = conn.createStatement().executeQuery(sqlSeleccionar);
            
            new JFrameJTable(sede, rs);

        } catch (Exception e) {
            Validaciones.mostrarVentanaError("No se pudo crear la tabla temporal argos activos");
            Validaciones.mostrarVentanaError(e);
            e.printStackTrace();
        }
        
        
    }
    
}
