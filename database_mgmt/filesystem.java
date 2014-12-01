import java.io.File;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class filesystem
{
	//returns true if database is created
	static boolean createdb(String dbname)
	{
		//Need to appropriately adjust input directory
		String basedir = "C:/Users/Chase/Desktop/bbex/";
		File folder = new File(basedir + dbname);
		if(!folder.exists())
		{
			if(folder.mkdir())
				System.out.println("Directory created.\n");
			else
				System.out.println("Directory failed to create.\n");
		}
		else
		{
			boolean namecheck = false;
			while(namecheck == false)
			{
				System.out.println("Database " + dbname + " already exists. Re-enter another name.\n");
				Scanner user_input = new Scanner(System.in);
				dbname = user_input.next();
				folder = new File(basedir + dbname);
				if(!folder.exists())
				{
					if(folder.mkdir())
					{
						System.out.println(dbname + " created.\n");
						return true;
					}
					else
						System.out.println("Directory failed to create.\n");
				}
			}
		}
		return true;
	}
/*
	static boolean deletedb(String dbname)
	{
		//Need to appropriately adjust input directory
		String basedir = "C:/Users/Chase/Desktop/bbex/";
		File folder = new File(basedir + dbname);
		if(folder.exists())
		{
			FileUtils.deleteDirectory(new File(basedir + dbname));
			return true;
		}
		else
		{
			System.out.println("Database doesn't exist.");
			return false;
		}
	}
*/

	static void addToTABLES(String[] temp, ArrayList<ArrayList<String>> attributes, ArrayList<ArrayList<String>> recordlist)
	{
		String tablename = temp[0];
		int i = 1;
		//takes in attribute names and types
		while(temp[i].equals("(") == false)
		{
			ArrayList<String> attlist = new ArrayList<>();
			attlist.add(temp[i]); //adds name
			attlist.add(temp[i++]); //adds type
			attributes.add(attlist);
			i++;
		}
		i++; //skips the first parenthesis
		//starts storing records
		while(temp[i].equals(")") == false )
		{
			ArrayList<String> record = new ArrayList<>();
			//saves entire array of strings as one, e.g. "Happy Birthday"
			if(temp[i].startsWith("\""))
			{
				if(temp[i].endsWith("\""))
					record.add(temp[i]);
				else
				{
					int j = i+1;
					String word = temp[i].substring(1,temp[i].length()-1) + " ";
					while(temp[j].endsWith("\"") == false)
					{
						word += temp[j] + " ";
						j++;
					}
					record.add(word + temp[j].substring(0,temp[j].length()-2));
					i = j-1;
				}
			}
			else if(temp[i].equals("|"))
				recordlist.add(record);
			else
				record.add(temp[i]);
			i++;
		}
		/*//print out arraylists for checking
		for(String value : attributes)
			System.out.print(value + "\t");
		for(ArrayList<String> value1 : recordlist)
		{
			for(String value2 : value1)
				System.out.print(value2 + "\t");
			System.out.println();
		}
		*/
//				TABLES.add(tablename,attributes,recordlist);
	}

	static void readdb(String dbname)
	{
		String s = new String();
		File dbfile = new File(dbname+".txt");
		try{
			Scanner inputF = new Scanner(dbfile);
			while(inputF.hasNextLine())
			{
				System.out.println("InNextLine\n");
				ArrayList<ArrayList<String>> attributes = new ArrayList<ArrayList<String>>();
				ArrayList<ArrayList<String>> recordlist = new ArrayList<ArrayList<String>>();
				String tablename;
				s = inputF.nextLine();
				String[] temp = s.split("\\s+");
				addToTABLES(temp,attributes,recordlist);
			}
			inputF.close();
		}catch(FileNotFoundException fnfe) { 
			System.out.println(fnfe.getMessage());
		}
	}	
	
	static void searchtbl(String dbname, String tblname)
	{
		String s = new String();
		File dbfile = new File(dbname+".txt");
		try{
			boolean foundtbl = false;
			Scanner inputF = new Scanner(dbfile);
			outerloop:while(inputF.hasNextLine())
			{
				ArrayList<ArrayList<String>> attributes = new ArrayList<ArrayList<String>>();
				ArrayList<ArrayList<String>> recordlist = new ArrayList<ArrayList<String>>();
				s = inputF.nextLine();
				Scanner tsearch = new Scanner(s);
				if(tsearch.hasNext())
				{
					String tablename = tsearch.next();
					if(tablename.equals(tblname))
					{
						String[] temp = s.split("\\s+");
						addToTables(temp,attributes,recordlist);
						foundtbl = true;
						break;
					}
				}
			}
			if(foundtbl == true)
				System.out.println("Theoretically found the table!");
			else
				System.out.println("Failed to find the table!");
			inputF.close();
		} catch(FileNotFoundException fnfe) { 
			System.out.println(fnfe.getMessage());
		}
				
	}
	public static void main(String[] args) 
	{
		String dbname = "database";
//		readdb(dbname);
//		createdb(dbname);
		searchtbl(dbname,"Books");
	}
}
