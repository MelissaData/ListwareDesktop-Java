/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import listware.Listware;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author truep
 */
public class ServiceHelper {
    private int[] recordID;
    boolean disposed = false;

    public Record[] sendRequest(String endpoint, HashMap<String, String> serviceOptions, Record[] inputRecords, Class<?> requestType, Class<?> recordType)
    {
        try {
            this.adjustDictionary(serviceOptions);
            JSONObject requestJObject = this.createInputJObject(serviceOptions, inputRecords, requestType, recordType);
            JSONObject responseJObject = this.sendJSONPOSTRequest(endpoint, requestJObject);
            return this.returnRecords(responseJObject);
        } catch (Exception ex) {
            Logger.getLogger(ServiceHelper.class.getName()).log(Level.SEVERE, null, ex);
            Listware._self.logError(ex.getMessage());
            return new Record[0];
        }
    }

    private void adjustDictionary(HashMap<String, String> serviceOptions) 
    {
        ArrayList<String[]> tempStrList = new ArrayList<String[]>();
        ArrayList<String> keysToRemove = new ArrayList<String>();
        
        for(String key : serviceOptions.keySet()) {
            if(key.contains("_")) {
                String[] tmp = {key.split("_")[0], key.split("_")[1] + ":" + serviceOptions.get(key)};
                tempStrList.add(tmp);
                keysToRemove.add(key);
            }
        } 
        
        for(String key : keysToRemove) {
            serviceOptions.remove(key);
        }

        HashMap<String, String> adjustedOptions = new HashMap<String, String>();
        
        for(String[] pair : tempStrList) {
            if(!adjustedOptions.containsKey(pair[0])) {
                adjustedOptions.put(pair[0], pair[1]);
            } else {
                adjustedOptions.replace(pair[0], pair[1]);
            }
        }
       
        for(String key : adjustedOptions.keySet()) {
            if(!serviceOptions.containsKey(key)) {
                serviceOptions.put(key, adjustedOptions.get(key));
            } else {
                serviceOptions.replace(key, adjustedOptions.get(key));
            }
        }
    }

    //Method that sends JSONObject as a JSON POST request to the endpoint given
    JSONObject sendJSONPOSTRequest(String endpoint, JSONObject jsonInput) throws InterruptedException, Exception 
    {
        JSONObject jsonOutput;
        int attempts = 0;
        do
        {
            try
            {
                attempts++;
                String charset = "UTF-8";
                HttpURLConnection conn = (HttpURLConnection)new URL(endpoint).openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("accept-charset", charset);
                conn.setRequestProperty("content-type", "application/json");
                OutputStreamWriter writer = null;
                try {
                    writer = new OutputStreamWriter(conn.getOutputStream(), charset);
                    System.out.println("Send request to " + endpoint + ": " + jsonInput.toJSONString());
                    writer.write(jsonInput.toJSONString()); // Write POST query string (if any needed).
                } finally {
                    if (writer != null) writer.close();
                }

                InputStream is = conn.getInputStream();
                StringBuilder sb = new StringBuilder();
                int x;
                while((x = is.read()) != -1) {
                    sb.append((char)x);
                }
                System.out.println(sb.toString());

                JSONParser parser = new JSONParser();
                jsonOutput = (JSONObject)parser.parse(sb.toString());
                
                break;
            }
            catch (Exception ex) 
            {
                if (attempts == 10)
                {
                    throw ex;
                }
                Thread.sleep(300);
            }
        } while (true);

        return jsonOutput;
    }

    //Method that creates a JSONObject based on request structure, field names, and input records
    private JSONObject createInputJObject(HashMap<String, String> serviceOptions, Record[] inputRecords, Class<?> requestType, Class<?> recordType) 
    {
        try {
            this.recordID = new int[inputRecords.length];
            for(int i = 0; i < inputRecords.length; i++) {
                this.recordID[i] = inputRecords[i].recordID;
            }
            
            Constructor<?> ctor = requestType.getConstructor();
            Object request = ctor.newInstance();
            for(String key: serviceOptions.keySet()) {
                setRequestFields(key, serviceOptions.get(key), requestType, request);
            }

            Object records = Array.newInstance(recordType, inputRecords.length);
            for(int j = 0; j < inputRecords.length; j++) {
                Record r = inputRecords[j];
                Constructor<?> ctor2 = recordType.getConstructor();
                Object rec = ctor2.newInstance();
                for(String key: r.map.keySet()) {
                    setRequestFields(key, r.map.get(key), recordType, rec);
                }
                Array.set(records, j, rec);
            }
                        
            this.setRequestFields("Records", records, requestType,  request);
            
            return request2JSONObject(request);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ServiceHelper.class.getName()).log(Level.SEVERE, null, ex);
            Listware._self.logError(ex.getMessage());
        } catch (SecurityException ex) {
            Logger.getLogger(ServiceHelper.class.getName()).log(Level.SEVERE, null, ex);
            Listware._self.logError(ex.getMessage());
        } catch (InstantiationException ex) {
            Logger.getLogger(ServiceHelper.class.getName()).log(Level.SEVERE, null, ex);
            Listware._self.logError(ex.getMessage());
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ServiceHelper.class.getName()).log(Level.SEVERE, null, ex);
            Listware._self.logError(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ServiceHelper.class.getName()).log(Level.SEVERE, null, ex);
            Listware._self.logError(ex.getMessage());
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ServiceHelper.class.getName()).log(Level.SEVERE, null, ex);
            Listware._self.logError(ex.getMessage());
        }
        return null;
    }
    
    public static JSONObject request2JSONObject(Object obj) {
        Field[] vars = obj.getClass().getFields();
        HashMap<String, Object> map = new HashMap();
        for(Field f: vars) {
            try {
                map.put(f.getName(), f.get(obj));
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ServiceHelper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ServiceHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Object records[] = (Object[])map.get("Records");
        JSONArray records2 = new JSONArray();
        for(Object r: records) {
            records2.add(class2JSONObject(r));
        }
        map.replace("Records", records2);
        return new JSONObject(map);
    }
    
    public static JSONObject class2JSONObject(Object obj) {
        Field[] vars = obj.getClass().getFields();
        HashMap<String, Object> map = new HashMap();
        for(Field f: vars) {
            try {
                map.put(f.getName(), f.get(obj));
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ServiceHelper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ServiceHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new JSONObject(map);
    }

    //Method that changes JSONObject output from service to array of Record
    private Record[] returnRecords(JSONObject serviceResponseJObject) 
    {
        ArrayList<Record> tempList = new ArrayList<Record>();
        JSONArray outputRecords = (JSONArray)serviceResponseJObject.get("Records");
        int recordCounter = 0;
        if(outputRecords == null) {
            return null;
        }
        for (Object r: outputRecords)
        {
            Record tempRecord = new Record();
            JSONObject record = (JSONObject)r;
            for (Object key: record.keySet())
            {
                tempRecord.addField("MD_" + key, (String)record.get(key));
            }
            tempRecord.recordID = this.recordID[recordCounter];
            recordCounter++;
            tempList.add(tempRecord);
        }

        //Return an array of output records
        Record[] processedRecords = tempList.toArray(new Record[tempList.size()]) ;
        return processedRecords;
    }

    //Method that sets a field name of an 
    private void setRequestFields(String fieldName, Object fieldValue, Class<?> inputObjectType,  Object inputObject) 
    {
        try {
            if (inputObjectType.getField(fieldName) != null)
            {
                inputObjectType.getField(fieldName).set(inputObject, fieldValue);
            }
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(ServiceHelper.class.getName()).log(Level.SEVERE, null, ex);
            Listware._self.logError(ex.getMessage());
        } catch (SecurityException ex) {
            Logger.getLogger(ServiceHelper.class.getName()).log(Level.SEVERE, null, ex);
            Listware._self.logError(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ServiceHelper.class.getName()).log(Level.SEVERE, null, ex);
            Listware._self.logError(ex.getMessage());
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ServiceHelper.class.getName()).log(Level.SEVERE, null, ex);
            Listware._self.logError(ex.getMessage());
        }
    }

    public String[] returnProperties(Class<?> recordType) 
    {
        Field[] vars = recordType.getFields();
        String[] varNames = new String[vars.length];
        for(int i = 0; i < vars.length; i++) {
            varNames[i] = vars[i].getName();
        }
        return varNames;
    }

}
