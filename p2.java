import java.sql.*;
import oracle.jdbc.*;
import java.sql.Timestamp;
import java.util.Date;
import java.io.*;
import oracle.jdbc.pool.OracleDataSource;
import java.awt.*;
import java.io.Console;

public class p2 {

	public static Connection conn;
	public static String username;

	public static ResultSet runQuery(String q) {
		try {
			Statement qStmt = conn.createStatement();
			ResultSet retVal = qStmt.executeQuery(q);
			/*while(retVal.next()) {
				System.out.println(retVal.getString(1));
			}*/
			return retVal;
		}
		catch (SQLException ex) { 
			System.out.println ("\n*** SQLException caught ***\n"+ ex);
		}
		catch (Exception e) {
			System.out.println ("\n*** other Exception caught ***\n"+e);
		}
		return null;
	}

	public static void log(String tableName, String operation) {
		//switch statement for getting key_value
		String key;

		switch(tableName) {

			case "students":
				key = "sid";
				break;
			
			case "courses":
				key = "dept_code, course#";
				break;
	
			case "prerequisites":
				key = "dept_code, course_no, pre_dept_code, pre_course_no";
				break;
			
			case "classes":
				key = "classid";
				break;

			case "enrollments":
				key = "sid, classid";
				break;

			default:
				key = "ERROR";


		}		
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());	
	
		String logQuery = "INSERT INTO logs VALUES (sequence_1.nextval, " + username + ", " + timestamp + ", " + tableName + ", " + operation + ", " + key + ")";
			
		runQuery(logQuery);
	}
	
	public static void displayTable() throws IOException {
		BufferedReader readKeyBoard;
		String tableSelection;
		String table;
		int numColumns;
		readKeyBoard = new BufferedReader(new InputStreamReader(System.in));			
		System.out.print("Table Options:\n 1- Students\n 2-Courses\n 3-Prereqs\n 4-Classes\n 5-Enrollments\n 6-Logs\n");
		System.out.print("Choose Table: ");
		tableSelection  = readKeyBoard.readLine();
		switch(tableSelection) {

			case "1":
				table = "students";
				numColumns = 6;
				break;
			
			case "2":
				table = "courses";
				numColumns = 3;
				break;
	
			case "3":
				table = "prerequisites";
				numColumns = 4;
				break;
			
			case "4":
				table = "classes";
				numColumns = 8;
				break;

			case "5":
				table = "enrollments";
				numColumns = 3;
				break;

			case "6":
				table = "logs";
				numColumns = 6;
				break;

			default:
				table = "NULL";
				numColumns = 0;
		}
		//System.out.println("TABLE: " + table);
		String displayQuery = "SELECT * FROM " + table;
		//System.out.println("DISPLAY QUERY: " + displayQuery);
		ResultSet rset = runQuery(displayQuery);
		try {
			while(rset.next()) {
				for(int i = 1; i <= numColumns; i++) {
					System.out.print(rset.getString(i) + " ");
				}
				System.out.println();
			}
		}
		catch (SQLException ex) { 
			System.out.println ("\n*** SQLException caught ***\n"+ ex);
		}
		catch (Exception e) {
			System.out.println ("\n*** other Exception caught ***\n"+e);
		}
		//log(table, "Display");
	}
	public static void clearScreen() {  
    	System.out.print("\033[H\033[2J");  
    	System.out.flush();  
		//code snippet taken from: 	https://stackoverflow.com/questions/2979383/java-clear-the-console
	}  
	public static void insertStudent(String sid, String firstName, String lastName, String status, String gpa, String emailAddress)  {
		double gradePoint = Double.parseDouble(gpa);
		try{
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO students(sid,firstName,lastName,status,gpa,email) VALUES (?, ?, ?, ?, ?, ?)");
			pstmt.setString(1,sid);
			pstmt.setString(2,firstName);
			pstmt.setString(3,lastName);
			pstmt.setString(4,status);
			pstmt.setDouble(5,gradePoint);
			pstmt.setString(6,emailAddress);

			pstmt.executeUpdate();
		}
		catch(SQLException e){
			System.out.println(e);
		}
		//String addQuery = "INSERT INTO students VALUES (" + sid + ", " + firstName + ", " + lastName + ", " + status + ", " + gpa + ", "+ emailAddress + ")"; 
		//runQuery(addQuery);
		//log here
	}
	public static void addStudent() throws IOException{
		clearScreen();
		BufferedReader readKeyBoard;
		String id;
		String first;
		String last;
		String standing;
		String grade;
		String mail;
		readKeyBoard = new BufferedReader(new InputStreamReader(System.in));

		System.out.print("Please insert student id: ");
		id  = readKeyBoard.readLine();

		System.out.print("Please insert first name: ");
		first = readKeyBoard.readLine();

		System.out.print("Please insert last name: ");
		last = readKeyBoard.readLine();

		System.out.print("Please insert status: ");
		standing = readKeyBoard.readLine();

		System.out.print("Please insert gpa: ");
		grade = readKeyBoard.readLine();

		System.out.print("Please insert email address (before the @): ");
		mail = readKeyBoard.readLine();

		insertStudent(id,first,last,standing,grade,mail);

	}
	public static void main ( String args[] ) throws IOException {

		Console console = System.console();
		boolean loggedIn = false;

		//login screen...
		do {
			try{
				//connection to Oracle server...
				OracleDataSource ds = new oracle.jdbc.pool.OracleDataSource();
				ds.setURL("jdbc:oracle:thin:@castor.cc.binghamton.edu:1521:ACAD111");

				System.out.println("Please log in to your PODS account...");
				BufferedReader readKeyBoard;
				String password;
				readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
				System.out.print("Username: ");
				username = readKeyBoard.readLine();
				System.out.print("Password: ");
				//password = readKeyBoard.readLine();
				char [] passwordChars = console.readPassword();
				password = new String(passwordChars);
				conn = ds.getConnection(username, password);
				//Statement stmt = conn.createStatement();
				//stmt.executeQuery("INSERT INTO students VALUES ('B002', 'jack', 'roth', 'freshman', 4.0, 'jrothbe4@bing.edu')");
				//stmt.executeQuery("INSERT INTO students VALUES ('B003', 'john', 'smith', 'freshman', 3.0, 'jsmith@bing.edu')");
				//break;
			}
			
			catch (SQLException ex) { 
				System.out.println ("\n*** SQLException caught ***\n"+ ex);
				continue;
			}
			catch (Exception e) {
				System.out.println ("\n*** other Exception caught ***\n"+e);
				continue;
			}
			//break;
			loggedIn = true;
			clearScreen();
		} while(!loggedIn);
		
		//Main Menu...
		/*Menu Options: */
	/* 	1: Display a table
	 * 		1. show students
	 * 		2. show courses
	 * 		3. show prereqs
	 * 		4. show classes
	 * 		5. show enrollments 
	 *
	 * 	2: Add a Student
	 *	3. Display Student Enrollment Info
	 *	4. Display Prereqs
	 *	5. Display Class Enrollment Info
	 *	6. Enroll Student in Class
	 *	7. Drop Student from Class
	 *	8. Expel Student from University*/
		
		System.out.print("Menu Options:\n 1- Display a Table\n 2- Add Students\n 3- Display Student Enrollment Info\n 4- Display Prereq\n 5- Display Class Enrollment Info\n 6- Enroll Student in Class\n 7- Drop Student from Class\n 8- Expel Student from University\n");
		
		while(true) {	
			BufferedReader readKeyBoard;
			String temp;
			readKeyBoard = new BufferedReader(new InputStreamReader(System.in));			
			System.out.print("Choose Option: ");
			temp  = readKeyBoard.readLine();
			if (temp.equals("exit")) {
				System.exit(0);
			}
			int optionNumber = Integer.parseInt(temp);

			switch(optionNumber) {

				case 1:
					displayTable();
					break;
				case 2:

					addStudent();
					break;
				default:
					System.out.println("Invalid input, try again");
					continue;
			}
		}
	}		
}