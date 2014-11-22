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
public class Attribute{
    private String name;
    private String type;
    
    public Attribute(){
        this.name = null;
        this.type = null;
    }
    
    public Attribute(String name, String type){
        setAttribute(name, type);
    }
    
    public void setAttribute(String name, String type){
        type = type.toUpperCase();
        name = name.toUpperCase();
        if(acceptType(type)){
            this.type = type;
            this.name = name;
        }
        else{
            throw new IllegalArgumentException(type);
        }
    }
    
    public void setName(String name){
        name = name.toUpperCase();
        this.name = name;
    }
    
    public void setType(String type){
        type = type.toUpperCase();
        if(acceptType(type)){
            this.type = type;
        }
        else{
            throw new IllegalArgumentException(type);
        }
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getType(){
        return this.type;
    }
    
    public boolean isEqual(Attribute other){
        if(other.name.equals(this.name) && other.type.equals(this.type)){
            return true;
        }
        else{
            return false;
        }
    }
    
    private boolean acceptType(String type){
        if(type.matches("STRING|FLOAT|DATE")){
            return true;
        }
        return false;
    }
    
    public boolean hasType(String type){
        if(this.type.equals(type))
            return true;
        else
            return false;
    }
}
