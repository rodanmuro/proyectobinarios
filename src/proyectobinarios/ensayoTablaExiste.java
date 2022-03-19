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
public class ensayoTablaExiste {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Connection CONN = ConsultasSQL.conexionBD("ofertaeducativa");
        ConsultasSQL.crearTablaTemporalEstudiantesActivosArgosSinRepetir(CONN);
        String nombreTablaEstudiantesActivos = ""+ConsultasSQL.NOMBRETABLATEMPORAL_ESTUDIANTESACTIVOS;

        String nombreTabla = nombreTablaEstudiantesActivos;
        boolean existe = ConsultasSQL.tablaTemporalExiste(CONN, nombreTabla);
        System.out.println(existe);
    }
    
}
