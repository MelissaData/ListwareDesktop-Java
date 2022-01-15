/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listware;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

/**
 *
 * @author truep
 */
public class FormInputNumThreads extends javax.swing.JDialog {

    class NumFilter extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String string,
                AttributeSet attr) throws BadLocationException {

            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.insert(offset, string);

            if (test(sb.toString())) {
                super.insertString(fb, offset, string, attr);
            } else {
                // warn the user and don't allow the insert
            }
        }

        private boolean test(String text) {
            if(text == null || text.isEmpty()) {
                btnSet.setEnabled(false);
                return true;
            }
            btnSet.setEnabled(true);
            try {
                int n = Integer.parseInt(text);
                if(n < 100 && n > 0) {
                    return true;
                }
            } catch (NumberFormatException e) {
            }
            return false;
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text,
                AttributeSet attrs) throws BadLocationException {

            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.replace(offset, offset + length, text);

            if (test(sb.toString())) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                // warn the user and don't allow the insert
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length)
                throws BadLocationException {
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.delete(offset, offset + length);

            if (test(sb.toString())) {
                super.remove(fb, offset, length);
            } else {
                // warn the user and don't allow the insert
            }

        }
    }

    /**
     * Creates new form FormInputNumThreads
     */
    public FormInputNumThreads(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        ((PlainDocument)txtNumThreads.getDocument()).setDocumentFilter(new NumFilter());

        txtNumThreads.setText(String.valueOf(Listware.numThreads));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        btnSet = new javax.swing.JToggleButton();
        txtNumThreads = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Number of Threads");
        setIconImage(null);
        setResizable(false);

        jLabel1.setText("Input Number of Threads:");

        btnSet.setText("OK");
        btnSet.setEnabled(false);
        btnSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNumThreads, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(btnSet, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtNumThreads, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnSet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetActionPerformed
        // TODO add your handling code here:
        try {
            int num = Integer.parseInt(txtNumThreads.getText());
            if (num <= 0 || num > 99) {
                JOptionPane.showMessageDialog(null,
                        "Please enter integer greater than 0 and less than 100", "Error Massage",
                        JOptionPane.ERROR_MESSAGE);
                int n = num < 1 ? 1 : 99;
                txtNumThreads.setText(String.valueOf(n));
                btnSet.setEnabled(false);
                return;
            } else {
                btnSet.setEnabled(true);
            }
        } catch (Exception e) {
            txtNumThreads.setText("1");
            btnSet.setEnabled(false);
            return;
        }
        Listware.numThreads = Integer.parseInt(txtNumThreads.getText());
        dispose();
    }//GEN-LAST:event_btnSetActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnSet;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JFormattedTextField txtNumThreads;
    // End of variables declaration//GEN-END:variables
}
