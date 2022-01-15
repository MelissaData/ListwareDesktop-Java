/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listware;

import framework.AutoDetectInputs;
import framework.IWS;
import framework.Input;
import framework.Output;
import framework.Record;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultCaret;
import javax.swing.text.PlainDocument;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import reporting.GenerateReport;
import reporting.HeaderData;


/**
 *
 * @author truep
 */
public class Listware extends javax.swing.JFrame {
    
    public static int numThreads = 1;
    public static int curNumThreads = 0;
    public static int credits = -1;
    public static String inputFilePath = "D:\\SystemData\\Downloads\\MdContacts5.csv";
    public static String outputFilePath = "";
    public static String userLicense = "";
    public static int recordCount = 0;
    
    public static String[] services = {"BusinessCoder", "GlobalAddress", "GlobalEmail", "GlobalIP", "GlobalName", "GlobalPhone", "Personator"};
    public static String serviceType = null;
    
    public static HashMap<String, String> inputAliases = new HashMap();
    public static String[] selectedOutputs = null;
    public static HashMap<String, String> serviceOptions = new HashMap<>();
    
    public static Listware _self = null;
    public Thread runner = null;
    public static boolean running = false;
    public static long lastLogTime = 0;
    public static boolean exitWithError = false;
    
    public Listware() {
        initComponents();
        customInit();
        _self = this;
    }
    
    public void disableGroupBoxes() {
        enableJPanel(panelInput, false);
        enableJPanel(panelConfiguration, false);
        enableJPanel(panelOutput, false);
        enableJPanel(panelProgress, false);
        enableJPanel(panelReporting, false);
    }
    
    public void log(String msg) {
        long now = new Date().getTime();
        if(now - lastLogTime < 500) {
            txtStatus.append(msg + "\r\n");
        } else {
            txtStatus.setText(msg + "\r\n");
        }
        lastLogTime = now;
        DefaultCaret caret = (DefaultCaret)txtStatus.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }
    
    public void logError(String msg) {
        log("[ERROR] - " + msg);
    }
    
    public void enableJPanel(JPanel panel, boolean enabled) {
        enableJPanel(panel, enabled, true);
    }
    
    public void enableJPanel(JPanel panel, boolean enabled, boolean children) {
        Component[] components = panel.getComponents();
        if(children) {
            for(int i = 0; i < components.length; i++) {
                if(components[i].getClass().getSimpleName().equalsIgnoreCase("JPanel")) {
                    enableJPanel((JPanel)components[i], enabled, children);
                }
                components[i].setEnabled(enabled);
            }
        }
        panel.setEnabled(enabled);
    }
    
    public void enableInputPanel() {
        enableJPanel(panelInput, true);
        if(rdInputDelimOther.isSelected()) {
            txtInputDelimiter.setEnabled(true);
        } else {
            txtInputDelimiter.setEnabled(false);
        }
        if(rdInputQualiOther.isSelected()) {
            txtInputQualifier.setEnabled(true);
        } else {
            txtInputQualifier.setEnabled(false);
        }
    }
    
    public void enableOutputPanel() {
        enableJPanel(panelOutput, true);
        if(rdOutputDelimOther.isSelected()) {
            txtOutputDelimiter.setEnabled(true);
        } else {
            txtOutputDelimiter.setEnabled(false);
        }
        if(rdOutputQualiOther.isSelected()) {
            txtOutputQualifier.setEnabled(true);
        } else {
            txtOutputQualifier.setEnabled(false);
        }
        if(chkStep6.isSelected() && chkStep2.isSelected() && chkStep1.isSelected()) {
            btnRun.setEnabled(true);
        } else {
            btnRun.setEnabled(false);
        }
        
        enableReportingPanel();
    }
    
    public void enableReportingPanel() {
        if(new File("Reporting\\ValidDescriptions.cfg").exists() && 
                new File("Reporting\\ValidFilters.cfg").exists() && 
                new File("Reporting\\ReportTemplate.html").exists() ) {
            enableJPanel(panelReporting, true, true);
            if(!chkEnableReporting.isSelected()) {
                txtReportClientName.setEnabled(false);
                txtReportJobDescription.setEnabled(false);
                txtReportMelissaContact.setEnabled(false);
            }
        } else {
            chkEnableReporting.setSelected(false);
        }
        
    }
    
    public static void setStepIndicator(JCheckBox chk, boolean set) {
        chk.setSelected(set);
        if(set) {
            chk.setForeground(new Color(0, 120, 0));
        } else {
            chk.setForeground(Color.red);
        }
    }
    
    public void setInputAliases(IWS serviceObject) {
        try {
            inputAliases = new HashMap<>();

            Input tempInput = new Input(inputFilePath, getInputDelimiter(), getInputQualifier());

            String[] headers = tempInput.headerNames;
            for (String serviceInput : serviceObject.getInputColumns())
            {
                boolean foundMatch = false;
                for(String h : headers) {
                    if(h.toLowerCase().contains(serviceInput)) {
                        inputAliases.put(h, serviceInput);
                        foundMatch = true;
                        break;
                    }
                }
                if(foundMatch) continue;
               
                if (AutoDetectInputs.variationDictionary.containsKey(serviceInput.toLowerCase())) 
                {
                    for (String headerInput : headers)  
                    {
                        boolean found = false;
                        for(String s : AutoDetectInputs.variationDictionary.get(serviceInput.toLowerCase())) {
                            if(s.compareToIgnoreCase(headerInput) == 0) {
                                found = true;
                                inputAliases.put(headerInput, serviceInput);
                                break;
                            }
                        }
                        if(found) break;
                    }
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
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

        btnGroupDelimiter = new javax.swing.ButtonGroup();
        btnGroupTxtQualifier = new javax.swing.ButtonGroup();
        btnGroupDelimiter2 = new javax.swing.ButtonGroup();
        btnGroupTxtQualifier2 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        lblLicenseStatus = new javax.swing.JLabel();
        panelInput = new javax.swing.JPanel();
        btnSelectInput = new javax.swing.JButton();
        txtInputFilePath = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        rdInputDelimComma = new javax.swing.JRadioButton();
        rdInputDelimTab = new javax.swing.JRadioButton();
        rdInputDelimPipe = new javax.swing.JRadioButton();
        rdInputDelimOther = new javax.swing.JRadioButton();
        txtInputDelimiter = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        rdInputQualiNone = new javax.swing.JRadioButton();
        rdInputQualiSingleQ = new javax.swing.JRadioButton();
        rdInputQualiDoubleQ = new javax.swing.JRadioButton();
        rdInputQualiOther = new javax.swing.JRadioButton();
        txtInputQualifier = new javax.swing.JTextField();
        btnInputPreview = new javax.swing.JButton();
        panelConfiguration = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cmbService = new javax.swing.JComboBox<>();
        btnSetConfiguration = new javax.swing.JButton();
        chkIncludeInputColumns = new javax.swing.JCheckBox();
        btnSetInputColumns = new javax.swing.JButton();
        btnSetOutputColumns = new javax.swing.JButton();
        panelOutput = new javax.swing.JPanel();
        btnSelectOutput = new javax.swing.JButton();
        txtOutputFilePath = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        rdOutputDelimComma = new javax.swing.JRadioButton();
        rdOutputDelimTab = new javax.swing.JRadioButton();
        rdOutputDelimPipe = new javax.swing.JRadioButton();
        rdOutputDelimOther = new javax.swing.JRadioButton();
        txtOutputDelimiter = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        rdOutputQualiNone = new javax.swing.JRadioButton();
        rdOutputQualiSingleQ = new javax.swing.JRadioButton();
        rdOutputQualiDoubleQ = new javax.swing.JRadioButton();
        rdOutputQualiOther = new javax.swing.JRadioButton();
        txtOutputQualifier = new javax.swing.JTextField();
        panelProgress = new javax.swing.JPanel();
        chkStep1 = new javax.swing.JCheckBox();
        chkStep2 = new javax.swing.JCheckBox();
        chkStep3 = new javax.swing.JCheckBox();
        chkStep4 = new javax.swing.JCheckBox();
        chkStep5 = new javax.swing.JCheckBox();
        chkStep6 = new javax.swing.JCheckBox();
        panelStatus = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtStatus = new javax.swing.JTextArea();
        panelReporting = new javax.swing.JPanel();
        chkEnableReporting = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        txtReportClientName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtReportJobDescription = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtReportMelissaContact = new javax.swing.JTextField();
        btnRun = new javax.swing.JButton();
        txtLicense = new javax.swing.JPasswordField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu4 = new javax.swing.JMenu();
        menuFileGetStarted = new javax.swing.JMenu();
        menuFileObtainLicense = new javax.swing.JMenuItem();
        menuFilePurchaseCredits = new javax.swing.JMenuItem();
        menuFileNumThreads = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        menuHelpAboutCredits = new javax.swing.JMenuItem();
        menuHelpAboutMelissa = new javax.swing.JMenuItem();
        menuHelpWiki = new javax.swing.JMenuItem();
        menuHelpGitlab = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Listware Desktop 2.1");
        setResizable(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        jLabel1.setText("Set License:");

        panelInput.setBorder(javax.swing.BorderFactory.createTitledBorder("Input"));

        btnSelectInput.setText("Select Input File");
        btnSelectInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectInputActionPerformed(evt);
            }
        });

        txtInputFilePath.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtInputFilePathFocusLost(evt);
            }
        });
        txtInputFilePath.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtInputFilePathKeyPressed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Delimiter"));

        btnGroupDelimiter.add(rdInputDelimComma);
        rdInputDelimComma.setSelected(true);
        rdInputDelimComma.setText("Comma");

        btnGroupDelimiter.add(rdInputDelimTab);
        rdInputDelimTab.setText("Tab");

        btnGroupDelimiter.add(rdInputDelimPipe);
        rdInputDelimPipe.setText("Pipe");

        btnGroupDelimiter.add(rdInputDelimOther);
        rdInputDelimOther.setText("Other");
        rdInputDelimOther.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdInputDelimOtherItemStateChanged(evt);
            }
        });

        txtInputDelimiter.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(rdInputDelimComma)
                        .addGap(16, 16, 16)
                        .addComponent(rdInputDelimTab)
                        .addGap(18, 18, 18)
                        .addComponent(rdInputDelimPipe))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(rdInputDelimOther)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtInputDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdInputDelimComma)
                    .addComponent(rdInputDelimTab)
                    .addComponent(rdInputDelimPipe))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdInputDelimOther)
                    .addComponent(txtInputDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Text Qualifier"));

        btnGroupTxtQualifier.add(rdInputQualiNone);
        rdInputQualiNone.setSelected(true);
        rdInputQualiNone.setText("None");

        btnGroupTxtQualifier.add(rdInputQualiSingleQ);
        rdInputQualiSingleQ.setText("Single Quote");

        btnGroupTxtQualifier.add(rdInputQualiDoubleQ);
        rdInputQualiDoubleQ.setText("Double Quote");

        btnGroupTxtQualifier.add(rdInputQualiOther);
        rdInputQualiOther.setText("Other");
        rdInputQualiOther.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdInputQualiOtherItemStateChanged(evt);
            }
        });

        txtInputQualifier.setEnabled(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(rdInputQualiNone)
                .addGap(2, 2, 2)
                .addComponent(rdInputQualiDoubleQ)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rdInputQualiSingleQ))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(rdInputQualiOther)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtInputQualifier, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdInputQualiNone)
                    .addComponent(rdInputQualiSingleQ)
                    .addComponent(rdInputQualiDoubleQ))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdInputQualiOther)
                    .addComponent(txtInputQualifier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        btnInputPreview.setText("Preview");
        btnInputPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInputPreviewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelInputLayout = new javax.swing.GroupLayout(panelInput);
        panelInput.setLayout(panelInputLayout);
        panelInputLayout.setHorizontalGroup(
            panelInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInputLayout.createSequentialGroup()
                .addGroup(panelInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInputLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelInputLayout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelInputLayout.createSequentialGroup()
                                .addComponent(btnSelectInput)
                                .addGap(18, 18, 18)
                                .addComponent(txtInputFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(panelInputLayout.createSequentialGroup()
                        .addGap(189, 189, 189)
                        .addComponent(btnInputPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelInputLayout.setVerticalGroup(
            panelInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInputLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelectInput)
                    .addComponent(txtInputFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnInputPreview)
                .addGap(7, 7, 7))
        );

        panelConfiguration.setBorder(javax.swing.BorderFactory.createTitledBorder("Configuration"));

        jLabel3.setText("Select Service:");

        cmbService.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbServiceItemStateChanged(evt);
            }
        });

        btnSetConfiguration.setText("Set Configuration");
        btnSetConfiguration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetConfigurationActionPerformed(evt);
            }
        });

        chkIncludeInputColumns.setSelected(true);
        chkIncludeInputColumns.setText("Include Input Columns in Output File");
        chkIncludeInputColumns.setActionCommand("");

        btnSetInputColumns.setText("Set Input Columns");
        btnSetInputColumns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetInputColumnsActionPerformed(evt);
            }
        });

        btnSetOutputColumns.setText("Set Output Columns");
        btnSetOutputColumns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetOutputColumnsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelConfigurationLayout = new javax.swing.GroupLayout(panelConfiguration);
        panelConfiguration.setLayout(panelConfigurationLayout);
        panelConfigurationLayout.setHorizontalGroup(
            panelConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelConfigurationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3)
                    .addComponent(cmbService, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSetConfiguration, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE))
                .addGap(44, 44, 44)
                .addGroup(panelConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkIncludeInputColumns)
                    .addGroup(panelConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnSetOutputColumns, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                        .addComponent(btnSetInputColumns, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelConfigurationLayout.setVerticalGroup(
            panelConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelConfigurationLayout.createSequentialGroup()
                .addGroup(panelConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkIncludeInputColumns)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbService, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSetInputColumns))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSetConfiguration)
                    .addComponent(btnSetOutputColumns))
                .addGap(0, 7, Short.MAX_VALUE))
        );

        panelOutput.setBorder(javax.swing.BorderFactory.createTitledBorder("Output"));

        btnSelectOutput.setText("Select Output File");
        btnSelectOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectOutputActionPerformed(evt);
            }
        });

        txtOutputFilePath.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtOutputFilePathFocusLost(evt);
            }
        });
        txtOutputFilePath.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtOutputFilePathKeyPressed(evt);
            }
        });

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Delimiter"));

        btnGroupDelimiter2.add(rdOutputDelimComma);
        rdOutputDelimComma.setSelected(true);
        rdOutputDelimComma.setText("Comma");

        btnGroupDelimiter2.add(rdOutputDelimTab);
        rdOutputDelimTab.setText("Tab");

        btnGroupDelimiter2.add(rdOutputDelimPipe);
        rdOutputDelimPipe.setText("Pipe");

        btnGroupDelimiter2.add(rdOutputDelimOther);
        rdOutputDelimOther.setText("Other");
        rdOutputDelimOther.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdOutputDelimOtherItemStateChanged(evt);
            }
        });

        txtOutputDelimiter.setEnabled(false);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(rdOutputDelimComma)
                        .addGap(16, 16, 16)
                        .addComponent(rdOutputDelimTab)
                        .addGap(18, 18, 18)
                        .addComponent(rdOutputDelimPipe))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(rdOutputDelimOther)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOutputDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdOutputDelimComma)
                    .addComponent(rdOutputDelimTab)
                    .addComponent(rdOutputDelimPipe))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdOutputDelimOther)
                    .addComponent(txtOutputDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Text Qualifier"));

        btnGroupTxtQualifier2.add(rdOutputQualiNone);
        rdOutputQualiNone.setSelected(true);
        rdOutputQualiNone.setText("None");

        btnGroupTxtQualifier2.add(rdOutputQualiSingleQ);
        rdOutputQualiSingleQ.setText("Single Quote");

        btnGroupTxtQualifier2.add(rdOutputQualiDoubleQ);
        rdOutputQualiDoubleQ.setText("Double Quote");

        btnGroupTxtQualifier2.add(rdOutputQualiOther);
        rdOutputQualiOther.setText("Other");
        rdOutputQualiOther.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdOutputQualiOtherItemStateChanged(evt);
            }
        });

        txtOutputQualifier.setEnabled(false);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(rdOutputQualiNone)
                .addGap(2, 2, 2)
                .addComponent(rdOutputQualiDoubleQ)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rdOutputQualiSingleQ))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(rdOutputQualiOther)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtOutputQualifier, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdOutputQualiNone)
                    .addComponent(rdOutputQualiSingleQ)
                    .addComponent(rdOutputQualiDoubleQ))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdOutputQualiOther)
                    .addComponent(txtOutputQualifier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout panelOutputLayout = new javax.swing.GroupLayout(panelOutput);
        panelOutput.setLayout(panelOutputLayout);
        panelOutputLayout.setHorizontalGroup(
            panelOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOutputLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelOutputLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelOutputLayout.createSequentialGroup()
                        .addComponent(btnSelectOutput)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtOutputFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelOutputLayout.setVerticalGroup(
            panelOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOutputLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelectOutput)
                    .addComponent(txtOutputFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(panelOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelProgress.setBorder(javax.swing.BorderFactory.createTitledBorder("Progress (* required)"));

        chkStep1.setForeground(new java.awt.Color(255, 0, 0));
        chkStep1.setText("1. Input file set*");
        chkStep1.setEnabled(false);

        chkStep2.setForeground(new java.awt.Color(255, 0, 0));
        chkStep2.setText("2. Service set*");
        chkStep2.setEnabled(false);

        chkStep3.setForeground(new java.awt.Color(255, 0, 0));
        chkStep3.setText("3. Input column set");
        chkStep3.setEnabled(false);

        chkStep4.setForeground(new java.awt.Color(255, 0, 0));
        chkStep4.setText("4. Options configured");
        chkStep4.setEnabled(false);

        chkStep5.setForeground(new java.awt.Color(255, 0, 0));
        chkStep5.setText("5. Output columns set");
        chkStep5.setEnabled(false);

        chkStep6.setForeground(new java.awt.Color(255, 0, 0));
        chkStep6.setText("6. Ouput file set*");
        chkStep6.setEnabled(false);

        javax.swing.GroupLayout panelProgressLayout = new javax.swing.GroupLayout(panelProgress);
        panelProgress.setLayout(panelProgressLayout);
        panelProgressLayout.setHorizontalGroup(
            panelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProgressLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkStep1)
                    .addComponent(chkStep2)
                    .addComponent(chkStep3))
                .addGap(27, 27, 27)
                .addGroup(panelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkStep6)
                    .addComponent(chkStep5)
                    .addComponent(chkStep4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelProgressLayout.setVerticalGroup(
            panelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProgressLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(panelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkStep1)
                    .addComponent(chkStep4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkStep2)
                    .addComponent(chkStep5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkStep3)
                    .addComponent(chkStep6)))
        );

        panelStatus.setBorder(javax.swing.BorderFactory.createTitledBorder("Status"));

        txtStatus.setEditable(false);
        txtStatus.setBackground(new java.awt.Color(240, 240, 240));
        txtStatus.setColumns(20);
        txtStatus.setLineWrap(true);
        txtStatus.setRows(5);
        jScrollPane1.setViewportView(txtStatus);

        javax.swing.GroupLayout panelStatusLayout = new javax.swing.GroupLayout(panelStatus);
        panelStatus.setLayout(panelStatusLayout);
        panelStatusLayout.setHorizontalGroup(
            panelStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        panelStatusLayout.setVerticalGroup(
            panelStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        panelReporting.setBorder(javax.swing.BorderFactory.createTitledBorder("Reporting"));

        chkEnableReporting.setText("Enable Reporting");
        chkEnableReporting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkEnableReportingActionPerformed(evt);
            }
        });

        jLabel4.setText("Client Name:");

        jLabel5.setText("Job Description:");

        jLabel6.setText("Melissa Contact:");

        javax.swing.GroupLayout panelReportingLayout = new javax.swing.GroupLayout(panelReporting);
        panelReporting.setLayout(panelReportingLayout);
        panelReportingLayout.setHorizontalGroup(
            panelReportingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelReportingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelReportingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelReportingLayout.createSequentialGroup()
                        .addComponent(chkEnableReporting)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelReportingLayout.createSequentialGroup()
                        .addGroup(panelReportingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(panelReportingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtReportClientName)
                            .addComponent(txtReportJobDescription, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtReportMelissaContact, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(28, 28, 28))))
        );
        panelReportingLayout.setVerticalGroup(
            panelReportingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelReportingLayout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(chkEnableReporting)
                .addGap(18, 18, 18)
                .addGroup(panelReportingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtReportClientName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelReportingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtReportJobDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelReportingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtReportMelissaContact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)))
        );

        btnRun.setText("Run");
        btnRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRunActionPerformed(evt);
            }
        });

        txtLicense.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLicenseFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtLicenseFocusLost(evt);
            }
        });
        txtLicense.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtLicenseKeyPressed(evt);
            }
        });

        jMenu4.setText("File");

        menuFileGetStarted.setText("Getting Started");

        menuFileObtainLicense.setText("Obtain License");
        menuFileObtainLicense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFileObtainLicenseActionPerformed(evt);
            }
        });
        menuFileGetStarted.add(menuFileObtainLicense);

        menuFilePurchaseCredits.setText("Purchase Credits");
        menuFilePurchaseCredits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFilePurchaseCreditsActionPerformed(evt);
            }
        });
        menuFileGetStarted.add(menuFilePurchaseCredits);

        jMenu4.add(menuFileGetStarted);

        menuFileNumThreads.setText("Number of Threads");
        menuFileNumThreads.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFileNumThreadsActionPerformed(evt);
            }
        });
        jMenu4.add(menuFileNumThreads);

        jMenuBar1.add(jMenu4);

        jMenu5.setText("Help");

        menuHelpAboutCredits.setText("About Credits");
        menuHelpAboutCredits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuHelpAboutCreditsActionPerformed(evt);
            }
        });
        jMenu5.add(menuHelpAboutCredits);

        menuHelpAboutMelissa.setText("About Melissa");
        menuHelpAboutMelissa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuHelpAboutMelissaActionPerformed(evt);
            }
        });
        jMenu5.add(menuHelpAboutMelissa);

        menuHelpWiki.setText("Wiki");
        menuHelpWiki.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuHelpWikiActionPerformed(evt);
            }
        });
        jMenu5.add(menuHelpWiki);

        menuHelpGitlab.setText("Gitlab");
        menuHelpGitlab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuHelpGitlabActionPerformed(evt);
            }
        });
        jMenu5.add(menuHelpGitlab);

        jMenuBar1.add(jMenu5);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelOutput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(panelReporting, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtLicense, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblLicenseStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(panelConfiguration, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelInput, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(panelProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRun, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblLicenseStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLicense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(panelProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelConfiguration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelReporting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelOutput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRun, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rdInputQualiOtherItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdInputQualiOtherItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == ItemEvent.SELECTED) {
            txtInputQualifier.setEnabled(true);
        } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
            txtInputQualifier.setEnabled(false);
        }
    }//GEN-LAST:event_rdInputQualiOtherItemStateChanged

    private void rdOutputDelimOtherItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdOutputDelimOtherItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == ItemEvent.SELECTED) {
            txtOutputDelimiter.setEnabled(true);
        } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
            txtOutputDelimiter.setEnabled(false);
        }
    }//GEN-LAST:event_rdOutputDelimOtherItemStateChanged

    private void rdOutputQualiOtherItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdOutputQualiOtherItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == ItemEvent.SELECTED) {
            txtOutputQualifier.setEnabled(true);
        } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
            txtOutputQualifier.setEnabled(false);
        }
    }//GEN-LAST:event_rdOutputQualiOtherItemStateChanged

    private void btnSelectInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectInputActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("CSV and TXT files", "csv", "txt");
        FileFilter filter1 = new FileNameExtensionFilter("CSV files", "csv");
        FileFilter filter2 = new FileNameExtensionFilter("TXT files", "txt");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.addChoosableFileFilter(filter1);
        fileChooser.addChoosableFileFilter(filter2);
        File workingDirectory = new File(System.getProperty("user.dir"));
        fileChooser.setCurrentDirectory(workingDirectory);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            txtInputFilePath.setText(path);
            setInputFilePath();
        }
    }//GEN-LAST:event_btnSelectInputActionPerformed

    private void menuFileObtainLicenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFileObtainLicenseActionPerformed
        // TODO add your handling code here:
        openWebPage("https://www.melissadata.com/user/signin.aspx");
    }//GEN-LAST:event_menuFileObtainLicenseActionPerformed

    private void menuFilePurchaseCreditsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFilePurchaseCreditsActionPerformed
        // TODO add your handling code here:
        openWebPage("https://www.melissa.com/credits/");
    }//GEN-LAST:event_menuFilePurchaseCreditsActionPerformed

    private void menuFileNumThreadsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFileNumThreadsActionPerformed
        // TODO add your handling code here:
        FormInputNumThreads form = new FormInputNumThreads(this, true);
        form.setLocationRelativeTo(this);
        form.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("res/LWDT-applogo.png")).getImage());
        form.setVisible(true);
        
    }//GEN-LAST:event_menuFileNumThreadsActionPerformed

    private void menuHelpAboutCreditsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuHelpAboutCreditsActionPerformed
        // TODO add your handling code here:
        openWebPage("https://www.melissa.com/credits/developer");
    }//GEN-LAST:event_menuHelpAboutCreditsActionPerformed

    private void menuHelpAboutMelissaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuHelpAboutMelissaActionPerformed
        // TODO add your handling code here:
        final JDialog frame = new JDialog(this, "About Melissa", true);
        frame.getContentPane().add(new FormAboutMelissa());
        frame.pack();
        frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("res/LWDT-applogo.png")).getImage());
        frame.setLocationRelativeTo(this);
        frame.setResizable(false);
        frame.setVisible(true);
        
    }//GEN-LAST:event_menuHelpAboutMelissaActionPerformed

    private void menuHelpWikiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuHelpWikiActionPerformed
        // TODO add your handling code here:
        openWebPage("http://wiki.melissadata.com/index.php?title=Listware_Desktop");
    }//GEN-LAST:event_menuHelpWikiActionPerformed

    private void menuHelpGitlabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuHelpGitlabActionPerformed
        // TODO add your handling code here:
        openWebPage("https://git.melissadata.com/Listware/ListwareDesktopJava");
    }//GEN-LAST:event_menuHelpGitlabActionPerformed

    public void getCreditCount(boolean showConsumed) {
        String license = txtLicense.getText().trim();
        if(license.isEmpty()) {
            disableGroupBoxes();
            btnRun.setEnabled(false);
            lblLicenseStatus.setText("");
            return;
        }
        userLicense = license;
        saveLicense();

//        int totalCredits = 1000;  //////////////// temp code begin
//        int cnt = 900;
//        if(Listware.credits == -1) {
//            lblLicenseStatus.setText("Available Credits: " + totalCredits);
//            Listware.credits = cnt;
//        } else {
//            int consumed = Listware.credits - cnt;
//            lblLicenseStatus.setText("Available Credits: " + totalCredits + " ( " + consumed + " credits consumed)");
//            Listware.credits = cnt;
//        }
//        enableInputPanel();
//        enableJPanel(panelProgress, true, false); ////////////// temp code end
        
        try {
            String url = "http://token.melissadata.net/v3/web/service.svc/QueryCustomerInfo?L=" + URLEncoder.encode(userLicense, "UTF-8") + "&P=&K=";
            System.out.println(url);
            
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            
            // optional default is GET
            con.setRequestMethod("GET");
            
            //add request header
            //con.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(response.toString())));
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("Result");
            if(nList.item(0).hasChildNodes()) {
                lblLicenseStatus.setText("Invalid license");
            } else {
                String totalCredits = doc.getElementsByTagName("TotalCredits").item(0).getTextContent();
                int cnt = Integer.parseInt(totalCredits);
                if(cnt >= 0) {
                    if(Listware.credits == -1 || !showConsumed) {
                        lblLicenseStatus.setText("Available Credits: " + totalCredits);
                        Listware.credits = cnt;
                        log("");
                    } else {
                        int consumed = Listware.credits - cnt;
                        lblLicenseStatus.setText("Available Credits: " + totalCredits + " ( " + consumed + " credits consumed)");
                        Listware.credits = cnt;
                    }
                }
            }
            
            enableInputPanel();
            enableJPanel(panelProgress, true, false);
            
            if(chkStep1.isSelected()) {
                enableJPanel(panelConfiguration, true, true);
                enableOutputPanel();
            }

            //print result
            System.out.println(response.toString());
            
            return;
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
            logError(ex.getMessage());
        } catch (MalformedURLException ex) {
            Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
            logError(ex.getMessage());
        } catch (ProtocolException ex) {
            Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
            logError(ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
            logError(ex.getMessage());
        } catch (SAXException ex) {
            Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
            logError(ex.getMessage());
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
            logError(ex.getMessage());
        }
        lblLicenseStatus.setText("");
    }
    
    private void txtLicenseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLicenseKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            getCreditCount(false);
        }
    }//GEN-LAST:event_txtLicenseKeyPressed

    private void txtLicenseFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLicenseFocusGained
        // TODO add your handling code here:
        txtLicense.setEchoChar('\0');
    }//GEN-LAST:event_txtLicenseFocusGained

    private void txtLicenseFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLicenseFocusLost
        // TODO add your handling code here:
        txtLicense.setEchoChar('*');
        String txt = txtLicense.getText();
        if(txt.compareTo(Listware.userLicense) != 0) {
            getCreditCount(false);
        }
    }//GEN-LAST:event_txtLicenseFocusLost

    private void rdInputDelimOtherItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdInputDelimOtherItemStateChanged
        // TODO add your handling code here:
        if(rdInputDelimOther.isSelected()) {
            txtInputDelimiter.setEnabled(true);
        } else {
            txtInputDelimiter.setEnabled(false);
        }
    }//GEN-LAST:event_rdInputDelimOtherItemStateChanged

    private void txtInputFilePathKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtInputFilePathKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            setInputFilePath();
        }
    }//GEN-LAST:event_txtInputFilePathKeyPressed

    private void btnInputPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInputPreviewActionPerformed
        // TODO add your handling code here:
        if(!chkStep1.isSelected()) {
            log("Please set input file");
            return;
        }
        String delimChar = getInputDelimiter();
        String txtQChar = getInputQualifier();
        FormPreviewInput formPreview = new FormPreviewInput(this, false, inputFilePath, delimChar, txtQChar);
        formPreview.setLocationRelativeTo(this);
        formPreview.setVisible(true);
        
    }//GEN-LAST:event_btnInputPreviewActionPerformed

    private void cmbServiceItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbServiceItemStateChanged
        // TODO add your handling code here:
        String service = String.valueOf(cmbService.getSelectedItem());
        if(Listware.serviceType != null && service.compareTo(Listware.serviceType) == 0) return;
        
        resetConfiguration();
        recordCount = 0;
        
        Listware.serviceType = service;
        if(chkStep1.isSelected() && Listware.serviceType != null) {
            log("Service type set.");
            setTitle("Listware Desktop 2.1 : " + Listware.serviceType);
            setStepIndicator(chkStep2, true);
            if(Listware.outputFilePath.isEmpty()) {
                String tmp[] = Listware.inputFilePath.split("\\.");
                String res = "";
                if(tmp.length > 1) {
                    for(int i = 0; i < tmp.length; i++) {
                        res += tmp[i];
                        if(i == tmp.length - 2) {
                            res += "_OUTPUT";
                        }
                        if(i < tmp.length - 1) res += ".";
                    }
                } else {
                    res = Listware.inputFilePath + "_OUTPUT";
                }
                
                txtOutputFilePath.setText(res);
                setOutputFilePath();
            }
            enableOutputPanel();
        }
    }//GEN-LAST:event_cmbServiceItemStateChanged

    private void btnSetInputColumnsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetInputColumnsActionPerformed
        if(serviceType == null) {
            log("Please select service type");
            return;
        }
        try {
            // TODO add your handling code here:
            
            String delimChar = getInputDelimiter();
            String txtQChar = getInputQualifier();
            FormSetInputs formSetInputs = new FormSetInputs(this, true, inputFilePath, delimChar, txtQChar, chkStep3);
            formSetInputs.setLocationRelativeTo(this);
            formSetInputs.setVisible(true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
            logError(ex.getMessage());
        }
    }//GEN-LAST:event_btnSetInputColumnsActionPerformed

    private void btnSetOutputColumnsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetOutputColumnsActionPerformed
        // TODO add your handling code here:
        if(serviceType == null) {
            log("Please select service type");
            return;
        }
        FormSetOutputs formSetOutputs = new FormSetOutputs(this, true, chkStep5);
        formSetOutputs.setLocationRelativeTo(this);
        formSetOutputs.setVisible(true);
    }//GEN-LAST:event_btnSetOutputColumnsActionPerformed

    private void btnSetConfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetConfigurationActionPerformed
        if(serviceType == null) {
            log("Please select service type");
            return;
        }
        try {
            // TODO add your handling code here:
            FormSetConfiguration formSetConfiguration = new FormSetConfiguration(this, true, chkStep4);
            formSetConfiguration.setLocationRelativeTo(this);
            formSetConfiguration.setVisible(true);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
            logError(ex.getMessage());
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
            logError(ex.getMessage());
        } catch (InstantiationException ex) {
            Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
            logError(ex.getMessage());
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
            logError(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
            logError(ex.getMessage());
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
            logError(ex.getMessage());
        }
    }//GEN-LAST:event_btnSetConfigurationActionPerformed

    private synchronized void createRecordID(Record[] inputRecords) 
    {
        for (Record inputRecord : inputRecords) 
        {
            inputRecord.recordID = recordCount;
            recordCount++;
        }
    }
    
    private void createNewThread(IWS serviceObject, Input input, Output output) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                increaseCurNumThreads();
                //log("Creating new thread. curNumThreads = " + curNumThreads); // temp
                try {
                    Record[] inputRecords = input.getRecords(serviceObject.getMaxRecsPerRequest());
                    createRecordID(inputRecords);
                    if(inputRecords.length > 0) {
                        Record[] aliasedRecords = aliasRecords(inputRecords);
                        serviceObject.sendToService(aliasedRecords);
                        Record[] localOutputRecords = serviceObject.getOutputRecords();

                        if(localOutputRecords == null) {
                            throw new Exception("No record in API reponse");
                        } else {
                            if (selectedOutputs != null) localOutputRecords = pruneNotSelectedOutputs(localOutputRecords);
                            if (chkIncludeInputColumns.isSelected()) localOutputRecords = combineRecordsIfPassThrough(inputRecords, localOutputRecords);

                            output.sendRecords(localOutputRecords);
                        }
                    }
                    Thread.sleep(100);
                } catch(Exception ex) {
                    logError(ex.getMessage()); // temp
                    Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
                    exitWithError = true;
                    stopRunning();
                } finally {
                    decreaseCurNumThreads();
                    //log("A thread finished. curNumThreads = " + curNumThreads); // temp
                    if(curNumThreads == 0 && running == false) {
                        try {
                            input.closeReader();
                            output.closeWriter();
                        } catch(Exception ex) {
                            
                        } finally {
                            if(!exitWithError) log("Processing finished.");
                            else log("Processing interrupted.");
                            log(output.linesWritten + " records written.");
                            if(chkEnableReporting.isSelected()) {
                                createReport();
                            }
                            runner = null;
                            getCreditCount(true);
                            btnRun.setText("Run");
                            btnRun.setEnabled(true);
                        }
                    }
                }
            }
        });
        thread.start();
    }
    
    public synchronized void stopRunning() {
        running = false;
    }
    
    public synchronized void increaseCurNumThreads() {
        curNumThreads++;
    }
    
    public synchronized void decreaseCurNumThreads() {
        curNumThreads--;
    }
    
    private void btnRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRunActionPerformed
        // TODO add your handling code here:
        if(runner == null) {
            
            File of = new File(outputFilePath);
            if(of.exists()) {
                int res = JOptionPane.showConfirmDialog(this, "Overwrite file?", "Warning", JOptionPane.OK_CANCEL_OPTION);
                if(res == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }
            
            runner = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Output output = new Output(outputFilePath, getOutputDelimiter(), getOutputQualifier());
                        Input input = new Input(inputFilePath, getInputDelimiter(), getInputQualifier());
                        Constructor<?> ctor = Class.forName("services." + serviceType).getConstructor();
                        IWS serviceObject = (IWS)ctor.newInstance();
                        serviceObject.setServiceOptions(new HashMap<String, String>(Listware.serviceOptions));
                        serviceObject.setUserLicense(Listware.userLicense);
                        
                        if(!chkStep3.isSelected()) {
                            setInputAliases(serviceObject);
                        }
                        if(inputAliases.size() == 0) {
                            log("Input columns were not able to be autodetected.\r\nPlease set manually.");
                            running = false;
                            runner = null;
                            btnRun.setText("Run");
                            return;
                        }
                        
                        recordCount = 0;
                        running = true;
                        exitWithError = false;
                        
                        if(!serviceObject.isNeedsAllRecords()){
                            log("Processing records.\r\nUsing " + numThreads + " threads.");
                            curNumThreads = 0;
                            while(true) {
                                if(input.checkForEnd() || !running) break;
                                
                                if(curNumThreads < numThreads) {
                                    createNewThread(serviceObject, input, output);
                                }
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            
                        } else {
                            log("Processing records.");
                            while (!input.checkForEnd() && running)
                            {
                                Record[] fullInputRecords = input.getRecords(serviceObject.getMaxRecsPerRequest());
                                createRecordID(fullInputRecords);
                                Record[] aliasedRecords = aliasRecords(fullInputRecords);
                                serviceObject.sendToService(aliasedRecords);
                            }

                            //When we're done processing, combine and write the records
                            Record[] outputRecords = serviceObject.getOutputRecords();
//                            if (chkIncludeInputColumns) outputRecords = combineRecordsIfPassThrough(inputHolder.ToArray(), outputRecords);
                            output.sendRecords(outputRecords);
                            
                            input.closeReader();
                            output.closeWriter();
                            
                            log("Processing finished!");
                            if(chkEnableReporting.isSelected()) {
                                createReport();
                            }
                            running = false;
                            runner = null;
                            getCreditCount(true);
                            btnRun.setText("Run");
                            btnRun.setEnabled(true);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
                        logError(ex.getMessage());
                    } finally {
                        running = false;
                    }
                }
            });
            runner.start();
            btnRun.setText("Cancel");
        } else {
            btnRun.setText("Stopping");
            btnRun.setEnabled(false);
            running = false;
        }
    }//GEN-LAST:event_btnRunActionPerformed

    public void createReport() {
        //Set reporting data
        HeaderData hd = new HeaderData();
        hd.Client = txtReportClientName.getText();
        hd.Contacts = txtReportMelissaContact.getText();
        hd.IDENT = userLicense;
        hd.JobDescription = txtReportJobDescription.getText();
        hd.InputFileName = new File(outputFilePath).getName();

        //Generate report, request the result code table, and set the file path/delimiter/qualifier
        //Then get the reports we want and request a report form to be generated
        GenerateReport reportModule = new GenerateReport(hd);
        reportModule.requestedRCTable = true;
        reportModule.ReadFileCreateTreeMapopenWith(outputFilePath, getOutputDelimiter(), getOutputQualifier());
        reportModule.GetRequestedReportsList(fillFilterList(reportModule));
        
        reportModule.GenerateReportFile();
    }
    
    private ArrayList<String> fillFilterList(GenerateReport reportModule) 
    {
        boolean AddedToList = false;
        String validTemplateCodes;
        String[] contributor;
        ArrayList<String> reportTypes = new ArrayList<>();
        for (String key : reportModule.validTemplates.keySet())
        {
            AddedToList = false;
            validTemplateCodes = reportModule.validTemplates.get(key);
            contributor = validTemplateCodes.split(",");
            for (String c : contributor)
            {
                // go thru each result code to see if it is in a template
                for (String key2 : reportModule.openWith.keySet())
                {
                    if (key2.contains(c) == true)
                    {
                        reportTypes.add(key);
                        AddedToList = true;
                        break;
                    }
                }
                if (AddedToList == true)
                    break;
            }
        }
        return reportTypes;
    }
    
    public void resetConfiguration() {
        if(Listware.inputAliases != null) Listware.inputAliases.clear();
        Listware.selectedOutputs = null;
        if(Listware.serviceOptions != null) Listware.serviceOptions.clear();
        setStepIndicator(chkStep3, false);
        setStepIndicator(chkStep4, false);
        setStepIndicator(chkStep5, false);
    }
    
    public void setInputFilePath() {
        String path = txtInputFilePath.getText();
        inputFilePath = path.trim();
        if(inputFilePath.isEmpty()) {
            setStepIndicator(chkStep1, false);
            log("Please set input file path.");
            
            enableJPanel(panelConfiguration, false, true);
            enableJPanel(panelOutput, false, true);
            enableJPanel(panelReporting, false, true);
            btnRun.setEnabled(false);
            
            resetConfiguration();
            
            return;
        }
        File f = new File(path);
        if(f.exists()) {
            setStepIndicator(chkStep1, true);
            Listware.inputFilePath = path;
            enableJPanel(panelConfiguration, true);
            if(chkStep2.isSelected()) {
                enableOutputPanel();
            }
            log("Input file set.");
        } else {
            setStepIndicator(chkStep1, false);
            log("Input file not existing.");
            
            enableJPanel(panelConfiguration, false, true);
            enableJPanel(panelOutput, false, true);
            enableJPanel(panelReporting, false, true);
            btnRun.setEnabled(false);

            resetConfiguration();
        }
    }
    
    private void txtInputFilePathFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtInputFilePathFocusLost
        // TODO add your handling code here:
        setInputFilePath();
    }//GEN-LAST:event_txtInputFilePathFocusLost

    private void btnSelectOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectOutputActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("CSV and TXT files", "csv", "txt");
        FileFilter filter1 = new FileNameExtensionFilter("CSV files", "csv");
        FileFilter filter2 = new FileNameExtensionFilter("TXT files", "txt");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.addChoosableFileFilter(filter1);
        fileChooser.addChoosableFileFilter(filter2);
        File workingDirectory = new File(System.getProperty("user.dir"));
        fileChooser.setCurrentDirectory(workingDirectory);
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            txtOutputFilePath.setText(path);
            setOutputFilePath();
        }
    }//GEN-LAST:event_btnSelectOutputActionPerformed

    private void txtOutputFilePathKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOutputFilePathKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            setOutputFilePath();
        }
    }//GEN-LAST:event_txtOutputFilePathKeyPressed

    private void txtOutputFilePathFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOutputFilePathFocusLost
        // TODO add your handling code here:
        setOutputFilePath();
    }//GEN-LAST:event_txtOutputFilePathFocusLost

    private void chkEnableReportingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkEnableReportingActionPerformed
        // TODO add your handling code here:
        if(chkEnableReporting.isSelected()) {
            txtReportClientName.setEnabled(true);
            txtReportJobDescription.setEnabled(true);
            txtReportMelissaContact.setEnabled(true);
        } else {
            txtReportClientName.setEnabled(false);
            txtReportJobDescription.setEnabled(false);
            txtReportMelissaContact.setEnabled(false);
        }
    }//GEN-LAST:event_chkEnableReportingActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
        this.requestFocus();
    }//GEN-LAST:event_formMouseClicked

    public void setOutputFilePath() {
        String path = txtOutputFilePath.getText().trim();
        Listware.outputFilePath = path;
        if(path.isEmpty()) {
            setStepIndicator(chkStep6, false);
            enableJPanel(panelReporting, false, true);
            btnRun.setEnabled(false);
            log("Please set output file path.");
            return;
        }
        log("Output file set.");
        setStepIndicator(chkStep6, true);
        enableReportingPanel();
        btnRun.setEnabled(true);
    }
//    private void sendRecordToService()
//    {
//        IWS localService = Activator.CreateInstance(serviceType) as IWS;
//        localService.userLicense = Listware.userLicense;
//        localService.serviceOptions = new HashMap<String,String>(Listware.serviceOptions);
//
//        Record[] inputRecords;
//        if (inputQueue.TryDequeue(out inputRecords))
//        {
//            Record[] aliasedRecords = this.aliasRecords(inputRecords);
//            localService.sendToService(aliasedRecords);
//            Record[] localOutputRecords = localService.outputRecords;
//
//            if (selectedOutputs != null) localOutputRecords = pruneNotSelectedOutputs(localOutputRecords);
//            if (passThroughCheckBox.Checked) localOutputRecords = combineRecordsIfPassThrough(inputRecords, localOutputRecords);
//
//            outputQueue.Enqueue(localOutputRecords);
//            ((ManualResetEvent)state).Set();
//        }
//    }

    //Method used to combine input and output records if passthrough is selected
    private Record[] combineRecordsIfPassThrough(Record[] inputRecords, Record[] outputRecords) 
    {
        HashMap<Integer, Record> inputPlaceHolder = new HashMap<>();
        for(Record r: inputRecords) {
            inputPlaceHolder.put(r.getRecordID(), r);
        }
        for(int i = 0; i < outputRecords.length; i++) 
        {
            Record tempOutputRecord = outputRecords[i];
            if (inputPlaceHolder.containsKey(tempOutputRecord.getRecordID())) 
            {
                Record tempInRecord = new Record(inputPlaceHolder.get(tempOutputRecord.recordID));
                Record tempOutRecord = new Record(tempOutputRecord);
                tempInRecord.combineRecord(tempOutRecord);
                outputRecords[i] = tempInRecord;
            }
        }
        return outputRecords;
    }

    //Method to prune the unselected outputs from the output record
    private Record[] pruneNotSelectedOutputs(Record[] outputRecords) 
    {
        ArrayList<Record> tempRecords = new ArrayList<Record>();
        for (Record tempRecord : outputRecords) 
        {
            Record newRecord = new Record();
            for (String selectedOutput : selectedOutputs) 
            {
                for (String key: tempRecord.map.keySet()) 
                {
                    if ((key.contains(selectedOutput)) || (key.toLowerCase().contains("results"))) 
                    {
                        newRecord.addField(key, tempRecord.map.get(key));
                    }
                }
            }
            newRecord.setRecordID(tempRecord.getRecordID());
            tempRecords.add(newRecord);
        }
        return tempRecords.toArray(new Record[tempRecords.size()]);
    }

    //Method to translate input file header names to service input record names
    private Record[] aliasRecords(Record[] fullInputRecords) 
    {
        Record[] aliasedRecords = new Record[fullInputRecords.length];
        for (int i = 0; i < fullInputRecords.length; i++) 
        {
            Record fullInputRecord = fullInputRecords[i];
            Record aliasedRecord = new Record();

            for (String key: fullInputRecord.map.keySet()) 
            {
                if (inputAliases.containsKey(key)) 
                {
                    aliasedRecord.map.put(inputAliases.get(key), fullInputRecord.map.get(key));
                }
            }
            aliasedRecord.setRecordID(fullInputRecord.getRecordID());
            aliasedRecords[i] = aliasedRecord;
        }
        return aliasedRecords;
    }
    
    private boolean openWebPage(String url) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI(url));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    public String getInputDelimiter() {
        if(rdInputDelimComma.isSelected()) {
            return ",";
        } else if(rdInputDelimTab.isSelected()) {
            return "\t";
        } else if(rdInputDelimPipe.isSelected()) {
            return "|";
        } else if(rdInputDelimOther.isSelected()) {
            return txtInputDelimiter.getText();
        }
        return ",";
    }
    
    public String getInputQualifier() {
        if(rdInputQualiNone.isSelected()) {
            return null;
        } else if(rdInputQualiDoubleQ.isSelected()) {
            return "\"";
        } else if(rdInputQualiSingleQ.isSelected()) {
            return "'";
        } else if(rdInputQualiOther.isSelected()) {
            return txtInputQualifier.getText();
        }
        return "\"";
    }
    
    public String getOutputDelimiter() {
        if(rdOutputDelimComma.isSelected()) {
            return ",";
        } else if(rdOutputDelimTab.isSelected()) {
            return "\t";
        } else if(rdOutputDelimPipe.isSelected()) {
            return "|";
        } else if(rdOutputDelimOther.isSelected()) {
            return txtOutputDelimiter.getText();
        }
        return ",";
    }
    
    public String getOutputQualifier() {
        if(rdOutputQualiNone.isSelected()) {
            return null;
        } else if(rdOutputQualiDoubleQ.isSelected()) {
            return "\"";
        } else if(rdOutputQualiSingleQ.isSelected()) {
            return "'";
        } else if(rdOutputQualiOther.isSelected()) {
            return txtOutputQualifier.getText();
        }
        return "\"";
    }
    
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
            java.util.logging.Logger.getLogger(Listware.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Listware.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Listware.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Listware.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Listware().setVisible(true);
            }
        });
    }
    
    public void readLicense() {
        try {
            File lf = new File("listware.properties");
            if(lf.exists()) {
                Properties props = new Properties();
                props.load(new FileInputStream(lf));
                String license = (String)props.getProperty("license");
                if(license != null && !license.isEmpty()) {
                    userLicense = license;
                    txtLicense.setText(license);
                    getCreditCount(false);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
            logError(ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
            logError(ex.getMessage());
        }
    }
    
    public void saveLicense() {
        try {
            Properties props = new Properties();
            props.setProperty("license", userLicense);
            props.store(new FileWriter("listware.properties"), "listware settings");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
            logError(ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(Listware.class.getName()).log(Level.SEVERE, null, ex);
            logError(ex.getMessage());
        }
    }
    
    public void customInit() {
        disableGroupBoxes();
        btnRun.setEnabled(false);
        setIconImage(new ImageIcon(getClass().getClassLoader().getResource("res/LWDT-applogo.png")).getImage());
        setResizable(false);
        setLocationRelativeTo(null);
        
        for(String c: this.services ) {
            cmbService.addItem(c);
        }
        cmbService.setSelectedIndex(-1);
        serviceType = null;
        
        txtLicense.setEchoChar('*');
                
        ((PlainDocument)txtInputDelimiter.getDocument()).setDocumentFilter(new PaiTextFilter("."));
        ((PlainDocument)txtOutputDelimiter.getDocument()).setDocumentFilter(new PaiTextFilter("."));
        ((PlainDocument)txtInputQualifier.getDocument()).setDocumentFilter(new PaiTextFilter("."));
        ((PlainDocument)txtOutputQualifier.getDocument()).setDocumentFilter(new PaiTextFilter("."));
        
        this.setFocusable(true);

        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                readLicense();
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup btnGroupDelimiter;
    private javax.swing.ButtonGroup btnGroupDelimiter2;
    private javax.swing.ButtonGroup btnGroupTxtQualifier;
    private javax.swing.ButtonGroup btnGroupTxtQualifier2;
    private javax.swing.JButton btnInputPreview;
    private javax.swing.JButton btnRun;
    private javax.swing.JButton btnSelectInput;
    private javax.swing.JButton btnSelectOutput;
    private javax.swing.JButton btnSetConfiguration;
    private javax.swing.JButton btnSetInputColumns;
    private javax.swing.JButton btnSetOutputColumns;
    private javax.swing.JCheckBox chkEnableReporting;
    private javax.swing.JCheckBox chkIncludeInputColumns;
    private javax.swing.JCheckBox chkStep1;
    private javax.swing.JCheckBox chkStep2;
    private javax.swing.JCheckBox chkStep3;
    private javax.swing.JCheckBox chkStep4;
    private javax.swing.JCheckBox chkStep5;
    private javax.swing.JCheckBox chkStep6;
    private javax.swing.JComboBox<String> cmbService;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblLicenseStatus;
    private javax.swing.JMenu menuFileGetStarted;
    private javax.swing.JMenuItem menuFileNumThreads;
    private javax.swing.JMenuItem menuFileObtainLicense;
    private javax.swing.JMenuItem menuFilePurchaseCredits;
    private javax.swing.JMenuItem menuHelpAboutCredits;
    private javax.swing.JMenuItem menuHelpAboutMelissa;
    private javax.swing.JMenuItem menuHelpGitlab;
    private javax.swing.JMenuItem menuHelpWiki;
    private javax.swing.JPanel panelConfiguration;
    private javax.swing.JPanel panelInput;
    private javax.swing.JPanel panelOutput;
    private javax.swing.JPanel panelProgress;
    private javax.swing.JPanel panelReporting;
    private javax.swing.JPanel panelStatus;
    private javax.swing.JRadioButton rdInputDelimComma;
    private javax.swing.JRadioButton rdInputDelimOther;
    private javax.swing.JRadioButton rdInputDelimPipe;
    private javax.swing.JRadioButton rdInputDelimTab;
    private javax.swing.JRadioButton rdInputQualiDoubleQ;
    private javax.swing.JRadioButton rdInputQualiNone;
    private javax.swing.JRadioButton rdInputQualiOther;
    private javax.swing.JRadioButton rdInputQualiSingleQ;
    private javax.swing.JRadioButton rdOutputDelimComma;
    private javax.swing.JRadioButton rdOutputDelimOther;
    private javax.swing.JRadioButton rdOutputDelimPipe;
    private javax.swing.JRadioButton rdOutputDelimTab;
    private javax.swing.JRadioButton rdOutputQualiDoubleQ;
    private javax.swing.JRadioButton rdOutputQualiNone;
    private javax.swing.JRadioButton rdOutputQualiOther;
    private javax.swing.JRadioButton rdOutputQualiSingleQ;
    private javax.swing.JTextField txtInputDelimiter;
    private javax.swing.JTextField txtInputFilePath;
    private javax.swing.JTextField txtInputQualifier;
    private javax.swing.JPasswordField txtLicense;
    private javax.swing.JTextField txtOutputDelimiter;
    private javax.swing.JTextField txtOutputFilePath;
    private javax.swing.JTextField txtOutputQualifier;
    private javax.swing.JTextField txtReportClientName;
    private javax.swing.JTextField txtReportJobDescription;
    private javax.swing.JTextField txtReportMelissaContact;
    private javax.swing.JTextArea txtStatus;
    // End of variables declaration//GEN-END:variables
}
