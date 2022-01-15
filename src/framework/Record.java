/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import java.util.HashMap;

/**
 *
 * @author truep
 */
public class Record {
    public HashMap<String, String> map = new HashMap<String, String>();
    
    public int recordID;
    
    public Record() {
        
    }
    
    public Record(HashMap<String, String> inputDictionary) 
    {
        this.map = new HashMap<String, String>(inputDictionary);
    }

    //Constructor
    public Record(String[] fieldNames, String[] data)
    {
        //Allocate new dictionary
        this.map = new HashMap<String, String>();
        if(fieldNames.length == data.length) {
            for(int i = 0; i < data.length; i++) {
                map.put(fieldNames[i], data[i]);
            }
        }
    }

    //Constructor to create a new record from the input record
    //There were some issues with records being passed by reference rather than value
    public Record(Record inputRecord) 
    {
        this.map = new HashMap<String, String>(inputRecord.map);
        this.recordID = inputRecord.recordID;
    }

    public int getRecordID() {
        return recordID;
    }

    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }
    
    public static Record parse(String[] headerNames, String line, String delimiter, String qualifier) {
        Record rec = new Record();
        String fields[] = line.split(delimiter);

        for(int i = 0; i < fields.length; i++) {
            if(qualifier != null) fields[i] = fields[i].replace(qualifier, "");
        }
        for(int i = 0; i < headerNames.length; i++) {
            if(i < fields.length) {
                rec.addField(headerNames[i], fields[i]);
            } else {
                rec.addField(headerNames[i], "");
            }
        }

        return rec;
    }
    
    public void addField(String key, String val) {
        map.put(key, val);
    }
    
    public String get(String key) {
        return map.get(key);
    }
    
    public void combineRecord(Record tempRecord) 
    {
        for (String key: tempRecord.map.keySet()) 
        {
            this.addField(key, tempRecord.map.get(key));
        }
    }
}
