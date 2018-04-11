package loggedInUserFactory;

import authenticatedUsers.LoggedInAdmin;
import authenticatedUsers.LoggedInAuthenticatedUser;
import authenticatedUsers.LoggedInInstructor;
import authenticatedUsers.LoggedInStudent;
import authenticationServer.AuthenticationToken;
import registrar.ModelRegister;
import systemUsers.AdminModel;
import systemUsers.InstructorModel;
import systemUsers.StudentModel;

public class LoggedInUserFactory {

	public LoggedInUserFactory() {

	}

	public LoggedInAuthenticatedUser createAuthenticatedUser(AuthenticationToken authenticationToken) {
		switch (authenticationToken.getUserType()) {
		case "admin":
			return createLoggedInAdmin(authenticationToken);
		case "student":
			return createLoggedInStudent(authenticationToken);
		case "instructor":
			return createLoggedInInstructor(authenticationToken);
		default:
			return null;
		}
	}

	public LoggedInStudent createLoggedInStudent(AuthenticationToken authenticationToken) {
		LoggedInStudent loggedInUser = new LoggedInStudent();
		StudentModel queriedUser = (StudentModel) ModelRegister.getInstance().getRegisteredUser(authenticationToken.getTokenID());
		loggedInUser.setAuthenticationToken(authenticationToken);
		loggedInUser.setID(queriedUser.getID());
		loggedInUser.setName(queriedUser.getName());
		loggedInUser.setSurname(queriedUser.getSurname());
		
		ModelRegister.getInstance().registerLoggedInUser(authenticationToken.getSessionID(), loggedInUser);
		return loggedInUser;
		
	}

	public LoggedInAdmin createLoggedInAdmin(AuthenticationToken authenticationToken) {
		LoggedInAdmin loggedInUser = new LoggedInAdmin();
		AdminModel queriedUser = (AdminModel) ModelRegister.getInstance().getRegisteredUser(authenticationToken.getTokenID());
		loggedInUser.setAuthenticationToken(authenticationToken);
		loggedInUser.setID(queriedUser.getID());
		loggedInUser.setName(queriedUser.getName());
		loggedInUser.setSurname(queriedUser.getSurname());
		
		ModelRegister.getInstance().registerLoggedInUser(authenticationToken.getSessionID(), loggedInUser);
		return loggedInUser;
	}

	public LoggedInInstructor createLoggedInInstructor(AuthenticationToken authenticationToken) {
		LoggedInInstructor loggedInUser = new LoggedInInstructor();
		InstructorModel queriedUser = (InstructorModel) ModelRegister.getInstance().getRegisteredUser(authenticationToken.getTokenID());
		loggedInUser.setAuthenticationToken(authenticationToken);
		loggedInUser.setID(queriedUser.getID());
		loggedInUser.setName(queriedUser.getName());
		loggedInUser.setSurname(queriedUser.getSurname());
		
		ModelRegister.getInstance().registerLoggedInUser(authenticationToken.getSessionID(), loggedInUser);
		return loggedInUser;
	}	
}
