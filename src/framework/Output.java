/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author truep
 */
public class Output
{
    private OutputStream streamWriter;
    private String[] headerFieldNames;
    private String filePath;
    private String delimiter;
    private String qualifier;
    public int linesWritten;

    //Constructor that sets all the settings for the writer
    public Output(String filePath, String delimiter, String qualifier) throws FileNotFoundException
    {
        this.linesWritten = 0;
        this.filePath = filePath;
        this.delimiter = delimiter;
        this.qualifier = qualifier == null? "" : qualifier;
        this.streamWriter = new FileOutputStream(filePath);
    }

    //Writes records with qualifier + delimiter
    public synchronized void sendRecords(Record[] recordsToBeWritten) throws IOException 
    {
        this.checkIfEmpty(recordsToBeWritten[0]);
        for (Record tempRecord : recordsToBeWritten) 
        {
            String tempString = "";
            boolean first = true;
            for (String currentHeader : headerFieldNames) 
            {
                if(!first) {
                    tempString += delimiter;
                } else {
                    first = false;
                }
                String val = tempRecord.map.get(currentHeader);
                if(val == null) val = "";
                if (qualifier.isEmpty() && val.contains(delimiter))
                {
                    tempString += "\"" + val.trim() + "\"";
                }
                else
                {
                    tempString += qualifier + val.trim() + qualifier;
                }
            }
            tempString += System.lineSeparator();
            streamWriter.write(tempString.getBytes());
            linesWritten++;
        }
    }

    synchronized int numberOfLinesWritten() 
    {
        return this.linesWritten;
    }

    //Writes header line
    public synchronized void writeHeaders(Record sampleRecord) throws IOException 
    {
        headerFieldNames = sampleRecord.map.keySet().toArray(new String[sampleRecord.map.keySet().size()]);

        String tempString = "";
        
        boolean first = true;
        for (String header : headerFieldNames) 
        {
            if(first) {
                first = false;
            } else {
                tempString += delimiter;
            }
            tempString += qualifier + header + qualifier;
            
        }
        tempString += System.lineSeparator();
        streamWriter.write(tempString.getBytes());
    }

    public synchronized void checkIfEmpty(Record sampleRecord) throws IOException 
    {
        if (new File(filePath).length() == 0) 
        {
            this.writeHeaders(sampleRecord);
        }
    }

    //Close writer
    public void closeWriter() throws IOException 
    {
        if(streamWriter != null)
            streamWriter.close();
    }
}
