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
public class SelectsEnArrayList {
    
    static ArrayList<ArrayList<Object>> aNAsignaturasSemestre(Connection conn, 
            String programa, String curriculo, int semestre){
        String nombreTabla = EstudiantesPorPrograma.crearTablaTemporalAsignaturasCarreraCurriculoDado(conn, programa, curriculo);
        
        return ConversorResultSetArrayList.selectToArrayList(conn, "select alfanumerico from "
                +nombreTabla+" where semestre="+semestre);
    }
    
    static ArrayList<ArrayList<Object>> idEstudiantesAprobadosSemestre(Connection conn, String sede, String programa, int semestre){
        String nombreTablaTemporalHistorialAcademico = EstudiantesPorPrograma.
                crearTablaTemporalHistorialAcademicoSedeCarreraCurriculoDado(conn, sede, programa);
        
        String nombreTablaHistorialAnEquivalentes = EstudiantesPorPrograma.
                crearTablaTemporalHistorialAcademicoAlfaNumericosEquivalentes(conn, nombreTablaTemporalHistorialAcademico);
    
        String nombreTablaAprobados = EstudiantesPorPrograma.crearTablaTemporalObtenerHistorialTemporalSegunNota(conn, nombreTablaHistorialAnEquivalentes, ">=", 3);
        
        String sql = "select "
                +ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO
                +" from "+nombreTablaAprobados+" group by "+ConsultasSQL.COLUMNACODIGOESTUDIANTEHISTORIALACADEMICO;
        return ConversorResultSetArrayList.selectToArrayList(conn, sql);
    }
}
