/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Usuario
 */
public class ensayoTotalMatriculados {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        // TODO code application logic here
        String sede = "PER";
        String nomenclatura = "AEMD";
        String curriculo = "AEMD";
        
        Connection conn = ConsultasSQL.conexionBD(ConsultasSQL.NOMBREBDOFERTAEDUCATIVA);
//        System.out.println(""+EstudiantesPorPrograma.totalMatriculadosEstudiantesActivosGeneral(sede, nomenclatura, curriculo)); ;
//        System.out.println(""+EstudiantesPorPrograma.totalMatriculadosEstudiantesArgos(sede, nomenclatura, curriculo));;
    }
    
}
