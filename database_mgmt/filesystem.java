package database_mgmt;

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class filesystem
{
	//Returns true if dbname.txt is inside of databases
	static boolean searchDb(String dbname)
	{
		File folder = new File("databases");
		if(!folder.exists()){
			return false;
		}
		File dbfile = new File("databases/"+dbname+".txt");
		return dbfile.exists();
	}
		
	//Returns true if database.txt is created
	//NOTE: table_security, user_table, and checkout are not added to the cat
	static boolean createDb(String dbname)
	{
		File folder = new File("databases");
		if(!folder.exists())
		{
			if(!folder.mkdir()){
				throw new IllegalArgumentException("Could not create databases directory");
			}
		}
		
		File dbfile = new File("databases/"+dbname+".txt");
		if(dbfile.exists()){
			return false;
		}
		try{
			dbfile.createNewFile();
			try (FileWriter f1 = new FileWriter("databases/" + dbname + ".txt", true)){
				f1.write("table_security 2 ID FLOAT table_name STRING security_lvl FLOAT ( \"0\" \"table_security\" \"3\" | \"1\" \"user_table\" \"2\" | \"2\" \"check_out\" \"2\" )\n"
						+ "user_table 0 ID FLOAT account_name STRING ( \"0\" \"admin\" )\n"
						+ "checkout -1 ID FLOAT patron_id FLOAT inv_type STRING inv_id FLOAT due_date DATE ( )\n");
				f1.flush();
				f1.close();
				return true;
			} catch (IOException x) {
				return false;
			}
		}catch(IOException x){
			return false;
		}
	}
	
	//Returns true if successful delete
	static boolean deleteDb(String dbname)
	{
		File file = new File("databases/" + dbname + ".txt");
		if(file.delete())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//Returns true if successful renaming
	//Returns false if previous olddb doesn't exist
	static boolean renameDb(String olddb, String newdb)
	{
		File oldfile = new File("databases/" + olddb + ".txt");
		File newfile = new File("databases/" + newdb + ".txt");
		if(!oldfile.exists()){
			throw new IllegalArgumentException("ERROR: no such database found");
		}
		if(oldfile.renameTo(newfile))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//Returns Table formatted from input string
	static Table strToTable(String[] temp)
	{
		String tablename = temp[0];
		int primarykey = Integer.parseInt(temp[1]);
		int i = 2;
		ArrayList<Attribute> attributes = new ArrayList<>();
		ArrayList<Field> record = new ArrayList<>();
		ArrayList<ArrayList<Field>> recordlist = new ArrayList<ArrayList<Field>>();
		//takes in attribute names and types
		while(!temp[i].equals("("))
		{
			attributes.add(new Attribute(temp[i],temp[i+1]));
			i+=2;
		}
		i++; //skips the first parenthesis
		//starts storing records
		int attIdx = 0;
		while(!temp[i].equals(")"))
		{
			//saves entire array of strings as one, e.g. "Happy Birthday"
			attIdx %= attributes.size();
			if(temp[i].startsWith("\""))
			{
				if(temp[i].endsWith("\"")){
					record.add( new Field(temp[i].substring(1,temp[i].length()-1), attributes.get(attIdx).getType()) );
				}
				else
				{
					int j = i+1;
					String word = temp[i].substring(1,temp[i].length()) + " ";
					while(!temp[j].endsWith("\""))
					{
						word += temp[j] + " ";
						j++;
					}
					word += temp[j].substring(0,temp[j].length()-1);
					record.add(new Field(word, attributes.get(attIdx).getType()));
					i = j;
				}
			}
			else if(temp[i].equals("|"))
			{
				recordlist.add(record);
				record = new ArrayList<>();
			}
			i++;
			attIdx++;
		}
		if(!record.isEmpty()){
			recordlist.add(record);
		}
		/*//print out arraylists for checking
			for(String value : attributeNames)
					System.out.print(value + " ");
			for(ArrayList<String> value1 : recordlist)
			{
				for(String value2 : value1)
					System.out.print(value2.getValue()+" ");
				System.out.println();
			}
		*/
		return new Table(tablename, attributes, recordlist, primarykey);
	}

	//Returns a string formatted from input Table
	static String tableToStr(Table t)
	{
		String newtblstr = t.getName() + " ";
		newtblstr += t.getMaxPrimary() + " ";
		for(int i = 0; i < t.getNumAttributes(); i++)
		{
			newtblstr += t.getAttribute(i).getName() + " " + t.getAttribute(i).getType() + " ";
		}
		
		newtblstr += "( ";
		
				int i = 0;
		for(ArrayList<Field> record : t.getRecords())
		{
			for(Field field : record){
					newtblstr += "\""+field.getValue()+"\" ";
			}
			if(i+1 == t.getNumRecords()){
				break;
			}
			newtblstr+= "| ";
			i++;
		}
		newtblstr += ")";
		return newtblstr;
	}
	
	//last priority, need to return arraylist of tables
/*	static boolean readDb(String dbname)
	{
		String s = new String();
		File dbfile = new File(dbname+".txt");
		//InputStream dbfile = BiblioBaseDBMS.class.getResourceAsStream(dbname+".txt");
		if(dbfile == null){
			throw new IllegalArgumentException("ERROR: no such database found");
		}
		try{
			Scanner inputF = new Scanner(dbfile);
			while(inputF.hasNextLine())
			{
				ArrayList<ArrayList<String>> attributes = new ArrayList<ArrayList<String>>();
				ArrayList<ArrayList<String>> recordlist = new ArrayList<ArrayList<String>>();
				String tablename;
				s = inputF.nextLine();
				String[] temp = s.split("\\s+");
				strToTable(temp,attributes,recordlist);
			}
			inputF.close();
		}catch(FileNotFoundException fnfe) { 
			System.out.println(fnfe.getMessage());
		}
}	*/
	
	//Returns desired table or NULL
	static Table getTable(String tblname, String dbname)
	{
		String s = new String();
		InputStream dbfile = null;
		try {
			dbfile = new FileInputStream("databases/"+dbname+".txt");
		} catch (FileNotFoundException ex) {
			Logger.getLogger(filesystem.class.getName()).log(Level.SEVERE, null, ex);
		}
		boolean foundtbl = false;
		Scanner inputF = new Scanner(dbfile);
		while(inputF.hasNextLine())
		{
			s = inputF.nextLine();
			Scanner tsearch = new Scanner(s);   
			if(tsearch.hasNext())
			{
				String tablename = tsearch.next();
				if(tablename.equals(tblname))
				{
					String[] temp = s.split("\\s+");
					return strToTable(temp);
				}
			}
		}
		inputF.close();
		
		return null;
	}
	
	//Returns true if table is modified
	//Returns false if table doesn't exist
	static boolean modifyTable(Table t, String dbname)
	{
		InputStream dbfile = null;
		try {
			dbfile = new FileInputStream("databases/"+dbname+".txt");
		} catch (FileNotFoundException ex) {
			Logger.getLogger(filesystem.class.getName()).log(Level.SEVERE, null, ex);
		}
		boolean foundtbl = false;
		Scanner inputF = new Scanner(dbfile);
		ArrayList<String> othertbls = new ArrayList<>();
		
		String newtblstr = tableToStr(t);
		while(inputF.hasNextLine())
		{
			String s = inputF.nextLine();
			Scanner tsearch = new Scanner(s);
			if(tsearch.hasNext())
			{
				String tablename = tsearch.next();
				if(tablename.equals(t.getName()))
				{
					othertbls.add(newtblstr);
					foundtbl = true;
				}
				else{
					othertbls.add(s);
				}
			}
		}
		inputF.close();
		if(foundtbl == true)
		{
			
			try (FileWriter f1 = new FileWriter("databases/" + dbname + ".txt", false))
			{
				for(int i = 0; i < othertbls.size(); i++){
					String var = othertbls.get(i);
					f1.write(var + "\n");
				}
				f1.flush();
				f1.close();
				return true;
			} catch (IOException x) {
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	//returns true if table is created
	//returns false if table already exists
	static boolean createTable(Table t, String dbname)
	{
		InputStream dbfile = null;
		try {
				dbfile = new FileInputStream("databases/"+dbname+".txt");
		} catch (FileNotFoundException ex) {
				Logger.getLogger(filesystem.class.getName()).log(Level.SEVERE, null, ex);
		}
		boolean foundtbl = false;
		String newtblstr = tableToStr(t);
		
		Scanner inputF = new Scanner(dbfile);
		
		while(inputF.hasNextLine())
		{
			String s = inputF.nextLine();
			Scanner tsearch = new Scanner(s);
			if(tsearch.hasNext())
			{
				if(tsearch.next().equals(t.getName()))
				{
					foundtbl = true;
					break;
				}
			}
		}
		inputF.close();
		if(foundtbl == true)
		{
			return false;
		}
		else
		{
			try (FileWriter f1 = new FileWriter("databases/" + dbname + ".txt", true)){
				f1.write(newtblstr+"\n");
				f1.flush();
				f1.close();
				return true;
			} catch (IOException x) {
				return false;
			}
		}
	}
	
	//Returns true if successful deletion
	//Returns false if table doesn't exist
	static boolean deleteTable(String tblname, String dbname)
	{
		InputStream dbfile = null;
		try {
				dbfile = new FileInputStream("databases/"+dbname+".txt");
		} catch (FileNotFoundException ex) {
				Logger.getLogger(filesystem.class.getName()).log(Level.SEVERE, null, ex);
		}
		boolean foundtbl = false;
		Scanner inputF = new Scanner(dbfile);
		ArrayList<String> othertbls = new ArrayList<>();
		
		while(inputF.hasNextLine())
		{
			String s = inputF.nextLine();
			Scanner tsearch = new Scanner(s);
			if(tsearch.hasNext())
			{
				String tablename = tsearch.next();	
				if(tablename.equals(tblname)){
					foundtbl = true;
				}
				else{
					othertbls.add(s);
				}
			}
		}
		inputF.close();
		if(foundtbl == true)
		{
			try (FileWriter f1 = new FileWriter("databases/" + dbname + ".txt"))
			{
				for(String var : othertbls){
					f1.write(var + "\n");
				}
				f1.flush();
				f1.close();
				return true;
			} catch (IOException x) {
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	//Returns true if successful renaming
	//Returns false if previous oldname doesn't exist
	static boolean renameTable(String oldname, String newname, String dbname)
	{
		InputStream dbfile = null;
		try {
				dbfile = new FileInputStream("databases/"+dbname+".txt");
		} catch (FileNotFoundException ex) {
				Logger.getLogger(filesystem.class.getName()).log(Level.SEVERE, null, ex);
		}
		boolean foundtbl = false;
		Scanner inputF = new Scanner(dbfile);
		ArrayList<String> othertbls = new ArrayList<>();
		
		while(inputF.hasNextLine())
		{
			String s = inputF.nextLine();
			Scanner tsearch = new Scanner(s);
			if(tsearch.hasNext())
			{
				String tablename = tsearch.next();	
				if(tablename.equals(oldname))
				{
					othertbls.add(newname+s.substring(oldname.length(),s.length()));
					foundtbl = true;
				}
				else{
					othertbls.add(s);
				}
			}
		}
		inputF.close();
		if(foundtbl == true)
		{
			try (FileWriter f1 = new FileWriter("databases/" + dbname + ".txt"))
			{
				for(String var : othertbls){
					f1.write(var + "\n");
				}
				return true;
			} catch (IOException x) {
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	public static void main(String[] args) 
	{
		String dbname = "fayetteville";
		createDb(dbname);
		Scanner in = new Scanner(System.in);
		String s = in.next();
		renameDb(dbname, "springdale");
	}
}
