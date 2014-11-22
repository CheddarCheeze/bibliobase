/*
 * Testing
 */

package database_mgmt;

import java.util.ArrayList;
import java.util.Arrays;

public class BiblioBaseDBMS {

    public static ArrayList<Table> Tables;
    
    public static void parseString(String str){
        ArrayList<ArrayList<String>> words= new ArrayList<ArrayList<String>>();
        str = str.toUpperCase();
        String[] command = str.split("[;]");
        for(int i = 0; i < command.length; i++){
            String[] temp = command[i].split("[ ,]|"
                    + "((?<=\\()|(?=\\()|(?<=\\))|(?=\\)))");
            if(temp.length <= 2){
                throw new IllegalArgumentException("invalid command length");
            }
            //tokenizes each command by word into a 2D ArrayList
            words.add(new ArrayList<String>(Arrays.asList(temp)));
            words.get(i).removeAll(Arrays.asList("", null));
            //find quotes and merge
            int quotePlace = -1;
            String s, s2;
            for(int j = 0; j < words.get(i).size(); j++){
                s = new String(words.get(i).get(j));
                //if quotes close on single token
                if(s.startsWith("\'") && s.endsWith("\'")){
                    s = s.substring(1, s.length()-1);
                    words.get(i).set(j, s);
                }
                else if(s.startsWith("\'") && quotePlace == -1){
                    quotePlace = j;
                }
                else if(s.endsWith("\'") && quotePlace != -1){
                    s2 = new String(words.get(i).get(quotePlace));
                    s = s2+" "+s;
                    s = s.substring(1, s.length()-1);
                    words.get(i).set(quotePlace, s);
                    words.get(i).remove(j);
                    j--;
                    quotePlace = -1;
                }
            }
            if(quotePlace != -1){
                throw new IllegalArgumentException("a quote was not closed");
            }
        }
        for(int i = 0; i < words.size(); i++){
            if(words.get(i).get(0).equals("CREATE")){
                //if there are an odd # of tokens and size is >= 7
                if((words.size() & 1) == 1 && words.get(i).size() >= 7){
                    createCheck(words.get(i));
                }
                else{
                    throw new IllegalArgumentException("invalid CREATE length");
                }
            }
            else if(words.get(i).get(0).equals("INSERT")){
                //if size of command is >= 7
                if(words.get(i).size() >= 7){
                    insertCheck(words.get(i));
                }
                else{
                    throw new IllegalArgumentException("invalid INSERT length");
                }
            }
            else if(words.get(i).get(0).equals("DELETE")){
                //deleteCheck(words.get(i);
            }else{
                throw new IllegalArgumentException("\""+words.get(i).get(0)+"\'"
                        + " is not a valid command");
            }
        }
    }
    public static void insertCheck(ArrayList<String> word){
        String attName = new String();
        String attType = new String();
        String tableName = new String();
        ArrayList<Attribute> columns = null;
        ArrayList<Field> record = new ArrayList<Field>();
        ArrayList<Field> record2 = new ArrayList<Field>();
        Attribute a = null;
        int tI = 0;  //index for current table insertion will be occuring in
        int wP = 0;  //keeps track of word place in command
        boolean allCol = false; //flags whether we're specifying columns or not
        
        //checks if second word is INTO
        if(!word.get(1).equals("INTO")){
            throw new IllegalArgumentException("invalid command \""+
                    word.get(1)+"\"");
        }
        //checks if 3rd word is a command instead of a table name
        if(isCommand(word.get(2))){
            throw new IllegalArgumentException("command cannot be table name"); 
        }else{  //search for table with name from Tables
            tableName = word.get(2);
            boolean found = false;
            for(int i = 0; i < Tables.size() && !found; i++){
                if(Tables.get(i).getName().equals(tableName)){
                    found = true;
                    tI = i;
                }
            }
            if(!found){
                throw new IllegalArgumentException("table name was not found");
            }
        }
        //check for VALUES or column-name listing
        if(word.get(3).equals("VALUES")){
            allCol = true;
            wP = 4; //move index over to the values' open parenthesis
        }
        else if(word.get(3).equals("(")){
            //check if the next column values aren't commands
            //if they pass, then add them to the column list
            columns = new ArrayList<Attribute>();
            int size = word.size()-4; //because of future ),VALUES,(,)
            for(int i = 4; i < size-1; i++){
                //break out of loop when closing brace is found
                if(word.get(i).equals(")")){
                    break;
                }
                attName = word.get(i);
                //Under Construction----------------------------------------------
                a = new Attribute(attName, attType);
                columns.add(a);
                size--;     //because we will expect a future associated value
                wP = i+1;  //will have us prepared with the next word during the next trail
            }
        }else{
            throw new IllegalArgumentException("should be VALUES or open parenthesis");
        }
        //check for opening parenthesis if user didn't select column names
        if(!word.get(wP).equals("(") && allCol){
            throw new IllegalArgumentException("missing opening parenthesis");
        }else{
            wP++;
        }
        //check for closing parenthesis; otherwise, if user selected column names move index forward
        if(!word.get(wP).equals(")") && !allCol){
            throw new IllegalArgumentException("missing close parenthesis");
        }
        else if(!allCol){
            wP++;
        }
        //check for word VALUE if allCol is false
        if(!word.get(wP).equals("VALUES") && !allCol){
            throw new IllegalArgumentException("missing VALUES command");
        }
        else if(!allCol){
            wP++;
        }
        //start saving values into list
        for(; wP < word.size()-1; wP++){
            //throw exception if word is a command
            if(isCommand(word.get(wP))){
                throw new IllegalArgumentException("invalid column name or type");
            }
            Field f = new Field(word.get(wP));
            record.add(f);
        }
        //check for closing parenthesis
        if(!word.get(wP).equals(")")){
            throw new IllegalArgumentException("missing close parenthesis");
        }
        //check if record data types are compatible with table column types
        if(allCol){ //if there were no user selected columns
            columns = Tables.get(tI).getAttributes();
            Field b = new Field();
            //check if there are as many values as columns
            if(record.size() != columns.size()){
                throw new IllegalArgumentException("column and value size don't match");
            }
            //see if there is an attribute missmatch
            for(int i = 0; i < columns.size(); i++){
                b.setField(record.get(i));  //Field sets type based off of value string
                if(!columns.get(i).hasType(b.getType())){ //if attribute is not same type as field
                    throw new IllegalArgumentException("invalid column type: "
                            +record.get(i));
                }
            }   
        }else{      //user selected columns case
            ArrayList<Attribute> tableCols = Tables.get(tI).getAttributes();
            int c = 0;  //tableCols index #
            Field b = new Field();
            //check if there are as many values as columns
            if(record.size() == columns.size()){
                throw new IllegalArgumentException("column #s and value #s don't match");
            }
            //check if attributes in table (in serated, linear order)
            int leftInTab = tableCols.size(); //number of items left to search in lists
            int leftInCol = columns.size()-c;
            for(int i = 0; i < tableCols.size() && c < columns.size(); i++){
                if(tableCols.get(i).getType().equals(columns.get(c))){
                    //do stuff
                    c++;    //move to next in user-selected columns
                }else{ //if attribute types don't match
                    record2.add(new Field(record.get(i)));
                }
                leftInTab--;
                if(leftInTab < leftInCol){  //finds if remaining columns can be checked
                    throw new IllegalArgumentException("some columns wont be found in table");
                }
            }
        }
        //add record to table
        if(record2.isEmpty()){
            Field f = new Field(record.get(0));
            Tables.get(tI).insertRecord(record);
        }else{
            Tables.get(tI).insertRecord(record2);
        }
    }
    
    public static void createCheck(ArrayList<String> word){
        String tableName = new String();
        String attName = new String();
        String attType = new String();
        ArrayList<Attribute> columns = new ArrayList<Attribute>();
        Attribute a;
        
        //checks if second word is TABLE
        if(!word.get(1).equals("TABLE")){
            throw new IllegalArgumentException("invalid command \""+
                    word.get(1)+"\"");
        }
        
        //checks if 3rd word is a command instead of a table name
        if(isCommand(word.get(2))){
            throw new IllegalArgumentException("command cannot be table name"); 
        }
        tableName = word.get(2);
        
        //check if open parenthesis isn't in correct location
        if(!word.get(3).equals("(")){
            throw new IllegalArgumentException("missing open parenthesis");
        }
        
        //check if the next two column values aren't commands
        //if they pass, then add them to the column list
        for(int i = 4; i < word.size()-1; i++){
            //throw excpetion if word is a command
            if(isCommand(word.get(i))){
                throw new IllegalArgumentException("invalid column name or type");
            } //for even word, check if it's closed in single quotes
            if((i & 1) == 0){   //check low bit for even-ness
                attName = word.get(i); //remove single quotes
            }
            else{
                attType = word.get(i);
                a = new Attribute(attName, attType);
                columns.add(a);
            }
        }
        //check for closing parenthesis
        if(!word.get(word.size()-1).equals(")")){
            throw new IllegalArgumentException("missing close parenthesis");
        }
        
        Tables.add(new Table(tableName, columns));
    }
    
    public static void displayTable(Table table){
        Attribute a;
        System.out.println("Table name: "+table.getName());
        for(int i = 0; i < table.getNumAttributes(); i++){
            a = table.getAttributes().get(i);
            System.out.format("%-12s",a.getName());
            if(i != table.getNumAttributes()-1)
                System.out.format("%-9s","|");
        }
        System.out.println();
        for(int i = 0; i < table.getNumAttributes(); i++){
            System.out.print("-----------------");
        }
        System.out.println();
        for(int r = 0; r < table.getNumRecords(); r++){
            for(int c = 0; c < table.getNumAttributes(); c++){
                ArrayList<Field> f = table.getRecord(r);
                System.out.format("%-21s",f.get(c).getValue());
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public static boolean isCommand(String str){
        if(str.matches("CREATE|TABLE|UPDATE|SET|WHERE|SELECT|FROM|"
                + "DELETE|INSERT|INTO|VALUES|DROP")){
            return true;
        }
        return false;
    }
    
    public static boolean isFloat(String s){
        try {
            Float.parseFloat(s);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
    
    public static void main(String[] args) {
        Tables = new ArrayList<Table>();
        String s = "create table friends("
                + "\'name\' string "
                + "\'hobby\' string "
                + "\'birthdate\' date "
                + "\'age\' float);";
        String s2 = "insert into friends values (\'Jane Kang\',\'guitar\',\'14-3-1989\',25);";
        String s3 = "insert into friends values (\'Mr. Burns\',\'lute\',\'16-6-1900\',114);"
                + "insert into friends values (\'King Soandso\',\'clavicord\',\'01-5-1877\',42);";
        String s4 = "insert into friends (name,hobby) values (\'Echo\',\'flute\');";
        parseString(s);
        displayTable(Tables.get(0));
        parseString(s2);
        displayTable(Tables.get(0));
        parseString(s3);
        displayTable(Tables.get(0));
        parseString(s4);
        displayTable(Tables.get(0));
    }
    
}
