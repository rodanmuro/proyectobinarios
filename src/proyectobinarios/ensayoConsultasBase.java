/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Usuario
 */
public class ensayoConsultasBase {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException, UnsupportedEncodingException {
        // TODO code application logic here

        Connection conn = ConsultasSQL.conexionBD("basenueva");

        String sql = "select * from historialacademico limit 1000";
        ResultSet rs = conn.createStatement().executeQuery(sql);

        while (rs.next()) {
            System.out.println("" + rs.getString(1)
                    + " " + rs.getString(2)
                    + " " + rs.getString(3)
                    + " " + rs.getString(4)
                    + " " + rs.getString(5)
                    + " " + rs.getString(6)
                    + " " + new String(rs.getString("DESCRIPCION_NIVEL").getBytes("ISO-8859-1"), StandardCharsets.ISO_8859_1)
            );
        }

    }

}
