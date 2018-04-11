package systemUserOperations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import authenticatedUsers.LoggedInAuthenticatedUser;

/** This class will print a welcome message to the Authenticated User and print out a list of the Operations that the User is allowed to perform
 * @author Group 30
 */

public class LoginOptions {
	
	public static void screen(LoggedInAuthenticatedUser user) throws IOException {		
		switch (user.getAuthenticationToken().getUserType()) {
		case "admin":
			// administrator view and commands
			System.out.println("Welcome " + user.getName() + " " + user.getSurname() + ",");
			UserOperationOptions.AdminMenu(user);	
			break;

		case "instructor":
			// instructor view and commands
			System.out.println("Welcome " + user.getName() + " " + user.getSurname() + ",");
			UserOperationOptions.InstructorMenu(user);	
			break;

		case "student":
			// student view and commands
			System.out.println("Welcome " + user.getName() + " " + user.getSurname() + ",");
			UserOperationOptions.StudentMenu(user);
			break;
		}
	}
}
