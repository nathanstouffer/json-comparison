/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsoncomparison;

import java.lang.ArrayIndexOutOfBoundsException;

/**
 *
 * @author natha
 */
public class Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //String f1name = "../input-files/BreweriesMaster.json";
        //String f2name = "../input-files/BreweriesMasterEdit.json";
        
        // assume no errors
        boolean error = false;
        // instantiate file names
        String f1name = "";
        String f2name = "";
        try {
            // get file names from arguments
            f1name = args[0];
            f2name = args[1];
        }
        // catch exception too few arguments entered
        catch (ArrayIndexOutOfBoundsException e) {
            error = true;
            System.err.println("enter two arguments when calling from command line.");
        }
        
        if (!error) {
            // output info for user
            System.out.println();
            System.out.println("File 1: " + f1name);
            System.out.println("File 2: " + f2name);
            
            // instantiate JsonContainer objects for each file
            JsonContainer obj1 = new JsonContainer(f1name);
            JsonContainer obj2 = new JsonContainer(f2name);

            // compute similarity from file1
            double similarity = obj1.similarity(obj2);
            String output = String.format("\nSimilarity called from File 1: %f", similarity);
            System.out.println(output);

            // recompute similarity from file2
            similarity = obj2.similarity(obj1);
            output = String.format("\nSimilarity called from File 2: %f", similarity);
            System.out.println(output);
        }
        
    }
    
}
