package database_mgmt;

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.nio.file.Files;

public class filesystem
{
	//Returns true if database.txt is created
	//done
	static boolean createDb(String dbname)
	{
		File folder = new File("databases");
		if(!folder.exists())
		{
			if(!folder.mkdir())
				throw new IllegalArgumentException("Could not create databases directory");
		}
		
		File dbfile = new File("databases/"+dbname+".txt");
                if(dbfile.exists())
                    return false;
		try{
			dbfile.createNewFile();
			return true;
		}catch(IOException x){
			return false;
		}
	}
	//Returns true if successful delete
	//done
	static boolean deleteDb(String dbname)
	{
		File file = new File("databases/" + dbname + ".txt");
		if(file.delete())
		{
			System.out.println("successful file deletion");
			return true;
		}
		else
		{
			System.out.println("Failed file deletion");
			return false;
		}
	}
	
	//Returns true if successful renaming
	//Returns false if previous olddb doesn't exist
	//done
	static boolean renameDb(String olddb, String newdb)
	{
		File oldfile = new File("database/" + olddb + ".txt");
		File newfile = new File("database/" + newdb + ".txt");
		if(!oldfile.exists()){
            throw new IllegalArgumentException("ERROR: no such database found");
        }
		if(oldfile.renameTo(newfile))
		{
			System.out.println("successful rename");
			return true;
		}
		else
		{
			System.out.println("Failed to rename");
			return false;
		}
	}
	
	//Returns Table formatted from input string
	//done
	static Table strToTable(String[] temp)
	{
		String tablename = temp[0];
		int i = 1;
		ArrayList<Attribute> attributes = new ArrayList<>();
		ArrayList<Field> record = new ArrayList<>();
		ArrayList<ArrayList<Field>> recordlist = new ArrayList<ArrayList<Field>>();
		//takes in attribute names and types
		while(temp[i].equals("(") == false)
		{
			attributes.add(new Attribute(temp[i],temp[i++]));
			i++;
		}
		i++; //skips the first parenthesis
		//starts storing records
		while(!temp[i].equals(")"))
		{
			//saves entire array of strings as one, e.g. "Happy Birthday"
			if(temp[i].startsWith("\""))
			{
				if(temp[i].endsWith("\""))
					record.add(new Field(temp[i].substring(1,temp.length-1)));
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
					record.add(new Field(word));
					i = j;
				}
			}
			else if(temp[i].equals("|") || temp[i].equals(")"))
			{
				recordlist.add(record);
				record = new ArrayList<>();
			}
			else
			{
				record.add(new Field(temp[i]));
				recordlist.add(record);
				record = new ArrayList<>();
			}
			i++;
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
		return new Table(tablename, attributes, recordlist);
	}

	//Returns a string formatted from input Table
	static String tableToStr(Table t)
	{
		String newtblstr = t.getName() + " ";
		
		for(int i = 0; i < t.getNumAttributes(); i++)
		{
			newtblstr += t.getAttribute(i).getName() + " " + t.getAttribute(i).getType() + " ";
		}
		
		newtblstr += "( ";
		
		for(int i = 0; i < t.getNumRecords(); i++)
		{
			if(i+1 != t.getNumRecords())
				newtblstr += t.getRecord(i) + " | ";
			else
				newtblstr += t.getRecord(i) + " )";
		}
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
	//done
	static Table getTable(String tblname, String dbname)
	{
		String s = new String();
		InputStream dbfile = BiblioBaseDBMS.class.getResourceAsStream(dbname+".txt");
		if(dbfile == null){
            throw new IllegalArgumentException("ERROR: no such database found");
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
		
		System.out.println("Failed to find the table!");
		return null;
	}
	
	//Returns true if table is modified
	//Returns false if table doesn't exist
	static boolean modifyTable(Table t, String dbname)
	{
		InputStream dbfile = BiblioBaseDBMS.class.getResourceAsStream(dbname+".txt");
		if(dbfile == null){
            throw new IllegalArgumentException("ERROR: no such database found");
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
				else
					othertbls.add(s);
			}
		}
		inputF.close();
		if(foundtbl == true)
		{
			
			try (FileWriter f1 = new FileWriter("databases/" + dbname + ".txt", false))
			{
				for(String var : othertbls)
					f1.write(var + "\n");
				f1.flush();
				f1.close();
				System.out.println("Updated the table.");
				return true;
			} catch (IOException x) {
				System.out.println("Error saving to modify file");
				return false;
			}
		}
		else
		{
			System.out.println("Table doesn't exist");
			return false;
		}
	}
	
	//returns true if table is created
	//returns false if table already exists
	//done
	static boolean createTable(Table t, String dbname)
	{
		InputStream dbfile = BiblioBaseDBMS.class.getResourceAsStream(dbname+".txt");
		if(dbfile == null){
            throw new IllegalArgumentException("ERROR: no such database found");
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
			System.out.println("Table doesn't exist");
			return false;
		}
		else
		{
			try (FileWriter f1 = new FileWriter("databases/" + dbname + ".txt", true)){
				f1.write(newtblstr);
				f1.flush();
				f1.close();
				System.out.println("Created the table.");
				return true;
			} catch (IOException x) {
				System.out.println("Error saving to add table in file");
				return false;
			}
		}
	}
	
	//Returns true if successful deletion
	//Returns false if table doesn't exist
	//done
	static boolean deleteTable(String tblname, String dbname)
	{
		InputStream dbfile = BiblioBaseDBMS.class.getResourceAsStream(dbname+".txt");
		if(dbfile == null){
            throw new IllegalArgumentException("ERROR: no such database found");
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
				if(tablename.equals(tblname))
					foundtbl = true;
				else
					othertbls.add(s);
			}
		}
		inputF.close();
		if(foundtbl == true)
		{
			try (FileWriter f1 = new FileWriter("databases/" + dbname + ".txt"))
			{
				for(String var : othertbls)
					f1.write(var + "\n");
				f1.flush();
				f1.close();
				System.out.println("Deleted the table.");
				return true;
			} catch (IOException x) {
				System.out.println("Error saving to modify file");
				return false;
			}
		}
		else
		{
			System.out.println("Table doesn't exist");
			return false;
		}
	}
	
	//Returns true if successful renaming
	//Returns false if previous oldname doesn't exist
	//done
	static boolean renameTable(String oldname, String newname, String dbname)
	{
		InputStream dbfile = BiblioBaseDBMS.class.getResourceAsStream(dbname+".txt");
		if(dbfile == null){
            throw new IllegalArgumentException("ERROR: no such database found");
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
				else
					othertbls.add(s);
			}
		}
		inputF.close();
		if(foundtbl == true)
		{
			try (FileWriter f1 = new FileWriter("databases/" + dbname + ".txt"))
			{
				for(String var : othertbls)
					f1.write(var + "\n");
				System.out.println("Renamed the table.");
				return true;
			} catch (IOException x) {
				System.out.println("Error saving to modify file");
				return false;
			}
		}
		else
		{
			System.out.println("Table doesn't exist");
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
