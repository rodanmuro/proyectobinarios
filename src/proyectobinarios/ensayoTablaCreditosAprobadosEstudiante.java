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
public class ensayoTablaCreditosAprobadosEstudiante {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Connection CONN = ConsultasSQL.conexionBD("ofertaeducativa");
        String SEDE = "PER";
        String PROGRAMA = "PSID";
        String CURRICULO = "PSID";
        
        ConsultasSQL.crearTablaTemporalEstudiantesActivosArgosSinRepetir(CONN);
        
        //
        String nombreTablaEstudiantesActivos = ConsultasSQL.NOMBRETABLATEMPORAL_ESTUDIANTESACTIVOS;
        
        try {
            
            String nombreTablaIDCreditos = "tablaTemporalIDCreditos";
            String sqlCrearTabla = "create temporary table  if not exists "
                    +nombreTablaIDCreditos
                    + "("
                    + "ID_ESTUDIANTE,"
                    + "creditosaprobados"
                    + ")";
            CONN.createStatement().execute(sqlCrearTabla);
            
            String sql = "select CODIGO from "
                    +nombreTablaEstudiantesActivos 
                    +" where "
                    + ConsultasSQL.COLUMNASEDEESTUDIANTESACTIVOSARGOS+"="+"'"+ SEDE+"' "
                    + " and "
                    + ConsultasSQL.COLUMNAPROGRAMAESTUDIANTESACTIVOSARGOS+"="+"'"+ PROGRAMA+"' ";
            
            ResultSet rs = CONN.createStatement().executeQuery(sql);
            
            EstudiantesNoHanVistoNRC enhv = new EstudiantesNoHanVistoNRC(CONN, SEDE, PROGRAMA, CURRICULO);
            
            int i=0;
            while(rs.next()){
                int codigo = rs.getInt("CODIGO");
                int creditosAprobados = enhv.creditosAprobadosEstudiante(codigo);
                
                String insertar = "insert into "
                        +nombreTablaIDCreditos
                        +"(ID_ESTUDIANTE, "
                        + "creditosaprobados)"
                        + "values("
                        + codigo
                        +","
                        +creditosAprobados
                        + ")"
                        + ""
                        ;//agregar acá los valores
                
                CONN.createStatement().execute(insertar);
                System.out.println((i++)+","+codigo+ ","+creditosAprobados);
                //enhv.creditosAprobadosEstudiante(codigo);
                //System.out.println(codigo+" "+enhv.creditosAprobadosEstudiante(codigo));;
            }
            
            ResultSet rsSelect = CONN.createStatement().executeQuery("select * from "+nombreTablaIDCreditos);
            
            new JFrameJTable("titulo", rsSelect);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
