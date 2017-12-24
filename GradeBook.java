

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class Gradebook
{
	private DBController dbc;

	public Gradebook() throws SQLException, IOException
	{
		startMenu();
	}

	public boolean gbExists()
	{
		String userDir = System.getProperty("user.dir");
		System.out.println(userDir);
		File csv = new File(userDir+"/" + "Gradebook.db");
		System.out.println(csv.getName());
		return csv.exists();
	}

	public List<String[]> acceptCSV(String fileName, String scoresORStudents)
	{
		List<String[]> rejected = null;
		File file = new File(System.getProperty("user.dir")+"/"+fileName);
		System.out.println(file);
		if (file.exists())
		{
			if (scoresORStudents.equalsIgnoreCase("scores"))
				rejected = dbc.inputScoresCSV(fileName);
			else
				rejected = dbc.inputStudentsCSV(fileName);
		}
		else
		{
			System.out.println("File is not in current directory or doesn't exist");
			return null;
		}
		if (rejected.size() != 0 && rejected != null)
			for (String[] elt : rejected)
				System.out.println(Arrays.toString(elt) + " Rejected from" + fileName);
		return rejected;
	}

	public void startMenu()
	{
//		if ( !gbExists())
//		{
//			dbc = new DBController();
//			String[] ch = {"Enter CSV file name", "Enter students manually"};
//			TerminalMenu tm = new TerminalMenu(ch);
//			tm.showMenu("Welcome to your Gradebook");
//			Scanner scan = new Scanner(System.in);
//			String input = tm.promptForInput(scan, "Enter a number or q to quit");
//			if (input.equals("1"))
//			{
//				tm.showLine();
//				System.out.println("NOTE*** CSV file must be in the current directory and must not begin or end with quotatation***");
//				System.out.println("NOTE*** File's format should be :  'name', 'id' ");
//				tm.showLine();
//				String fileName = tm.promptForInput(scan, "Please enter the csv file name");
//				if (acceptCSV(fileName, "students") == null)
//					startMenu();		//errors?
//			}
//			else if (input.equals("q"))
//			{
//				System.exit(0);
//			}
//			else
//			{
//				String[] record = new String[2];
//				record[0] = tm.promptForInput(scan, "Enter the id: ");
//				record[1] = tm.promptForInput(scan, "Enter the name: ");
//				dbc.inputStudent(record);
//			}
//
//		}
//		showResumeMenu();
//	}
//
//	public void showResumeMenu()
//	{
		if (dbc == null)
			dbc = new DBController();
		String[] ch = {"Create Assignment Category",
				"Create Assignment",
				"Input scores",
				"Modify Gradebook",
				"Assign Letter Grade Criteria",
				"Show Assignment Min/Max/Avg",
				"Show Table",
		"Input Students"};

		TerminalMenu tm = new TerminalMenu(ch);
		tm.showMenu("Welcome back to your Gradebook");
		Scanner scan = new Scanner(System.in);
		String quit = "";
		tm.sop("Please enter a number or press q to Quit  ", false);
		while (scan.hasNext())
		{
			String next = scan.nextLine();
			if (next.equals("1"))
			{
				tm.showLine();
				String[] cat = new String[2];
				cat[0] = tm.promptForInput(scan, "Please enter the new category name: ");
				cat[1] = tm.promptForInput(scan, "Please enter the new category's weight: ");
				dbc.inputCategoryAndWeight(cat);
				break;
			}
			else if (next.equals("2"))
			{
				tm.showLine();
				String[] asmt = new String[2];
				asmt[0] = tm.promptForInput(scan, "Please enter the name of the new assignment");
				asmt[1] = tm.promptForInput(scan, "Please enter the assignment's category, if it doesn't exist please create it first : ");
				dbc.inputAssignment(asmt);
				break;
			}
			else if (next.equals("3"))
			{
				tm.showLine();
				String[] score = new String[3];
				String in = tm.promptForInput(scan,"Enter the student's id OR the name of the CSV file (eg.  test.csv )");
				if (in.length() > 4 && in.trim().substring(in.length()-4).equals(".csv"))
				{
					tm.sop("PLEASE ensure file is located in the current working directory", true);
					this.acceptCSV(in, "scores");
				}
				else
				{
					score[0] = in;
					score[1] = tm.promptForInput(scan, "Please enter the student's score: ");
					score[2] = tm.promptForInput(scan, "Please enter the assignment name: ");
					dbc.inputScore(score);
				}
				break;
			}
			else if (next.equals("4"))
			{
				tm.showLine();
				String[] subChoices = {"Delete Student",
						"Delete Score",
						"Change Score",
						"Delete Assignment",
						"Delete Category",
						"Change Grade Criteria",
				};
				tm.showSubMenu(subChoices, "Modify Gradebook");
				tm.sop("Please choose a modification choice:  ", false);
				while(scan.hasNextLine())
				{
					String choice = scan.nextLine();
					if (choice.equals("1"))
					{
						String id = tm.promptForInput(scan, "Enter id for student to delete");
						dbc.deleteFromStudents(id);
						break;
					}
					else if (choice.equals("2"))
					{
						String[] sc = new String[2];
						sc[0] = tm.promptForInput(scan, "Please enter the student's id for deletion: ");
						sc[1] = tm.promptForInput(scan, "Please enter the assignment name: ");
						dbc.deleteScore(sc);
						break;
					}
					else if (choice.equals("4"))
					{
						String id = tm.promptForInput(scan, "Enter assignment name to delete");
						dbc.deleteAssignment(id);
						break;
					}
					else if (choice.equals("5"))
					{
						String id = tm.promptForInput(scan, "Enter category name to delete:	");
						dbc.deleteCategory(id);
						break;
					}
					else if (choice.equals("6"))
					{
						String[] cr = new String[2];
						cr[0] = tm.promptForInput(scan, "Choose the grade to change a, b, c, d: ");
						cr[1] = tm.promptForInput(scan, "Enter the new Lower bound for the grade's threshhold: ");
						dbc.alterGradeCriteria(cr);
						break;
					}
					else if (choice.equals("3"))
					{
						String[] row = new String[3];
						row[0] = tm.promptForInput(scan, "Enter the id of the student whose score should change ");
						row[1] = tm.promptForInput(scan, "Enter the new score for this student: ");
						row[2] = tm.promptForInput(scan, "Enter the name of the assignment: ");
						dbc.alterScoresTable(row);
						break;
					}
					else
					{
						quit = choice;
						tm.stack.pop();
						break;
					}
				}
				break;
			}
			else if (next.equals("5"))
			{
				tm.showLine();
				String[] crit = new String[2];
				char letter = 'A';
				for (int i = 0; i < 4 ; i++)
				{
					if (letter != 'E')
					{
						crit[0] = String.valueOf(letter);
						crit[1] = tm.promptForInput(scan, "Please enter the letter grade criteria for " + letter++);
						dbc.alterGradeCriteria(crit);
					}
				}
				break;
			}
			else if (next.equals("6"))
			{
				tm.showLine();
				String choice = tm.promptForInput(scan, "Please enter the assignment whose aggregates you'd like to view: ");

				dbc.getStats(choice);
				break;
			}
			else if (next.equals("7"))
			{
				tm.showLine();
				String choice = tm.promptForInput(scan, "Choose: students, assignments, weights, gradeCriteria. OR enter 'all' to show final grades OR status to show the whole Gradebook: ");
				if (choice.equalsIgnoreCase("all"))
					dbc.showGrades();
				else if (choice.equalsIgnoreCase("status"))
					dbc.displayGradebookState();
				else
				{
					dbc.displayTable(choice);
				}
				break;
			}
			else if (next.equals("8"))
			{
				tm.showLine();
				String in = tm.promptForInput(scan,"Enter the student's id OR the name of the CSV file (eg.  test.csv )");
				if (in.length() > 4 && in.trim().substring(in.length()-4).equals(".csv"))
				{
					tm.sop("PLEASE ensure file is located in the current working directory", true);
					this.acceptCSV(in, "students");
				}
				else
				{
					System.out.println("NOTE*** File's format should be :  'id', 'score', 'assignment name' ");
					String[] row = new String[2];
					row[0] = in;
					row[1] = tm.promptForInput(scan, "Please enter the name of the student");
					dbc.inputStudent(row);
				}
				break;
			}
			else
			{
				quit = next;
				break;
			}
		}

		if (!quit.equals("q"))
		{
//			tm.showLine();tm.showLine();
//			String c = tm.promptForInput(scan, "Press B to return to the main menu:  ");
//			if (c.equalsIgnoreCase("b"))
//				this.showResumeMenu();
//			else
//				return;
			startMenu();
		}
		else
			return;
	}

	public DBController getDBC()
	{
		return dbc;
	}


	class TerminalMenu
	{
		String[] choices;
		Stack<TerminalMenu> stack;

		public TerminalMenu(String[] choices)
		{
			this.choices = choices;
			stack = new Stack<TerminalMenu>();
		}

		public void showMenu(String welcomeMsg)
		{
			showLine();
			showLine();
			sop(welcomeMsg, true);
			showLine();
			for (int i = 0; i < choices.length; i++)
			{
				sop((i + 1) + ". " + choices[i], true);
			}
			showLine();
		}

		public void showSubMenu(String [] subChoices, String subMenuMsg)
		{
			TerminalMenu sub = new TerminalMenu(subChoices);
			stack.push(sub);
			sub.showMenu(subMenuMsg);

		}

		public void sop(Object x, boolean skip)
		{
			String ln = "";
			if (skip)
				ln = "\n";
			System.out.print(x + ln);
		}

		public void showLine()
		{
			String line = "--------------------";
			sop(line, true);
		}

		public String promptForInput(Scanner in, String prompt)
		{
			sop(prompt + " : ", false);
			return in.nextLine();
		}

	}
	public static void main(String[] args)
	{
		try {
			Gradebook gb = new Gradebook();
			System.out.println("Goodbye for now");
			gb.getDBC().getConn().close();
		} catch (IOException | SQLException e) {
			System.out.println(e.getCause().toString());
			e.printStackTrace();
		}
	}
}


