/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import framework.IWS;
import framework.Record;
import framework.ServiceHelper;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author truep
 */
public class BusinessCoder extends IWS {

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String[] getInputColumns() {
        return inputColumns;
    }

    public void setInputColumns(String[] inputColumns) {
        this.inputColumns = inputColumns;
    }

    public String[] getOutputColumns() {
        return outputColumns;
    }

    public void setOutputColumns(String[] outputColumns) {
        this.outputColumns = outputColumns;
    }

    public int getMaxRecsPerRequest() {
        return maxRecsPerRequest;
    }

    public void setMaxRecsPerRequest(int maxRecsPerRequest) {
        this.maxRecsPerRequest = maxRecsPerRequest;
    }

    public HashMap<String, String> getServiceOptions() {
        return serviceOptions;
    }

    public void setServiceOptions(HashMap<String, String> serviceOptions) {
        this.serviceOptions = serviceOptions;
    }

    public String getUserLicense() {
        return userLicense;
    }

    public void setUserLicense(String userLicense) {
        this.userLicense = userLicense;
    }

    public boolean isNeedsAllRecords() {
        return needsAllRecords;
    }

    public void setNeedsAllRecords(boolean needsAllRecords) {
        this.needsAllRecords = needsAllRecords;
    }

    public boolean isServiceFinishedProcessing() {
        return serviceFinishedProcessing;
    }

    public void setServiceFinishedProcessing(boolean serviceFinishedProcessing) {
        this.serviceFinishedProcessing = serviceFinishedProcessing;
    }

    public boolean isInputRecordsFinished() {
        return inputRecordsFinished;
    }

    public void setInputRecordsFinished(boolean inputRecordsFinished) {
        this.inputRecordsFinished = inputRecordsFinished;
    }

    public Record[] getOutputRecords() {
        return outputRecords;
    }

    public void setOutputRecords(Record[] outputRecords) {
        this.outputRecords = outputRecords;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public boolean isErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(boolean errorStatus) {
        this.errorStatus = errorStatus;
    }

    public HashMap<String, ArrayList<String>> getSettingsList() {
        return settingsList;
    }

    public void setSettingsList(HashMap<String, ArrayList<String>> settingsList) {
        this.settingsList = settingsList;
    }

    public ArrayList<Integer> getRecordID() {
        return recordID;
    }

    //region settings
    public void setRecordID(ArrayList<Integer> recordID) {
        this.recordID = recordID;
    }
    public String endpoint;
    public String[] inputColumns;
    public String[] outputColumns;
    public int maxRecsPerRequest;
    public HashMap<String, String> serviceOptions;
    public String userLicense;
    public boolean needsAllRecords;
    public boolean serviceFinishedProcessing;
    public boolean inputRecordsFinished;
    public Record[] outputRecords;
    public String statusMessage;
    public boolean errorStatus;
    public HashMap<String, ArrayList<String>> settingsList;
    private ArrayList<Integer> recordID;
    
    //endregion
    
    public static class BusinessCoderRequest
    {
        public String T;
        public String Cols;
        public String ID;
        public String Opt;
        public BusinessCoderRecord[] Records;
    }

    public static class BusinessCoderRecord
    {
        public String Rec;
        public String Comp;
        public String Phone;
        public String A1;
        public String A2;
        public String City;
        public String State;
        public String Postal;
        public String Ctry;
        public String MAK;
        public String MEK;
        public String Stock;
        public String Web;
    }

    //Send records to service and return output records
    public void sendToService(Record[] inputRecords)
    {
        //Add customer ID if it's not in there already
        if (!this.serviceOptions.containsKey("ID"))
        {
            this.serviceOptions.put("ID", this.userLicense);
        }
        else
        {
            this.serviceOptions.replace("ID", this.userLicense);
        }

        ServiceHelper sh = new ServiceHelper();
        this.outputRecords = sh.sendRequest(this.endpoint, this.serviceOptions, inputRecords, BusinessCoderRequest.class, BusinessCoderRecord.class);
    }

    public BusinessCoder()
    {
        this.endpoint = "http://businesscoder.melissadata.net/WEB/BusinessCoder/doBusinessCoderUS";
        this.maxRecsPerRequest = 100;
        this.needsAllRecords = false;

        ServiceHelper sh = new ServiceHelper();
        this.inputColumns = sh.returnProperties(BusinessCoderRecord.class);
        
        //region Service Settings
        this.serviceOptions = new HashMap<String, String>();
        this.settingsList = new HashMap<String, ArrayList<String>>();
        String[] cols = {"Multiple","GrpAddressDetails","GrpBusinessCodes","GrpBusinessDescription","GrpGeoCode","GrpCensus","LocationType","Phone","EmployeesEstimate","SalesEstimate","StockTicker","WebAddress","Contacts"};
        settingsList.put("Cols", new ArrayList<>(Arrays.asList(cols)));
        String[] opt_rdb = { "Single", "Yes", "No" };
        settingsList.put("Opt_ReturnDominantBusiness", new ArrayList<>(Arrays.asList(opt_rdb)));
        String[] opt_ch = { "Single", "None", "Name", "Address", "Phone" };
        settingsList.put("Opt_CentricHint", new ArrayList<String>(Arrays.asList(opt_ch)));
        String[] opt_mc = { "Manual", "5" };
        settingsList.put("Opt_MaxContacts", new ArrayList<String>(Arrays.asList(opt_mc))) ;
        String[] opt_sc = { "Strict", "Loose" };
        settingsList.put("Opt_SICNAICSConfidence", new ArrayList<String>(Arrays.asList(opt_sc)) );
        //endregion

        //region Output Columns
        //Set output columns
        this.outputColumns = new String[]
        {
            "CompanyName",
            "AddressLine1",
            "Suite",
            "City",
            "State",
            "PostalCode",
            "MelissaEnterpriseKey",
            "LocationType",
            "Phone",
            "EmployeesEstimate",
            "SalesEstimate",
            "StockTicker",
            "WebAddress",
            "CountryCode",
            "CountryName",
            "DeliveryIndicator",
            "MelissaAddressKey",
            "MelissaAddressKeyBase",
            "EIN",
            "SICCode1",
            "SICCode2",
            "SICCode3",
            "NAICSCode1",
            "NAICSCode2",
            "NAICSCode3",
            "SICDescription1",
            "SICDescription2",
            "SICDescription3",
            "NAICSDescription1",
            "NAICSDescription2",
            "NAICSDescription3",
            "Latitude",
            "Longitude",
            "CountyName",
            "CountyFIPS",
            "CensusTract",
            "CenusBlock",
            "PlaceCode",
            "PlaceName",
            "TotalContacts",
            "TotalSuggestions",
            "Results"
        };
        //endregion
    }

    

}
