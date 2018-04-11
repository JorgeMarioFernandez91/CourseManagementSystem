package systemUserOperations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import authenticatedUsers.LoggedInAuthenticatedUser;
import customDatatypes.EvaluationTypes;
import customDatatypes.Marks;
import customDatatypes.NotificationTypes;
import customDatatypes.SystemState;
import customDatatypes.Weights;
import offerings.CourseOffering;
import offerings.ICourseOffering;
import offerings.OfferingFactory;
import registrar.ModelRegister;
import systemUserModelFactories.StudentModelFactory;
import systemUsers.AdminModel;
import systemUsers.IStudentModel;
import systemUsers.InstructorModel;
import systemUsers.StudentModel;
import systemUsers.SystemUserModel;

public class AdminOperations {
	/**
	 * Start system sets the state to on and populates the database with courses and
	 * users read from file
	 * 
	 * @throws IOException
	 */
	public boolean startSystem() throws IOException {

		// add all the courses and its associated students
		OfferingFactory factory = new OfferingFactory();

		// course 1 - from note_1
		BufferedReader br = new BufferedReader(new FileReader("note_1.txt"));
		if (factory.createCourseOffering(br) == null)
			return false;

		// course 2 - from note_2
		br = new BufferedReader(new FileReader("note_2.txt"));
		if (factory.createCourseOffering(br) == null)
			return false;
		
		// load in new users
		loadNewStudents();

		//associated students to courses they have already registered in
		loadCourses();
		
		//associate student to their grades save in DB
		loadGrades();
		
		//associates students to their notification type saved in the DB
		syncNotificationPreference();
		
		// set state to start
		SystemState.start();
		br.close();
		return true;

	}

	/**
	 * Start system sets the state to on and saves the courses and users from
	 * database back to files
	 * 
	 * @throws IOException
	 */
	public void stopsystem(LoggedInAuthenticatedUser lu) {

		// set state to stop
		SystemState.stop();
		
		// switch off system
		System.exit(0);
	}

	/**
	 * This method adds a new user to the system.
	 * 
	 * @param type
	 *            this is the user type that is being added
	 * @throws IOException
	 */
	public void createNewUser(String type) throws IOException {
		// create new user for add
		SystemUserModel userToAdd;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		// switch to create the appropriate user
		switch (type) {

		case "admin": // create an administrator
			// initialize the user as an admin
			userToAdd = new AdminModel();

			queryBasicUserInfo(userToAdd);

			// checks if user ID has been used or not
			if (ModelRegister.getInstance().checkIfUserHasAlreadyBeenCreated(userToAdd.getID())) {
				System.out.println("Error: Admin being added is already in system:");

				SystemUserModel foundUser = ModelRegister.getInstance().getRegisteredUser(userToAdd.getID());

				System.out.println("User in system is:\tID: " + foundUser.getID() + "\tFull Name: "
						+ foundUser.getName() + " " + foundUser.getSurname());
			} else {
				ModelRegister.getInstance().registerUser(userToAdd.getID(), userToAdd);
				System.out.println("User " + userToAdd.getName() + " " + userToAdd.getSurname() + ", "
						+ userToAdd.getID() + " has been added.");
			}
			break;

		case "student":
			// initialize user as student
			userToAdd = new StudentModel();

			queryBasicUserInfo(userToAdd);

			// checks if user ID has been used
			if (ModelRegister.getInstance().checkIfUserHasAlreadyBeenCreated(userToAdd.getID())) {
				System.out.println("Error: Student being added is already in system:");

				SystemUserModel foundUser = ModelRegister.getInstance().getRegisteredUser(userToAdd.getID());

				System.out.println("User in system is:\tID: " + foundUser.getID() + "\tFull Name: "
						+ foundUser.getName() + " " + foundUser.getSurname());
			} else {
				List<ICourseOffering> eligibleList = new ArrayList<ICourseOffering>();
				((StudentModel) userToAdd).setEvaluationEntities(new HashMap<ICourseOffering, EvaluationTypes>());
				((StudentModel) userToAdd).setCoursesEnrolled(new ArrayList<ICourseOffering>());
				((StudentModel) userToAdd).setPerCourseMarks(new HashMap<ICourseOffering, Marks>());


				// query for courses the student is eligible for
				while (true) {
					System.out.println("Please enter course code of an eligible course for this student:");
					String courseID = br.readLine();

					// if course exists, add to list. else, print error and request a new course
					// code
					if (ModelRegister.getInstance().checkIfCourseHasAlreadyBeenCreated(courseID)) {
						CourseOffering targetCourse = ModelRegister.getInstance().getRegisteredCourse(courseID);
						targetCourse.getStudentsAllowedToEnroll().add((StudentModel) userToAdd);
						eligibleList.add(targetCourse);
						
						System.out.println("Please choose the student type: (1-4)");
						System.out.println(
								"1. Full Time, For Credit\n2. Full Time, For Audit\n3. Part Time, For Credit\n4. Part Time, For Audit");
						switch (br.readLine()) {
						case "1":
							((StudentModel) userToAdd).getEvaluationEntities().put(targetCourse,
									EvaluationTypes.FULL_CREDIT);
							break;
						case "2":
							((StudentModel) userToAdd).getEvaluationEntities().put(targetCourse,
									EvaluationTypes.FULL_AUDIT);
							break;
						case "3":
							((StudentModel) userToAdd).getEvaluationEntities().put(targetCourse,
									EvaluationTypes.PART_CREDIT);
							break;
						case "4":
							((StudentModel) userToAdd).getEvaluationEntities().put(targetCourse,
									EvaluationTypes.PART_AUDIT);
							break;
						}
						
						for (NotificationTypes p : NotificationTypes.values()){
							((IStudentModel) userToAdd).setNotificationType(NotificationTypes.EMAIL);
					         break;
					    }

						String line;
						FileWriter fw = new FileWriter("new_users_list.txt",true);
						
						line = courseID + "\n";
						fw.write(line);
						line = userToAdd.getName() + "\t" + userToAdd.getSurname() + "\t" + userToAdd.getID() + "\t" + ((StudentModel) userToAdd).getEvaluationEntities().get(targetCourse).getText() + "\n";
						fw.write(line);
						fw.close();

						//extract evaluation entities from the course object
						Weights courseWeighting = targetCourse.getEvaluationStrategies().get(((StudentModel)userToAdd).getEvaluationEntities().get(targetCourse));
						courseWeighting.initializeIterator();
						
						//adds the entities to a mark object
						Marks m = new Marks();
						while (courseWeighting.hasNext()) {
							m.addToEvalStrategy(courseWeighting.getNextEntry().getKey(), null);
						}
						
						//adds the new course and marks to perCourseMarks for student
						((StudentModel) userToAdd).getPerCourseMarks().put(targetCourse, m);

						// if eligible for multiple course, hit Y to add more courses
						System.out.println("Add another eligible course? (Y/N)");

						// if no new courses to add, break loop
						if (br.readLine().equals("n"))
							break;
					} else
						System.out.print("Error: Course code entered does not exist. ");
				}
				// save course list back to user
				((StudentModel) userToAdd).setCoursesAllowed(eligibleList);

				// register user
				ModelRegister.getInstance().registerUser(userToAdd.getID(), userToAdd);

				System.out.println("User " + userToAdd.getName() + " " + userToAdd.getSurname() + ", "
						+ userToAdd.getID() + " has been added.");
			}
			break;

		case "instructor":
			// initialize user as instructor
			userToAdd = new InstructorModel();

			queryBasicUserInfo(userToAdd);

			// check if user ID has been used
			if (ModelRegister.getInstance().checkIfUserHasAlreadyBeenCreated(userToAdd.getID())) {
				System.out.println("Error: Instructor being added is already in system:");
				SystemUserModel foundUser = ModelRegister.getInstance().getRegisteredUser(userToAdd.getID());
				System.out.println("User in system is:\tID: " + foundUser.getID() + "\tFull Name: "
						+ foundUser.getName() + " " + foundUser.getSurname());
			} else {
				List<ICourseOffering> courseList = new ArrayList<ICourseOffering>();

				// query for courses the instructor teaches
				while (true) {
					System.out.println("Please enter course code for the new instructor:");
					String courseID = br.readLine();

					// if course exists, add to list. else, print error and request a new course
					// code
					if (ModelRegister.getInstance().checkIfCourseHasAlreadyBeenCreated(courseID)) {
						CourseOffering targetCourse = ModelRegister.getInstance().getRegisteredCourse(courseID);
						targetCourse.addInstructor((InstructorModel) userToAdd);
						courseList.add(targetCourse);

						// if teaches multiple course, hit Y to add more courses
						System.out.println("Add another course? (Y/N");

						// if no new courses to add, break loop
						if (br.readLine().equals("n"))
							break;
					} else
						System.out.print("Error: Course code entered does not exist. ");
				}
				// save course list back to user
				((InstructorModel) userToAdd).setIsTutorOf(courseList);

				// register user
				ModelRegister.getInstance().registerUser(userToAdd.getID(), userToAdd);

				System.out.println("User " + userToAdd.getName() + " " + userToAdd.getSurname() + ", "
						+ userToAdd.getID() + " has been added.");
			}
			break;
		}
	}

	/**
	 * Private method to query administrator for the new user's basic information.
	 * 
	 * @param user
	 *            the new user to be registered. It may be a new admin, student, or
	 *            instructor
	 * @throws IOException
	 */
	private void queryBasicUserInfo(SystemUserModel user) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please enter the following user details\n");

		System.out.println("User ID: (4-digit code to identify user)");
		user.setID(br.readLine());

		System.out.println("User's Name: (The user's first name)");
		user.setName(br.readLine());

		System.out.println("User's Surname: (The user's last name)");
		user.setSurname(br.readLine());

	}
	
	private void loadCourses() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("enroll_courses.txt"));
		String line = br.readLine();
		String studentID, courseID;
		
		while(line!=null) {
			studentID = line.split("\t")[0];
			courseID = line.split("\t")[1];
			StudentModel student = (StudentModel) ModelRegister.getInstance().getRegisteredUser(studentID);
			CourseOffering course = ModelRegister.getInstance().getRegisteredCourse(courseID);
			
			if(!student.getPerCourseMarks().containsKey(course)) {
				//extract evaluation entities from the course object
				Weights courseWeighting = course.getEvaluationStrategies().get(student.getEvaluationEntities().get(course));
				courseWeighting.initializeIterator();
				
				//adds the entities to a mark object
				Marks m = new Marks();
				while (courseWeighting.hasNext()) {
					m.addToEvalStrategy(courseWeighting.getNextEntry().getKey(), null);
				}
				
				//adds the new course and marks to perCourseMarks for student
				student.getPerCourseMarks().put(course, m);
			}
						
			List<ICourseOffering> courseEnrolled = student.getCoursesEnrolled();
			courseEnrolled.add(course);
			student.setCoursesEnrolled(courseEnrolled);
			
			List<StudentModel> allStudents = course.getStudentsEnrolled();
			allStudents.add(student);
			course.setStudentsEnrolled(allStudents);
			
			line = br.readLine();
		}
		
		br.close();
	}
	/**
	 * Load the grades for each student in each course from the data base. 
	 * This ensures that students are up to date even if the system get turned off and on again
	 */
	private void loadGrades() throws IOException {
		int j = 0;
		int i = 0;
		String studentID, courseID;
	    String line;
	    //search for every student in every course
		for (CourseOffering course : ModelRegister.getInstance().getAllCourses()) {
			for (StudentModel student : course.getStudentsAllowedToEnroll()){
				//read in from DB
				BufferedReader freader = new BufferedReader(new FileReader("SavedGrades.txt"));
				line = freader.readLine();
				//while not end of BD
				while (line != null){
					//if line from DB and student match then update that students info
					if (line.split("\t")[0].equals(student.getID()) && line.split("\t")[1].equals(course.getCourseID())){
						studentID = line.split("\t")[0];
						courseID = line.split("\t")[1];
						
						Map<ICourseOffering, Marks> perCourseMarks = student.getPerCourseMarks();						
						CourseOffering course1 = ModelRegister.getInstance().getRegisteredCourse(courseID);
						Marks a = new Marks();
						//get student's assessments and grades which begin at index 2 and 3 respectively
						for (i = 2, j = 3; i <= 6; i = i + 2, j = j + 2){
							//if student eval type is 'PC' then they only have 1 assessment, so care not to get null pointer exception
							if (student.getEvaluationEntities().get(course1).getText().equals("PC") && i == 4){
								break;
							}
							//if 'null' string found in DB then just skip it so not to get an error
							else if (!line.split("\t")[j].equals("null")){
								a.addToEvalStrategy(line.split("\t")[i], Double.parseDouble(line.split("\t")[j]));
							}
							else if (line.split("\t")[j].equals("null")){
								a.addToEvalStrategy(line.split("\t")[i], null);
							}
						}
						//update map with info
						perCourseMarks.put(course,  a);
						//update student object with new updated map
						student.setPerCourseMarks(perCourseMarks);
					}
					//next line
					line = freader.readLine();
				}
				//close reader
				freader.close();
			}
		}
	}
	
	private void syncNotificationPreference() throws IOException{
		
		int j = 0;
		int i = 0;
		String studentID, courseID;
	    String line;
	    //search for every student in every course
		for (CourseOffering course : ModelRegister.getInstance().getAllCourses()) {
			for (StudentModel student : course.getStudentsAllowedToEnroll()){
				//read in from DB
				BufferedReader freader = new BufferedReader(new FileReader("NotificationPref.txt"));
				line = freader.readLine();
				//while not end of BD
				while (line != null){
					//if line from DB and student match then update that students info
					if (line.split("\t")[0].equals(student.getID()) ){

						if (line.split("\t")[1].equals("EMAIL")){
							student.setNotificationType(NotificationTypes.EMAIL);
						}
						else if (line.split("\t")[1].equals("CELLPHONE")){
							student.setNotificationType(NotificationTypes.CELLPHONE);
						}
						else if (line.split("\t")[1].equals("PIGEON_POST")){
							student.setNotificationType(NotificationTypes.PIGEON_POST);
						}
					}
					//next line
					line = freader.readLine();
				}
				//close reader
				freader.close();
			}
		}		
	}

	private void loadNewStudents() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File("new_users_list.txt")));
		StudentModelFactory factory = new StudentModelFactory();
		
		//read line for course ID
		String cid = br.readLine();
		
		while(cid != null) {
			//pass course to factory with br, it will read next line in factory to create user.
			ModelRegister.getInstance().getRegisteredCourse(cid).getStudentsAllowedToEnroll().add(factory.createSystemUserModel(br, ModelRegister.getInstance().getRegisteredCourse(cid)));
			
			//get next course ID
			cid = br.readLine();
		}
		
		br.close();
	}
}
