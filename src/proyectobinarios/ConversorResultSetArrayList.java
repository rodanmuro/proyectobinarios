/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Usuario
 */
public class ConversorResultSetArrayList {
    
    public static ArrayList<ArrayList<Object>> selectToArrayList(Connection conn, String selectQuery){
        ArrayList<ArrayList<Object>> arrayList = new ArrayList<ArrayList<Object>>();
        
        try {
            
            ResultSet rs = conn.createStatement().executeQuery(selectQuery);
            
            int numeroCampos = rs.getMetaData().getColumnCount();
            ArrayList<Object> cabeceras = new ArrayList<Object>();
            
            for (int i = 1; i < numeroCampos+1; i++) {
                cabeceras.add(rs.getMetaData().getColumnName(i));
            }
            
            arrayList.add(cabeceras);
            
            rs = conn.createStatement().executeQuery(selectQuery);
            
            while(rs.next()){
                
                
                ArrayList<Object> registro = new ArrayList<Object>();
                
                for (int j = 1; j < numeroCampos+1; j++) {
                    registro.add(rs.getObject(j));
                }
                
                arrayList.add(registro);
            }
            
            return arrayList;
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se puedo pasar de ResultSet a ArrayLisy", e);
        }
        
        return null;
    }
    
    static String arrayListACabeceras(ArrayList<Object> arrayList){
        String cabeceras = null;
        for (int i = 0; i < arrayList.size(); i++) {
            if(i==0){
                cabeceras = "("+arrayList.get(i)+",";
            }else if(i>0 && i<arrayList.size()-1){
                cabeceras = cabeceras+""+arrayList.get(i)+",";
            }
            else if(i==arrayList.size()-1){
                cabeceras = cabeceras+""+arrayList.get(i)+")";
            }
        }
        return cabeceras;
    }
    
    static String arrayListAValues(ArrayList<Object> arrayList){
        String cabeceras = null;
        for (int i = 0; i < arrayList.size(); i++) {
            if(i==0){
                cabeceras = "('"+arrayList.get(i)+"',";
            }else if(i>0 && i<arrayList.size()-1){
                cabeceras = cabeceras+"'"+arrayList.get(i)+"',";
            }
            else if(i==arrayList.size()-1){
                cabeceras = cabeceras+"'"+arrayList.get(i)+"')";
            }
        }
        return cabeceras;
    }
    
    static String sentenciaInsertDesdeArrayList(String tabla, ArrayList<Object> arrayListCabeceras, ArrayList<Object> arrayListValues){
        String cabeceras = arrayListACabeceras(arrayListCabeceras);
        String values = arrayListAValues(arrayListValues);
        
        String insert = "insert into "
                +tabla
                +" "+cabeceras
                +" values "
                +values;
        
        return insert;
    }
    
    static void insertArrayListADataBase(Connection conn, String nombreTabla, ArrayList<ArrayList<Object>> arrayList){
        try {
            ArrayList<Object> arrayListCabeceras = arrayList.get(0);
            
            for (int i = 1; i < arrayList.size(); i++) {
                ArrayList<Object> arrayListValues = arrayList.get(i);
                String sqlInsert = sentenciaInsertDesdeArrayList(nombreTabla, arrayListCabeceras, arrayListValues);
                conn.createStatement().execute(sqlInsert);
            }
            
        } catch (Exception e) {
            Validaciones.mostrarErroresTotal("No se pudo hacer todo el insert del arraylist", e);
        }
    
    }
    
}
