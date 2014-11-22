/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database_mgmt;

/**
 *
 * @author Carl
 */
public class Field{
    private String type;
    private String value;
    
    public Field(){
        this.type = null;
        this.value = null;
    }
    
    public Field(Field other){
        setField(other);
    }
    
    public Field(String value){
        setField(value);
    }
    
    public void setField(String value){
        if(Date.checkIfDate(value)){
            this.value = value;
            this.type = "DATE";
        }
        else if(isFloat(value)){
            this.value = value;
            this.type = "FLOAT";
        }
        else if(value.getClass().equals(String.class)){
            this.value = value;
            this.type = "STRING";
        }
        else{
            throw new IllegalArgumentException("Invalid type for Field");
        }
    }
    
    public void setField(Field other){
        setField(other.getValue());
    }
    
    public void setValue(Field other){
        if(this.type.equals("DATE") && Date.checkIfDate(value)){
            this.value = value;
        }
        else if(this.type.equals("STRING") && 
                value.getClass().equals(String.class)){
            this.value = value;
        }
        else if(this.type.equals("FLOAT") && isFloat(value)){
            this.value = value;
        }
        else{
            throw new IllegalArgumentException("Invalid type for Field");
        }
    }
    
    public String getValue(){
        return this.value;
    }
    
    public String getType(){
        return this.type;
    }
    
    private boolean isFloat(String s){
        try {
            Float.parseFloat(s);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
}
