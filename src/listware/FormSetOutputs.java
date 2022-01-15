/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listware;

import framework.IWS;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author truep
 */
public class FormSetOutputs extends javax.swing.JDialog {

    JCheckBox chkStep = null;
    JTable table = null;
    boolean allSelected = false;
    
    /**
     * Creates new form FormSetOutputs
     */
    public FormSetOutputs(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public FormSetOutputs(java.awt.Frame parent, boolean modal, JCheckBox chkStep) {
        super(parent, modal);
        initComponents();
        this.chkStep = chkStep;
        
        JCheckBox chk = new JCheckBox();
        
        DefaultTableModel tm = new DefaultTableModel(new Object[] {"FieldName", "Select"}, 0);
        try {
            Class<?> cl = Class.forName("services." + Listware.serviceType);
            Constructor<?> ctor = cl.getConstructor();
            IWS service = (IWS)ctor.newInstance();
            String[] outputColumns = service.getOutputColumns();
            
            for(String s : outputColumns) {
                Boolean field = false;
                if(Listware.selectedOutputs != null) {
                    for(String so: Listware.selectedOutputs) {
                        if(s.compareTo(so) == 0) {
                            field = true;
                            break;
                        }
                    }
                } else {
                    field = false;
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
        table = new JTable(tm){
            @Override
            public Class getColumnClass(int c) {
                switch (c) {
                    case 0:
                        return String.class;
                    case 1:
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }
        };
        table.setRowHeight(24);
        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(chk));
        panelTableContainer.getViewport().add(table);
        
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                int prefHeight = (table.getRowCount() + 1) * 24;
                if(prefHeight < 72) prefHeight = 72;
                if(prefHeight > 384) prefHeight = 384;
                prefHeight += 6;
                Dimension dim = panelTableContainer.getSize();
                int diffHeight = getSize().height - panelTableContainer.getSize().height;
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
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelTableContainer = new javax.swing.JScrollPane();
        btnSave = new javax.swing.JButton();
        btnSelectAll = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Set Outputs");

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnSelectAll.setText("Select All");
        btnSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectAllActionPerformed(evt);
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelTableContainer)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 60, Short.MAX_VALUE)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSelectAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel)
                        .addGap(0, 61, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelTableContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnSelectAll)
                    .addComponent(btnCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        if(table == null) return;
        DefaultTableModel tm = (DefaultTableModel)table.getModel();
        ArrayList<String> outputs = new ArrayList<>();
        for(int i = 0; i < tm.getRowCount(); i++) {
            if((Boolean)tm.getValueAt(i, 1) == true) {
                outputs.add((String)tm.getValueAt(i, 0));
            }
        }
        Listware.selectedOutputs = outputs.toArray(new String[outputs.size()]);
        if(chkStep != null) {
            Listware.setStepIndicator(chkStep, true);
        }
        dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectAllActionPerformed
        // TODO add your handling code here:
        if(table == null) return;
        allSelected = !allSelected;
        if(allSelected) {
            DefaultTableModel tm = (DefaultTableModel)table.getModel();
            for(int i = 0; i < tm.getRowCount(); i++) {
                tm.setValueAt(true, i, 1);
            }
            btnSelectAll.setText("Deselect All");
        } else {
            DefaultTableModel tm = (DefaultTableModel)table.getModel();
            for(int i = 0; i < tm.getRowCount(); i++) {
                tm.setValueAt(false, i, 1);
            }
            btnSelectAll.setText("Select All");
        }
        
    }//GEN-LAST:event_btnSelectAllActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSelectAll;
    private javax.swing.JScrollPane panelTableContainer;
    // End of variables declaration//GEN-END:variables
}
