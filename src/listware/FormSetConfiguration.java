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
import java.util.HashMap;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author truep
 */
public class FormSetConfiguration extends javax.swing.JDialog {
    
    JCheckBox chkStep = null;
    IWS inputService = null;
    HashMap<String, String> localServiceOptions;
    HashMap<String, String> localOptionNameTranslations = new HashMap<>();
    JTable table = new JTable();

    /**
     * Creates new form FormSetConfiguration
     */
    public FormSetConfiguration(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public FormSetConfiguration(java.awt.Frame parent, boolean modal, JCheckBox chkStep) 
            throws ClassNotFoundException, 
            NoSuchMethodException, 
            InstantiationException, 
            IllegalAccessException, 
            IllegalArgumentException, 
            InvocationTargetException {
        super(parent, modal);
        this.chkStep = chkStep;
        initComponents();
        
        Class<?> cl = Class.forName("services." + Listware.serviceType);
        Constructor<?> ctor = cl.getConstructor();
        this.inputService = (IWS)ctor.newInstance();
        localServiceOptions = Listware.serviceOptions;
        
        DefaultTableModel tm = new DefaultTableModel(new Object[]{"Option Name", "Value"}, 0);
        
        HashMap<String, ArrayList<String>> settingsList = this.inputService.getSettingsList();
        for(String key : settingsList.keySet()) {
            String optionName = key;
            if (optionName.contains("_")) 
            {
                String optionNameShort = key.split("_")[1];
                this.localOptionNameTranslations.put(optionNameShort, optionName);
                optionName = optionNameShort;

            }
            ArrayList<String> settingValue = settingsList.get(key);
            String optionType = settingValue.get(0);
            List<String> optionValues = settingValue.subList(1, settingValue.size());

            //Go through each option in the service settings list and set the default values depending on the option type
            if(optionType.compareToIgnoreCase("manual") == 0) {
                if (optionValues.size() > 0)
                {
                    tm.addRow(new Object[]{optionName, optionValues.get(0)});
                }
                else 
                {
                    tm.addRow(new Object[]{optionName, ""});
                }
            } else if(optionType.compareToIgnoreCase("single") == 0) {
                tm.addRow(new Object[]{optionName, ""});
            } else if(optionType.compareToIgnoreCase("multiple") == 0) {
                tm.addRow(new Object[]{optionName, "Click to Select"});
            }
        }
        
        //Go through the datagridview row by row, get the option type, and set the cell type depending on what type of option it is
        for (int i = 0; i < tm.getRowCount(); i++) 
        {
            String optionName = this.translateColumn(String.valueOf(tm.getValueAt(i, 0)));
            String optionType = settingsList.get(optionName).get(0);
            List<String> optionValues = settingsList.get(optionName).subList(1, settingsList.get(optionName).size());

            if (settingsList.containsKey(optionName)) 
            {
                if(optionType.compareToIgnoreCase("manual") == 0) {
                    if (localServiceOptions.containsKey(optionName)) 
                    {
                        tm.setValueAt(localServiceOptions.get(optionName), i, 1);
                    }
//                    row.Cells[1].ReadOnly = false;
                } else if(optionType.compareToIgnoreCase("single") == 0) {
                    
                    String val = "";
                    if (localServiceOptions.containsKey(optionName)) 
                    {
                        val = localServiceOptions.get(optionName);
                    }
                    tm.setValueAt(val, i, 1);
                    
                } else if(optionType.compareToIgnoreCase("multiple") == 0) {
                    if (localServiceOptions.containsKey(optionName))
                    {
                        tm.setValueAt(localServiceOptions.get(optionName), i, 1);
                    }
                    
                }
            }
        }
        
        table = new JTable(tm) {
            @Override
            public TableCellEditor getCellEditor(int row, int col) {
                if(col != 1) return null;
                String optionName = translateColumn(String.valueOf(tm.getValueAt(row, 0)));
                String optionType = settingsList.get(optionName).get(0);
                if(settingsList.containsKey(optionName) && optionType.compareToIgnoreCase("single") == 0) {
                    return new DefaultCellEditor(new JComboBox(settingsList.get(optionName).subList(1, settingsList.get(optionName).size()).toArray(new String[settingsList.get(optionName).size() - 1])));
                } else {
                    return super.getCellEditor(row, col);
                }
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                if(col != 1) return false;
                String optionName = translateColumn(String.valueOf(tm.getValueAt(row, 0)));
                String optionType = settingsList.get(optionName).get(0);
                if(settingsList.containsKey(optionName) && optionType.compareToIgnoreCase("multiple") == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        
        table.setRowHeight(24);
        
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                int prefHeight = (table.getRowCount() + 1) * 24;
                if(prefHeight < 72) prefHeight = 72;
                if(prefHeight > 216) prefHeight = 216;
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
        
        panelTableContainer.getViewport().add(table);
        
        JDialog self = this;
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if(col != 1) return;
                String optionName = translateColumn(String.valueOf(tm.getValueAt(row, 0)));
                String optionType = settingsList.get(optionName).get(0);
                List<String> optionValues = settingsList.get(optionName).subList(1, settingsList.get(optionName).size());
                String value = String.valueOf(tm.getValueAt(row, col));
                if (settingsList.containsKey(optionName) && optionType.compareToIgnoreCase("multiple") == 0) 
                {
                    FormMultiSelect form = new FormMultiSelect((JFrame)self.getParent(), true, optionValues.toArray(new String[optionValues.size()]), value, new FormMultiSelect.Callback() {
                        @Override
                        public void onDone(String[] options) {
                            if(options.length > 0) {
                                String val = String.join(",", options);
                                tm.setValueAt(val, row, col);
                            } else {
                                tm.setValueAt("Click to Select", row, col);
                            }
                            
                        }
                    });
                    form.setLocationRelativeTo(self);
                    form.setVisible(true);
                }
            }
        });
    }
    
    private String translateColumn(String optionName) 
    {
        if (this.localOptionNameTranslations.containsKey(optionName))
        {
            return this.localOptionNameTranslations.get(optionName);
        }
        return optionName;
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
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Set Configuration");

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
                .addComponent(panelTableContainer)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addComponent(btnSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 102, Short.MAX_VALUE)
                .addComponent(btnCancel)
                .addGap(90, 90, 90))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelTableContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        DefaultTableModel tm = (DefaultTableModel)table.getModel();
        HashMap<String, ArrayList<String>> settingsList = inputService.getSettingsList();
        for (int i = 0; i < tm.getRowCount(); i++)
        {
            String optionName = this.translateColumn(String.valueOf(tm.getValueAt(i, 0)));
            String optionType = settingsList.get(optionName).get(0);
            List<String> optionValues = settingsList.get(optionName).subList(1, settingsList.get(optionName).size());

            //Go through each row in the datagridview and save the values to userService.serviceOptions
            String value = String.valueOf(tm.getValueAt(i, 1));
            if (value != null)
            {
                if (localServiceOptions.containsKey(optionName))
                {
                    if (value.trim().equals(""))
                    {
                        localServiceOptions.remove(optionName);
                    }
                    else if(!value.equals("Click to Select"))
                    {
                        localServiceOptions.replace(optionName, value);
                    }
                }
                else if (!value.equals("Click to Select")) 
                {
                    localServiceOptions.put(optionName, value);
                }
            }
        }
        
        Listware.serviceOptions = new HashMap<String, String>(localServiceOptions);
        if (localServiceOptions.size() > 0)
        {
            if(chkStep != null) {
                Listware.setStepIndicator(chkStep, true);
            }
        }
        dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JScrollPane panelTableContainer;
    // End of variables declaration//GEN-END:variables
}
