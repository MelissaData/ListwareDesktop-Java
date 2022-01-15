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
public class GlobalPhone extends IWS
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
            this.outputRecords = sh.sendRequest(this.endpoint, this.serviceOptions, inputRecords, GlobalPhoneRequest.class, GlobalPhoneRecord.class);
        }
    }

    public GlobalPhone() 
    {
        this.endpoint = "http://globalphone.melissadata.net/v4/WEB/GlobalPhone/doGlobalPhone";
        this.maxRecsPerRequest = 100;
        this.needsAllRecords = false;

        ServiceHelper sh = new ServiceHelper();
        {
            this.inputColumns = sh.returnProperties(GlobalPhoneRecord.class);
        }

        //region Service Settings
        this.serviceOptions = new HashMap<String, String>();
        this.settingsList = new HashMap<String, ArrayList<String>>();
        settingsList.put("Options_VerifyPhone" , new ArrayList<String>(Arrays.asList(new String[] {"Single","Express","Premium"})));
        settingsList.put("Options_CallerID" , new ArrayList<String>(Arrays.asList(new String[] {"Single","False","True"})));
        settingsList.put("Options_TimeToWait" , new ArrayList<String>(Arrays.asList(new String[] {"Manual"})));
        settingsList.put("Options_DefaultCallingCode", new ArrayList<String>(Arrays.asList(new String[] { "Manual" })));
        //endregion

        //region Output Columns
        //Set output columns
        this.outputColumns = new String[]
        {
            "PhoneNumber",
            "AdministrativeArea",
            "CountryAbbreviation",
            "CountryName",
            "Carrier",
            "CallerID",
            "DST",
            "InternationalPhoneNumber",
            "Language",
            "Latitude",
            "Longitude",
            "InternationalPrefix",
            "CountryDialingCode",
            "NationPrefix",
            "NationalDestinationCode",
            "SubscriberNumber",
            "UTC",
            "PostalCode",
            "Suggestions",
            "TimeZoneCode",
            "TimeZoneName"
        };
        //endregion
    }

    public static class GlobalPhoneRequest 
    {
        public String TransmissionReference;
        public String CustomerID;
        public String Options;
        public GlobalPhoneRecord[] Records;
    }

    public static class GlobalPhoneRecord 
    {
        public String RecordID;
        public String PhoneNumber;
        public String Country;
        public String CountryOfOrigin;
    }
}
