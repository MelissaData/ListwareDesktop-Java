/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listware;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ScrollPaneLayout;

/**
 *
 * @author truep
 */
public class FormMultiSelect extends javax.swing.JDialog {

    String options[] = null;
    Callback callback = null;
    ArrayList<JCheckBox> checkboxes = new ArrayList<>();
    
    interface Callback {
        public void onDone(String[] options);
    }
    /**
     * Creates new form FormMultiSelect
     */
    public FormMultiSelect(java.awt.Frame parent, boolean modal, String[] options, String value, Callback callback) {
        super(parent, modal);
        initComponents();
        this.options = options;
        this.callback = callback;
        ArrayList<String> selectedList = new ArrayList<String>();
        if(value != null) {
            selectedList = new ArrayList<String>(Arrays.asList(value.split(",")));
        }
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setLayout(new GridLayout(options.length, 1, 4, 4));
        for(int i = 0; i < options.length; i++) {
            JCheckBox chk = null;
            if(selectedList.contains(options[i]))
                chk = new JCheckBox(options[i], true);
            else 
                chk = new JCheckBox(options[i], false);
            panel.add(chk);
            this.checkboxes.add(chk);
        }
        panelListContainer.setViewportView(panel);
        
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                int prefHeight = options.length * 24;
                if(prefHeight < 72) prefHeight = 72;
                if(prefHeight > 264) prefHeight = 264;
                prefHeight += 6;
                Dimension dim = panelListContainer.getSize();
                int diffHeight = getSize().height - panelListContainer.getSize().height;
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

        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        panelListContainer = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select Options");

        btnOK.setText("OK");
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        panelListContainer.setHorizontalScrollBar(null);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelListContainer)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                        .addComponent(btnCancel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelListContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK)
                    .addComponent(btnCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        // TODO add your handling code here:
        if(this.callback != null ) {
            ArrayList<String> selectedList = new ArrayList();
            for(JCheckBox chk : this.checkboxes) {
                if(chk.isSelected()) {
                    selectedList.add(chk.getText());
                }
            }
            this.callback.onDone(selectedList.toArray(new String[selectedList.size()]));
        }
        dispose();
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JScrollPane panelListContainer;
    // End of variables declaration//GEN-END:variables
}
