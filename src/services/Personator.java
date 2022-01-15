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
public class Personator extends IWS
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
            this.outputRecords = sh.sendRequest(this.endpoint, this.serviceOptions, inputRecords, PersonatorRequest.class, PersonatorRecord.class);
        }
    }

    public Personator() 
    {
        this.endpoint = "https://personator.melissadata.net/v3/WEB/ContactVerify/doContactVerify";
        this.maxRecsPerRequest = 100;
        this.needsAllRecords = false;

        ServiceHelper sh = new ServiceHelper();
        {
            this.inputColumns = sh.returnProperties(PersonatorRecord.class);
        }

        //region Service Settings
        this.serviceOptions = new HashMap<String, String>();
        this.settingsList = new HashMap<String, ArrayList<String>>();
        settingsList.put("Actions", new ArrayList<String>(Arrays.asList(new String[]{ "Multiple", "Check", "Verify", "Append", "Move" })));
        settingsList.put("Columns", new ArrayList<String>(Arrays.asList(new String[] {"Multiple", "GrpAll", "GrpNameDetails", "GrpParsedAddress", 
            "GrpAddressDetails", "GrpCensus", "GrpParsedEmail", "GrpParsedPhone", "GrpGeoCode", "GrpDemographicBasic", "GrpCensus2",
            "Plus4","PrivateMailBox","Suite","MoveDate","Occupation","OwnRent"})));
        // Options Section
        settingsList.put("Options_CentricHint" , new ArrayList<String>(Arrays.asList(new String[] {"Single","Auto","Address","Phone","Email","Name","SSN"})));
        settingsList.put("Options_Append", new ArrayList<String>(Arrays.asList(new String[] { "Single", "Blank", "CheckError", "Always"})));
        settingsList.put("Options_Diacritics", new ArrayList<String>(Arrays.asList(new String[] { "Single", "Auto", "On", "Off" })));
        settingsList.put("Options_SSNCascade", new ArrayList<String>(Arrays.asList(new String[] { "Single", "On", "Off" })));
        settingsList.put("Options_UsePreferredCity", new ArrayList<String>(Arrays.asList(new String[] { "Single", "On", "Off" })));
        settingsList.put("Options_dvancedAddressCorrection", new ArrayList<String>(Arrays.asList(new String[] { "Single", "On", "Off" })));
        //endregion

        //region Output Columns
        //Set output columns
        this.outputColumns = new String[]
        {
            /*
            "GrpAddressDetails",
            "GrpCensus",
            "GrpCensus2",
            "GrpGeocode",
            "GrpDemographicBasic",
            "GrpIPAddress",
            "GrpNameDetails",
            "GrpParsedAddress",
            "GrpParsedEmail",
            "GrpParsedPhone",
            "MoveDate",
            "Occupation",
            "OwnRent",
            "PhoneCountryCode",
            "PhoneCountryName",
            "Plus4",
            "PrivateMailBox",
            "Suite"*/
            "AddressDeliveryInstallation",
            "AddressExtras",
            "AddressHouseNumber",
            "AddressKey",
            "AddressLine1",
            "AddressLine2",
            "AddressLockBox",
            "AddressPostDirection",
            "AddressPreDirection",
            "AddressPrivateMailboxName",
            "AddressPrivateMailboxRange",
            "AddressRouteService",
            "AddressStreetName",
            "AddressStreetSuffix",
            "AddressSuiteName",
            "AddressSuiteNumber",
            "AddressTypeCode",
            "AreaCode",
            "CBSACode",
            "CBSADivisionCode",
            "CBSADivisionLevel",
            "CBSADivisionTitle",
            "CBSALevel",
            "CBSATitle",
            "CarrierRoute",
            "CensusBlock",
            "CensusTract",
            "ChildrenAgeRange",
            "City",
            "CityAbbreviation",
            "CompanyName",
            "CongressionalDistrict",
            "CountryCode",
            "CountryName",
            "CountyFIPS",
            "CountyName",
            "CountySubdivisionCode",
            "CountySubdivisionName",
            "CountryName",
            "CreditCardUser",
            "DateOfBirth",
            "DateOfDeath",
            "DeliveryIndicator",
            "DeliveryPointCheckDigit",
            "DeliveryPointCode",
            "DemographicsGender",
            "DemographicsResults",
            "DistanceAddressToIP",
            "DomainName",
            "Education",
            "ElementarySchoolDistrictCode",
            "ElementarySchoolDistrictName",
            "EmailAddress",
            "EstimatedHomeValue",
            "EthnicCode",
            "EthnicGroup",
            "Gender",
            "Gender2",
            "HouseholdIncome ",
            "HouseholdSize",
            "IPAddress",
            "IPCity",
            "IPConnectionSpeed",
            "IPConnectionType",
            "IPContinent",
            "IPCountryAbbreviation",
            "IPCountryName",
            "IPDomainName",
            "IPISPName",
            "IPLatitude",
            "IPLongitude",
            "IPPostalCode",
            "IPProxyDescription",
            "IPProxyType",
            "IPRegion",
            "IPUTC",
            "Latitude",
            "LengthOfResidence ",
            "Longitude",
            "MailboxName",
            "MaritalStatus",
            "MelissaAddressKey",
            "MelissaAddressKeyBase",
            "MoveDate",
            "NameFirst",
            "NameFirst2",
            "NameFull",
            "NameLast",
            "NameLast2",
            "NameMiddle",
            "NameMiddle2",
            "NamePrefix",
            "NamePrefix2",
            "NameSuffix",
            "NameSuffix2",
            "NewAreaCode",
            "Occupation",
            "OwnRent",
            "PhoneCountryCode",
            "PhoneCountryName",
            "PhoneExtension",
            "PhoneNumber",
            "PhonePrefix",
            "PhoneSuffix",
            "PlaceCode",
            "PlaceName",
            "Plus4",
            "PoliticalParty",
            "PostalCode",
            "PresenceOfChildren",
            "PresenceOfSenior",
            "PrivateMailbox",
            "RecordExtras",
            "Salutation",
            "SecondarySchoolDistrictCode",
            "SecondarySchoolDistrictName",
            "State",
            "StateDistrictLower",
            "StateDistrictUpper",
            "StateName",
            "Suite",
            "TopLevelDomain",
            "TypesOfVehicles",
            "UTC",
            "UnifiedSchoolDistrictCode",
            "UnifiedSchoolDistrictName",
            "UrbanizationName "
        };
        //endregion
    }

    public static class PersonatorRequest
    {
        public String TransmissionReference;
        public String Actions;
        public String Columns;
        public String CustomerID;
        public String Options;
        public PersonatorRecord[] Records;
    }

    public static class PersonatorRecord
    {
        public String AddressLine1;
        public String AddressLine2;
        public String BirthDay;
        public String BirthMonth;
        public String BirthYear;
        public String City;
        public String CompanyName;
        public String Country;
        public String EmailAddress;
        public String FirstName;
        public String FreeForm;
        public String FullName;
        public String IPAddress;
        public String LastLine;
        public String LastName;
        public String MelissaAddressKey;
        public String PhoneNumber;
        public String PostalCode;
        public String RecordID;
        public String SocialSecurity;
        public String State;
    }

}  
