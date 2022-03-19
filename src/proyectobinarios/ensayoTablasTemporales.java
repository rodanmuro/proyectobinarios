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
public class ensayoTablasTemporales {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Connection conn = ConsultasSQL.conexionBD(ConsultasSQL.NOMBREBDOFERTAEDUCATIVA);
        String sqlTabla1 = "create TEMPORARY table t1 as \n"
                + "select asignatura, id_carrera, semestre, alfa, numerico \n"
                + "from \n"
                + "asignaturas \n"
                + "where \n"
                + "asignaturas.id_carrera=(SELECT id_carrera from carreras where (trim(carreras.nomenclatura),trim(carreras.curriculo1banner)) \n"
                + "in (select trim(PROGRAMA), trim(CURRICULO_1) from estudiantesactivosgeneral where cast(CODIGO as real)=556611))";

        String sqlTabla2 = "create temporary table t2 as "
                + "select PROGRAMA, DESCRIPION, NOMBRE_ESTUDIANTE, DEFINITIVA, ALFA,NUMERI from historialacademico where cast(ID_ESTUDIANTE as real)=556611";

        String sqlJoin = "select \n"
                + "tabla1.asignatura, \n"
                + "tabla1.id_carrera, \n"
                + "tabla1.semestre, \n"
                + "tabla2.PROGRAMA, \n"
                + "tabla1.alfa || trim(replace(tabla1.numerico,\".0\",\"\")) as alfaNumericoActual, \n"
                + "trim(tabla2.ALFA) || trim(tabla2.NUMERI) as alfaNumericoCursado,\n"
                + "tabla2.DESCRIPION,\n"
                + "tabla2.NOMBRE_ESTUDIANTE, \n"
                + "tabla2.DEFINITIVA \n"
                + "\n"
                + "from t1 as tabla1\n"
                + "\n"
                + "left join t2 as tabla2\n"
                + "\n"
                + "on trim(tabla1.alfa) || trim(replace(tabla1.numerico,\".0\",\"\")) = (select alfaNumericoReciente from \n"
                + "    alfanumericoshomologados \n"
                + "    where\n"
                + "    alfaNumericoAntiguo=trim(tabla2.ALFA) || trim(tabla2.NUMERI) \n"
                + "    AND\n"
                + "    alfaNumericoReciente=trim(tabla1.alfa) || trim(replace(tabla1.numerico,\".0\",\"\"))\n"
                + "    and\n"
                + "    nomenclatura=tabla2.PROGRAMA) \n"
                + "OR\n"
                + "trim(tabla1.alfa) || trim(replace(tabla1.numerico,\".0\",\"\")) = alfaNumericoCursado \n"
                + "    \n"
                + "--trim(tabla2.ALFA) || trim(tabla2.NUMERI)\n"
                + "\n"
                + "order by cast(tabla1.semestre as real) ASC\n"
                + "--order by tabla2.DESCRIPION ASC";
        try {
            
            conn.createStatement().execute(sqlTabla1);
            conn.createStatement().execute(sqlTabla2);
            ResultSet rs = conn.createStatement().executeQuery(sqlJoin);
            
            while (rs.next()) {
                for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
                    System.out.print(" " + rs.getMetaData().getColumnName(i) + ": " + rs.getObject(i) + ", ");;
                }
                System.out.println("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
