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
    
    public Table(String name, ArrayList<Attribute> cols, 
            ArrayList<ArrayList<Field>> rows, Attribute primaryKey){
        this.name = name;
        this.attributes = new ArrayList<Attribute>(cols);    //not deep------------------------
        this.records = new ArrayList<ArrayList<Field>>(rows);  //not deep----------------
        setPrimaryKey(primaryKey);
    }
    
    public Table(String name, ArrayList<Attribute> cols, Attribute primaryKey){
        this.name = name;
        this.attributes = new ArrayList<Attribute>(cols);    //not deep------------------------
        this.records = new ArrayList<ArrayList<Field>>();
        setPrimaryKey(primaryKey);
    }
    
    public Table(String name, ArrayList<Attribute> cols){
        this.name = name;
        this.attributes = new ArrayList<Attribute>(cols);    //not deep------------------------
        this.records = new ArrayList<ArrayList<Field>>();
        setPrimaryKey();
    }
    
    public Table(Table other){
        this.name = other.getName();
        this.attributes = new ArrayList<Attribute>(other.getAttributes());    //not deep------------------------
        setPrimaryKey(other.getPrimaryKey());
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
        for(int i = 0; i < getNumAttributes(); i++){
            if(this.attributes.get(i).isEqual(col)){
                return true;
            }
        }
        return false;
    }
    
    public boolean nameInAttributes(String name){
        for (Attribute col : this.attributes) {
            if (col.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean typeInAttributes(String type, ArrayList<Attribute> cols){
        for (Attribute col : cols) {
            if (col.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }
    
    public void updateRecord(int row, int col, Field value){
        if( (row > -1 && row < this.getNumRecords()) &&
                (col > -1 && col < this.getNumAttributes()) &&
                (value.getType().equals(this.records.get(row).get(col).getType())) ){
            this.records.get(row).get(col).setField(value);
        }
        else{
            throw new IllegalArgumentException("invalid field value in update");
        }
    }
    
    public void insertRecord(ArrayList<Field> record){  //inserts one new record
        if(record.size() != getNumAttributes())
            throw new IllegalArgumentException("invalid record length");
        this.records.add(record);
    }
    
    public void deleteRecord(int i){
        if(i < this.records.size() && i >= 0){  
            this.records.remove(i);
        }
        else{
            throw new IllegalArgumentException("invalid record/index number");
        }
    }
    
    public int getNumAttributes(){
        return this.attributes.size();
    }
    
    public ArrayList<Attribute> getAttributes(){
        return this.attributes;
    }
    
    public int getAttributeIdx(String col){
        for(int i = 0; i < getNumAttributes(); i++){
            if(col.equals(this.attributes.get(i).getName())){
                return i;
            }
        }
        throw new IllegalArgumentException("attribute not found in table");
    }
    
    public String getAttType(String name){
        for (Attribute col : this.attributes) {
            if (col.getName().equals(name)) {
                return col.getType();
            }
        }
        return null;
    }
    
    public int getNumRecords(){
        return this.records.size();
    }
    
    public ArrayList<Field> getRecord(int i){
        if(i >= 0 || i < this.records.size()){
            return this.records.get(i);
        }
        else{
            throw new IllegalArgumentException("invalid record index number");
        }
    }
    
    public ArrayList<ArrayList<Field>> getRecords(){
        return this.records;
    }
    
    public String getName(){
        if(this.name.isEmpty())
            throw new IllegalArgumentException("table has no name");
        else{
            return this.name;
        }
    }
}
