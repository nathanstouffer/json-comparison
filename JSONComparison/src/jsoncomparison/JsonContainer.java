/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsoncomparison;

import com.google.gson.*;
import java.util.ArrayList;

/**
 * class to store a JsonElement and tell the similarity
 * with another JsonElement
 * 
 * @author natha
 */
public class JsonContainer {
    
    // variable to store a JsonElement
    private JsonElement item;
    
    public JsonContainer(String fname) {
        // read in file
        DataReader freader = new DataReader(fname);
        // instantiate Gson parser
        Gson g = new Gson();
        // store object in global variable
        this.item = g.fromJson(freader.getData(), JsonElement.class);
    }
    
    /**
     * method to compute the similarity between the 'this'
     * object and the JsonContainer passed in
     * 
     * @param other
     * @return 
     */
    public double similarity(JsonContainer other) {
        // get items
        JsonElement el1 = this.getItem();
        JsonElement el2 = other.getItem();
        
        // test for null values
        // array of the form { continue, return_val }
        boolean[] indicator = this.checkNull(el1, el2);
        // test if similarity measure should continue
        if (!indicator[0]) { 
            if (indicator[1]) { return 1.0; }
            else { return 0.0; }
        }
        
        // compute the similarity of the two elements
        return computeSim(el1, el2);
    }
    
    /**
     * method to recursively compute the similarity of two JsonElements
     * @param curr
     * @param other
     * @return 
     */
    private double computeSim(JsonElement el1, JsonElement el2) {
        // test for null values
        // array of the form { continue, return_val }
        boolean[] indicator = this.checkNull(el1, el2);
        // test if similarity measure should continue
        if (!indicator[0]) { 
            if (indicator[1]) { return 1.0; }
            else { return 0.0; }
        }
        
        // return 0.0 if elements are of different type
        // get class types in string format
        String type1 = el1.getClass().toString();
        String type2 = el2.getClass().toString();
        // if element types differ, return false
        if (!type1.equals(type2)) { return 0.0; }
        
        // we now know the elements are of the same type
        
        // cases for different types
        // in general, each case casts the elements and calls the 
        // appropriate similarity measure
        if (el1.isJsonPrimitive()) {
            // cast as primitive
            JsonPrimitive prim1 = (JsonPrimitive)el1;
            JsonPrimitive prim2 = (JsonPrimitive)el2;
            // compute primitive similarity
            return this.primSim(prim1, prim2);
        }
        else if (el1.isJsonObject()) {
            // cast as JsonObject
            JsonObject obj1 = (JsonObject)el1;
            JsonObject obj2 = (JsonObject)el2;
            // compute object similarity
            return this.objSim(obj1, obj2);
        }
        else if (el1.isJsonArray()) {
            // cast as JsonArray
            JsonArray arr1 = (JsonArray)el1;
            JsonArray arr2 = (JsonArray)el2;
            // compute array similarity
            return this.arrSim(arr1, arr2);
        }
        // case that is hopefully never reached
        else {
            System.err.println("Type is not null, primitive, object, or array");
            return 0.0;
        }
    }
    
    /**
     * method to compute the similarity of two primitive types
     * @param prim1
     * @param prim2
     * @return 
     */
    private double primSim(JsonPrimitive prim1, JsonPrimitive prim2) {
        // get string versions of primitive values
        String str1 = prim1.toString();
        String str2 = prim2.toString();

        // if strings are equal, return 1.0
        if (str1.equals(str2)) { return 1.0; } 
        // otherwise, return 0.0
        else { return 0.0; }
    }
    
    /**
     * method to compute the similarity of two JsonObjects
     * @param obj1
     * @param obj2
     * @return 
     */
    private double objSim(JsonObject obj1, JsonObject obj2) {
        // compute number of distinct keys
        ArrayList<String> keys = this.getDistinctKeys(obj1, obj2);
        // if keys.size() is 0, return 1.0
        if (keys.size() == 0) { return 1.0; }
        // weight represents how much influence each element
        // in the object has on the similarity
        double weight = 1.0 / keys.size();

        // variabl to store the similarity of the objects
        double sim = 0.0;
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            // recursive call on each value in the object
            sim += weight * this.computeSim(obj1.get(key), obj2.get(key));
        }
        return sim;
    }
    
    /**
     * method to compute the similarity of two JsonArrays
     * @param arr1
     * @param arr2
     * @return 
     */
    private double arrSim(JsonArray arr1, JsonArray arr2) {
        // compute number of elements in each array
        int size1 = arr1.size();
        int size2 = arr2.size();
        
        // assign smaller and larger to appropriate sizes
        int smaller = 0;
        int larger = 0;
        // this covers all cases of both sizes
        if (size1 < size2) { smaller = size1; larger = size2; }
        else { smaller = size2; larger = size1; }
        
        // weight represents how much influence each element
        // in the array has on the similarity
        double weight = 1.0 / larger;
        
        // variable to store the similarity of the arrays
        double sim = 0.0;
        for (int i = 0; i < smaller; i++) {
            // recursive call on each element in the array
            sim += weight * this.computeSim(arr1.get(i), arr2.get(i)); 
        }
        return sim;
    }
    
    /**
     * method to return the distinct keys in two JsonObjects
     * @param obj1
     * @param obj2
     * @return 
     */
    private ArrayList<String> getDistinctKeys(JsonObject obj1, JsonObject obj2) {
        // list of distinct keys
        ArrayList<String> keys = new ArrayList<String>();
        
        // add each key in obj1 to keys
        for (String key : obj1.keySet()) { keys.add(key); }
        
        // add each key (that is not already in keys) in obj2 to keys
        for (String key : obj2.keySet()) {
            if (!keys.contains(key)) { keys.add(key); }
        }
        
        // return keys
        return keys;
    }
    
    /**
     * method to tell whether elements are null
     * 
     * return value is an array of form { continue, return_val }
     * where each variable is boolean
     * 
     * continue tells the program whether the similarity measurement 
     * should continue or not
     * 
     * if return_val is true, return 1.0
     * if return_val is false, return 0.0
     * 
     * return_val is not always used (if this method does not compute
     * the similarity)
     * 
     * @param el1
     * @param el2
     * @return 
     */
    private boolean[] checkNull(JsonElement el1, JsonElement el2) {
        // array of the form { continue, return_val } where elements
        // correspond to boolean variables
        boolean[] indicator = new boolean[2];
        
        // check if elements are null
        if (el1 == null && el2 == null) {
            // similarity measurement should not continue
            indicator[0] = false;
            // similarity of values is 1.0
            indicator[1] = true;
        }
        // check if one element is null
        else if (el2 == null || el1 == null) {
            // similarity measurement should not continue
            indicator[0] = false;
            // similarity of values is 0.0
            indicator[1] = false;
        }        
        else if (el1.isJsonNull() && el1.isJsonNull()) {
            // similarity measurement should not continue
            indicator[0] = false;
            // similarity of values is 1.0
            indicator[1] = true;
        }
        else if (el1.isJsonNull() || el2.isJsonNull()) {
            // similarity measurement should not continue
            indicator[0] = false;
            // similarity of values is 0.0
            indicator[1] = false;
        }
        else { 
            // similarity measurement should continue
            indicator[0] = true;
            // unused value
            indicator[1] = true;
        }
        
        return indicator;
    }
    
    /**
     * method to return the item
     * @return 
     */
    public JsonElement getItem() { return this.item; }
    
}
