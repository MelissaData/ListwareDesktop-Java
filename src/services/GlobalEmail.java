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
import java.util.List;

/**
 *
 * @author truep
 */
public class GlobalEmail extends IWS
{
    //region settings
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
    //endregion settings

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

    public List<Integer> getRecordID() {
        return recordID;
    }

    public void setRecordID(ArrayList<Integer> recordID) {
        this.recordID = recordID;
    }

    //Send records to service and return output records
    public void sendToService(Record[] inputRecords)
    {
        //Add customer ID if it's not in there already
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
            this.outputRecords = sh.sendRequest(this.endpoint, this.serviceOptions, inputRecords, GlobalEmailRequest.class, GlobalEmailRecord.class);
        }
    }

    public GlobalEmail()
    {
        this.endpoint = "http://globalemail.melissadata.net/v3/WEB/GlobalEmail/doGlobalEmail";
        this.maxRecsPerRequest = 10;
        this.needsAllRecords = false;

        ServiceHelper sh = new ServiceHelper();
        {
            this.inputColumns = sh.returnProperties(GlobalEmailRecord.class);
        }

        //region Service Settings
        this.serviceOptions = new HashMap<String, String>();
        this.settingsList = new HashMap<String, ArrayList<String>>();
        settingsList.put("Options_VerifyMailBox", new ArrayList<String>(Arrays.asList(new String[]{"Single", "Express", "Premium" })));
        settingsList.put("Options_DomainCorrection", new ArrayList<String>(Arrays.asList(new String[]{"Single", "On", "Off" })));
        settingsList.put("Options_TimeToWait", new ArrayList<String>(Arrays.asList(new String[]{"Manual", "5-45" })));
        //endregion Service Settings

        //region Output Columns

        //Set output columns
        this.outputColumns = new String[]
        {
            "EmailAddress",
            "MailboxName",
            "DomainName",
            "TopLevelDomain",
            "TopLevelDomainName",
            "DateChecked"
        };

        //endregion Output Columns
    }

    public static class GlobalEmailRequest 
    {
        public String TransmissionReference;
        public String CustomerID;
        public String Options;
        public String Format;
        public GlobalEmailRecord[] Records;
    }

    public static class GlobalEmailRecord 
    {
        public String RecordID;
        public String Email;
    }
}
