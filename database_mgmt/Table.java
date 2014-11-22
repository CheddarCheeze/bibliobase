/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database_mgmt;

import java.util.ArrayList;

/**
 *
 * @author Carl
 */
public class Table{
    private String name;
    private ArrayList<Attribute> attributes;
    private ArrayList<ArrayList<Field>> records;
    private Attribute primaryKey;
    private int numAttributes;
    private int numRecords;
    
    public Table(String name, ArrayList<Attribute> cols, 
            ArrayList<ArrayList<Field>> rows, Attribute primaryKey){
        this.name = name;
        this.attributes = new ArrayList<Attribute>(cols);    //not deep------------------------
        this.records = new ArrayList<ArrayList<Field>>(rows);  //not deep----------------
        setPrimaryKey(primaryKey);
        this.numAttributes = cols.size();
        this.numRecords = rows.size();
    }
    
    public Table(String name, ArrayList<Attribute> cols, Attribute primaryKey){
        this.name = name;
        this.attributes = new ArrayList<Attribute>(cols);    //not deep------------------------
        this.records = new ArrayList<ArrayList<Field>>();
        setPrimaryKey(primaryKey);
        this.numAttributes = cols.size();
        this.numRecords = 0;
    }
    
    public Table(String name, ArrayList<Attribute> cols){
        this.name = name;
        this.attributes = new ArrayList<Attribute>(cols);    //not deep------------------------
        this.records = new ArrayList<ArrayList<Field>>();
        setPrimaryKey();
        this.numAttributes = cols.size();
        this.numRecords = 0;
    }
    
    public Table(Table other){
        this.name = other.getName();
        this.attributes = new ArrayList<Attribute>(other.getAttributes());    //not deep------------------------
        setPrimaryKey(other.getPrimaryKey());
        this.numAttributes = other.getNumAttributes();
        this.numRecords = other.getNumRecords();
        this.records = other.getRecords();
    }
 
    public void setPrimaryKey(){
        this.primaryKey = this.attributes.get(0);   //change this to find min-candidate key
    }
    
    public void setPrimaryKey(Attribute pKey){
        if(attInAttributes(primaryKey)){
            this.primaryKey = primaryKey;
        }
        else{
            throw new IllegalArgumentException("primary key is invalid");
        }
        //code for finding if it actually is a candidate key
    }
    
    public Attribute getPrimaryKey(){
        return this.primaryKey;
    }
    
    public boolean attInAttributes(Attribute col){
        for(int i = 0; i < this.numAttributes; i++){
            if(this.attributes.get(i).isEqual(col)){
                return true;
            }
        }
        return false;
    }
    
    public boolean typeInAttributes(String type, ArrayList<Attribute> cols){
        for(int i = 0; i < cols.size(); i++){
            if(cols.get(i).getType().equals(type)){
                return true;
            }
        }
        return false;
    }
    
    public void insertRecord(ArrayList<Field> record){  //inserts one new record
        ArrayList<Field> rec = new ArrayList<Field>();
        if(record.size() != this.numAttributes)
            throw new IllegalArgumentException("invalid record length");
        this.records.add(record);
        this.numRecords++;
    }
    
    public void deleteRecord(int i){
        if(i < records.size() && i >= 0){  
            records.remove(i);
            this.numRecords--;
        }
        else{
            throw new IllegalArgumentException("invalid record/index number");
        }
    }
    
    public int getNumAttributes(){
        return this.numAttributes;
    }
    
    public int getNumRecords(){
        return this.numRecords;
    }
    
    public ArrayList<Field> getRecord(int i){
        if(i >= 0 || i < this.records.size()){
            return this.records.get(i);
        }
        else{
            throw new IllegalArgumentException("invalid record index number");
        }
    }
    
    public String getName(){
        if(this.name.isEmpty())
            throw new IllegalArgumentException("table has no name");
        else{
            return this.name;
        }
    }
    
    public ArrayList<Attribute> getAttributes(){
        return this.attributes;
    }
    
    public ArrayList<ArrayList<Field>> getRecords(){
        return this.records;
    }
}
