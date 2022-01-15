/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reporting;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author truep
 */
public class mdRCTable {
    public String tableName;
    public DefaultTableModel rcTable;
    public String chartType;

    public mdRCTable() { }
    public mdRCTable(String tn, DefaultTableModel dt, String ct)
    {
        tableName = tn;
        rcTable = dt;
        chartType = ct;
    }
}
