package database_mgmt;

import java.util.ArrayList;

public class Table{
    private String name;
    private ArrayList<Attribute> attributes;
    private ArrayList<ArrayList<Field>> records;
    private int maxPrimaryKey;
     
    public Table(String name, ArrayList<Attribute> cols, 
            ArrayList<ArrayList<Field>> rows, int mP){
        this.name = name;
        this.attributes = new ArrayList<Attribute>(cols);    //not deep------------------------
        this.records = new ArrayList<ArrayList<Field>>(rows);  //not deep----------------
        this.maxPrimaryKey = mP;
    }
    
    public Table(String name, ArrayList<Attribute> cols, 
            ArrayList<ArrayList<Field>> rows){
        this.name = name;
        this.attributes = new ArrayList<Attribute>(cols);    //not deep------------------------
        this.records = new ArrayList<ArrayList<Field>>(rows);  //not deep----------------
        this.maxPrimaryKey = -1;
        setPrimaryKey();
    }
    
    public Table(String name, ArrayList<Attribute> cols){
        this.name = name;
        this.attributes = new ArrayList<Attribute>(cols);    //not deep------------------------
        this.records = new ArrayList<ArrayList<Field>>();
        this.maxPrimaryKey = -1;
        setPrimaryKey();
    }
    
    public Table(Table other){
        this.name = other.getName();
        this.attributes = new ArrayList<Attribute>(other.getAttributes());    //not deep------------------------
        this.records = other.getRecords();
        this.maxPrimaryKey = -1;
        setPrimaryKey();
    }
    
    private void setAttributeTable(){
        for(Attribute att : this.attributes){
            att.setTable(this.name);
        }
    }
    
    public void setTableName(String name){
        this.name = name;
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
    
    public void insertAttribute(String name, String type){
        this.attributes.add(new Attribute(name, type, this.name)); //Attribute class throws exception if type is invalid
        for(ArrayList<Field> rec : this.records){
            rec.add(new Field(null, type));
        } 
    }
    
    public void insertAttribute(Attribute a){
        String name = a.getName();
        String type = a.getType();
        insertAttribute(name, type);
    }
    
    public boolean typeInAttributes(String type, ArrayList<Attribute> cols){
        for (Attribute col : cols) {
            if (col.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }
    
    public void renameAttribute(String oldName, String newName){
        if(this.nameInAttributes(newName)){
            throw new IllegalArgumentException("ERROR: attribute name already in attributes");
        }
        for(Attribute a : this.attributes){
            if(a.getName().equals(oldName)){
                a.setName(newName);
            }
        }
    }
    
    public void updateRecord(int row, int col, Field value){
        if( (row > -1 && row < this.getNumRecords()) &&
                (col > -1 && col < this.getNumAttributes()) &&
                (value.getType().equals(this.records.get(row).get(col).getType())) ){
            this.records.get(row).get(col).setField(value);
        }
        else{
            throw new IllegalArgumentException("ERROR: invalid field value in update");
        }
    }
    
    private void setPrimaryKey(){
        String val = null;
        int valued = -1;
        //if id attribute doesn't exist, add it to table and rows
        if( !(this.attributes.size() > 0 && 
                this.attributes.get(0).getName().equals("ID"))){
            this.attributes.add(0, new Attribute("ID", "FLOAT"));
            int pI = this.maxPrimaryKey;
            for(ArrayList<Field> rec : this.records){
                pI++;
                String v = Integer.toString(pI);
                rec.add(0, new Field(v));
            }
            this.maxPrimaryKey = pI;
            return;
        }
        //if records size is greater than zero set valued to highest id
        if(this.records.size() != 0){
            val = this.records.get(this.records.size()-1).get(0).getValue();
            valued = Integer.parseInt(val);
        }
        //initially empty table, added first values
        if(this.maxPrimaryKey == -1 && this.records.size() != 0){
            this.maxPrimaryKey = valued;
        }
        else if(valued > this.maxPrimaryKey){
            this.maxPrimaryKey = valued;
        }
    }
    
    public void insertRecord(ArrayList<Field> record){  //inserts one new record
        Field f = null;
        int value;
        String val = null;
        
        //test if record has correct size
        if(record.size() != getNumAttributes()){  
            throw new IllegalArgumentException("ERROR: invalid record length");
        }
        
        //find primary key number for record to be inserted
        value = this.maxPrimaryKey + 1;
        val = Integer.toString(value);

        //add ID field,  add record to records, then set new maxPrimaryKey
        f = new Field(val,"FLOAT");
        record.get(0).setField(f);
        this.records.add(record);
        this.setPrimaryKey();
        
    }
    
    public void deleteRecord(int i){
        if(i < this.records.size() && i >= 0){  
           this.records.remove(i);
        }
        else{
            throw new IllegalArgumentException("ERROR: invalid record/index number");
        }
        this.setPrimaryKey();
    }
    
    public void deleteAttribute(String s){
        int idx = this.getAttributeIdx(s);
        this.attributes.remove(idx);
        for(ArrayList<Field> record : this.records){
            record.remove(idx);
        }
    }
    
    public ArrayList<Attribute> getDetailedAttributes(){        //returns attributes with format table.attribute
        ArrayList<Attribute> detailedAtts = new ArrayList<>();
        Attribute detAtt = null;
        for(Attribute att : this.attributes){
            detAtt = new Attribute(this.name+"."+att.getName(),att.getType());
            detailedAtts.add(detAtt);
        }
        return detailedAtts;
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
        throw new IllegalArgumentException("ERROR: attribute not found in table");
    }
    
    public Attribute getAttribute(String col){
        for(int i = 0; i < getNumAttributes(); i++){
            if(col.equals(this.attributes.get(i).getName())){
                return this.attributes.get(i);
            }
        }
        throw new IllegalArgumentException("ERROR: attribute not found in table");
    }
    
    public Attribute getAttribute(int idx){
        if(idx > -1 && idx < this.getNumAttributes()){
            return this.attributes.get(idx);
        }else{
            throw new IllegalArgumentException("ERROR: program tried to get an attribute with an invalid index number");
        }
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
            throw new IllegalArgumentException("ERROR: invalid record index number");
        }
    }
    
    public ArrayList<ArrayList<Field>> getRecords(){
        return this.records;
    }
    
    public String getName(){
        return this.name;
    }
    
    public int getMaxPrimary(){
        return this.maxPrimaryKey;
    }
    
    public void convertToView(){
        String value;
        for(int i = this.records.size()-1; i >= 0; i--){
            value = this.records.get(i).get(0).getValue();
            this.records.get(i).remove(0);
            if(value == null){
                this.records.remove(i);
            }
        }
        this.attributes.remove(0);
    }
    
}
