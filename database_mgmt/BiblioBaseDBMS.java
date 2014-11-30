/*
 * Testing
 */

package database_mgmt;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class BiblioBaseDBMS {

    static ArrayList<Table> TABLES;
    static String USERNAME;
    static boolean MAXLOGIN;
    
    static void getFile(){
        String s = new String();
        Scanner inputF;
        InputStream in = BiblioBaseDBMS.class.getResourceAsStream("tables.txt");
        inputF = new Scanner(in);
        
        while(inputF.hasNextLine())
            s = inputF.nextLine();
        
        System.out.println(s);
        inputF.close();
    }
    
    static void parseString(String str){
        ArrayList<ArrayList<String>> words= new ArrayList<>();
        str = str.toUpperCase();
        String[] command = str.split("[;]");
        for(int i = 0; i < command.length; i++){
            String[] temp = command[i].split("[ ,]|"
                    + "((?<=\\()|(?=\\()|(?<=\\))|(?=\\)))");
            if(temp.length < 1){
                throw new IllegalArgumentException("ERROR: empty command sent to parser");
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
                throw new IllegalArgumentException("ERROR: a quote was not closed");
            }
        }
        for (ArrayList<String> word : words){
            if(word.size() < 1){
                throw new IllegalArgumentException("ERROR: empty command sent to parser --2");
            }
            switch (word.get(0)){
                case "CREATE":
                    //if there are an odd # of tokens and size is >= 7
                    if((word.size() & 1) == 1 && word.size() >= 7){
                        createCheck(word);
                    } else {
                        throw new IllegalArgumentException("ERROR: invalid CREATE command length");
                    }   break;
                case "INSERT":
                    //if size of command is >= 7
                    if(word.size() >= 7){
                        insertCheck(word);
                    } else {
                        throw new IllegalArgumentException("ERROR: invalid INSERT command length");
                    }   break;
                case "DELETE":
                    //if size of command is < 3 words, the command is invalid
                    if(word.size() >= 3){
                        deleteCheck(word);
                    } else {
                        throw new IllegalArgumentException("ERROR: invalid DELETE command length");
                    }   break;
                case "SELECT":
                    if(word.size() >= 4){
                        selectCheck(word);
                    } else {
                        throw new IllegalArgumentException("ERROR: invalid SELECT command length");
                    }   break;
                case "UPDATE":
                    if(word.size() >= 6){
                        updateCheck(word);
                    } else {
                        throw new IllegalArgumentException("ERROR: invalid UPDATE command length");
                    }   break;
                case "EXIT":
                    break;
                default:
                    throw new IllegalArgumentException("\"" + word.get(0) + "\"" + " is not a valid command");
            }
        }
    }
    
    static void updateCheck(ArrayList<String> word){
        String tableName = new String();
        String attName = new String();
        String attType = new String();
        int tI = 0;  //index for current table insertion will be occuring in
        int wP = 0;  //keeps track of word place in command
        ArrayList<ArrayList<Field>> records = TABLES.get(tI).getRecords();
        ArrayList<Attribute> columns = new ArrayList<>();
        ArrayList<Field> values = new ArrayList<>();
        ArrayList<Integer> colIdx = new ArrayList<>();
        ArrayList<Integer> I = null;
        
        //check if second word is a command instead of a table name
        wP++;
        if(isCommand(word.get(wP))){
            throw new IllegalArgumentException("ERROR: command cannot be table name"); 
        }else{  //search for table with name from Tables
            tI = tableSearch(word.get(wP));
        }
        //check if third word is SET
        wP++;
        if(!word.get(wP).equals("SET")){
            throw new IllegalArgumentException("\""+word.get(wP)+"\""
                    + " is not SET");
        }
        //retrieve column names and values
        wP++;
        for(int i = wP; i < word.size(); i++){
            Field f;
            Attribute a;
            
            if(i % 3 == 0 && word.get(i).equals("WHERE")){
                break;
            }else if(word.get(i).equals("WHERE")){
                throw new IllegalArgumentException("ERROR: WHERE word is in "
                        + " invalid position");
            }
            //conduct tests
            if(i % 3 == 0 && !isValue(word.get(i))){
                throw new IllegalArgumentException("ERROR: \""+word.get(i)+"\""
                 + " has to be a value, not a command or operator");
            }
            if(i % 3 == 0 && i+2 < word.size()){
                String type = TABLES.get(tI).getAttType(word.get(i));
                int idx = TABLES.get(tI).getAttributeIdx(word.get(i));
                f = new Field(word.get(i+2));
                a = new Attribute(word.get(i), type);
                //check if equal sign is available
                if(!word.get(i+1).equals("=")){
                    throw new IllegalArgumentException("ERROR: equal sign not present"
                            + " in SET clause");
                }
                //check is column and value are same type
                if(!isValue(word.get(i+2)) || !f.getType().equals(a.getType())){
                    throw new IllegalArgumentException("ERROR: column and value"
                            + " types do no match in SET clause");
                }
                columns.add(a);
                colIdx.add(idx);
                values.add(f);
            }else if(i % 3 == 0){
                throw new IllegalArgumentException("ERROR: SET column-value information"
                        + " format is incorrect");
            }
            wP++;
        }
        //check if there is no where clause, if not use update all
        if(wP == word.size()){
            for(int i = records.size()-1; i > -1; i--){
                for(int j = colIdx.size()-1; j > -1; j--){
                    TABLES.get(tI).updateRecord(i, colIdx.get(j), values.get(j));
                }  
            }
            return; //exit function
        }
        //check if WHERE is next word
        if(!word.get(wP).equals("WHERE")){
            throw new IllegalArgumentException("ERROR: The WHERE clause is not in "
                    + "the correct location");
        }else{
            wP++;
            I = where(word, tI, wP);
            for(int i = 0; i < I.size(); i++){
                int row = I.get(i);
                for(int j = 0; j < colIdx.size(); j++){
                    int col = colIdx.get(j);
                    TABLES.get(tI).updateRecord(row, col, values.get(j));
                }
            }
        }
    }
    
        static void selectCheck(ArrayList<String> word){
        String tableName = new String();
        String attName = new String();
        String attType = new String();
        int tI = 0;  //index for current table insertion will be occuring in
        int wP = 0;  //keeps track of word place in command
        ArrayList<String> colNames = null;
        ArrayList<ArrayList<Field>> records = TABLES.get(tI).getRecords();
        ArrayList<Attribute> columns = null;
        ArrayList<Integer> colIdx = null;
        ArrayList<Integer> I = null;
        boolean selectAll = false;
        
        //check if selecting all columns
        wP++;
        if(word.get(wP).equals("*")){
            selectAll = true;
        }else{  //check for column names
            colNames = new ArrayList<>();
            int size = word.size();
            for(int i = wP; i < size; i++){
                //break out of loop when FROM word is found
                if(word.get(i).equals("FROM")){
                    if(i == 2){
                        throw new IllegalArgumentException("ERROR: no columns selected");
                    }else{
                        break;
                    }
                }
                else if(isCommand(word.get(i))){
                    throw new IllegalArgumentException("ERROR: column name cannot be a command word");
                }
                attName = word.get(i);
                colNames.add(attName);
                wP = i;
            }   //check if loop didn't have inside columns
            if(colNames.isEmpty()){
                throw new IllegalArgumentException("ERROR: no columns selected--2");
            }
        }
        //check from FROM word
        wP++;
        if(!word.get(wP).equals("FROM")){
            throw new IllegalArgumentException("ERROR: no FROM word detected");
        }
        //checks if 3rd word is a command instead of a table name
        wP++;
        if(isCommand(word.get(wP))){
            throw new IllegalArgumentException("ERROR: command cannot be table name"); 
        }else{  //search for table with name from Tables
            tI = tableSearch(word.get(wP));
        }
        //check if there are no extra words
        wP++;
        if(wP == word.size()){
            displayTable(TABLES.get(tI));   //execute basic select display
            return;
        }
        //get attributes and attribute indexes
        if(selectAll){
            columns = TABLES.get(tI).getAttributes();
        }else{
            columns = new ArrayList<Attribute>();
            colIdx = new ArrayList<Integer>();
            for(String col : colNames){
                columns.add(TABLES.get(tI).getAttribute(col));
                colIdx.add(TABLES.get(tI).getAttributeIdx(col));
            }
        }      
        //check if the next word is not WHERE
        ArrayList<ArrayList<Field>> recordsCrop = new ArrayList<>();
        Table t = null;
        if(!word.get(wP).equals("WHERE")){
            throw new IllegalArgumentException("ERROR: word should be WHERE");
        }
        //create tables
        wP++;
        if(!selectAll){  //if user selected all columns
            I = where(word, tI, wP);
            String a;
            ArrayList<Field> record = null;
            //get records
            for(int i = 0; i < I.size(); i++){
                int row = I.get(i);
                record = new ArrayList<>();
                for(int j = 0; j < colIdx.size(); j++){
                    record.add(records.get(row).get(colIdx.get(j)));
                }
                recordsCrop.add(record);
            }
            //create table 
            t = new Table(TABLES.get(tI).getName(), columns, recordsCrop);
        }else{
            //get records
            I = where(word, tI, wP);
            for(int i = 0; i < I.size(); i++){
                int idx = I.get(i);
                recordsCrop.add(records.get(idx));
            }
            //create table
            t = new Table(TABLES.get(tI).getName(), columns, recordsCrop);
        }
        displayTable(t);
    }
    
    static void deleteCheck(ArrayList<String> word){
        String tableName = new String();
        int tI = 0;  //index for current table insertion will be occuring in
        ArrayList<Integer> I = null;
        
        //checks if second word is FROM
        if(!word.get(1).equals("FROM")){
            throw new IllegalArgumentException("ERROR: invalid command \""+
                    word.get(1)+"\"");
        }
        //checks if 3rd word is a command instead of a table name
        if(isCommand(word.get(2))){
            throw new IllegalArgumentException("ERROR: command cannot be table name"); 
        }else{  //search for table with name from Tables
            tI = tableSearch(word.get(2));
        }
        //checks if the command is only 3 words long, if so delete all records.
        if(word.size() == 3){
            int size = TABLES.get(tI).getNumRecords();
            for(int i = 0; i < size; i++){
                TABLES.get(tI).deleteRecord(0);
            }
            return; //exit function
        }
        //checks if there is a where command
        if(!word.get(3).equals("WHERE")){
            throw new IllegalArgumentException("ERROR: invalid command \""+
                    word.get(3)+"\"");
        }else{
            I = where(word, tI, 4);
            for(int k = I.size()-1; k > -1; k--){
                TABLES.get(tI).deleteRecord(I.get(k));
            }
        }
        
    }
    
    static Set<Integer> operateOnRecords(int tI, List<String> tok, 
            ArrayList<ArrayList<Field>> records){
        //initialize variables
        Set<Integer> I = new HashSet<Integer>();
        ArrayList<Field> F = new ArrayList<>();     //subject to removal
        int attIdx = 0;
        String currentColType = null;
        
        //perform operations
        attIdx = TABLES.get(tI).getAttributeIdx(tok.get(0));
        currentColType = TABLES.get(tI).getAttributes().get(attIdx).getType();
        int j = 0;
        Iterator<ArrayList<Field>> it = TABLES.get(tI).getRecords().iterator();
        while(it.hasNext()){
            Field val = new Field(tok.get(2), currentColType);
            ArrayList<Field> rec = it.next();
            //ignore occurances where the column as a null entree but query value is not null
            if(rec.get(attIdx).getValue() == null && val.getValue() != null){
                //ignore
            }
            //compare number field
            else if(val.getType().equals("FLOAT") && 
                    ( (tok.get(1).equals("=") && 
                        rec.get(attIdx).compareFields(val,"=")) ||
                    (tok.get(1).equals("!=") && 
                        rec.get(attIdx).compareFields(val,"!=")) ||
                    (tok.get(1).equals("<") && 
                        rec.get(attIdx).compareFields(val,"<")) || 
                    (tok.get(1).equals(">") && 
                        rec.get(attIdx).compareFields(val,">")) ||
                    (tok.get(1).equals("<=") && 
                        rec.get(attIdx).compareFields(val,"<=")) ||
                    (tok.get(1).equals(">=") && 
                        rec.get(attIdx).compareFields(val,">=")) ) ){
                F.add(rec.get(attIdx));
                I.add(j);
            }
            //compare string fields
            else if(val.getType().equals("STRING") &&
                    ( (tok.get(1).equals("=") &&
                        rec.get(attIdx).isEqual(val)) ||
                    (tok.get(1).equals("!=") &&
                        !rec.get(attIdx).isEqual(val)) ) ){
                F.add(rec.get(attIdx));
                I.add(j);
            }
            //compare date fields
            else if(val.getType().equals("DATE") &&
                    ( (tok.get(1).equals("=") &&
                        rec.get(attIdx).isEqual(val)) ||
                    (tok.get(1).equals("!=") &&
                        !rec.get(attIdx).isEqual(val)) ||
                    (tok.get(1).equals("<") && 
                        rec.get(attIdx).compareFields(val,"<")) || 
                    (tok.get(1).equals(">") && 
                        rec.get(attIdx).compareFields(val,">")) ||
                    (tok.get(1).equals("<=") && 
                        rec.get(attIdx).compareFields(val,"<=")) ||
                    (tok.get(1).equals(">=") && 
                        rec.get(attIdx).compareFields(val,">=")) ) ){
                F.add(rec.get(attIdx));
                I.add(j);
            }
            j++;
        }
        return I;
    }
    
    static void AND(ArrayList<String> word, int w, int tI, 
            Set<Integer> s, boolean reParse){
        //initialize variables
        Set<Integer> set1 = new HashSet<Integer>();
        Set<Integer> set2 = new HashSet<Integer>();
        ArrayList<String> tok = new ArrayList<>();   //stores operation tokens 
        ArrayList<ArrayList<Field>> records = TABLES.get(tI).getRecords();
        int attIdx = 0;

        //store two operations and a logic operator
        for(int i = 0; i < 7; i++){
            if(i != 3)  //do not store logic operator
                tok.add(word.get(w+i-3));  
        }
        //perform operations
        List<String> temp = tok.subList(0, 3);
        set1 = operateOnRecords(tI, temp, records);
        temp = tok.subList(3, 6);
        set2 = operateOnRecords(tI, temp, records);
        if(reParse){            //3-way intersection
            set1.retainAll(set2);
            s.retainAll(set1);
        }
        else{                   //2-way intersection
            set1.retainAll(set2);
            s.addAll(set1);     //union previous records with current
        }
    }
    
    static void OR(int tI, Set<Integer> s, ArrayList<String> tok){
        //initialize variables
        Set<Integer> set = new HashSet<>(); 
        ArrayList<ArrayList<Field>> records = TABLES.get(tI).getRecords();

        //perform OR operations
        records = TABLES.get(tI).getRecords();
        List<String> temp;
        for(int i = 0; i < tok.size(); i+=3){ //i+=3 will parse operations in tok
            temp = tok.subList(i, i+3);
            set.addAll(operateOnRecords(tI, temp, records));
        }
        //merge with s
        s.addAll(set);
    }
    
    static ArrayList<Integer> where(ArrayList<String> word, int tI, 
            int wP){
        //handles multiple operations and returns concatenated fields
        int place = 1;
        int attIdx = 0;
        String colName = null;
        String operator = null;
        String value = null;
        ArrayList<String> tok = new ArrayList<>();
        ArrayList<ArrayList<Field>> records = null;
        ArrayList<Field> F = new ArrayList<>();     //subject to removal
        ArrayList<Integer> I = new ArrayList<>();
        Set<Integer> set = new HashSet<Integer>();
        boolean reParse = false;
        
        //test entrees, execute AND operations, store OR operations
        for(int i = wP; i < word.size(); i++){
            if(place > 4){  //col = val (only groups of four at a time)
                place = 1;
            }
            //set operation components
            if(place == 1){ 
                colName = word.get(i);
            }
            else if(place == 2){
                operator = word.get(i);
            }
            else if(place == 3){
                value = word.get(i);
                //if we're at the end of the command and format is correct
                if(i == word.size()-1 && !isLogicOp(word.get(i)) &&
                        !TABLES.get(tI).nameInAttributes(word.get(i))){
                    tok.add(colName); //add column name
                    tok.add(operator); //add operator
                    tok.add(value); //add value
                }
                else if(i == word.size()-1){
                    throw new IllegalArgumentException("ERROR: invalid formatting for "
                            + "last operation in where clause");
                }
            }
            else if(place == 4){ //test and store string entrees
                if(!isLogicOp(word.get(i))){
                    throw new IllegalArgumentException("ERROR: \""+word.get(i)+
                            "\" should be a logical operator");
                }
                if(!TABLES.get(tI).nameInAttributes(colName)){
                    throw new IllegalArgumentException("ERROR: column name \"" +
                            colName + "\" not in table");
                }
                if(!isOperator(operator)){
                    throw new IllegalArgumentException("ERROR: "+operator
                            + " is an invalid operator");
                }
                if(isCommand(value)){
                    throw new IllegalArgumentException("ERROR: "+value+"is a command"
                            + " word, not a valid value");
                } 
                //store OR operations
                if(word.get(i).equals("OR")){
                    if(colName != null && operator != null && value != null){
                        tok.add(colName); //add column name
                        tok.add(operator); //add operator
                        tok.add(value); //add value
                    }
                }
                else{   //word must be "AND"
                    colName = null; //set so OR wont add to tok
                    operator = null;
                    value = null;
                    if(!reParse){
                        //test next 4 tokens and check if next logical op is AND
                        if(i+4 < word.size() && !isLogicOp(word.get(i+4))){
                            throw new IllegalArgumentException("ERROR: "+word.get(i+4)+
                                    " is an invalid operator");
                        }
                        else if(!TABLES.get(tI).nameInAttributes(word.get(i+1))){
                            throw new IllegalArgumentException("ERROR: column name \"" +
                                    word.get(i+1) + "\" not in table");
                        }
                        else if(!isOperator(word.get(i+2))){
                            throw new IllegalArgumentException("ERROR: "+word.get(i+2)
                                    + " is an invalid operator");
                        }
                        else if(isCommand(word.get(i+3))){
                            throw new IllegalArgumentException("ERROR: "+word.get(i+3)+
                                    "is a command word, not a valid value");
                        } 
                        
                        AND(word, i, tI, set, false);
                        
                        if(i+4 < word.size() && word.get(i+4).equals("AND")){
                            reParse = true;
                            i+=3; //3 + i++ = 4 (jump to next operator
                            place--;    //we're moving to the same place -compensate
                        }
                        else if(i+4 >= word.size()){
                            break;
                        }
                    }
                    else{
                        AND(word, i, tI, set, true);
                        reParse = false;
                    }
                }
            }
            place++;
        }
        if(place != 4){
            throw new IllegalArgumentException("ERROR: incomplete entrees in WHERE"
                    + " clause");
        }
        //perform OR operations
        records = TABLES.get(tI).getRecords();
        OR(tI, set, tok);
        
        //return list of sorted row indexes
        I = new ArrayList(set); 
        Collections.sort(I);    //mergesort list of row indexes
        return I;
    }
    
    static void insertCheck(ArrayList<String> word){
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
            throw new IllegalArgumentException("ERROR: invalid command \""+
                    word.get(1)+"\"");
        }
        //checks if 3rd word is a command instead of a table name
        if(isCommand(word.get(2))){
            throw new IllegalArgumentException("ERROR: command cannot be table name"); 
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
                            throw new IllegalArgumentException("ERROR: no columns selected");
                        }else{
                            break;
                        }
                    }
                    else if(isCommand(word.get(i))){
                        throw new IllegalArgumentException("ERROR: column name cannot be a command word");
                    }
                    attName = word.get(i);
                    colNames.add(attName);
                    size--;     //because we will expect a future associated value
                    wP = i+1;  //will have us prepared with the next word during the next trail
                }   //check if loop didn't have inside columns
                if(colNames.isEmpty()){
                    throw new IllegalArgumentException("ERROR: no columns selected--2");
                }   break;
            default:
                throw new IllegalArgumentException("ERROR: token should be VALUES or open parenthesis");
        }
        //check for opening parenthesis if user didn't select column names
        if(!word.get(wP).equals("(") && allCol){
            throw new IllegalArgumentException("ERROR: missing opening parenthesis");
        }else if(allCol){
            wP++;
        }
        //check for closing parenthesis; otherwise, if user selected column names move index forward
        if(!word.get(wP).equals(")") && !allCol){
            throw new IllegalArgumentException("ERROR: missing close parenthesis");
        }
        else if(!allCol){
            wP++;
        }
        //check for word VALUE if allCol is false
        if(!word.get(wP).equals("VALUES") && !allCol){
            throw new IllegalArgumentException("ERROR: missing VALUES command");
        }
        else if(!allCol){
            wP+=2;  //move index to first value
        }
        //start saving values into list
        for(; wP < word.size()-1; wP++){
            //throw exception if word is a command
            if(isCommand(word.get(wP))){
                throw new IllegalArgumentException("ERROR: invalid column name or type");
            }
            Field f = new Field(word.get(wP));
            record.add(f);
        }
        //check for closing parenthesis
        if(wP >= word.size() || !word.get(wP).equals(")")){
            throw new IllegalArgumentException("ERROR: missing close parenthesis or incorrect command");
        }
        //check if record data types are compatible with table column types
        if(allCol){ //if there were no user selected columns
            columns = TABLES.get(tI).getAttributes();
            Field b = new Field();
            //check if there are as many values as columns
            if(record.size() != columns.size()){
                throw new IllegalArgumentException("ERROR: column and value size don't match");
            }
            //see if there is an attribute missmatch
            for(int i = 0; i < columns.size(); i++){
                b.setField(record.get(i));  //Field sets type based off of value string
                if(!columns.get(i).hasType(b.getType())){ //if attribute is not same type as field
                    throw new IllegalArgumentException("ERROR: invalid column type: "
                            +record.get(i));
                }
            }   
        }else{      //user selected columns case
            columns = TABLES.get(tI).getAttributes();
            int c = 0;  //colNames index #
            Field b = new Field();
            //check if there are as many values as columns chosen
            if(record.size() != colNames.size()){
                throw new IllegalArgumentException("ERROR: column #s and value #s don't match");
            }
            //check if attributes in table (in serated, linear order)
            int leftInTab = columns.size(); //number of items left to search in lists
            int leftInCol = colNames.size()-c;
            
            for (Attribute column : columns) {
                Field f = null;
                if (c < colNames.size() && column.getName().equals(colNames.get(c))) {
                    f = new Field(record.get(c));
                    record2.add(f);
                    leftInCol--;    //move to next in user-selected columns
                    c++;
                } //if attributes don't match
                else {
                    f = new Field();
                    f.setType(column.getType());
                    record2.add(f);
                }
                leftInTab--;
                if(leftInTab < leftInCol){  //finds if remaining columns can be checked
                    throw new IllegalArgumentException("ERROR: some columns wont be found in table");
                }
            }
        }
        //add record to table
        if(allCol){
            Field f = new Field(record.get(0));
            TABLES.get(tI).insertRecord(record);
        }else{
            TABLES.get(tI).insertRecord(record2);
        }
    }
    
    static void createCheck(ArrayList<String> word){
        String tableName = new String();
        String attName = new String();
        String attType = new String();
        ArrayList<Attribute> columns = new ArrayList<>();
        Attribute a;
        
        //checks if second word is TABLE
        if(!word.get(1).equals("TABLE")){
            throw new IllegalArgumentException("ERROR: invalid command \""+
                    word.get(1)+"\"");
        }
        
        //checks if 3rd word is a command instead of a table name
        if(isCommand(word.get(2))){
            throw new IllegalArgumentException("ERROR: command cannot be table name"); 
        }
        tableName = word.get(2);
        
        //check if open parenthesis isn't in correct location
        if(!word.get(3).equals("(")){
            throw new IllegalArgumentException("ERROR: missing open parenthesis");
        }
        
        //check if the next two column values aren't commands
        //if they pass, then add them to the column list
        for(int i = 4; i < word.size()-1; i++){
            //throw exception if word is a command
            if(isCommand(word.get(i))){
                throw new IllegalArgumentException("ERROR: invalid column name or type");
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
            throw new IllegalArgumentException("ERROR: missing close parenthesis");
        }
        
        TABLES.add(new Table(tableName, columns));
    }
    
    static int tableSearch(String tableName){
        //------------update at a later time for a binary search over sorted names------------------
        boolean found = false;
        int tI = 0; //index for table
            for(int i = 0; i < TABLES.size() && !found; i++){
                if(TABLES.get(i).getName().equals(tableName)){
                    found = true;
                    tI = i;
                }
            }
            if(!found){
                throw new IllegalArgumentException("ERROR: table name was not found");
            }
        return tI;
    }
    
    static void displayTable(Table table){
        Attribute a;
        
        if(MAXLOGIN){
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
        }else{
            //print table name
            System.out.print("<"+table.getName()+">");
            //print attribute names
            System.out.print("[");
            for(int i = 0; i < table.getNumAttributes(); i++){
                a = table.getAttributes().get(i);
                System.out.print(a.getName());
                if(i != table.getNumAttributes()-1){
                    System.out.print(",");
                }
            }
            System.out.print("]");
            //print values
            for(int r = 0; r < table.getNumRecords(); r++){
                System.out.print("(");
                for(int c = 0; c < table.getNumAttributes(); c++){
                    ArrayList<Field> f = table.getRecord(r);
                    System.out.print(f.get(c).getValue());
                    if(c != table.getNumAttributes()-1){
                        System.out.print(",");
                    }
                }
                System.out.print(")");
            }
        }
        System.out.println();
    }
    
    static boolean isCommand(String str){
        return str.matches("CREATE|TABLE|UPDATE|SET|WHERE|SELECT|FROM|"
                + "DELETE|INSERT|INTO|VALUES|DROP|COMMIT|ROLLBACK");
    }
    
    static boolean isOperator(String str){
        return str.matches("[/*+-=<>]|<=|>=|!=");
    }
    
    static boolean isLogicOp(String str){
        return str.matches("OR|AND");
    }
    
    static boolean isFloat(String s){
        try {
            Float.parseFloat(s);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
    
    static boolean isValue(String s){
        return !(isCommand(s)||isOperator(s)||isLogicOp(s));
    }
    
    static boolean login(String usr){
        //------------------------UNDER_CONSTRUCTION----------------------------------
        if(usr.length() > 1 && usr.substring(0, 1).equals("-")){
            //check if username is in database
            USERNAME = usr.substring(1, usr.length());
            MAXLOGIN = true;
            return true;
        }else if(usr.length() > 0){
            //check if username is in database
            USERNAME = usr;
            MAXLOGIN = false;
            return true;
        }
        return false;
    }
    
    public static void main(String[] args) {
        TABLES = new ArrayList<>();
/*----artificial data--------       
create table friends('name' string 'hobby' string 'birthdate' date 'age' float);
insert into friends values ('Jane Kang','guitar','14-3-1989',25);
insert into friends values ('Mr. Burns','lute','16-6-1900',114);
insert into friends values ('King Soandso','clavicord','01-5-1877',114);
insert into friends (name,hobby) values ('echo','guitar');
select * from friends;
update friends set hobby = 'dancing' where hobby = 'lute';
select * from friends;
select name, birthdate, hobby from friends where hobby = 'guitar';
delete from friends;
*/
        InputStream is = System.in;
        Scanner sc = new Scanner(is);
        String s = new String();
        boolean go = true;
        boolean login = false;
        while(go){
            //login to account
            if(!login){
                System.out.print("Username: ");
                String usr = sc.nextLine();
                //exit program if user inputs exit
                if(usr.matches("exit|exit;"))
                    break;
                //check for username, if invalid, message than break
                login = login(usr);
                if(!login){
                    System.out.println("user was not found");
                    continue;
                }
            }
            //get input
            s = "";
            System.out.print("SQL> ");
            while(true){
                String temp = sc.useDelimiter(Pattern.compile("\\z")).next();
                temp = temp.replaceAll("(\\r|\\n)", "");
                s+=temp;
                if(temp.length() != 0 && 
                        temp.substring(temp.length()-1).equals(";")){
                    break;
                }
            }
            System.out.println();
            //exit or do commands
            if(s.length() >= 5 && 
                    s.substring(s.length()-5).toUpperCase().equals("EXIT;")){
                go = false;
            } 
            else{
                try{
                    parseString(s);
                }
                catch (Exception e){
                    System.out.println(e.getMessage());
                }
                //parseString(s);
            }
        }
           
        sc.close();
    }
    
}
