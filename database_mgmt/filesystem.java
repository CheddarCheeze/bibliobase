import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.io.InputStream;
import java.io.FileNotFoundException;

public class filesystem
{
	//Reads every table from database, saving each table into TABLES variable
	//input = database name
	static void readdb(String dbname)
	{
		String s = new String();
		File dbfile = new File(dbname+".txt");
		try{
			Scanner inputF = new Scanner(dbfile);
			while(inputF.hasNextLine())
			{
				System.out.println("InNextLine\n");
				ArrayList<String> attributes = new ArrayList<String>();
				ArrayList<ArrayList<String>> recordlist = new ArrayList<ArrayList<String>>();
				String tablename;
				s = inputF.nextLine();
				String[] temp = s.split("\\s+");
				//saves tablename
				tablename = temp[0];
				int i = 1;
				//takes in attribute names
				while(temp[i].equals("(") == false)
				{
					attributes.add(temp[i]);
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
				TABLES.add(tablename,attributes,recordlist);
			}
			inputF.close();
		}catch(FileNotFoundException fnfe) { 
			System.out.println(fnfe.getMessage());
		}
	}
	public static void main(String[] args) 
	{
		String dbname = "database";
		readdb(dbname);
	}
}
