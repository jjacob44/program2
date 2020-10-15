import java.sql.*;
import oracle.jdbc.*;
import java.sql.Timestamp;
import java.util.Date;
import java.io.*;
import oracle.jdbc.pool.OracleDataSource;
import java.awt.*;
import java.io.Console;

public class p2 {

	public void log(String who, String tableName, String operation) {
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

	}
	
	public static void displayTable() {
		System.out.println("Display Table");
	}
	public static void clearScreen() {  
    	System.out.print("\033[H\033[2J");  
    	System.out.flush();  
		//code snippet taken from: 	https://stackoverflow.com/questions/2979383/java-clear-the-console
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
				String username;
				String password;
				readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
				System.out.print("Username: ");
				username = readKeyBoard.readLine();
				System.out.print("Password: ");
				//password = readKeyBoard.readLine();
				char [] passwordChars = console.readPassword();
				password = new String(passwordChars);
				Connection conn = ds.getConnection(username, password);
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
			
				default:
					System.out.println("Invalid input, try again");
					continue;
			}
		}
	}		
}