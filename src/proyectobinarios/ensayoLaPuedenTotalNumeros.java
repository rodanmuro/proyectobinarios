/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import static proyectobinarios.EstudiantesPorPrograma.crearTablaInformeTotalProgramasLoPuedenVer;
import static proyectobinarios.EstudiantesPorPrograma.listadoCarreras;
import static proyectobinarios.EstudiantesPorPrograma.listadoCurriculos;

/**
 *
 * @author Usuario
 */
public class ensayoLaPuedenTotalNumeros {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        EstudiantesNoHanVistoNRC ENHV;

        Connection CONN = ConsultasSQL.conexionBD(ConsultasSQL.NOMBREBDOFERTAEDUCATIVA);
        ConsultasSQL.crearTablaTemporalEstudiantesActivosArgosSinRepetir(CONN);
        //acá es necesario variar las carreras y los currículos
        String nombreTablaTodosProgramasPuedenVerNumeros
                = crearTablaInformeTodosProgramasLoPuedenVerTotalNumeros(CONN, "PER");

        try {
            String consulta = "create temporary table progacadaminscritos as "
                    + "select tabla.*, sum(tabla.INSCRITOS) as sumainscritos from (select nrc, titulo, (alfa || num) as alfanumerico, rectoria, sede, inscritos "
                    + "from programacionacademicaargos where sede='PER' group by nrc)  as tabla group by alfanumerico";
            CONN.createStatement().execute(consulta);

            ResultSet rsinscritos = CONN.createStatement().executeQuery("select * from progacadaminscritos");

            ResultSet rs = CONN.createStatement().executeQuery("select * from " + nombreTablaTodosProgramasPuedenVerNumeros);

            String consultajoin = "select " + nombreTablaTodosProgramasPuedenVerNumeros + ".*, progacadaminscritos.* from "
                    + nombreTablaTodosProgramasPuedenVerNumeros + " "
                    + " left join "
                    + " progacadaminscritos "
                    + " on "
                    + " " + nombreTablaTodosProgramasPuedenVerNumeros + ".alfanumerico=progacadaminscritos.alfanumerico";

            ResultSet rsjoin = CONN.createStatement().executeQuery(consultajoin);

            JFrameJTable jfjt = new JFrameJTable("tabla", rsjoin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String crearTablaInformeTodosProgramasLoPuedenVerTotalNumeros(Connection CONN, String sede) {
        String nombreTablaInformeTodosProgramasLoPuedenVerNumerosTotal
                = "'" + ConsultasSQL.PREFIJOTABLAINFORMETODOSPROGRAMASLOPUEDEVENVER_NUMEROS_TOTALES + "'";
        ResultSet rsCarreras = listadoCarreras(CONN);

        ArrayList<String> nombresTablas = new ArrayList<String>();

        try {
            if (rsCarreras != null) {
                while (rsCarreras.next()) {

                    String nomenclatura = rsCarreras.getString(1);

                    ResultSet rsCurriculos = listadoCurriculos(CONN, nomenclatura);

                    if (rsCurriculos != null) {
                        
                        while (rsCurriculos.next()) {
                            String curriculo = rsCurriculos.getString(1);
                            EstudiantesNoHanVistoNRC ENHV = new EstudiantesNoHanVistoNRC(CONN, sede, nomenclatura, curriculo);
                            String nombreTabla = ENHV.crearTablaTemporalFinalNoLaHanVistoLaPuedenVer();
                            nombresTablas.add(nombreTabla);
                        }
                    }
                }
            }

            String consultaUnionTodasTablas = "";
            for (int i = 0; i < nombresTablas.size(); i++) {
                if (i == 0) {
                    consultaUnionTodasTablas = " create temporary table if not exists "
                            + nombreTablaInformeTodosProgramasLoPuedenVerNumerosTotal
                            + " as "
                            + " select * from " + nombresTablas.get(i)
                            + " union ";
                }
                if (i > 0 && i <= nombresTablas.size() - 2) {
                    consultaUnionTodasTablas = consultaUnionTodasTablas
                            + " select * from " + nombresTablas.get(i)
                            + " union ";
                }
                if (i == nombresTablas.size() - 1) {
                    consultaUnionTodasTablas = consultaUnionTodasTablas
                            + " select * from " + nombresTablas.get(i);
                }
            }

            CONN.createStatement().execute(consultaUnionTodasTablas);

        } catch (Exception e) {
            e.printStackTrace();
            Validaciones.mostrarErroresTotal("Error al crear la tabla de informe de todos los programas lo pueden ver", e);
        }
        return nombreTablaInformeTodosProgramasLoPuedenVerNumerosTotal;
    }

}
