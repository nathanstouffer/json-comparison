/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsoncomparison;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * class to read in a data file as one string
 * 
 * @author natha
 */
public class DataReader {
    
    // variable to store file name
    private final String FILE_NAME;
    // data in the file
    // after reading the file, this is just the entire
    // file as a string
    private String data;
    
    /**
     * constructor to read in the file
     * @param fname 
     */
    public DataReader(String fname) {
        this.FILE_NAME = fname;
        this.data = "";
        
        // read and process file
        try{ readFile(); }
        catch(IOException e){
            System.err.println("Opening file error");
            //e.printStackTrace();
        }
    }
    
    /**
     * method to readin the file specified in the constructor
     * all lines are add to one variable
     * @throws IOException 
     */
    private void readFile() throws IOException {
        // construct file to be read
        File file = new File(this.FILE_NAME);
        
        // construct buffered reader
        BufferedReader br = new BufferedReader(new FileReader(file));
        
        // br returns null when end of file is reached
        String line = br.readLine();
        // test if there is a new line
        while (line != null) {
            //System.out.println(line);
            // add line to data
            this.data += line;
            // move to next line
            line = br.readLine();
        }
        
        // close buffered reader
        br.close();
    }
    
    /**
     * method to return the file as one string
     * @return 
     */
    public String getData() { return this.data; }
    
}
