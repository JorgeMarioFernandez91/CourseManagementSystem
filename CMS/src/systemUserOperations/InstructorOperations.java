package systemUserOperations;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;
import customDatatypes.Marks;
import offerings.CourseOffering;
import offerings.ICourseOffering;
import registrar.ModelRegister;
import systemUsers.StudentModel;

public class InstructorOperations extends Marks{
	
	private String examOrAss, c = "", n = "", s = "", iD = "";
	private Double grade;
	private int input;
	
	/**
	 * Add a mark and assessment for a particular student if a mark doesn't exist 
	 */
	public void addMark() throws IOException{
		
		Scanner scan = new Scanner(System.in);
		
		//ask instructor for course name
		System.out.println("Course: ");	
		c = scan.next();
		//search courses in ModelRegister
		for(CourseOffering course : ModelRegister.getInstance().getAllCourses()){
			//check if course from input equals course stored in database
			if (course.getCourseID().equals(c)){
				System.out.println("Student iD: ");	
				iD = scan.next();
				//check if student id that instructor is looking for exists in the course
				for(StudentModel student : course.getStudentsAllowedToEnroll()){
					//print name of the student
					if (student.getID().equals(iD)){
						//store name and surname of student
						n = student.getName();
						s = student.getSurname();
						//get particular name of assessment depending on the type of student, then the result
						//is used to prompt the Instructor for an assessment to add a mark to
						Marks j = student.getPerCourseMarks().get(course);
						j.initializeIterator();
						j.next();
						String a1 = j.getCurrentKey();
						j.next();
						String a2 = j.getCurrentKey();
						j.next();
						String a3 = j.getCurrentKey();
						//ask instructor for assessment 
						System.out.println("Please enter: \n1 for " + a2 + " \n2 for " + a1 + " \n3 for " + a3);	
						input = scan.nextInt();
						//give user choices for input
						if (input == 1){
							examOrAss = a2;
						}
						else if (input == 2){
							examOrAss = a1;
						}
						else if (input == 3){
							examOrAss = a3;
						}
						else{
							System.out.println("Invalid Assessment");
						}
						//if there is no grade associated with assessment then add grade
						if (!gradeExists(student, course)){
							
							System.out.println("Mark: ");
							
							grade = (double) scan.nextInt();
							if (grade > 100.0 || grade < 0.0){
								System.out.println("Please enter a valid grade!");
								return;
							}
							//save changes
							saveMark(student, course);
							return;
						}
						else{
							System.out.println("Mark already exists for " + examOrAss + "\nPlease return to main menu and choose Modify Mark");
							return;
						}
					}
				}
				System.out.println("Student Does not exist, please enter a valid student ID");
				return;
			}
		}
		System.out.println("Course Does Not Exist, please enter a valid course ID");
	}
	/**
	 * Modify a mark for an assesment if mark already exists
	 */
	public void modifyMark() throws IOException{
		
		Scanner scan = new Scanner(System.in);
		
		//ask instructor for course name
		System.out.println("Course: ");	
		c = scan.next();
		//search courses in ModelRegister
		for(CourseOffering course : ModelRegister.getInstance().getAllCourses()){
			//check if course from input equals course stored in database
			if (course.getCourseID().equals(c)){
				System.out.println("Student iD: ");	
				iD = scan.next();
				//check if student id that instructor is looking for exists in the course
				for(StudentModel student : course.getStudentsAllowedToEnroll()){
					//print name of the student
					if (student.getID().equals(iD)){
						//store name and surname of student
						n = student.getName();
						s = student.getSurname();
						//get particular name of assessment depending on the type of student, then the result
						//is used to prompt the Instructor for an assessment to modify
						Marks j = student.getPerCourseMarks().get(course);
						j.initializeIterator();
						j.next();
						String a1 = j.getCurrentKey();
						j.next();
						String a2 = j.getCurrentKey();
						j.next();
						String a3 = j.getCurrentKey();
						//ask instructor for assessment 
						System.out.println("Please enter: \n1 for " + a2 + " \n2 for " + a1 + " \n3 for " + a3);	
						input = scan.nextInt();
						//give user choices for input
						if (input == 1){
							examOrAss = a2;
						}
						else if (input == 2){
							examOrAss = a1;
						}
						else if (input == 3){
							examOrAss = a3;
						}
						else{
							System.out.println("Invalid Assessment");
						}
						//if there is a grade associated with assessment then ask for new grade and make the change
						if (gradeExists(student, course)){
							
							System.out.println("Mark: ");
							grade = (double) scan.nextInt();
							if (grade > 100.0 || grade < 0.0){
								System.out.println("Please enter a valid grade!");
								return;
							}
							//save changes to student object
							saveMark(student, course);
							return;
						}
						else{
							System.out.println("Mark does not exist for " + examOrAss + "\nPlease return to main menu and choose Add Mark");
							return;
						}
					}
				}
				System.out.println("Student Does not exist, please enter a valid student ID");
				return;
			}
		}
		System.out.println("Course Does Not Exist, please enter a valid course ID");
	}
	
	/**
	 * Calculates final grade for a student. The student must have a grade assigned to each assessment 
	 * otherwise an error message will be printed asking the Instructor to finish adding grades to every assignment
	 * before attempting to calculate final grade
	 */
	public void calcFinalGrade() throws IOException{
		
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Course: ");	
		c = scan.next();
		for(CourseOffering course : ModelRegister.getInstance().getAllCourses()){
			
			//check if course from input equals course stored in database
			if (course.getCourseID().equals(c)){
				System.out.println("Student iD: ");	
				iD = scan.next();
				//check if student id that instructor is looking for exists in the course
				//should change to course.getStudentsEnrolled, but not populated right now
				for(StudentModel student : course.getStudentsAllowedToEnroll()){
					
					//if student matches then continue
					if (student.getID().equals(iD)){
						
						Double result = course.calculateFinalGrade(student);
						//check if student has every evaluation paired with a grade if not then print error message
						if (result == 0.0){
							System.out.println("Student is missing evaluations! "
									+ "\nPlease finish adding grades for student before calculating final grade.");			
						}
						else{
							System.out.println("Final Grade Calculated: " + result + "%");
							return;
						}
					}
				}
			}
		}
	}
	
	/**
	 * this method saves assignment and grade for a student to a 'DB'
	 */
	private void saveMark(StudentModel student, CourseOffering course) throws IOException{
		
		//create a Marks object to iterate through students assignments and grades
		Marks a = student.getPerCourseMarks().get(course);
		Double old = null;
		
		a.initializeIterator();
		//iterate through the marks to check for assessment
		while (a.hasNext()){
			a.next();
			//once assessment is found then change the old value to the new value, and save it to map
			if (a.getCurrentKey().equals(examOrAss)){
				old = a.getCurrentValue();
				a.addToEvalStrategy(examOrAss, grade);
				student.getPerCourseMarks().put(course, a);
				if (old == null)
				{
					System.out.println("For " + a.getCurrentKey() + ", grade changed from - to " + a.getCurrentValue() + "%");
				}
				else{
					System.out.println("For " + a.getCurrentKey() + ", grade changed from " + old + "% to " + a.getCurrentValue() + "%");
				}
				//save changes to 'DB'
				saveToDB(student, course);
				
				System.out.println(student.getName() + " " + student.getSurname() + " will be notified via: " + student.getNotificationType());
			}
		}
	}
	
	/**
	 * Checks to see if student already has a grade for a particular assignment.
	 * This determines weather the instructor can add a grade to a pre-existing assignment.
	 * If a grade exists then the Instructor is asked to chose 'modify grade' option
	 * Else the Instructor is able to add the brand new grade
	 */
	private boolean gradeExists(StudentModel student, CourseOffering course){
		//check if mark already exists for this assessment
		//create a Marks object to iterate through students assignments and grades
		Marks a = student.getPerCourseMarks().get(course);
		
		a.initializeIterator();
		//iterate through the marks to check for assessment
		while (a.hasNext()){
			a.next();
			//iterate through assignments and see if mark exists for assessment
			//if so return true, else return false
			if (a.getCurrentKey().equals(examOrAss)){
				if(a.getCurrentValue() != null){
					
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Saves any changes done to a student's grades to the 'DB'
	 */
	@SuppressWarnings("resource")
	private void saveToDB(StudentModel student, CourseOffering course ) throws IOException{
		
		Writer output = new BufferedWriter(new FileWriter("SavedGrades.txt"));
		
		int m = 0;
		int n = 0;
		n = 0;
		output = new BufferedWriter(new FileWriter("SavedGrades.txt"));
		//save grades in student object into text file, aka database
		for (CourseOffering course1 : ModelRegister.getInstance().getAllCourses()) {

			for (StudentModel student1 : course1.getStudentsAllowedToEnroll()){
				
				//if (student1.getCoursesEnrolled().equals(o))
				m = 0;
			    n = 0;
	    		//separate each item in array with a tab and write to text file
	    		output.write(student1.getID() + "\t");
	    		output.write(course1.getCourseID() + "\t");
	    		Map<ICourseOffering, Marks> allMark = student1.getPerCourseMarks();
				Marks j = allMark.get(course1);
	    	    j.initializeIterator();
	    	    while (j.hasNext()){
	    	    	j.next();
	    	    	output.write(j.getCurrentKey() + "\t");
	    	    	output.write(j.getCurrentValue() + "\t");
	    	    }
		    	//move onto the next line
		    	output.write("\n");
			}
		}
	    output.close();
	}
	/**
	 * Print out all the course information and eligible students and the enrolled students of the course to console
	 */
	public void printClassRecord() {
		for (CourseOffering course : ModelRegister.getInstance().getAllCourses()) {
			System.out.println("ID : " + course.getCourseID() + "\nCourse name : " + course.getCourseName()
					+ "\nSemester : " + course.getSemester());
			System.out.println("Students allowed to enroll\n");
			for (StudentModel student : course.getStudentsAllowedToEnroll()) {
				System.out.println("Student name : " + student.getName() + "\nStudent surname : " + student.getSurname()
						+ "\nStudent ID : " + student.getID() + "\nStudent EvaluationType : "
						+ student.getEvaluationEntities().get(course) + "\n\n");
			}

			for (StudentModel student : course.getStudentsAllowedToEnroll()) {
				for (ICourseOffering course2 : student.getCoursesAllowed())
					System.out.println(student.getName() + "\t\t -> " + course2.getCourseName());
			}
		}
	}
	
}
