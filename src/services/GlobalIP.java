/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import framework.IWS;
import framework.Record;
import framework.ServiceHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author truep
 */
public class GlobalIP extends IWS
{

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

    public boolean isErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(boolean errorStatus) {
        this.errorStatus = errorStatus;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public HashMap<String, ArrayList<String>> getSettingsList() {
        return settingsList;
    }

    public void setSettingsList(HashMap<String, ArrayList<String>> settingsArrayList) {
        this.settingsList = settingsArrayList;
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
    public boolean errorStatus;
    public String statusMessage;
    public HashMap<String, ArrayList<String>> settingsList;
    private ArrayList<Integer> recordID;
    //endregion

    //Send records to service and return output records
    public void sendToService(Record[] inputRecords)
    {            
        // Add CustomerID to ServiceOptions
        if (!this.serviceOptions.containsKey("CustomerID"))
        {
            this.serviceOptions.put("CustomerID", this.userLicense);
        }
        else
        {
            this.serviceOptions.replace("CustomerID", this.userLicense);
        }

        ServiceHelper sh = new ServiceHelper();
        {
            this.outputRecords = sh.sendRequest(this.endpoint, this.serviceOptions, inputRecords, GlobalIPRequest.class, GlobalIPRecord.class);
        }
    }

    public GlobalIP() 
    {
        this.endpoint = "https://globalip.melissadata.net/v4/web/iplocation/doiplocation";
        this.maxRecsPerRequest = 100;
        this.needsAllRecords = false;

        ServiceHelper sh = new ServiceHelper();
        {
            this.inputColumns = sh.returnProperties(GlobalIPRecord.class);
        }

        this.serviceOptions = new HashMap<String, String>();
        this.settingsList = new HashMap<String, ArrayList<String>>();
        //endregion

        //region Output Columns
        //Set output columns
        this.outputColumns = new String[] {
            "IPAddress",
            "Latitude",
            "Longitude",
            "PostalCode",
            "Region",
            "ISPName",
            "DomainName",
            "City",
            "CountryName",
            "CountryAbbreviation",
            "ConnectionSpeed",
            "ConnectionType",
            "UTC",
            "Continent",
            "ProxyType",
            "ProxyDescription",
            //"Result"
        };
        //endregion
    }

    public static class GlobalIPRequest 
    {
        public String TransmissionReference;
        public String CustomerID;
        public GlobalIPRecord[] Records;
    }

    public static class GlobalIPRecord 
    {
        public String RecordID;
        public String IPAddress;
    }
}
