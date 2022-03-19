/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import com.mysql.cj.jdbc.result.ResultSetImpl;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Usuario
 */
public class ensayoInsertsMasivosTablaTemporal {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {

            Connection conn = ConsultasSQL.conexionBD(ConsultasSQL.NOMBREBDOFERTAEDUCATIVA);
            
            SelectsEnArrayList.aNAsignaturasSemestre(conn, "AEMD", "AEMD", 1);
            SelectsEnArrayList.idEstudiantesAprobadosSemestre(conn, "PER", "AEMD", 1);
            
            String selectQuery = "select * from " + EstudiantesPorPrograma.
                    crearTablaTemporalHistorialAcademicoSedeCarreraCurriculoDado(conn, "PER", "AEMD");
            ResultSet rs = conn.
                    createStatement().
                    executeQuery(selectQuery);
            rs.next();
            System.out.println("t1 "+rs.getInt(1));

            ArrayList<ArrayList<Object>> al = ConversorResultSetArrayList.selectToArrayList(conn, selectQuery);

            String nombreTT = "te" + System.currentTimeMillis();
            String sqlCrearTT = "create temporary table "
                    + nombreTT
                    + ConversorResultSetArrayList.arrayListACabeceras(al.get(0));
            conn.createStatement().execute(sqlCrearTT);

            long t1 = System.currentTimeMillis();

            ConversorResultSetArrayList.insertArrayListADataBase(conn, nombreTT, al);

            long t2 = System.currentTimeMillis();

            rs = conn.createStatement().executeQuery("select count(ID_ESTUDIANTE) from " + nombreTT);
            rs.next();
            System.out.println("total " + rs.getInt(1));
            System.out.println("tiempo " + (t2 - t1));

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
