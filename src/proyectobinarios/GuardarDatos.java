/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author Usuario
 */
public class GuardarDatos {

    Constantes C;
    static String NOMBREARCHIVOGUARDARDATOS = "data/serializaciones/constantesOE4.ser";

    public GuardarDatos(Constantes c) {
        C = c;
    }

    public static void guardarDatos(Constantes c) {
        try {
            FileOutputStream fos = new FileOutputStream(NOMBREARCHIVOGUARDARDATOS);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(c);
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Constantes recuperarDatos() {
        Constantes c = null;
        try {
            File f = new File(NOMBREARCHIVOGUARDARDATOS);
            if (f.exists()) {
                FileInputStream fis = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fis);
                c = (Constantes) ois.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

}
