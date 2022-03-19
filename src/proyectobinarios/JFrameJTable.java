/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectobinarios;

import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Usuario
 */
public class JFrameJTable extends JFrame {

    public JFrameJTable(String title, ResultSet rs) {
        DefaultTableModel dtm = new DefaultTableModel();
        JTable jtable = new JTable();
        jtable.setModel(dtm);
        ConversorResultSetADefaultTableModel
                .rellena(rs, dtm);
        JScrollPane jsp = new JScrollPane(jtable);
        org.oxbow.swingbits.table.filter.TableRowFilterSupport.forTable(jtable).apply();
        this.setSize(800, 600);
        this.setTitle(title);
//        this.setLayout(new GridLayout(2, 1));
//        this.add(new JPanel().add(new JButton("Descargar")));
        this.add(jsp);
        this.setVisible(true);
    }

}
