/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Usuario
 */
public class VentanaNoLaHanVisto extends javax.swing.JFrame {

    /**
     * Creates new form VentanaNoLaHanVisto
     */
    public VentanaNoLaHanVisto() {
        initComponents();

        try {
            Connection conn = ConsultasSQL.conexionBD(ConsultasSQL.NOMBREBDOFERTAEDUCATIVA);
            String sede = "PER";
            String programa = "AEMP";
            String curriculo = "AEMP";
            String alfaNumericoAEvaluar = "ETIC190";

            String tablaTemporalAsignaturas = EstudiantesPorPrograma.crearTablaTemporalAsignaturasCarreraCurriculoDado(conn, programa, curriculo);
            String sqlListadoAlfaNumericos = "select alfanumerico from " + tablaTemporalAsignaturas;
            ResultSet rslistadoan = conn.createStatement().executeQuery(sqlListadoAlfaNumericos);

            long t1 = System.currentTimeMillis();
            ConsultasSQL.crearTablaTemporalEstudiantesActivosArgosSinRepetir(conn);
            EstudiantesNoHanVistoNRC esnhv = new EstudiantesNoHanVistoNRC(conn, sede, programa, curriculo);

            ResultSet rsnohanvistoypuedenver
                    = conn.createStatement().executeQuery("select * from "
                            + esnhv.crearTablaTemporalFinalNoLaHanVistoLaPuedenVer());

            String nombreTablaNoHanVistoCreditosAprbados
                    = esnhv.crearTablaTemporalEstudiantesNoHanVistoANCreditosAprobados(alfaNumericoAEvaluar);
            String tabla = esnhv.crearTablaTemporalEStudiantesNoHanVistoYPuedenVer(nombreTablaNoHanVistoCreditosAprbados, alfaNumericoAEvaluar);
            ResultSet rs = conn.createStatement().executeQuery("select * from " + tabla);

            EstudiantesPorPrograma.crearTablaInformeTotalProgramasLoPuedenVer(conn, esnhv, sede, programa, curriculo);
            EstudiantesPorPrograma.crearTablaInformeTotalProgramasLoPuedenVer(conn, esnhv, sede, "AEMD", "AEMD");
            EstudiantesPorPrograma.crearTablaInformeTodosProgramasLoPuedenVer(conn, esnhv, sede);
            new JFrameJTable("Estudiantes que no han visto y pueden ver " + alfaNumericoAEvaluar, rs);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaNoLaHanVisto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaNoLaHanVisto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaNoLaHanVisto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaNoLaHanVisto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaNoLaHanVisto().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
