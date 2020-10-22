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

	/**
	  * Method to run queries
	  * @param q query string to execute  *
	  * @return ResultSet This returns the query result 
	  */
	public static ResultSet runQuery(String q) {
		try {
			Statement qStmt = conn.createStatement();
			ResultSet retVal = qStmt.executeQuery(q);
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
	/**
	  * Method to display a database table based on table name given by user
	  * @param None
	  * @return Nothing */
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
		String displayQuery = "SELECT * FROM " + table;
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
			return;
		}
		System.out.println("------------End of Results-----------");
	}
	/**
	 * Method to clear terminal to avoid clutter
	 * @param None  
	 * @return Nothing */

	public static void clearScreen() {  
    	System.out.print("\033[H\033[2J");  
    	System.out.flush();  
		//code snippet taken from: 	https://stackoverflow.com/questions/2979383/java-clear-the-console
	}
	/**
	  * Helper method to insert student into students table
	  * @param sid Student id
	  * @param firstName Students first name *
	  * @param lastName Students last name 
	  * @param status Freshman, Sophomore, Junior, or Senior 
	  * @param gpa Student gpa 0 - 4 
	  * @param emailAddress Students email address 
	  * @return Nothing */  
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
			return;
		}
		catch (NumberFormatException e){
			System.out.println(e);
			return;
		}
		System.out.println("---Enroll Successful---");
	}
	/** Method to enroll a student in the university. Takes in inputs from the user and passes them into the helper method insertStudent()
	 * @param none 
	 * @return Nothing 
	 */
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

		System.out.print("Please insert email address: ");
		mail = readKeyBoard.readLine();

		insertStudent(id,first,last,standing,grade,mail);
		

	}
	/**
	 * Helper method for displayClasses
	 * @param sid
	 * @return Nothing
	 */
	public static void showClasses(String sid){
		int c = 1;
		try {
			PreparedStatement stmt1 = conn.prepareStatement("SELECT sid FROM students WHERE sid  = ?");
			stmt1.setString(1,sid);
			ResultSet rset1 = stmt1.executeQuery();
			if(rset1.next() == false) {
				System.out.println("The sid is invalid.");
				//proceed();
				return;
			}
			stmt1 = conn.prepareStatement("SELECT sid FROM enrollments WHERE sid  = ?");
			stmt1.setString(1,sid);
			rset1 = stmt1.executeQuery();
			if(rset1.next() == false) {
				System.out.println("The student has not taken any courses.");
				//proceed();
				return;
			}
			PreparedStatement stmt = conn.prepareStatement("SELECT students.sid, firstname, lastname, status, classes.classid, classes.dept_code, classes.course_no, title  FROM students JOIN enrollments ON enrollments.sid = students.sid JOIN classes ON classes.classid = enrollments.classid JOIN courses ON courses.dept_code = classes.dept_code AND courses.course_no = classes.course_no WHERE students.sid = ?");
			stmt.setString(1, sid);
			ResultSet rset = stmt.executeQuery();
			while(rset.next()) {
				if(c == 1) {
					System.out.print(rset.getString(1) + " ");
					System.out.print(rset.getString(2) + " ");
					System.out.print(rset.getString(3) + " ");
					System.out.println(rset.getString(4) + " ");
					System.out.println("-----------------------");
				}
				System.out.print(rset.getString(5) + " ");
				System.out.print(rset.getString(6) + rset.getString(7) + " ");	
				System.out.println(rset.getString(8) + "\n"); 
				c = 2;
			}
			//proceed();
		}
		catch (SQLException ex) { 
			System.out.println ("\n*** SQLException caught ***\n"+ ex);
		}
		catch (Exception e) {
			System.out.println ("\n*** other Exception caught ***\n"+e);
		}
	}
	/** Takes input from the user and calls the helper function showClasses() to display the classes taken by the selected student
	 * @param none
	 * @return Nothing 
	 */
	public static void displayClasses() throws IOException {
		System.out.print("Please enter sid: ");
		BufferedReader readKeyBoard;
		String sid;
		readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
		sid = readKeyBoard.readLine();
		clearScreen();
		showClasses(sid);
		System.out.println("------------End of Results-----------");
	}
	/**
	 * After a query is run, this method just allows the user to view his results, and when ready, proceed back to the main menu
	 * @throws IOException
	 * @return Nothing
	 */
	public static void proceed() throws IOException{
		BufferedReader readKeyBoard;
		readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
		//System.out.println("------------End of Results-----------");
		System.out.print("Press Enter to continue...");
		readKeyBoard.readLine();
		clearScreen();
		return;
	}
	/**
	 * Displays prerequisite information based on the student specified by the user. User selection is passed in to the helper method prereqs()
	 * @throws IOException
	 * @return Nothing
	 */
	public static void showPrereqs() throws IOException {
		BufferedReader readKeyBoard;
		String deptCode;
		String courseNo;
		readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Please Enter Dept Code: ");
		deptCode = readKeyBoard.readLine();
		System.out.print("Please Enter Course #: ");
		courseNo = readKeyBoard.readLine();
		clearScreen();
		System.out.println("---" + deptCode + courseNo + " Prereqs:---\n");
		System.out.println(prereqs(deptCode, courseNo));
		System.out.println("------------End of Results-----------");
	}
	/**
	 * Helper method for showPrereqs(). Runs the query based on the parameters passed in. Takes in a course numer and department code and recursively collects the whole chain of prerequisites for the specified course
	 * @param deptCode
	 * @param courseNo
	 * @return CourseNo
	 * @throws IOException
	 */
	public static String prereqs(String deptCode, String courseNo) throws IOException {
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT prerequisites.pre_dept_code, prerequisites.pre_course_no FROM prerequisites JOIN courses ON prerequisites.dept_code = courses.dept_code AND prerequisites.course_no = courses.course_no WHERE courses.dept_code = ? AND courses.course_no = ?");
			stmt.setString(1, deptCode);
			stmt.setString(2, courseNo);
			ResultSet rset = stmt.executeQuery();
			while(rset.next()) {
				String pre_dept_code = rset.getString(1);
				String pre_course_no = rset.getString(2);
				return (pre_dept_code + pre_course_no + "\n" + prereqs(pre_dept_code, pre_course_no));
			}
			return "";
		}
		catch (SQLException ex) { 
			System.out.println ("\n*** SQLException caught ***\n"+ ex);
		}
		catch (Exception e) {
			System.out.println ("\n*** other Exception caught ***\n"+e);
		}
		return courseNo;
	}
	/**
	 * This function takes user input from the interface to specify which class, and then calls the helper function classEnrollments() to display the students enrolled in a particular class
	 * @throws IOException
	 * @return Nothing
	 */
	public static void showClassEnrollments() throws IOException {
		BufferedReader readKeyBoard;
		String classid;
		readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Please Enter Class ID: ");
		classid = readKeyBoard.readLine();
		clearScreen();
		classEnrollments(classid);
		System.out.println("------------End of Results-----------");
		return;
	}
	/**
	 * Based on the classid, this method shows which students are enrolled in that class
	 * @param classid
	 */
	public static void classEnrollments(String classid) {
		int c = 1;
		try {
			PreparedStatement stmt1 = conn.prepareStatement("SELECT classid FROM classes WHERE classes.classid = ?");
			stmt1.setString(1, classid);
			ResultSet rset1 = stmt1.executeQuery();
			if(rset1.next() == false) {
				System.out.println("The classid is invalid.");
				return;
			}
			stmt1 = conn.prepareStatement("SELECT sid FROM enrollments WHERE classid  = ?");
			stmt1.setString(1, classid);
			rset1 = stmt1.executeQuery();
			if(rset1.next() == false) {
				System.out.println("No student is enrolled in this class.");
				return;
			}
			PreparedStatement stmt = conn.prepareStatement("SELECT classes.classid, title, semester, year, students.sid, firstName, lastName FROM classes JOIN enrollments ON classes.classid = enrollments.classid JOIN students ON students.sid = enrollments.sid JOIN courses ON courses.dept_code = classes.dept_code AND courses.course_no = classes.course_no WHERE classes.classid = ?");
			stmt.setString(1, classid);
			ResultSet rset = stmt.executeQuery();
			while(rset.next()) {
				if(c == 1) {
					System.out.print("Class Info: ");
					System.out.print(rset.getString(1) + " ");
					System.out.print(rset.getString(2) + " ");
					System.out.print(rset.getString(3) + " ");
					System.out.println(rset.getString(4) + " ");
					System.out.println("-----------Students------------");
				}
				System.out.print(rset.getString(5) + " ");
				System.out.print(rset.getString(6) + " ");	
				System.out.println(rset.getString(7) + "\n"); 
				c = 2;
			}
		}
		catch (SQLException ex) { 
			System.out.println ("\n*** SQLException caught ***\n"+ ex);
		}
		catch (Exception e) {
			System.out.println ("\n*** other Exception caught ***\n"+e);
		}
		return;
	}
	/**
	 * This method enrolls students into courses. It takes input from the user, who specifies the student and classid to enroll him into, and uses that information to call the helper method enrollStudent()
	 * @throws IOException
	 */
	public static void enroll() throws IOException {
		BufferedReader readKeyBoard;
		String classid;
		String sid;
		readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Please Enter sid: ");
		sid = readKeyBoard.readLine();
		System.out.print("Please Enter Class ID: ");
		classid = readKeyBoard.readLine();
		clearScreen();
		enrollStudent(sid, classid);
		return;
	}
	/**
	 * Run a query to enroll students into a class based on sid and class id 
	 * @param sid
	 * @param classid
	 */
	public static void enrollStudent(String sid, String classid) {
		//check if student exists
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT sid FROM students where sid = ?");
			stmt.setString(1, sid);
			ResultSet rset = stmt.executeQuery();
			if(!rset.next()) {
				System.out.println("sid not found");
				return;
			}
			stmt = conn.prepareStatement("SELECT classid FROM classes where classid = ?");
			stmt.setString(1, classid);
			rset = stmt.executeQuery();
			if(!rset.next()) {
				System.out.println("The classid is invalid");
				return;
			}
			stmt =  conn.prepareStatement("SELECT * FROM ((SELECT CONCAT(pre_dept_code, pre_course_no) AS cid FROM prerequisites JOIN courses ON courses.dept_code = prerequisites.dept_code AND courses.course_no = prerequisites.course_no JOIN classes ON classes.dept_code = courses.dept_code AND classes.course_no = courses.course_no WHERE classid = ?)) WHERE cid NOT IN (SELECT * FROM ((SELECT CONCAT(courses.dept_code, courses.course_no) AS cid FROM courses JOIN classes ON classes.dept_code = courses.dept_code AND classes.course_no = courses.course_no JOIN enrollments ON enrollments.classid = classes.classid WHERE sid = ? AND lgrade < 'D' AND lgrade IS NOT NULL AND CONCAT(courses.dept_code, courses.course_no) IN (SELECT * FROM ((SELECT CONCAT(pre_dept_code, pre_course_no) AS cid FROM prerequisites JOIN courses ON courses.dept_code = prerequisites.dept_code AND courses.course_no = prerequisites.course_no JOIN classes ON classes.dept_code = courses.dept_code AND classes.course_no = courses.course_no WHERE classid = ?))))))"); 

			stmt.setString(1, classid);
			stmt.setString(2, sid);
			stmt.setString(3,classid);
			rset = stmt.executeQuery();
			if(rset.next()) {
				System.out.println("Prerequisite courses have not been completed");
				return;
			}
			stmt = conn.prepareStatement("SELECT sid, CONCAT(semester, year) from enrollments JOIN classes ON classes.classid = enrollments.classid WHERE sid = ? AND CONCAT(semester, year) IN (SELECT CONCAT(semester, year) FROM classes WHERE classid = ?) GROUP BY sid, semester, year HAVING COUNT(*) >= 3");
			stmt.setString(1, sid);
			stmt.setString(2, classid);
			rset = stmt.executeQuery();
			if(rset.next()) {
				System.out.println("You are overloaded");
				return;
			}
			stmt = conn.prepareStatement("SELECT classid FROM classes where classid = ? AND limit = class_size");
			stmt.setString(1, classid);
			rset = stmt.executeQuery();
			if(rset.next()) {
				System.out.println("The class is full");
				return;
			}
			stmt = conn.prepareStatement("SELECT classes.classid FROM classes JOIN enrollments ON enrollments.classid = classes.classid JOIN students ON students.sid = enrollments.sid WHERE classes.classid = ? AND students.sid = ?");
			stmt.setString(1, classid);
			stmt.setString(2, sid);
			rset = stmt.executeQuery();
			if(rset.next()) {
				System.out.println("The student is already in the class");
				return;
			}
			stmt = conn.prepareStatement("INSERT INTO enrollments VALUES(?, ?, NULL)");
			stmt.setString(1, sid);
			stmt.setString(2, classid);
			rset = stmt.executeQuery();
			BufferedReader readKeyBoard;
			readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("---Enroll Successful---");
		}
		catch (SQLException ex) { 
			System.out.println ("\n*** SQLException caught ***\n"+ ex);
			return;
		}
		catch (Exception e) {
			System.out.println ("\n*** other Exception caught ***\n"+e);
			return;
		}

	}
	/**
	 * This method takes in input from the user who specifies a student and which class to drop him from by calling the helper function dropStudents()
	 * @throws IOException
	 */
	public static void drop() throws IOException {
		BufferedReader readKeyBoard;
		String classid;
		String sid;
		readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Please Enter sid: ");
		sid = readKeyBoard.readLine();
		System.out.print("Please Enter Class ID: ");
		classid = readKeyBoard.readLine();
		clearScreen();
		dropStudent(sid, classid);
		return;
	}
	/**
	 * Runs the sql query to drop the specified student from the specified class
	 * @param sid
	 * @param classid
	 */
	public static void dropStudent(String sid, String classid) {
		try
		{
			PreparedStatement stmt = conn.prepareStatement("SELECT sid FROM students where sid = ?");
			stmt.setString(1, sid);
			ResultSet rset = stmt.executeQuery();
			if(!rset.next()) {
				System.out.println("sid not found");
				return;
			}
			stmt = conn.prepareStatement("SELECT classid FROM classes where classid = ?");
			stmt.setString(1, classid);
			rset = stmt.executeQuery();
			if(!rset.next()) {
				System.out.println("classid not found");
				return;
			}
			stmt = conn.prepareStatement("SELECT sid FROM enrollments where sid = ? AND classid = ?");
			stmt.setString(1, sid);
			stmt.setString(2, classid);
			rset = stmt.executeQuery();
			if(!rset.next()) {
				System.out.println("The student is not enrolled in the class");
				return;
			}
			stmt = conn.prepareStatement("SELECT * FROM (SELECT CONCAT(pre_dept_code, pre_course_no) AS pres FROM prerequisites JOIN courses ON courses.dept_code = prerequisites.dept_code AND courses.course_no = prerequisites.course_no JOIN classes ON classes.dept_code = courses.dept_code AND classes.course_no = courses.course_no JOIN enrollments ON enrollments.classid = classes.classid WHERE sid = ?) WHERE pres IN (SELECT CONCAT(dept_code, course_no) AS p FROM classes WHERE classid = ?)");
			stmt.setString(1, sid);
			stmt.setString(2, classid);
			rset = stmt.executeQuery();
			if(rset.next()) {
				System.out.println("The drop is not permitted because another class uses it as a prerequisite");
				return;
			}
			stmt = conn.prepareStatement("DELETE FROM enrollments WHERE sid = ? AND classid = ?");
			stmt.setString(1, sid);
			stmt.setString(2, classid);
			rset = stmt.executeQuery();
			
			stmt = conn.prepareStatement("SELECT * FROM enrollments WHERE sid = ?");
			stmt.setString(1, sid);
			rset = stmt.executeQuery();
			if(!rset.next()) {
				System.out.println("This student is enrolled in no class");
			}

			stmt = conn.prepareStatement("SELECT * FROM classes WHERE classid = ?");
			stmt.setString(1, classid);
			rset = stmt.executeQuery();
			while(rset.next()) {
				if(rset.getString(8).equals("0")) {
					System.out.println("The class now has no students.");
				}
			}

			BufferedReader readKeyBoard;
			readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("---DROP Successful---");
		}
		catch (SQLException ex) { 
			System.out.println ("\n*** SQLException caught ***\n"+ ex);
		}
		catch (Exception e) {
			System.out.println ("\n*** other Exception caught ***\n"+e);
		}
	}
	/**
	 * Takes user input to specify which student to delete from the university database by calling the helper method deleteStudent on the selected student
	 * @throws IOException
	 */
	public static void delete() throws IOException {
		BufferedReader readKeyBoard;
		String sid;
		readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Please Enter sid: ");
		sid = readKeyBoard.readLine();
		clearScreen();
		deleteStudent(sid);
		return;
	}
	/**
	 * Runs the sql query to delete the student from the university database specified by the sid
	 * @param sid
	 */
	public static void deleteStudent(String sid) {
		try 
		{
			PreparedStatement stmt = conn.prepareStatement("SELECT sid FROM students where sid = ?");
			stmt.setString(1, sid);
			ResultSet rset = stmt.executeQuery();
			if(!rset.next()) {
				System.out.println("sid not found");
				return;
			}
			stmt = conn.prepareStatement("SELECT classes.classid, class_size FROM enrollments JOIN classes ON classes.classid = enrollments.classid WHERE sid = ?");
			stmt.setString(1, sid);
			rset = stmt.executeQuery();
			while(rset.next()) {
				if(rset.getString(2).equals("1")) {
					System.out.println("The class " + rset.getString(1) + " now has no students.");
				}
			} 

			stmt = conn.prepareStatement("DELETE FROM students WHERE sid = ?");
			stmt.setString(1, sid);
			rset = stmt.executeQuery();

			BufferedReader readKeyBoard;
			readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("---REMOVAL Successful---");
		}
		catch (SQLException ex) { 
			System.out.println ("\n*** SQLException caught ***\n"+ ex);
		}
		catch (Exception e) {
			System.out.println ("\n*** other Exception caught ***\n"+e);
		}
	}
	/**
	 * Main function
	 * @param args
	 * @throws IOException
	 */
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
				char [] passwordChars = console.readPassword();
				password = new String(passwordChars);
				conn = ds.getConnection(username, password);
				
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
		
		while(true) {	
			BufferedReader readKeyBoard;
			String temp;
			readKeyBoard = new BufferedReader(new InputStreamReader(System.in));			
			System.out.print("Menu Options:\n 1- Display a Table\n 2- Add Students\n 3- Display Student Enrollment Info\n 4- Display Prereq\n 5- Display Class Enrollment Info\n 6- Enroll Student in Class\n 7- Drop Student from Class\n 8- Expel Student from University\n");
			System.out.print("Choose Option: ");
			temp  = readKeyBoard.readLine();
			if (temp.equals("exit")) {
				System.exit(0);
			}
			int optionNumber = Integer.parseInt(temp);
			clearScreen();
			switch(optionNumber) {

				case 1:
					displayTable();
					proceed();
					break;
				case 2:

					addStudent();
					proceed();
					break;

				case 3:
					displayClasses();
					proceed();
					break;

				case 4:
					showPrereqs();
					proceed();
					break;

				case 5:
					showClassEnrollments();
					proceed();
					break;
				
				case 6:
					enroll();
					proceed();
					break;
				
				case 7:
					drop();
					proceed();
					break;
				
				case 8:
					delete();
					proceed();
					break;

				default:
					System.out.println("Invalid input, try again");
					continue;
			}
		}
	}		
}

