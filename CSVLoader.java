

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CSVLoader
{
	private String csvFilePath;
	private List<String[]> studentsList;
	private List<String[]> scores;

	public CSVLoader(String csvFilePath)
	{
		this.csvFilePath = csvFilePath;
		studentsList = new ArrayList<String[]>();
		scores = new ArrayList<String[]>();
	}



	//this will return a list of rejected values,ideally the return size should be 0!
	public List<String[]> loadScores()
	{
		List<String[]> rejectedVals = new ArrayList<String[]>();
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(csvFilePath));
			String line = "";
			br.readLine();

			while ((line = br.readLine()) != null)
			{
				String [] row = line.split(",");
				if ((row.length == 3) && Integer.class.isInstance(Integer.parseInt(row[0]))
						&& (Double.class.isInstance(Double.parseDouble(row[1])))&&(row[2] instanceof String))
				{
					row[0].trim();
					row[1].trim();
					row[2].trim();
					scores.add(row);
				}
				else
					rejectedVals.add(row);
			}

		}catch(Exception e){
			System.out.println("File not found in the current working directory");
		}
		finally
		{
			try
			{
				br.close();
			}
			catch(IOException ie)
			{
				System.out.println("Error occured while closing the BufferedReader");
				ie.printStackTrace();
			}
		}
		return rejectedVals;
	}

	//this will return a list of rejected values,ideally the return size should be 0!
	public List<String[]> loadNames()
	{

		//initialize arraylist for storing invalid lines
		List<String[]> rejectedVals = new ArrayList<String[]>();
		BufferedReader br = null;
		try
		{
			//Reading the csv file
			br = new BufferedReader(new FileReader(csvFilePath));
			String line = "";
			//skip the first line
			br.readLine();

			while ((line = br.readLine()) != null)
			{
				//split line delimited by commas
				String[] row = line.split(",");
				//after split, should have 2 elements in array, else save array as rejected value and read the next line
				//line of data must be: int id, string name
				//note: negative ids are not disallowed in current implementation
				//since csv lines begin and end with quotation marks, remove them from first and last element
				if ((row.length == 2 ) && Integer.class.isInstance(Integer.parseInt(row[0]))
						&&(row[1] instanceof String) && Integer.parseInt(row[0]) >= 0)
				{
					row[0].trim();
					row[1].trim();
					studentsList.add(row);
				}
				else
					rejectedVals.add(row);

			}
		}
		catch(Exception e)
		{
			System.out.println("File not found in the current working directory");
		}
		finally
		{
			try
			{
				br.close();
			}
			catch(IOException ie)
			{
				System.out.println("Error occured while closing the BufferedReader");
			}
		}
		return rejectedVals;
	}

	public List<String[]> getStudentsList()
	{
		return  studentsList;
	}

	public List<String[]> getScores()
	{
		return scores;
	}
	public static void main(String[] args) {
		CSVLoader test = new CSVLoader("/Users/Maq0831/eclipse-workspace/test/src/project131/scores.csv");
		List<String[]> rejects = test.loadScores();
//
		System.out.println(rejects.size());
		for (String[] elt: rejects)
			System.out.println(Arrays.toString(elt));
		for (String[] elt: test.getScores())
			System.out.println("\t"+Arrays.toString(elt));

	}
}

