/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author truep
 */
public class Input {
    
    String filePath = null;
    String delimiter = null;
    String qualifier = null;
    public String[] headerNames = {};
    public Record[] records = {};
    Scanner streamReader = null;
    
    public Input(String path, String delimiter, String qualifier) throws FileNotFoundException {
        this.filePath = path;
        this.delimiter = Pattern.quote(delimiter);
        this.qualifier = qualifier;
        
        streamReader = new Scanner(new File(filePath));
        if(!checkForEnd()) {
            headerNames = getFields(streamReader.nextLine());
        }
//        readData();
    }
    
    public void readData() {
        if(streamReader == null) return;
        String line = null;
        if(streamReader.hasNext()) {
            line = streamReader.nextLine();
            headerNames = line.split(delimiter);
            if(qualifier != null) {
                for(int i = 0; i < headerNames.length; i++) {
                    headerNames[i] = headerNames[i].replace(qualifier, "");
                }
            }
        }
        
        ArrayList<Record> recList = new ArrayList<>();
        while(streamReader.hasNext()) {
            line = streamReader.nextLine();
            recList.add(Record.parse(headerNames, line, delimiter, qualifier));
        }
        records = new Record[recList.size()];
        records = recList.toArray(records);
        streamReader.close();
    }
    
    public Record[] getRecordsForPreview() 
    {
        ArrayList<Record> recordList = new ArrayList<Record>();

        for (int i = 0; i < 100; i++) 
        {
            if (streamReader.hasNext()) 
            {
                recordList.add(new Record(headerNames, getFields(streamReader.nextLine())));
            } else {
                break;
            }
        }

        return recordList.toArray(new Record[recordList.size()]);
    }
    
    public synchronized String[] getFields(String inputText) 
    {
        String[] res;
        if (qualifier == null)
        {
            res = inputText.split(delimiter);
            for(int i = 0; i < res.length; i++) {
                res[i] = res[i].trim();
            }
            return res;
        }
        else 
        {
            String qReg = Pattern.quote(qualifier);
            String pattern = String.format("%s(?=(?:[^%s]*%s[^%s]*%s)*(?![^%s]*%s))", delimiter, qReg, qReg, qReg, qReg, qReg, qReg);
            res = inputText.split(pattern);
            for(int i = 0; i < res.length; i++) {
                res[i] = res[i].trim().replace(qualifier, "");
            }
            return res;
        }
    }
    
    public synchronized Record[] getRecords(int amountOfRecords) 
    {
        ArrayList<Record> returnRecordList = new ArrayList<Record>();

        for (int i = 0; i < amountOfRecords; i++) 
        {
            if (streamReader.hasNext()) 
            {
                String[] fields = getFields(streamReader.nextLine());
                returnRecordList.add(new Record(headerNames, fields));
            } else {
                break;
            }
        }

        return returnRecordList.toArray(new Record[returnRecordList.size()]);
    }

    //To make sure we end when we're supposed to
    public synchronized boolean checkForEnd() 
    {
        return !streamReader.hasNext();
    }

    //To dispose of the reader
    public void closeReader()
    {
        streamReader.close();
    }
    
}
