package systemUserOperations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import authenticatedUsers.LoggedInAuthenticatedUser;
import customDatatypes.EvaluationTypes;
import customDatatypes.Marks;
import customDatatypes.NotificationTypes;
import customDatatypes.Weights;
import offerings.CourseOffering;
import offerings.ICourseOffering;
import registrar.ModelRegister;
import systemUsers.IStudentModel;
import systemUsers.StudentModel;
import systemUsers.SystemUserModel;


/**
 * The Student Operations class will perform operations related to enrolling in a course, adding notification preferences and printing student records for a course
 * @author Group 30
 *
 */
public class StudentOperations {
	
	/**
	 * The enrollInCourse method will enroll the student in the class if the student is eligible to enroll and if the class is not full
	 * @param LoggedInAuthenitcatedUser lu
	 * @throws IOException
	 */
	
	public void enrollInCourse(LoggedInAuthenticatedUser lu) throws IOException {
		if (lu.getAuthenticationToken().getUserType().equals("student")) {
			String courseID; //The course the student would like to enroll in
			int choiceCourse; //Holds the users choice
			boolean allowed = false;
			
			StudentModel student = (StudentModel) ModelRegister.getInstance().getRegisteredUser(lu.getID()); //Gets the student object from the ModelRegister
			Map<Integer, String> choice = new HashMap<Integer, String>(); //Creates a Map to associate the users choice with a course ID
			
			while(true) {
				System.out.println("Select the Course ID of the course you would like to enroll in:  ");
				
				
				List<ICourseOffering> courselist = student.getCoursesAllowed(); //Gets the list of courses the student is allowed to enroll in
				Iterator<ICourseOffering> courseIteratorChoice = courselist.iterator(); //Creates an iterator to iterate through the list of eligible courses
				
				Scanner scan = new Scanner(System.in);
				
				int x = 1;
				String courseChoices; //Stores the eligible courses the student can take
				while (courseIteratorChoice.hasNext()) {
					courseChoices = courseIteratorChoice.next().getCourseID();
					choice.put(x, courseChoices);
					System.out.println(x + ". " + courseChoices);
					x++;
				}
				
				System.out.print("Your Choice (-1 to cancel) ------> ");
				choiceCourse = scan.nextInt();
				
				if (choiceCourse < x && choiceCourse >=1 || choiceCourse == -1)
					break;
				
				System.out.println("\n-----INVALID OPTION-----\n");
			}
			
			if (choiceCourse != -1) {
				courseID = choice.get(choiceCourse);
				
				CourseOffering course = ModelRegister.getInstance().getRegisteredCourse(courseID); //Gets the course object from the ModelRegister
				
				//Verifies that the student is eligible to enroll in the course
				List<ICourseOffering> courseAllowed = student.getCoursesAllowed();
				Iterator<ICourseOffering> courseIterator = courseAllowed.iterator();
				while(courseIterator.hasNext() && allowed == false) {
					if (courseIterator.next().getCourseID().equals(courseID))
						allowed = true;
				}
				
				//Flag to check if the student is already enrolled in the course
				boolean enrolled = false;
				
				
				if (allowed) {
					List<ICourseOffering> courseEnrolled = student.getCoursesEnrolled();
					Iterator<ICourseOffering> courseEnrollIterator = courseEnrolled.iterator();
					
					//Checks to see if the user is already enrolled in the requested course
					while(courseEnrollIterator.hasNext()) {
						if(courseEnrollIterator.next().getCourseID().equals(courseID)) {
							System.out.println("\n-----ERROR: Already Enrolled-----\n");
							enrolled = true;
						}
					}
					
					if(!enrolled) {
						courseEnrolled.add(course);
						student.setCoursesEnrolled(courseEnrolled);
						
						List<StudentModel> allStudents = course.getStudentsEnrolled();
						
						//Only Enrolls in class is the class size is less than 600
						if (allStudents.size() < 600) {
							
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
							
							allStudents.add(student);
							course.setStudentsEnrolled(allStudents);
							
							
							
							//Prints the student ID and course ID to the file to ensure changes are made when the system is started again
							String line;
							line = student.getID() + "\t" + courseID + "\n";
							
							FileWriter fw = new FileWriter("enroll_courses.txt",true);
							fw.write(line);
							fw.close();
							
							System.out.println("\n" + student.getName() + " " + student.getSurname() + " has successfully been enrolled in " + courseID);
							
						}
						else
							System.out.println("Class is Full");
						
					}
				}	
			}
		}
		else
			System.out.println("\nYou are not authorized to enroll in a Course");
	}
	
	
	/**
	 * The printStudentRecord method will print the information regarind a specific course to the console
	 * @param LoggedInAuthenticatedUser lu
	 */
	public void printStudentRecord(LoggedInAuthenticatedUser lu) {
		if (lu.getAuthenticationToken().getUserType().equals("student")) {
			Scanner scan = new Scanner(System.in);
			int choice; //Holds the users choice
			String courseID; //Holds the course ID of the course
			
			StudentModel student = (StudentModel) ModelRegister.getInstance().getRegisteredUser(lu.getID()); //Gets the student object associated with the logged in user
			Map<Integer, String> courseOption = new HashMap<Integer, String>(); //Creates a map to associate the users choice with a course ID
			
			
			while(true) {
				System.out.println("Select a course for which you would like to print your records?");
				
				List<ICourseOffering> coursesEnrolled = student.getCoursesEnrolled();
				Iterator<ICourseOffering> coursesEnrolledIterator = coursesEnrolled.iterator();
				
				//Prints a list of courses that the student is currently enrolled in
				int x = 1;
				String courseChoices; //Stores the enrolled course
				while (coursesEnrolledIterator.hasNext()) {
					courseChoices = coursesEnrolledIterator.next().getCourseID();
					courseOption.put(x, courseChoices);
					System.out.println(x + ". " + courseChoices);
					x++;
				}
				
				//If the student tries to print records when they are not enrolled in any courses
				if (x==1) {
					System.out.println("\n-----ERROR: You are not enrolled in any courses!-----");
					choice = 0;
					break;
				}
				
				System.out.print("Your Choice (-1 to cancel)------> ");
				choice = scan.nextInt();
				
				if (choice < x && choice >=1 || choice == -1)
					break;
				
				System.out.println("\n-----INVALID OPTION-----\n");
			}
			
			
			if (choice != -1) {
				courseID = courseOption.get(choice);
				
				boolean enrolled = false;
				
				List<ICourseOffering> courseEnrolled = student.getCoursesEnrolled();
				Iterator<ICourseOffering> courseIterator = courseEnrolled.iterator();
				
				//Ensures that the student is enrolled in the course
				while(courseIterator.hasNext() && enrolled == false) {
					if (courseIterator.next().getCourseID().equals(courseID))
						enrolled = true;
				}
				
				if(enrolled) {
					Map<ICourseOffering, EvaluationTypes> allEval = student.getEvaluationEntities(); //Retrieves the evaluation type of the student
					CourseOffering course = ModelRegister.getInstance().getRegisteredCourse(courseID);
					EvaluationTypes eval = allEval.get(course); 
					
					System.out.println("Course Name: " + course.getCourseName());
					System.out.println("Course ID: " + courseID + "\n");
					Map<EvaluationTypes, Weights> allWeights = course.getEvaluationStrategies();
					Weights weight = allWeights.get(eval);
					
					//Uses the weight iterator to iterate through the different weights
					weight.initializeIterator();
					Entry<String,Double> type = weight.getNextEntry(); //Gets the evaluation entity and weights
					System.out.println("Your Evaluation Type: " + eval.getText());
					System.out.println("Your Evaluation Entities: "); 
					while(type!=null) {
						System.out.print(type.getKey() + "--> ");
						System.out.println(type.getValue());
						type = weight.getNextEntry();
					}
					
					//Gets all the marks for a student
					Map<ICourseOffering, Marks> allMark = student.getPerCourseMarks();
					Marks marks = allMark.get(course);
					
					//Iterates through the marks for the student and prints the marks associated with the evaluation entity
					marks.initializeIterator();
					Entry<String, Double> yourMarks = marks.getNextEntry();
					System.out.println("\nYour Marks: ");
					while(yourMarks != null) {
						System.out.print(yourMarks.getKey() + "--> ");
						
						if(yourMarks.getValue() == null)
							System.out.println("--");
						else
							System.out.println(yourMarks.getValue());
						
						yourMarks = marks.getNextEntry();
					}
					
					System.out.println("\nYour Notification Type: " + student.getNotificationType());
				}	
			}
		}
	}
	
	/**
	 * 
	 * @param insys
	 */
	public boolean StudentSelectNotificationStatus(LoggedInAuthenticatedUser insys){
		
			boolean status = false;
			while(true){
			if (insys.getAuthenticationToken().getUserType().equals("student")) {
				Scanner input = new Scanner(System.in);
				System.out.println("Please pick your notification status, 1 or 2.");
				System.out.println("1.ON (To be notified)\n2.OFF (Not to be notified)\n");
				String option = input.next(); 											// getting a String value
				int optionInt = Integer.parseInt(option);
			
				switch (optionInt){
				case 1: 
        	 			//((IStudentModel) insys).setNotificationType(NotificationTypes.EMAIL);
        	 			status = true;
        	 			System.out.println();
        	 			System.out.println("Thank you!!!You have successfully turned ON your notification status");
        	 			break;
				case 2:  
        	 			//((IStudentModel) insys).setNotificationType(NotificationTypes.CELLPHONE);
        	 			status = false;
        	 			System.out.println();
        	 			System.out.println("Thank you!!!You have successfully turned OFF your notification status");
						SystemUserModel user = ModelRegister.getInstance().getRegisteredUser(insys.getID());
        	 			((IStudentModel) user).setNotificationType(NotificationTypes.OFF);
        	 			break;
				default: System.out.println("\n-----INVALID OPTION--TRY AGAIN---\n");
        				break;
							}
				if (optionInt == 1 || optionInt == 2){
					 break;
					 	}
					}
				}
			return status;
		}
	
	/**
	 * adding student's preferred notification type 
	 * @throws IOException 
	 */
	public void AddNotificationPreference(LoggedInAuthenticatedUser insys) throws IOException{

		boolean status = StudentSelectNotificationStatus(insys);
		
			while(status == true){ //status is on for this case
				SystemUserModel user = ModelRegister.getInstance().getRegisteredUser(insys.getID());
				Scanner input = new Scanner(System.in);
				System.out.println("How would you like to be notified?");
				System.out.println("1.EMAIL\n2.CELLPHONE\n3.PIGEON_POST\n");
				System.out.println("Please pick option 1 or 2 or 3 : ");
				String option = input.next(); // getting a String value
				int optionInt = Integer.parseInt(option);
			
				switch (optionInt){
				case 1: 
	        	 		((IStudentModel) user).setNotificationType(NotificationTypes.EMAIL);
	        	 		System.out.println();
	        	 		System.out.println("You will be notified by email");
	        	 		break;
				case 2:  
	        	 		((IStudentModel) user).setNotificationType(NotificationTypes.CELLPHONE);
	        	 		System.out.println();
	        	 		System.out.println("You will be notified through the phone");
	        	 		break;
				case 3:  
	        	 		((IStudentModel) user).setNotificationType(NotificationTypes.PIGEON_POST);
	        	 		System.out.println();
	        	 		System.out.println("You will be notified via the pigeon_post");
	        	 		break;
				default: System.out.println("\n-----INVALID OPTION-----\n");
	            		break;
				}
			 
			 if (optionInt == 1 || optionInt == 2 || optionInt == 3){
				 break;
				 }
			}
			SystemUserModel user = ModelRegister.getInstance().getRegisteredUser(insys.getID());
			
			Writer output = new BufferedWriter(new FileWriter("NotificationPref.txt"));
			
			//save notification type in student object into text file, aka database
			for (CourseOffering course1 : ModelRegister.getInstance().getAllCourses()) {

				for (StudentModel student1 : course1.getStudentsAllowedToEnroll()){
					
					output.write(student1.getID() + "\t" + student1.getNotificationType());	
					output.write("\n");
				}
			}
		    output.close();
			
		}
	
}
