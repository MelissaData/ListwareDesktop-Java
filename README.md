# Listware Desktop

## [Link for Pre-Compiled Executable](http://update2.melissadata.com/sE5h4nBe/LWDTJava.zip). For more information, see below.

## Getting Started
To get started with Listware Desktop, you will need a Melissa license with credits, as well as the compiled application.

*  Sign in or create an account to get your license key by clicking [here](https://www.melissadata.com/user/signin.aspx).
*  Activate your 1,000 free credits or purchase more credits [here](https://www.melissa.com/credits/).
*  **The application can either be compiled using the code in this Git repository, or a pre-compiled version can be downloaded [here](http://update2.melissadata.com/sE5h4nBe/LWDTJava.zip).**
*  NET version is available [here](https://git.melissadata.com/Listware/ListwareDesktopNET) and [here](http://update2.melissadata.com/4L15tWar3/ListwareDesktop.zip).

Information is also available at our [wiki](http://wiki.melissadata.com/index.php?title=Listware_Desktop).

Disclaimer: Although Listware Desktop is a ready to use application, it is not meant to be used as an enterprise tool. As an open sourced tool we encourage you to change and fix any issues that you experience with the software. We will note any issues you submit to Melissa Global Intelligence, however they will not always be put at a high priority level.

## Description

This is **Listware Desktop** by Melissa Global Intelligence.

This is a batch application that enables users to quickly utilize our Web Services without having to do much coding.

A pre-compiled version of the application will shortly be available on our [wiki](http://wiki.melissadata.com/index.php?title=Listware_Desktop).
  
Currently, seven services are incorporated into the application.  
*  BusinessCoder
*  Global Address
*  Global Email
*  Global IP
*  Global Name
*  Global Phone
*  Personator
 
Users and developers are welcome to fork the project and develop their own processes.

  -  -  -  -
## How It Works
The application is built through around 13 different classes. A quick runthrough of each:

* Framework
 * AutoDetectInputs – A static file that is used to auto detect variations of inputs (for example, it will auto field “address” to “addressline1”).
 * Input – A class built to read the input file and pass the information in forms of the record class.
 * IWS – A webservice interface. Every integrated webservice must have the methods and fields mentioned in this class.
 * Output – A class built to send records to an output file.
 * Record – A class that holds the field names and values for a single record.
* Images
 * This file contains the images used for the logos
* Reporting
 * GenerateReport - This class contains the logic to generate an HTML report and charts
 * ValidDescriptions.cfg - A configuration file containing information regarding result codes
 * ValidFilters.cfg - A configuration file that controls which charts go into which section
* Services (All WS classes will go in here)
 * BusinessCoder
 * Global Address
 * Global Email
 * Global IP
 * Global Name
 * Global Phone
 * Personator
* Windows (These are just different windows that appear when different parts of the UI is clicked)
 * AboutForm - A simple window that shows Melissa contact information
 * InputPreviewForm – Input preview pane to let the user see if they selected the correct delimiters/text qualifiers
 * OverwriteWarningForm - A window that prompts the user if they want to overwrite the current output file, if one exists
 * SetConfigurationForm – Window that lets users select the WS options
 * SetInputsForm – Window that lets users field in inputs (the GUI will attempt to automatically field them if this is not done)
 * SetOutputsForm – Window that lets users select what output fields they want (all will be returned if left alone)
* MainForm -  This is the main GUI class that is essentially the whole “brain” for the operation. It sets values, opens windows, sends objects at certain times to other classes

  -  -  -  -
  
## Application Flow
When running the application, the events will occur in the following order:

* Enter customer ID.
* Select input file and any delimiters/text qualifiers. Preview input file if needed.
* Select service from the service dropdown that is automatically generated using the titles of the classes within the “Services” folder. It is important to name your class what you want to be shown in this dropdown.
* Select or deselect pass through (selected by default).
* Field inputs by  pressing “Set Input Columns”. If this is not done then the input columns will attempt to auto-field based on AutoDetectInputs.cs. If no fields could be detected, an error message is thrown.
* Set service options through “Set Configuration”. If this is not done, then no options will be sent to the service. Classes like SmartMover will work, however, MatchUp, which requires the matchcode to be set, will throw an error.
* Select the output fields that you desire to be written through “Set Output Columns”. All will be returned by default.
* Select output file, delimiter, and text qualifier.
* When enough steps are done, then the Run button and Reporting Pane will enable. The GUI has been configured so that some steps are required (the ones suffixed with an asterisk within the Progress pane), but others are not.
* Enable/disable reporting. This will create an html report containing a summary of the results from the run with the information in this pane at the head of the page. Note that you are required to have the "Reporting" folder in the same directory as the executable, and in that folder must be "ReportTemplate.html", "ValidDescriptions.cfg", and "ValidFilters.cfg".
* When run is selected, records are then sent to the web service class to be processed. The GUI does not care how this is done. The only thing it cares about is sending and receiving records, therefore you may process the records in whatever way you wish.
* When the records are processed, they are returned by the web service class to the GUI, and then written to the output file. 

  -  -  -  -
  
## Integrating a Service
All services must extend IWS.cs and integrate the following properties:
* `Dictionary<string, List<string>> settingsList { get; set; }`
 * A dictionary of all top level elements and values. Declare and set this in the constructor. For example, Smartmover would use columns, JobID, NumberOfMonthsRequested,ProcessingType,ListOwnerFreqProcessing. There are three types of options integrated into settingsList: **Manual**, **Single**, and **Multiple**
        * Manual is a manual entry option. For example, NumberOfMonthsRequested requires the user to put in a value between 6 and 48.
         * Therefore, the entry for this option would look like the following:
         * Key: NumberOfMonthsRequested
         * Value: Manual,6-48
         * With manual entry options, the second value of the key is what the display box will show to the user. This value will not be taken into the WS
        * Single is for options where users can only select a single option. For example, ProcessingType requires a user to pick either Residence, Business, Individual, etc.
         * Therefore, the entry for the option would look like the following:
         * Key: ProcessingType
         * Value: Single, Standard, Individual, IndividualAndBusiness, Business, Residential
        * Multiple is for options where users can select multiple options simultaneously. For example, Columns, the user can pick grpStandardized, grpOriginal, or even individual columns like so:
         * Key: Columns
         * Value: Multiple, DPVFootNotes, MoveReturnCode, Plus4, PrivateMailBox, Suite, GrpParsed, GrpName, GrpOriginal, GrpStandardized

* `Dictionary<string, string> serviceOptions { get; set; }`
 * This is a dictionary of all of the options, as well as the values corresponding to the options that the user has chosen. This will be passed back into the class after the user has selected all of their options. There is a possibility where this can be blank if a user has not selected anything at all. Declare this in the constructor but DO NOT set any values. 

* `string[] inputColumns { get; set; }`
 * A list of all of the input columns for the service. Declare and set this in the constructor.

* `string[] outputColumns { get; set; }`
 * A list of all of the possible output columns for the service. Declare and set this in the constructor.

* `int maxRecsPerRequest { get; set; }`
 * This controls how many records will be passed to the service at a time by the GUI. Declare and set this in the constructor.

* `void sendToService(Record[] inputRecords)`
 * This is the method that the GUI passes the records into. This is the "black box". This method is where you will do your processing however you wish.

* `Record[] outputRecords { get; set; }`
 * This is where you will place the output records when you are finished processing.

* `bool errorStatus { get; set; }`
 * A boolean that will be false by default. Set to true when there is an error and the GUI will stop processing.

* `string statusMessage { get; set; }`
 * A statusMessage that will be passed to the GUI. When there is an issue, set this to some message for the user and set errorStatus to true. If there is no error but you want to send a message to the user regardless (like a warning or processing %), set this with errorStatus set to false.

* `string userLicense { get; set; }`
 * The user's license will be set here by the GUI for the class.

* `bool needsAllRecords { get; set; }`
 * Set this to true if the class needs all records before processing (such as the MatchUp Webservice)

* `bool serviceFinishedProcessing { get; set; }`
 * If needsAllRecords is set to true, set this bool to true when you are done processing. Otherwise, you can leave it alone.

* `bool inputRecordsFinished { get; set; }`
 * If needs all records is set to true, then the GUI will set this bool to true when it has finished passing in all of the records.

 - - - -
 
## To Do List
* Improve column selection interface
* Improve GUI using JavaFX