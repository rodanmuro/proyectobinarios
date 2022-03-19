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
public class ensayoImportarAlfaNumericosHomoologados {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Connection CONN = ConsultasSQL.conexionBD(ConsultasSQL.NOMBREBDOFERTAEDUCATIVA);
        ConsultasSQL.eliminarTablaSiExiste(CONN, 
                ConsultasSQL.NOMBREBDOFERTAEDUCATIVA, ConsultasSQL.TABLAALFANUMERICOSHOMOLOGADOS);
        
                ConsultasSQL.importarBaseDatosSqlite(ConsultasSQL.NOMBREBDOFERTAEDUCATIVA,
                        ConsultasSQL.TABLAALFANUMERICOSHOMOLOGADOS,
                        "C:\\Users\\Usuario\\Documents\\NetBeansProjects\\ProyectoBinarios\\insumos\\alfanumericoshomologados\\alfanumericoshomologados.csv",
                        ConsultasSQL.CODIFICACIONORIGEN, ConsultasSQL.CODIFICACIODESTINO,
                        true);
    }
    
}
