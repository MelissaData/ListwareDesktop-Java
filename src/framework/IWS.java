/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author truep
 */
public abstract class IWS {
    /*
    * Those who are creating a web service class must do so in the following way
    * All of the following methods must be implemented, with the class for the service extending this interface
    * 
    *  settingsList - A dictionary of all top level elements and values. Declare and set this in the constructor. For example,
    *          Smartmover would use columns, JobID, NumberOfMonthsRequested,ProcessingType,ListOwnerFreqProcessing
    *          There are three types of options integrated into settingsList: Manual, Single, and Multiple
    *          Manual is a manual entry option. For example, NumberOfMonthsRequested requires the user to put in a value between 6 and 48.
    *              Therefore, the entry for this option would look like the following:
    *              Key: NumberOfMonthsRequested
    *              Value: Manual,6-48
    *              With manual entry options, the second value of the key is what the display box will show to the user. This value will not be taken into the WS
    *          Single is for options where users can only select a single option. For example, ProcessingType requires a user to pick either Residence, Business, Individual, etc.
    *              Therefore, the entry for the option would look like the following:
    *              Key: ProcessingType
    *              Value: Single, Standard, Individual, IndividualAndBusiness, Business, Residential
    *          Multiple is for options where users can select multiple options simultaneously. For example, Columns, the user can pick grpStandardized,grpOriginal, or even individual columns like so:
    *              Key: Columns
    *              Value: Multiple, DPVFootNotes, MoveReturnCode, Plus4, PrivateMailBox, Suite, GrpParsed, GrpName, GrpOriginal, GrpStandardized
    *              
    *  serviceOptions - This is a dictionary of all of the options, as well as the values corresponding to the options that the user has chosen. This will be passed back 
    *          into the class after the user has selected all of their options. There is a possibility where this can be blank if a user has not selected anything at all.
    *          Declare this in the constructor but DO NOT set any values.
    *          
    *  inputColumns - A list of all of the input columns for the service. Declare and set this in the constructor.
    *
    *  outputColumns - A list of all of the possible output columns for the service. Declare and set this in the constructor.
    *
    *  maxRecsPerRequest - This controls how many records will be passed to the service at a time by the GUI. Declare and set this in the constructor.
    *
    *  sendToService - This is the method that the GUI passes the records into. This is the "black box". This method is where you will do your processing however you wish.
    *
    *  outputRecords - This is where you will place the output records when you are finished processing.
    *  
    *  errorStatus - A boolean that will be false by default. Set to true when there is an error and the GUI will stop processing.
    *  
    *  statusMessage - A statusMessage that will be passed to the GUI. When there is an issue, set this to some message for the user and set errorStatus to true. If there is no error
    *          but you want to send a message to the user regardless (like a warning or processing %), set this with errorStatus set to false.
    *          
    *  userLicense - The user's license will be set here by the GUI for the class.
    * 
    *  needsAllRecords - Set this to true if the class needs all records before processing (such as the MatchUp Webservice)
    *  
    *  serviceFinishedProcessing - If needsAllRecords is set to true, set this boolean to true when you are done processing. Otherwise, you can leave it alone.
    *  
    *  inputRecordsFinished - If needs all records is set to true, then the GUI will set this boolean to true when it has finished passing in all of the records.
    */

    String endpoint;

    //Dictionary of all options and the values the user has set for them
    HashMap<String, String> serviceOptions;

    //Dictionary of all options and their possible values
    HashMap<String, ArrayList<String>> settingsList;

    //All possible input columns DECLARE AND SET IN CONSTRUCTOR
    String[] inputColumns;

    //All possible output columns DECLARE AND SET IN CONSTRUCTOR
    String[] outputColumns;

    //Maximum records per request, typically 100 or 1 DECLARE AND SET IN CONSTRUCTOR
    int maxRecsPerRequest;

    //Send input records to service and set the output records
    public void sendToService(Record[] inputRecords) {};

    //Set this to the outputRecords that are to be returned to the GUI
    Record[] outputRecords;

    //This is set to true if there is an error
    boolean errorStatus;

    //Any possible status messages to pass to the GUI, including error status messages
    String statusMessage;

    //This is set so the service can access the license more easily
    String userLicense;

    //This is to differentiate the types of services
    //For services such as Personator, SmartMover that can receive x request records then return y response records select false
    //For services such as MUWS where you need all records before returning select true
    boolean needsAllRecords;

    //This boolean is set by the WS class
    //Lets the GUI know when the class is ready to output records
    boolean serviceFinishedProcessing;

    //This boolean is set by the GUI
    //Lets the WS class know when the GUI is finished sending in records
    boolean inputRecordsFinished;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

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
    
    
}
