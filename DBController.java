

import java.sql.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.sqlite.SQLiteConfig;

public class DBController
{
	static Connection conn;
	private String dbURL;


	public DBController()
	{
		//create a new file, name it Gradebook.db, and place it in the current directory
		//get user's current dir, and create then initialize
		String userDirectory = System.getProperty("user.dir");
		String dbName = "Gradebook.db";
		File  dbFile = new File(userDirectory+dbName);
		if (!dbFile.exists())
			createDBFile(userDirectory, dbFile);
		getDBURL();
		conn = getConn();
		try {
			createAllTables();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Could not create all necessary database tables");
		}
	}

	private void createAllTables() throws SQLException
	{
		this.createWeightsTable();
		this.createAssignmentsTable();
		this.createStudentsTable();
		this.createScoresTable();
		this.createCriteriaTable();
	}
	private void createDBFile(String userDirectory, File dbFile)
	{
		try {
			if (dbFile.createNewFile())
			{
				System.out.println("Gradebook.db file created successfully");
			}
		} catch (IOException e) {
			System.out.println("couldn't create the new file Gradebook.db");
			e.printStackTrace();
		}
	}
	private void createWeightsTable() throws SQLException
	{
		if (conn == null || conn.isClosed())
			getConn();
		String sql = "CREATE TABLE IF NOT EXISTS weights (\n"
				+ "category TEXT PRIMARY KEY, weight REAL CHECK(weight >= 0),\n"
				+ " CONSTRAINT no_dupes UNIQUE(category));";
		Statement stmt = conn.createStatement();
		stmt.execute(sql);
	}

	private void createStudentsTable() throws SQLException
	{
		if (conn == null || conn.isClosed())
			getConn();
		String tableStmt = "CREATE TABLE IF NOT EXISTS students("
				+ " id integer not null primary key, "
				+ "name text);";
		Statement stmt = conn.createStatement();
		//from statements api: execute() returns true if the first result is a ResultSet object; false if it is an update count or there are no results
		stmt.execute(tableStmt);

	}
	private void createScoresTable() throws SQLException
	{
		if (conn == null || conn.isClosed())
			getConn();
		String sql = "CREATE TABLE IF NOT EXISTS scores ( "
				+ "id INTEGER, score REAL CHECK(score >= 0), assignment TEXT, CONSTRAINT no_dupes UNIQUE(id, assignment)"
				+ ", FOREIGN KEY(assignment) REFERENCES ASSIGNMENTS(name) ON DELETE CASCADE,"
				+ " FOREIGN KEY(id) REFERENCES STUDENTS(id) ON DELETE CASCADE);";
		Statement stmt = conn.createStatement();
		stmt.execute(sql);
	}

	private void createAssignmentsTable() throws SQLException
	{
		if (conn == null || conn.isClosed())
			getConn();
		String sql = "CREATE TABLE IF NOT EXISTS assignments("
				+ "name TEXT PRIMARY KEY, category TEXT, FOREIGN KEY(category) "
				+"REFERENCES WEIGHTS(category) ON DELETE CASCADE);";
		Statement stmt = conn.createStatement();
		stmt.execute(sql);
	}

	public void createCriteriaTable() throws SQLException
	{
		if(conn == null || conn.isClosed())
			getConn();
		String sql = "CREATE TABLE IF NOT EXISTS gradeCriteria("
				+ " grade text,"
				+ " floor integer,"
				+ " CONSTRAINT unique_pairs UNIQUE(grade,floor))";
		Statement stmt = conn.createStatement();
		stmt.execute(sql);

		String[] a = {"a", "90"};
		String[] b = {"b", "80"};
		String[] c = {"c", "70"};
		String[] d = {"d", "60"};
		String[] f = {"f", "50"};
		inputGradeCriteria(a);
		inputGradeCriteria(b);
		inputGradeCriteria(c);
		inputGradeCriteria(d);
		inputGradeCriteria(f);
	}
	public List<String[]> inputStudentsCSV(String csvFile)
	{
		CSVLoader l = new CSVLoader(csvFile);
		List<String[]> rejects = l.loadNames();
		List<String[]> names = l.getStudentsList();
		for (String[] elt : names)
			inputStudent(elt);
		return rejects;
	}
	public List<String[]> inputScoresCSV(String fileName)
	{
		CSVLoader l = new CSVLoader(fileName);
		List<String[]> rejects = l.loadScores();
		List<String[]> scores = l.getScores();
		for (String[] elt : scores)
			inputScore(elt);
		return rejects;
	}

	public void inputAssignment(String[] row)
	{
		try
		{
			if (row.length != 2)
				throw new IOException();
			createAssignmentsTable(); 	//if already created should do nothing
			String sql = "INSERT INTO ASSIGNMENTS(name, category) VALUES(?,?)";
			if (conn == null || conn.isClosed())
				getConn();
			conn.setAutoCommit(false);
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1,row[0].toLowerCase());
			pst.setString(2, (row[1].toLowerCase()));
			pst.executeUpdate();
			conn.commit();

		}catch(SQLException | IOException e) {
			System.out.println(Arrays.toString(row) + " Record not added to Assignments Table: ");
		}
	}

	private void inputGradeCriteria(String[] row)
	{
		try
		{
			if (row.length != 2)
				throw new IOException();
			String sql = "INSERT INTO gradeCriteria(grade, floor) VALUES(?,?)";
			if (conn == null || conn.isClosed())
				getConn();
			conn.setAutoCommit(false);
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1,row[0].toLowerCase());
			pst.setInt(2, (Integer.parseInt(row[1])));
			pst.executeUpdate();
			conn.commit();
		}catch(SQLException | IOException | NumberFormatException e) {
			System.out.println(Arrays.toString(row) + " Record not added to Criteria Table: ");

		}
	}
	public void inputCategoryAndWeight(String[] row)

	{
		try
		{
			createWeightsTable(); 	//if already created should do nothing
			String sql = "INSERT INTO weights (category, weight) VALUES(?,?)";
			if (conn == null || conn.isClosed())
				getConn();
			conn.setAutoCommit(false);
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1,row[0].toLowerCase());
			pst.setDouble(2, Double.parseDouble(row[1]));
			pst.executeUpdate();
			conn.commit();

		}catch(SQLException | NumberFormatException e) {
			System.out.println("Record not added to database: " + Arrays.toString(row));
		}
	}

	public void alterGradeCriteria(String[] row)
	{
		try
		{
			String sql = "UPDATE gradeCriteria SET floor = ? WHERE GRADE = ?";
			if (conn == null || conn.isClosed())
				getConn();
			conn.setAutoCommit(false);
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, Integer.parseInt((row[1])));
			pst.setString(2,row[0].toLowerCase());
			pst.executeUpdate();
			conn.commit();

		}catch(SQLException | NumberFormatException e) {
			System.out.println("Record not added to database: " + Arrays.toString(row));
		}
	}

	public void alterScoresTable(String [] row)
	{
		try
		{
			String sql = "UPDATE scores SET score = ? WHERE id = ? AND "
					+ "assignment = ?;";
			if (conn == null || conn.isClosed())
				getConn();
			conn.setAutoCommit(false);
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setDouble(1,Double.parseDouble(row[1]));
			pst.setInt(2, Integer.parseInt((row[0])));
			pst.setString(3, (row[2]));
			pst.executeUpdate();
			conn.commit();

		}catch(SQLException | NumberFormatException e) {
			e.printStackTrace();
			System.out.println("Record not added to database: " + Arrays.toString(row));
		}

	}

	public void inputStudent(String[] row)
	{
		try	{
			if (conn == null || conn.isClosed())
				getConn();
			createStudentsTable();
			String sql = "INSERT INTO students(id,name) VALUES(?,?)";
			if (row.length != 2)
				throw new IOException();
			conn.setAutoCommit(false);
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, Integer.parseInt(row[0]));
			pst.setString(2, row[1].toLowerCase());
			pst.executeUpdate();
			conn.commit();
		}catch (SQLException | IOException | NumberFormatException e) {
			System.out.println("Record not added to database: " + Arrays.toString(row));
		}
	}

	public void inputScore(String[] row)
	{
		try	{
			if (conn == null || conn.isClosed())
				getConn();
			createScoresTable();
			String sql = "INSERT INTO scores(id,score,assignment) VALUES(?,?,?)";
			if (row.length != 3)
				throw new IOException();
			conn.setAutoCommit(false);
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, Integer.parseInt(row[0]));
			pst.setDouble(2,  Double.parseDouble(row[1]));
			pst.setString(3, row[2].toLowerCase());
			pst.executeUpdate();
			conn.commit();
		}catch (SQLException | IOException | NumberFormatException e) {
			System.out.println("Record not added to database: " + Arrays.toString(row));
		}
	}

	//deleting a student will delete all instances of that student in students and scores table
	public void deleteFromStudents(String id)
	{
		try
		{
			if (conn == null || conn.isClosed())
				getConn();
			conn.setAutoCommit(false);
			String sql = "DELETE from STUDENTS WHERE ID = ? ;";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, Integer.parseInt(id));
			int done = pst.executeUpdate();
			conn.commit();
			if (done > 0)
				System.out.println("Student with id : "+id + " has been deleted from Database");
			else
				throw new NumberFormatException();
		} catch (NumberFormatException | SQLException e) {
			System.out.println("Could not delete student with id "+id + " from Database ");
		}
	}

	//deleting a category will delete all instances of 'cat' in assignments, and scores table
	public void deleteCategory(String cat)
	{
		try {
			if (conn == null || conn.isClosed())
				getConn();
			conn.setAutoCommit(false);
			String sql = "DELETE from WEIGHTS WHERE category = ? ;";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, cat.toLowerCase());
			int done = pst.executeUpdate();
			conn.commit();
			if (done > 0)
				System.out.println("Category  : "+cat + " has been deleted from Database");
			else
				throw new NumberFormatException();
		} catch (NumberFormatException | SQLException e) {
			System.out.println(cat + "Is not formatted correctly or is not a valid database entry ");
		}

	}

	public void deleteAssignment(String ast)
	{
		try
		{
			if (conn == null || conn.isClosed())
				getConn();
			conn.setAutoCommit(false);
			String sql = "DELETE from ASSIGNMENTS WHERE name = ? ;";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, ast.toLowerCase());
			int done = pst.executeUpdate();
			conn.commit();
			if (done > 0)
			{
				System.out.println("Assignment  : "+ast + " has been deleted from Assignments table");
			}
			else
				throw new NumberFormatException();
		} catch (NumberFormatException | SQLException e) {
			System.out.println(ast + "Is not formatted correctly or is not a valid database entry ");
		}

	}

	public void deleteScore(String[] idAndAsst)
	{
		try {
			if (conn == null || conn.isClosed())
				getConn();
			conn.setAutoCommit(false);
			String sql = "DELETE from SCORES WHERE (id = ? AND  assignment = ?);";
			PreparedStatement pst = conn.prepareStatement(sql);

			pst.setInt(1, Integer.parseInt(idAndAsst[0]));
			pst.setString(2, idAndAsst[1].toLowerCase());
			int done = pst.executeUpdate();
			conn.commit();
			if (done > 0)
			{
				System.out.println("Scores for " +Arrays.toString(idAndAsst)+ " has been deleted from Scores table");
			}
			else
				throw new NumberFormatException();
		} catch (NumberFormatException | SQLException e) {
			System.out.println("Could not delete score from Scores table, expected "
					+ "id and assignment name, got: "+Arrays.toString(idAndAsst));
		}
	}

	public void getStats(String name)
	{
		//returns the min, max, and avg for a particular assignment
		//min: for min, here the query: SELECT assignment, min(score) FROM Scores WHERE assignment = "some assignment name"
		Statement s;
		String sql;

		//first try/catch to find the minimum value
		try {
			if(conn==null || conn.isClosed())
				getConn();
			s = conn.createStatement();
			sql = "CREATE TABLE IF NOT EXISTS scores("
					+ "id integer, "
					+ "score integer, "
					+ "assignment text)";
			s.executeUpdate(sql);

			s = conn.createStatement();
			sql = "SELECT min(score), assignment FROM scores WHERE assignment = '"+name+"'";
			s.execute(sql);
			ResultSet min = s.getResultSet();
			int n = min.getInt(1);
			System.out.println("The minimum score for " + name + " is " + n + '.');
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//second try/catch to find the maximum value
		try {
			if(conn==null || conn.isClosed())
				getConn();
			s = conn.createStatement();
			sql = "CREATE TABLE IF NOT EXISTS scores("
					+ "id integer, "
					+ "score integer, "
					+ "assignment text)";
			s.executeUpdate(sql);

			s = conn.createStatement();
			sql = "SELECT max(score), assignment FROM scores WHERE assignment = '"+name+"'";
			s.execute(sql);
			ResultSet max = s.getResultSet();
			int n = max.getInt(1);
			System.out.println("The maximum score for " + name + " is " + n + '.');
		} catch (SQLException e) {
			System.out.println("Error inserting into the database, make sure to create: categories >> assignments >> students >> scores");
		}catch ( NumberFormatException e) {
			System.out.println("The input value is incompatible for this database configuration");
		}
		//final try/catch to find avg score for a certain assignment
		try {
			if(conn==null || conn.isClosed())
				getConn();
			s = conn.createStatement();
			s = conn.createStatement();
			sql = "SELECT avg(score), assignment FROM scores WHERE assignment = '"+name+"'";
			s.execute(sql);
			ResultSet avg = s.getResultSet();
			int n = avg.getInt(1);
			System.out.println("The average score for " + name + " is " + n + '.');
		} catch (SQLException e) {
			System.out.println("Error inserting into the database, make sure to create: categories >> assignments >> students >> scores");
		}
	}

	public void displayTable(String tableName)
	{
		try
		{
			if (conn == null || conn.isClosed())
				getConn();
			String sql = "SELECT * FROM "+ tableName+";";
			Statement st2 = conn.createStatement();		//we'll need a return value from the database, this will be the result set;
			ResultSet rs = st2.executeQuery(sql);		// the rs is a set of records that must be looped through
			displayResultSet(rs);
			conn.close();
		}catch (SQLException e) {
			System.out.println("Table doesn't exist: " + tableName);
		}
	}

	private void displayResultSet(ResultSet rs)
	{
		ResultSetMetaData rsmd;
		try {
			rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next())
			{
				for(int i = 1; i <= cols; i++)
				{
					System.out.print(rs.getObject(i) +"\t");
				}
				System.out.println("");
			}
		} catch (SQLException e) {
			System.out.println("Can't print result Set");
		}
	}

	public void displayGradebookState()
	{
		try
		{
			if (conn == null || conn.isClosed())
				getConn();
			String sql = "SELECT students.*, scores.score, assignments.name FROM students"
					+ " INNER JOIN scores ON scores.id = students.id"
					+ " INNER JOIN assignments ON assignments.name = scores.assignment;";
			Statement st2 = conn.createStatement();		//we'll need a return value from the database, this will be the result set;
			ResultSet rs = st2.executeQuery(sql);		// the rs is a set of records that must be looped through
			displayResultSet(rs);
			conn.close();
		}catch (SQLException e) {
			System.out.println("error printing gb state");
			e.printStackTrace();
		}
	}
	public void displayCategory(String cat)
	{
		try
		{
			if (conn == null || conn.isClosed())
				getConn();
			String sql = "SELECT students.name, scores.score, assignments.name FROM students"
					+"WHERE weights.category = "+cat.toLowerCase().trim() + ";";
			//					+ " INNER JOIN scores" //ON scores.id = students.id"
			//					+ " INNER JOIN assignments"// ON assignments.name = scores.assignment"
			//					+ "INNER JOIN weights ON weights.category = assignments.category;";
			Statement st2 = conn.createStatement();		//we'll need a return value from the database, this will be the result set;
			ResultSet rs = st2.executeQuery(sql);		// the rs is a set of records that must be looped through
			displayResultSet(rs);
			conn.close();
		}catch (SQLException e) {
			System.out.println("error printing gb state");
			e.printStackTrace();
		}
	}
	public Connection getConn()
	{
		try {
			Class.forName("org.sqlite.JDBC");
			SQLiteConfig config = new SQLiteConfig();
			config.enforceForeignKeys(true);
			conn = DriverManager.getConnection(getDBURL(),config.toProperties());
		} catch (ClassNotFoundException e) {
			System.out.println("Error establishing connection to Database,"
					+ "please ensure you have the JDBC driver added to you classpath");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return conn;
	}

	public String getDBURL()
	{
		dbURL = "jdbc:sqlite:Gradebook.db";
		return dbURL;
	}

	public void showGrades() {

		Statement st;
		String sql;
		int quizGrades[] = new int[100];
		int hwGrades[] = new int[200];
		int examGrades[] = new int[100];
		float fin = 0;
		float quizSum = 0, quizDiv=0;
		float hwSum = 0, hwDiv=0;
		float examSum = 0, examDiv = 0;
		float accumSum = 0;
		float weight=0;
		int c = 1, i = 0, h = 0, l = 0;

		try {
			if(conn==null || conn.isClosed())
				getConn();
			st = conn.createStatement();
			sql = "SELECT students.id AS StudentID, Scores.id AS ScoreID, Scores.assignment AS ScoreName,"
					+ " assignments.name AS AssignmentName, assignments.category AS AssignmentCategory, Scores.score AS AssignmentScore,"
					+ "weights.weight AS Weight, weights.category AS WeightCategory "
					+ "FROM students, Scores, assignments, weights "
					+ "INNER JOIN students StudentID ON (StudentID = ScoreID) "
					+ "INNER JOIN Scores ScoreName ON (ScoreName = AssignmentName)"
					+ "INNER JOIN assignments AssignmentCategory on (AssignmentCategory = WeightCategory)"
					+ "WHERE AssignmentCategory = 'quiz'";
			ResultSet r = null;
			ResultSetMetaData rr = null;
			r = st.executeQuery(sql);
			rr = r.getMetaData();

			while(r.next()) {
				if(c != rr.getColumnCount()) {
					String col = r.getString(c);
					//
					c++;
				}
				else {
					quizGrades[i] = r.getInt(6);
					weight = r.getFloat(7);
					i++;
					c=1;
				}
			}
			for(Integer j : quizGrades) {
				if(j > 0) {
					quizSum+=j;
					quizDiv++;
				}

			}
			accumSum += ((quizSum/(100*quizDiv))*weight);
			i=0;
			sql = "SELECT students.id AS StudentID, Scores.id AS ScoreID, Scores.assignment AS ScoreName,"
					+ " assignments.name AS AssignmentName, assignments.category AS AssignmentCategory, Scores.score AS AssignmentScore,"
					+ "weights.weight AS Weight, weights.category AS WeightCategory "
					+ "FROM students, Scores, assignments, weights "
					+ "INNER JOIN students StudentID ON (StudentID = ScoreID) "
					+ "INNER JOIN Scores ScoreName ON (ScoreName = AssignmentName)"
					+ "INNER JOIN assignments AssignmentCategory on (AssignmentCategory = WeightCategory)"
					+ "WHERE AssignmentCategory = 'homework'";

			r = st.executeQuery(sql);
			rr = r.getMetaData();
			while(r.next()) {
				if(c != rr.getColumnCount()) {
					String col = r.getString(c);
					//
					c++;
				}
				else {
					hwGrades[h] = r.getInt(6);
					weight = r.getFloat(7);
					h++;
					c=1;
				}
			}
			for(Integer j : hwGrades) {
				if(j > 0) {
					hwSum+=j;
					hwDiv++;
				}
			}

			accumSum += ((hwSum/(100*hwDiv))*weight);
			i=0;
			sql = "SELECT students.id AS StudentID, Scores.id AS ScoreID, Scores.assignment AS ScoreName,"
					+ " assignments.name AS AssignmentName, assignments.category AS AssignmentCategory, Scores.score AS AssignmentScore,"
					+ "weights.weight AS Weight, weights.category AS WeightCategory "
					+ "FROM students, Scores, assignments, weights "
					+ "INNER JOIN students StudentID ON (StudentID = ScoreID) "
					+ "INNER JOIN Scores ScoreName ON (ScoreName = AssignmentName)"
					+ "INNER JOIN assignments AssignmentCategory on (AssignmentCategory = WeightCategory)"
					+ "WHERE AssignmentCategory = 'exam'";

			r = st.executeQuery(sql);
			rr = r.getMetaData();
			while(r.next()) {
				if(c != rr.getColumnCount()) {
					String col = r.getString(c);
					//
					c++;
				}
				else {
					examGrades[i] = r.getInt(6);
					weight = r.getFloat(7);
					i++;
					c = 1;
				}
			}
			for(Integer j : examGrades) {
				if(j > 0) {
					examSum+=j;
					examDiv++;
				}
			}

			accumSum += ((examSum/(100*examDiv))*weight);
			i=0;
			sql = "SELECT students.id AS StudentID, Scores.id AS ScoreID, Scores.assignment AS ScoreName,"
					+ " assignments.name AS AssignmentName, assignments.category AS AssignmentCategory, Scores.score AS AssignmentScore,"
					+ "weights.weight AS Weight, weights.category AS WeightCategory "
					+ "FROM students, Scores, assignments, weights "
					+ "INNER JOIN students StudentID ON (StudentID = ScoreID) "
					+ "INNER JOIN Scores ScoreName ON (ScoreName = AssignmentName)"
					+ "INNER JOIN assignments AssignmentCategory on (AssignmentCategory = WeightCategory)"
					+ "WHERE AssignmentCategory = 'final'";

			r = st.executeQuery(sql);
			rr = r.getMetaData();
			while(r.next()) {
				if(c != rr.getColumnCount()) {
					String col = r.getString(c);

					c++;
				}
				else {
					fin = r.getInt(6);
					weight = r.getFloat(7);
					i++;
					c=1;
				}
			}
			accumSum += (fin/100)*weight;
			System.out.println(accumSum*100);
		} catch (SQLException e) {

			e.printStackTrace();
		}


	}
}


