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
public class GlobalName extends IWS
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
            this.outputRecords = sh.sendRequest(this.endpoint, this.serviceOptions, inputRecords, GlobalNameRequest.class, GlobalNameRecord.class);
        }
    }

    public GlobalName() 
    {
        this.endpoint = "https://globalname.melissadata.net/V3/WEB/GlobalName/doGlobalName";
        this.maxRecsPerRequest = 100;
        this.needsAllRecords = false;

        ServiceHelper sh = new ServiceHelper();
        {
            this.inputColumns = sh.returnProperties(GlobalNameRecord.class);
        }

        //region Service Settings
        this.serviceOptions = new HashMap<String, String>();
        this.settingsList = new HashMap<String, ArrayList<String>>();
        settingsList.put("Options_CorrectFirstName", new ArrayList<String>(Arrays.asList(new String[]{ "Single", "OFF", "ON" })));
        settingsList.put("Options_NameHint", new ArrayList<String>(Arrays.asList(new String[] { "Single", "DefinitelyFull", "VeryLikelyFull", "ProbablyFull", "Varying", "ProbablyInverse", "VeryLikelyInverse", "DefinitelyInverse", "MixedFirstName", "MixedLastName" })));
        settingsList.put("Options_GenderPopulation", new ArrayList<String>(Arrays.asList(new String[] { "Single", "Male", "Mixed", "Female" })));
        settingsList.put("Options_GenderAggression", new ArrayList<String>(Arrays.asList(new String[] { "Single", "Aggressive", "Neutral", "Conservative" })));
        settingsList.put("Options_MiddleNameLogic", new ArrayList<String>(Arrays.asList(new String[] { "Single", "ParseLogic", "HyphenatedLast", "MiddleName" })));
        //endregion

        //region Output Columns
        //Set output columns
        this.outputColumns = new String[] {
            "Company",
            "NamePrefix",
            "NameFirst",
            "NameMiddle",
            "NameLast",
            "NameSuffix",
            "Gender",
            "NamePrefix2",
            "NameFirst2",
            "NameMiddle2",
            "NameLast2",
            "NameSuffix2",
            "Gender2"
        };
        //endregion
    }

    public static class GlobalNameRequest 
    {
        public String TransmissionReference;
        public String CustomerID;
        public String Options;
        public String Format;
        public GlobalNameRecord[] Records;
    }

    public static class GlobalNameRecord 
    {
        public String RecordID;
        public String Company;
        public String FullName;
    }
}