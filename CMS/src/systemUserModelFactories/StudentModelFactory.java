package systemUserModelFactories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import customDatatypes.EvaluationTypes;
import customDatatypes.Marks;
import customDatatypes.NotificationTypes;
import customDatatypes.Weights;
import offerings.CourseOffering;
import offerings.ICourseOffering;
import registrar.ModelRegister;
import systemUsers.StudentModel;

public class StudentModelFactory implements SystemUserModelFactory {

	public StudentModel createSystemUserModel(BufferedReader br, ICourseOffering course) {
		// TODO Auto-generated method stub
		StudentModel newStudent;
		Map<ICourseOffering, Marks> toInput2 = null;
		try{
		String line = br.readLine();
		if(!ModelRegister.getInstance().checkIfUserHasAlreadyBeenCreated(line.split("\t")[2])){
//			Consume a line and populate the available fields as well as initialize all fields that need initialization
//			notice that we are using ModelRegister which is used to keep track of previously created instances with specific IDs
			newStudent = new StudentModel();
			newStudent.setName(line.split("\t")[0]);
			newStudent.setSurname(line.split("\t")[1]);
			newStudent.setID(line.split("\t")[2]);
			List<ICourseOffering> toInput = new ArrayList<ICourseOffering>();
			newStudent.setCoursesAllowed(toInput);
			Map<ICourseOffering, EvaluationTypes> toInput1 = new HashMap<ICourseOffering, EvaluationTypes>();
			
			//Initialize list of enrolled classes
			List<ICourseOffering> enrolled = new ArrayList<ICourseOffering>();
			newStudent.setCoursesEnrolled(enrolled);
			
			
			newStudent.setEvaluationEntities(toInput1);
			//---------------------------------------------
			//made a new map to store marks for students
			toInput2 = new HashMap<ICourseOffering, Marks>();
			
			//add assessments here
			newStudent.setPerCourseMarks(toInput2);
			//---------------------------------------------
			ModelRegister.getInstance().registerUser(newStudent.getID(), newStudent);
		} 	
			newStudent = (StudentModel) ModelRegister.getInstance().getRegisteredUser(line.split("\t")[2]);
			(newStudent.getCoursesAllowed()).add(course);
			newStudent.getEvaluationEntities().put(course, EvaluationTypes.fromString(line.split("\t")[3]));
			
			//gets the type of this particular student. Types: FA, FC, PC, PA
			String nType = newStudent.getEvaluationEntities().get(course).getText();
						
//			br = new BufferedReader(new FileReader(new File("note_1.txt")));
//			
//			line = br.readLine();
//			//loops through note_1.txt and add the assessments depending on the student evaluation type
//			while (line != null)
//			{
//				if (line.equals(nType)){
//					
//					line = br.readLine();
//					
//					int totalEntities = Integer.parseInt(line.split("\t")[2]);
//					int i = 0;
//					Marks m = new Marks();
//					for (i = 0; i < totalEntities; i++){
//						
//						line = br.readLine();
//						
//						String assessment = line.split("\t")[0];
//						//add assessments to map
//						m.addToEvalStrategy(assessment, null);
//					}
//					newStudent.getPerCourseMarks().put(course, m);
//				}
//				line = br.readLine();	
//			}	
			
			
			
//			br.close();
			
			for (NotificationTypes p : NotificationTypes.values()){
		           newStudent.setNotificationType(p);
		           break;
		    }

			return newStudent;
		}catch(IOException ioe){
			System.out.println(ioe.getMessage() + "exception thrown at StudentModelCreation"); 
			return null;
		}
	}
}
