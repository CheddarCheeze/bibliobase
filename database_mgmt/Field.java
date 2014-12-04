package database_mgmt;

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
    
    public Field(String value, String type){
        setField(value, type);
    }
    
    public void setField(String value, String type){
        if(value == null || value.toUpperCase().equals("NULL")){
            this.value = null;
            this.type = type;
        }
        else if(Date.checkIfDate(value)){
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
            throw new IllegalArgumentException("ERROR: Invalid type for Field");
        }
    }
    
    public void setField(String value){
        if(value == null || value.toUpperCase().equals("NULL")){
            throw new IllegalArgumentException("ERROR: this Field method does not accept null as argument");
        }
        else if(Date.checkIfDate(value)){
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
            throw new IllegalArgumentException("ERROR: Invalid type for Field");
        }
    }
    
    public void setField(Field other){
        this.value = other.getValue();
        this.type = other.getType();
    }
    
    public void setType(String type){
        if(type.toUpperCase().matches("DATE|FLOAT|STRING")){
            this.type = type;
        }
    }
    
    public void setValue(String value){
        if(value == null || value.equals("NULL")){
            this.value = null;
        }
        else if(this.type.equals("DATE") && Date.checkIfDate(value)){
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
            throw new IllegalArgumentException("ERROR: Invalid type for Field");
        }
    }
    
    public String getValue(){
        return this.value;
    }
    
    public String getType(){
        return this.type;
    }
    
    public boolean isEqual(Field f){
        if(this.value == null && f.value == null){
            return true;
        }
        if(this.value == null || f.value == null){ //basically an xor considering its predecessor
            return false;
        }
        if(this.type.equals(f.getType()) &&
                this.value.equals(f.getValue())){
            return true;
        }
        return false;
    }
    
    public boolean compareFields(Field f, String operator){
        if(this.value == null && f.value == null){
            if(operator.equals("=")){
                return true;
            }
            else{
                return false;
            }
        }
        else if(this.value == null || f.value == null){ //because of the first if, this is an xor operation
            if(operator.equals("!=")){
                return true;
            }
            else{
                return false;
            }
        }
        else if( this.type.equals("FLOAT") ){ 
            float oValue = Float.parseFloat(f.getValue());
            float value = Float.parseFloat(this.value);

            if( (operator.equals("=") && isFloatEqual(value, oValue)) || 
                    (operator.equals("!=") && !isFloatEqual(value, oValue)) ||
                    (operator.equals("<") && value < oValue) ||
                    (operator.equals(">") && value > oValue) ||
                    (operator.equals("<=") && value <= oValue)||
                    (operator.equals(">=") && value >= oValue) ){
                return true;
            }
        }
        else if( this.type.equals("DATE") ){
            Date dO = new Date(f.getValue());
            Date d = new Date(this.value);
            if(d.compareDates(dO, operator)){
                return true;
            }
        }
        else if( this.type.equals("STRING") ){
            if( (operator.equals("=") && this.value.equals(f.getValue())) ||
                    (operator.equals("!=") && !this.value.equals(f.getValue())) ){
                return true;
            }
        }
        return false;
    }
    
    private boolean isFloat(String s){
        try {
            Float.parseFloat(s);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
    
    private boolean isFloatEqual(Float a, Float b){      //taken from: http://floating-point-gui.de/errors/comparison/
        final float absA = Math.abs(a);
        final float absB = Math.abs(b);
        final float diff = Math.abs(a - b);
        
        if (a == b) { // shortcut, handles infinities
            return true;
        } else if (a == 0 || b == 0 || diff < Float.MIN_NORMAL) {
            // a or b is zero or both are extremely close to it
            // relative error is less meaningful here
            return diff < (0.00001 * Float.MIN_NORMAL);
        } else { // use relative error
            return diff / (absA + absB) < 0.00001;
        }
    }
}
