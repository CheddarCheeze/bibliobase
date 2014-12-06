package database_mgmt;

public class Attribute{
    private String name;
    private String type;
    private String table;
    
    public Attribute(){
        this.name = null;
        this.type = null;
        this.table = null;
    }
    
    public Attribute(String name, String type){
        setAttribute(name, type);
    }
    
    public Attribute(String name, String type, String table){
        setAttribute(name, type, table);
    }
    
    public void setAttribute(String name, String type){
        type = type.toUpperCase();
        if(acceptType(type)){
            this.type = type;
            this.name = name;
        }
        else{
            throw new IllegalArgumentException(type);
        }
        this.table = null;
    }
    
    public void setAttribute(String name, String type, String table){
        type = type.toUpperCase();
        if(acceptType(type)){
            this.type = type;
            this.name = name;
        }
        else{
            throw new IllegalArgumentException(type);
        }
        this.table = table;
    }
    
    public void setName(String name){
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
    
    public void setTable(String table){
        this.table = table;
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getType(){
        return this.type;
    }
    
    public String getTable(){
        return this.table;
    }
    
    public boolean isEqual(Attribute other){
        String n = this.name.toUpperCase();
        String nO = other.getName().toUpperCase();
        if(nO.equals(n) && other.type.equals(this.type)){
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
