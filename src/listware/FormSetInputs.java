/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listware;

import framework.AutoDetectInputs;
import framework.IWS;
import framework.Input;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author truep
 */
public class FormSetInputs extends javax.swing.JDialog {

    JTable table = null;
    JCheckBox chkStep = null;
    Input input = null;
    /**
     * Creates new form FormSetInputs
     */
    public FormSetInputs(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public FormSetInputs(java.awt.Frame parent, boolean modal, String inputPath, String delimiter, String txtQualifier, JCheckBox chkStep) throws FileNotFoundException {
        super(parent, modal);
        initComponents();
        this.chkStep = chkStep;
        input = new Input(inputPath, delimiter, txtQualifier);
        JComboBox cmb = new JComboBox();
        cmb.addItem(null);
        for(String n: input.headerNames) {
            cmb.addItem(n);
        }
        
        DefaultTableModel tm = new DefaultTableModel(new Object[] {"Inputs", "Values"}, 0);
        try {
            Class<?> cl = Class.forName("services." + Listware.serviceType);
            Constructor<?> ctor = cl.getConstructor();
            IWS service = (IWS)ctor.newInstance();
            String[] inputColumns = service.getInputColumns();
            
            for(String s : inputColumns) {
                String field = null;
                if(Listware.inputAliases != null) {
                    field = Listware.inputAliases.get(s);
                    if(field != null && field.compareTo("null") == 0) {
                        field = null;
                    }
                } else {
                    for(String f: input.headerNames) {
                        if(s.equalsIgnoreCase(f)) {
                            field = f;
                            break;
                        }
                    }
                }
                tm.addRow(new Object[]{s, field});
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FormSetInputs.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(FormSetInputs.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(FormSetInputs.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(FormSetInputs.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(FormSetInputs.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(FormSetInputs.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(FormSetInputs.class.getName()).log(Level.SEVERE, null, ex);
        }
        setDefaultValuesForInputs(input.headerNames, tm);
        table = new JTable(tm);
        table.setRowHeight(24);
        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cmb));
        paneTabeContainer.getViewport().add(table);
        
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                int prefHeight = (table.getRowCount() + 1) * 24;
                if(prefHeight < 72) prefHeight = 72;
                if(prefHeight > 360) prefHeight = 360;
                prefHeight += 6;
                Dimension dim = paneTabeContainer.getSize();
                int diffHeight = getSize().height - paneTabeContainer.getSize().height;
                setSize(new Dimension(dim.width, diffHeight + prefHeight));
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
        
        input.closeReader();
        
//        ColumnModel
    }
    
    private void setDefaultValuesForInputs(String[] headerNames, DefaultTableModel tm) 
    {
        for(int i = 0; i < tm.getRowCount(); i++) {
            String key = String.valueOf(tm.getValueAt(i, 0));
            boolean exactMatch = false;
            for(String header: headerNames) {
                if(key.equalsIgnoreCase(header)) {
                    tm.setValueAt(header, i, 1);
                    exactMatch = true;
                    break;
                }
            }
            if(exactMatch) continue;
            
            if(AutoDetectInputs.variationDictionary.containsKey(key.toLowerCase())) {
                boolean found = false;
                for(String s: AutoDetectInputs.variationDictionary.get(key.toLowerCase())) {
                    for(String header: headerNames) {
                        if(s.equalsIgnoreCase(header)) {
                            tm.setValueAt(header, i, 1);
                            found = true;
                            break;
                        }
                    }
                    if(found) break;
                }
            }
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

        paneTabeContainer = new javax.swing.JScrollPane();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Set Inputs");

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(paneTabeContainer)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(94, Short.MAX_VALUE)
                .addComponent(btnSave)
                .addGap(18, 18, 18)
                .addComponent(btnCancel)
                .addContainerGap(93, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(paneTabeContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        HashMap<String, String> aliases = new HashMap<>();
        if(table == null) dispose();
        DefaultTableModel tm = (DefaultTableModel)table.getModel();
        for(int i = 0; i < tm.getRowCount(); i++) {
            String tmp = String.valueOf(tm.getValueAt(i, 1)).trim();
            if(tmp != null && tmp.compareTo("null") != 0 && !tmp.isEmpty()) {
                aliases.put(String.valueOf(tm.getValueAt(i, 1)), String.valueOf(tm.getValueAt(i, 0)));
            }
            
        }
        if(aliases.size() > 0) {
            Listware.inputAliases = aliases;
            if(chkStep != null) {
                Listware.setStepIndicator(chkStep, true);
            }
            dispose();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JScrollPane paneTabeContainer;
    // End of variables declaration//GEN-END:variables
}
