/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author Usuario
 */
public class Cargando extends Thread implements Runnable {

    Thread hilo = null;
    JDialog Jdialog;
    
    public Cargando(JDialog jdialog){
        Jdialog = jdialog;
        hilo = new Thread(this);
        hilo.start();
    }

    public void run() {
        Jdialog.setVisible(true);
    }
    
    public void detener(){
        hilo=null;
        Jdialog.setVisible(false);
    }
}
