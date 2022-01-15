/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporting;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.table.DefaultTableModel;
import listware.Listware;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author truep
 */
public class GenerateReport {
    private HeaderData hd            ;
    private String OutputReportName;
    private long Total;

    private ArrayList<Integer> rcFields = new ArrayList<>();
    public TreeMap<String, Integer> openWith = new TreeMap<>();
    public HashMap<String, String> validTemplates = new HashMap<>();
    private HashMap<String, String> validDescriptions = new HashMap<String, String>();
    private ArrayList<Object> requestedReportCharts;
    public boolean requestedRCTable;

    public GenerateReport(HeaderData reportHeaderData)
    {
        this.hd = reportHeaderData;
        this.LoadFilters();
        this.LoadDescriptions();
        this.requestedReportCharts = new ArrayList<Object>();
        this.requestedRCTable = false;
        this.Total = 0;
    }

    public void LoadDescriptions()
    {
        Scanner scanner;
        String cfgRecord;
        String[] Fields;

        try
        {
            scanner = new Scanner(new FileInputStream("Reporting\\ValidDescriptions.cfg"));
        }
        catch (Exception ex)
        {
            Logger.getLogger(GenerateReport.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        String line = scanner.nextLine();  // ignore header
        
        while (scanner.hasNext())
        {
            line = scanner.nextLine();
            Fields = line.split("\t");
            this.validDescriptions.put(Fields[0], Fields[1]);            
        }
        scanner.close();
    }

    public void LoadFilters() 
    {
        Scanner scanner;
        String cfgRecord;
        String[] Fields;

        try
        {
            scanner = new Scanner(new FileInputStream("Reporting\\ValidFilters.cfg"));
            
        }
        catch (Exception ex)
        {
            Logger.getLogger(GenerateReport.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        String line = scanner.nextLine();  // ignore header
        while (scanner.hasNext())
        {
            line = scanner.nextLine();
            Fields = line.split("\t");
            this.validTemplates.put(Fields[0], Fields[1]);
        }
        scanner.close();
    }


    public void ReadFileCreateTreeMapopenWith(String dINPUTFILE, String delimiter, String qualifier)
    {
        Scanner scanner;
        String Record;
        String[] Fields;
        String delimReg = Pattern.quote(delimiter);
        long TTotal = 0; 

        //region ErrorCheckIncomingFile
        try
        {
            scanner = new Scanner(new File(dINPUTFILE));
        }
        catch (Exception ex)
        {
            Logger.getLogger(GenerateReport.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        //endregion ErrorCheckIncomingFile

        //region getResultcodefieldsfromheader
        Record = scanner.nextLine();

        Fields = Record.split(delimReg);
        if (qualifier != null)
        {
            for(int i = 0; i < Fields.length; i++) {
                String f = Fields[i];
                Fields[i] = f.replace(qualifier, "");
            }
        }

        int i = 0;
        for (String field : Fields)
        {
            if (field.toLowerCase().contains("results"))
            {
                rcFields.add(i);
            }
            i++;
        }
        //endregion getResultcodefieldsfromheader

        //region ReadFileCreateResultDictionary
        while (scanner.hasNext())
        {
            Record = scanner.nextLine();
            
            if (qualifier == null)
            {
                if (delimiter.compareTo(",") == 0)
                {
                    String qReg = Pattern.quote("\"");
                    String pattern = String.format("%s(?=(?:[^%s]*%s[^%s]*%s)*(?![^%s]*%s))", delimReg, qReg, qReg, qReg, qReg, qReg, qReg);
                    Fields = Record.split(pattern);
                    for(i = 0; i < Fields.length; i++) {
                        String f = Fields[i].trim();
                        Fields[i] = f.replace("\"", "");
                    }
                }
                else 
                {
                    Fields = Record.split(delimReg);
                }
            }
            else
            {
                String qReg = Pattern.quote(qualifier);
                String pattern = String.format("%s(?=(?:[^%s]*%s[^%s]*%s)*(?![^%s]*%s))", delimReg, qReg, qReg, qReg, qReg, qReg, qReg);
                Fields = Record.split(pattern);
                for(i = 0; i < Fields.length; i++) {
                    String f = Fields[i].trim();
                    Fields[i] = f.replace(qualifier, "");
                }
            }

            for (int x : rcFields)
            {
                String mdresults = "";
                try
                {
                    mdresults = Fields[x];
                }
                catch (Exception ex) 
                {
                    mdresults = "XXXX";
                    Logger.getLogger(GenerateReport.class.getName()).log(Level.SEVERE, null, ex);
                }

                String[] rowcodes = mdresults.split(",");
                for (String rcode : rowcodes)
                {
                    String key;
                    if ((rcode.length() != 4) || ((rcode.length() == 4) && ((!Character.isLetter(rcode.charAt(0))) || 
                            (!Character.isLetter(rcode.charAt(1))) || (!Character.isDigit(rcode.charAt(2))) || (!Character.isDigit(rcode.charAt(3))))))
                    {
                        key = "XXXX";
                    } else {
                        key = rcode;
                    }
                    
                    if (!this.openWith.containsKey(key))
                    {
                        this.openWith.put(key, 1);
                    }
                    else
                    {
                        this.openWith.replace(key, openWith.get(key) + 1);
                    }
                    
                }
            }

            TTotal++;

        }  // end while has records
        this.Total = TTotal;
        //endregion ReadFileCreateResultDictionary

        scanner.close();
    }

    public void GetRequestedReportsList(ArrayList<String> reqRepChrts) 
    {
        for (String s : reqRepChrts)
            requestedReportCharts.add(s);
    }

    public mdRCTable CreateTable(String filter)
    {
        String validTemplateCodes;
        String rcDescription;
        String[] contibutor;

        mdRCTable rcT = new mdRCTable();
        DefaultTableModel dt = new DefaultTableModel();
        dt.addColumn("ResultCode");
        dt.addColumn("Count");
        dt.addColumn("Percent");
        dt.addColumn("Category");
        validTemplateCodes = this.validTemplates.get(filter);
        contibutor = validTemplateCodes.split(",");   // MS = MS01,MS02,MS03,MS07
        for (String c : contibutor)
        {
            rcDescription = this.validDescriptions.get(c);
            if(rcDescription == null) rcDescription = c;

            if (openWith.containsKey(c))
            {
                dt.insertRow(dt.getRowCount(), new Object[]{c, openWith.get(c), String.valueOf((double)Math.round((openWith.get(c) * 100) * 100.0 / (double)this.Total) / 100.0), rcDescription});
            }
            else
                dt.insertRow(dt.getRowCount(), new Object[]{c, 0, 0, rcDescription});
        }

        rcT.tableName = filter;
        rcT.rcTable = dt;
        if (filter.contains("Levels"))
            rcT.chartType = "Pie";
        else if (filter.contains("Quality"))
            rcT.chartType = "Column";
        else if (filter.contains("Uplift"))
            rcT.chartType = "Column";
        else
            rcT.chartType = "Table";
        return rcT;
    }

    public String ConvertChartFileto64Base(String png)
    {
        try {
            byte[] filebytes = Files.readAllBytes(new File(png).toPath());
            String imm = "data:image/png;base64," + Base64.getEncoder().encodeToString(filebytes);
            return imm;
        } catch (Exception ex) {
            Logger.getLogger(GenerateReport.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        }
        return "";
    }

    public String CreateChartTypePie(mdRCTable rcT)
    {        
        DefaultPieDataset dataSet = new DefaultPieDataset();
        double totalCount = 0;
        for (int i = 0; i < rcT.rcTable.getRowCount(); i++)
        {
            if (String.valueOf(rcT.rcTable.getValueAt(i, 1)).compareTo("0") != 0) {
                totalCount += Double.parseDouble(String.valueOf(rcT.rcTable.getValueAt(i, 1)));
            }
        }
        for (int i = 0; i < rcT.rcTable.getRowCount(); i++)
        {
            if (String.valueOf(rcT.rcTable.getValueAt(i, 1)).compareTo("0") != 0) {
                double percent = (double)Math.round(Double.parseDouble(String.valueOf(rcT.rcTable.getValueAt(i, 1))) * 10000 / totalCount) / 100.0;
                dataSet.setValue(String.valueOf(rcT.rcTable.getValueAt(i, 0)) + "(" + percent + "%)", Double.parseDouble(String.valueOf(rcT.rcTable.getValueAt(i, 1))));
            }
        }
        JFreeChart chart = ChartFactory.createPieChart(null, dataSet, false, true, false);
        int width = 300;
        int height = 200;
        String fileName = "Reporting/pieChart.png";
        File chartFile = new File(fileName);
        try {
            ChartUtilities.saveChartAsPNG(chartFile, chart, width, height);
            return fileName;
        } catch (IOException ex) {
            Logger.getLogger(GenerateReport.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }


    public String CreateChartTypeColumn(mdRCTable rcT)
    {
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
        for (int i = 0; i < rcT.rcTable.getRowCount(); i++)
        {
            dataSet.addValue(Double.parseDouble(String.valueOf(rcT.rcTable.getValueAt(i, 1))), "", String.valueOf(rcT.rcTable.getValueAt(i, 0)));
        }
        JFreeChart barChart = ChartFactory.createBarChart(
            null,
            "ResultCode",
            "Count",
            dataSet,
            PlotOrientation.VERTICAL,
            false, true, false);
        
        int width = 300;
        int height = 200;
        String fileName = "Reporting/barChart.png";
        File chartFile = new File(fileName);
        try {
            ChartUtilities.saveChartAsPNG(chartFile, barChart, width, height);
            return fileName;
        } catch (IOException ex) {
            Logger.getLogger(GenerateReport.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
        
    }

    public void GenerateReportFile()
    {
        try {
            String chartToAdd = "";
            FileInputStream fs = null;
            fs = new FileInputStream("Reporting\\ReportTemplate.html");
            String strHtmlTemplate = new Scanner(fs).useDelimiter("\\Z").next();
            strHtmlTemplate = strHtmlTemplate.replace("[Client]", hd.Client);
            strHtmlTemplate = strHtmlTemplate.replace("[IDENT]", hd.IDENT);
            strHtmlTemplate = strHtmlTemplate.replace("[JOB_DESC]", hd.JobDescription);
            strHtmlTemplate = strHtmlTemplate.replace("[Melissa_Contact]", hd.Contacts);
            strHtmlTemplate = strHtmlTemplate.replace("[ProcessedFile]", hd.InputFileName);
            strHtmlTemplate = strHtmlTemplate.replace("[Total]", String.valueOf(Total));
            //region for each requested ReportChart Option create a table, create a chart, start the html doc.
            for (Object itemChecked : requestedReportCharts)
            {
                //CreateTable(itemChecked.ToString());
                mdRCTable rcT = new mdRCTable();
                rcT = CreateTable(String.valueOf(itemChecked));
                if (rcT.chartType == "Pie")
                    chartToAdd = CreateChartTypePie(rcT);
                else// if(rcT.chartType == "Column")
                    chartToAdd = CreateChartTypeColumn(rcT);

                strHtmlTemplate += "<h3>" + rcT.tableName + "</h3>";
                strHtmlTemplate += "<table class=\"table4\" ><tr><td width=\"350\" height=\"200\">\r\n<table class=\"table2\">\r\n";
                strHtmlTemplate += "<tr><th>" + rcT.rcTable.getColumnName(3) + "</th><th>" + rcT.rcTable.getColumnName(0) + "</th><th>" + rcT.rcTable.getColumnName(1) + "</th></tr>\r\n";
                //for each row in table
                for (int i = 0; i < rcT.rcTable.getRowCount(); i++)
                {
                    strHtmlTemplate += "<tr><td>" + rcT.rcTable.getValueAt(i, 3) + "</td><td>"
                            + rcT.rcTable.getValueAt(i, 0) + "</td><td>" + rcT.rcTable.getValueAt(i, 1) + "</td></tr>\r\n";
                }
                strHtmlTemplate += "</table></td><td width=\"350\" height=\"200\">\r\n";

                strHtmlTemplate += "<img src=\"" + ConvertChartFileto64Base(chartToAdd) + "\" align=\"right\" alt=\"" + chartToAdd + "\" height=\"200\" width=\"300\"></td></tr></table><br />\r\n\r\n";
            }   
            if (requestedRCTable == true)
            {
                strHtmlTemplate += "<h3>" + "RESULT CODE COUNTS" + "</h3>";
                strHtmlTemplate += "<table class=\"table3\">\r\n";
                strHtmlTemplate += "<tr><th>" + "RESULT CODE" + "</th><th>" + "DESCRIPTION" + "</th><th>" + "COUNT" + "</th><th>" + "PERCENT" + "</th></tr>\r\n";

                for (String key : this.openWith.keySet())
                {
                    if (this.validDescriptions.containsKey(key))
                    {
                        Integer val = this.openWith.get(key);
                        strHtmlTemplate += "<tr><td>" + key + "</td><td>" + this.validDescriptions.get(key)
                                + "</td><td>" + val + "</td><td>" + ((double)Math.round((val * 10000) / (double)Total)) / 100.0 + "</td></tr>\r\n";
                    }
                }
                strHtmlTemplate += "</table>";
            }   strHtmlTemplate += "</div></body></html>";
            //endregion for each checked list create a table, create a chart, start the html doc.
            FileOutputStream fo = new FileOutputStream("Report.html");
            fo.write(strHtmlTemplate.getBytes());
            Listware._self.log("Generated <Report.html>.");
            File[] filePaths = new File("Reporting").listFiles();
            for(File file : filePaths)
            {
                if(file.getName().contains(".png")) {
                    file.delete();
                }
            }
            fs.close();

        } catch (Exception ex) {
            Logger.getLogger(GenerateReport.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            
        }
    }
} 