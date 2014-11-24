/*
 * Testing
 */

package database_mgmt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class BiblioBaseDBMS {

    public static ArrayList<Table> Tables;
    
    public static void parseString(String str){
        ArrayList<ArrayList<String>> words= new ArrayList<>();
        str = str.toUpperCase();
        String[] command = str.split("[;]");
        for(int i = 0; i < command.length; i++){
            String[] temp = command[i].split("[ ,]|"
                    + "((?<=\\()|(?=\\()|(?<=\\))|(?=\\)))");
            if(temp.length <= 2){
                throw new IllegalArgumentException("invalid command length");
            }
            //tokenizes each command by word into a 2D ArrayList
            words.add(new ArrayList<>(Arrays.asList(temp)));
            words.get(i).removeAll(Arrays.asList("", null));
            //find quotes and merge
            int quotePlace = -1;
            String s, s2;
            for(int j = 0; j < words.get(i).size(); j++){
                s = words.get(i).get(j);
                //if quotes close on single token
                if(s.startsWith("\'") && s.endsWith("\'")){
                    s = s.substring(1, s.length()-1);
                    words.get(i).set(j, s);
                }
                else if(s.startsWith("\'") && quotePlace == -1){
                    quotePlace = j;
                }
                else if(s.endsWith("\'") && quotePlace != -1){
                    s2 = words.get(i).get(quotePlace);
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
        for (ArrayList<String> word : words) {
            switch (word.get(0)) {
                case "CREATE":
                    //if there are an odd # of tokens and size is >= 7
                    if ((word.size() & 1) == 1 && word.size() >= 7) {
                        createCheck(word);
                    } else {
                        throw new IllegalArgumentException("invalid CREATE command length");
                    }   break;
                case "INSERT":
                    //if size of command is >= 7
                    if (word.size() >= 7) {
                        insertCheck(word);
                    } else {
                        throw new IllegalArgumentException("invalid INSERT command length");
                }   break;
                case "DELETE":
                    //if size of command is < 3 words, the command is invalid
                    if (word.size() >= 3) {
                        deleteCheck(word);
                    } else {
                        throw new IllegalArgumentException("invalid DELETE command length");
                    }   break;
                default:
                    throw new IllegalArgumentException("\"" + word.get(0) + "\'" + " is not a valid command");
            }
        }
    }
    
    public static void deleteCheck(ArrayList<String> word){
        //--------------------------------UNDER CONSTRUCTION------------------------------------
        String tableName = new String();
        int tI = 0;  //index for current table insertion will be occuring in
        
        //checks if second word is FROM
        if(!word.get(1).equals("FROM")){
            throw new IllegalArgumentException("invalid command \""+
                    word.get(1)+"\"");
        }
        //checks if 3rd word is a command instead of a table name
        if(isCommand(word.get(2))){
            throw new IllegalArgumentException("command cannot be table name"); 
        }else{  //search for table with name from Tables
            tI = tableSearch(word.get(2));
        }
        //checks if the command is only 3 words long, if so delete all records.
        if(word.size() == 3){
            int size = Tables.get(tI).getNumRecords();
            for(int i = 0; i < size; i++){
                Tables.get(tI).deleteRecord(0);
            }
            return; //exit function
        }
        //checks if there is a where command
        if(!word.get(3).equals("WHERE")){
            throw new IllegalArgumentException("invalid command \""+
                    word.get(3)+"\"");
        }else{
            
        }
        
    }
    
    public static ArrayList<Field> where(ArrayList<String> word, int tI){
        //----------------------------UNDER CONSTRUCTION-----------------------------------
        //handles multiple operations and returns concatenated fields
        return null;
    }
    
    public static ArrayList<Field> operation(String colName, String operater, String value, int tI){
        //----------------------------UNDER CONSTRUCTION-----------------------------------
        //does operation and returns relevant fields
        return null;
    }
    
    public static void insertCheck(ArrayList<String> word){
        String attName = new String();
        String attType = new String();
        String tableName = new String();
        ArrayList<Attribute> columns = null;
        ArrayList<String> colNames = null;
        ArrayList<Field> record = new ArrayList<>();
        ArrayList<Field> record2 = new ArrayList<>();
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
            tI = tableSearch(word.get(2));
        }
        //check for VALUES or column-name listing
        switch (word.get(3)) {
            case "VALUES":
                allCol = true;
                wP = 4; //move index over to the values' open parenthesis
                break;
            case "(":
                //check if the next column values aren't commands
                //if they pass, then add them to the column list
                colNames = new ArrayList<>();
                int size = word.size();
                for(int i = 4; i < size-1; i++){
                    //break out of loop when closing brace is found
                    if(word.get(i).equals(")")){
                        if(i == 5){
                            throw new IllegalArgumentException("no columns selected");
                        }else{
                            break;
                        }
                    }
                    else if(isCommand(word.get(i))){
                        throw new IllegalArgumentException("column name cannot be a command word");
                    }
                    attName = word.get(i);
                    colNames.add(attName);
                    size--;     //because we will expect a future associated value
                    wP = i+1;  //will have us prepared with the next word during the next trail
                }   //check if loop didn't have inside columns
                if(colNames.isEmpty()){
                    throw new IllegalArgumentException("no columns selected--2");
                }   break;
            default:
                throw new IllegalArgumentException("token should be VALUES or open parenthesis");
        }
        //check for opening parenthesis if user didn't select column names
        if(!word.get(wP).equals("(") && allCol){
            throw new IllegalArgumentException("missing opening parenthesis");
        }else if(allCol){
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
            wP+=2;  //move index to first value
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
        if(wP >= word.size() || !word.get(wP).equals(")")){
            throw new IllegalArgumentException("missing close parenthesis or incorrect command");
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
            columns = Tables.get(tI).getAttributes();
            int c = 0;  //colNames index #
            Field b = new Field();
            //check if there are as many values as columns chosen
            if(record.size() != colNames.size()){
                throw new IllegalArgumentException("column #s and value #s don't match");
            }
            //check if attributes in table (in serated, linear order)
            int leftInTab = columns.size(); //number of items left to search in lists
            int leftInCol = colNames.size()-c;
            
            for (Attribute column : columns) {
                if (c < colNames.size() && column.getName().equals(colNames.get(c))) {
                    record2.add(new Field(record.get(c)));
                    leftInCol--;    //move to next in user-selected columns
                    c++;
                } //if attributes don't match
                else {
                    record2.add(new Field());
                }
                leftInTab--;
                if(leftInTab < leftInCol){  //finds if remaining columns can be checked
                    throw new IllegalArgumentException("some columns wont be found in table");
                }
            }
        }
        //add record to table
        if(allCol){
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
        ArrayList<Attribute> columns = new ArrayList<>();
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
    
    public static int tableSearch(String tableName){
        //------------update at a later time for a binary search over sorted names------------------
        boolean found = false;
        int tI = 0; //index for table
            for(int i = 0; i < Tables.size() && !found; i++){
                if(Tables.get(i).getName().equals(tableName)){
                    found = true;
                    tI = i;
                }
            }
            if(!found){
                throw new IllegalArgumentException("table name was not found");
            }
        return tI;
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
        return str.matches("CREATE|TABLE|UPDATE|SET|WHERE|SELECT|FROM|"
                + "DELETE|INSERT|INTO|VALUES|DROP");
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
        Tables = new ArrayList<>();
/*----artificial data--------       
create table friends('name' string 'hobby' string 'birthdate' date 'age' float);
insert into friends values ('Jane Kang','guitar','14-3-1989',25);
insert into friends values ('Mr. Burns','lute','16-6-1900',114);
insert into friends values ('King Soandso','clavicord','01-5-1877',42);
insert into friends (name,hobby) values ('echo','guitar');
delete from friends;
*/
        Scanner sc = new Scanner(System.in);
        String s = new String();
        while(true){
            String temp = sc.nextLine();
            if(temp.equals("")){
                break;
            }
            s+=temp;
        }
        
        parseString(s);
        displayTable(Tables.get(0));
        sc.close();
    }
    
}
