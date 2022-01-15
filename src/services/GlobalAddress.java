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
public class GlobalAddress extends IWS {

    public HashMap<String, String> getServiceOptions() {
        return serviceOptions;
    }

    public void setServiceOptions(HashMap<String, String> serviceOptions) {
        this.serviceOptions = serviceOptions;
    }

    public HashMap<String, ArrayList<String>> getSettingsList() {
        return settingsList;
    }

    public void setSettingsList(HashMap<String, ArrayList<String>> settingsList) {
        this.settingsList = settingsList;
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

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public ArrayList<Integer> getRecordID() {
        return recordID;
    }

    //region Global Address Options (Settings)
    public void setRecordID(ArrayList<Integer> recordID) {
        this.recordID = recordID;
    }
    public HashMap<String, String> serviceOptions;          
    public HashMap<String, ArrayList<String>> settingsList;      
    public String[] inputColumns;                              
    public String[] outputColumns;                            
    public int maxRecsPerRequest;                              
    public boolean errorStatus;                                   
    public String statusMessage;                               
    public String userLicense;                                
    public boolean needsAllRecords;                               
    public boolean serviceFinishedProcessing;                     
    public boolean inputRecordsFinished;                          
    public Record[] outputRecords;                             
    public String endpoint;
    private ArrayList<Integer> recordID;
    //endregion

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
            this.outputRecords = sh.sendRequest(this.endpoint, this.serviceOptions, inputRecords, GlobalAddressRequest.class, GlobalAddressRecord.class);
        }
    }

    public GlobalAddress()
    {
        this.endpoint = "https://address.melissadata.net/v3/WEB/GlobalAddress/doGlobalAddress";
        this.maxRecsPerRequest = 100;
        this.needsAllRecords = false;

        ServiceHelper sh = new ServiceHelper();
        {
            this.inputColumns = sh.returnProperties(GlobalAddressRecord.class);
        }

        //region settings
        this.serviceOptions = new HashMap<String, String>();
        this.settingsList = new HashMap<String, ArrayList<String>>();
        settingsList.put("Options_DeliveryLines", new ArrayList<String>(Arrays.asList(new String[]{ "Single", "ON", "OFF" })));
        settingsList.put("Options_LineSeparator", new ArrayList<String>(Arrays.asList(new String[]{ "Single", "SEMICOLON", "PIPE", "CR", "LF", "CRLF", "TAB", "BR" })));
        settingsList.put("Options_OutputScript", new ArrayList<String>(Arrays.asList(new String[] { "Single", "NOCHANGE", "LATIN", "NATIVE" })));
        settingsList.put("Options_OutputGeo", new ArrayList<String>(Arrays.asList(new String[] { "Single", "ON", "OFF" })));
        settingsList.put("Options_CountryOfOrigin", new ArrayList<String>(Arrays.asList(new String[] { "Manual" })));
        //endregion

        //region output
        this.outputColumns = new String[]
        {
            "FormattedAddress", 
            "Organization",
            "AddressLine1", 
            "AddressLine2", 
            "AddressLine3", 
            "AddressLine4",
            "AddressLine5", 
            "AddressLine6", 
            "AddressLine7", 
            "AddressLine8",
            "SubPremises", 
            "DoubleDependentLocality",
            "DependentLocality", 
            "Locality",
            "SubAdministrativeArea", 
            "AdministrativeArea", 
            "PostalCode", 
            "AddressType",
            "AddressKey", 
            "SubNationalArea", 
            "CountryName", 
            "CountryISO3166_1_Alpha2",
            "CountryISO3166_1_Alpha3", 
            "CountryISO3166_1_Numeric", 
            "CountrySubdivisionCode", 
            "Thoroughfare", 
            "ThoroughfarePreDirection", 
            "ThoroughfareLeadingType",
            "ThoroughfareName",
            "ThoroughfareTrailingType", 
            "ThoroughfarePostDirection", 
            "DependentThoroughfare", 
            "DependentThoroughfarePreDirection", 
            "DependentThoroughfareLeadingType",
            "DependentThoroughfareName", 
            "DependentThoroughfareTrailingType", 
            "DependentThoroughfarePostDirection", 
            "Building",
            "PremisesType",
            "PremisesNumber", 
            "SubPremisesType", 
            "SubPremisesNumber", 
            "PostBox",
            "Latitude", 
            "Longitude"
        };
        //endregion
    }

    public static class GlobalAddressRequest 
    {
        public String TransmissionReference;
        public String CustomerID;
        public String Options;
        public String Format;
        public GlobalAddressRecord[] Records;

    }

    public static class GlobalAddressRecord 
    {
        public String Organization;
        public String AddressLine1;
        public String AddressLine2;
        public String AddressLine3;
        public String AddressLine4;
        public String AddressLine5;
        public String AddressLine6;
        public String AddressLine7;
        public String AddressLine8;
        public String DoubleDependentLocality;
        public String DependentLocality;
        public String Locality;
        public String SubAdministrativeArea;
        public String AdministrativeArea;
        public String PostalCode;
        public String SubNationalArea;
        public String Country;

    }
}
