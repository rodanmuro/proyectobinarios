/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.sql.Connection;
import java.util.ArrayList;

/**
 *
 * @author Usuario
 */
public class ensayoConversorResultSet {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Connection conn = ConsultasSQL.conexionBD(ConsultasSQL.NOMBREBDOFERTAEDUCATIVA);
        try {
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
                
        
        
        ArrayList<Object> al = new ArrayList<Object>();
        for (int i = 0; i < 10; i++) {
            al.add(i);
        }
        
        ;
        
        System.out.println(ConversorResultSetArrayList.sentenciaInsertDesdeArrayList("nombreTabla", al, al));;
        
    }
    
}
