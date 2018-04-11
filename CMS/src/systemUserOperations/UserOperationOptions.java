package systemUserOperations;

import java.io.IOException;
import java.util.Scanner;

import authenticatedUsers.LoggedInAuthenticatedUser;
import authenticationServer.LoginAuthentication;

/**
 * The UserOperationOptions class will provide an interface which will list and
 * perform the Operations that the User desires
 * 
 * @author Group 30
 *
 */

public class UserOperationOptions {

	/**
	 * This method allows Administrators to choose an Operations
	 * 
	 * @param LoggedInAuthenitcatedUser
	 *            lu
	 * @throws IOException
	 */
	public static void AdminMenu(LoggedInAuthenticatedUser user) throws IOException {
		int option;
		Scanner scan = new Scanner(System.in);
		while (true) {
			System.out.println("\nPlease select one of the following operations: ");
			System.out.println("1. Start System");
			System.out.println("2. Stop System");
			System.out.println("3. Create New User");
			System.out.println("4. Logout");

			System.out.print("Your Option (1-4) ------> ");
			option = scan.nextInt();

			System.out.println("");
			System.out.println("-------------------------------------------------------------------------------");
			AdminOperations admin = new AdminOperations();
			switch (option) {
			case 1:
				if (admin.startSystem()) {
					System.out.println("Start Completed. System in Online.");
				} else {
					System.out.println("Start up failed. Try Again.");
				}
				break;

			case 2:
				System.out.println("System Shut Down.");
				System.exit(0);
				break;

			case 3:
				System.out.println("Please specify the new user's user type:");
				System.out.println("1. Administrator\n2. Instructor\n3. Student");

				switch (scan.nextInt()) {
				case 1:
					admin.createNewUser("admin");
					break;
				case 2:
					admin.createNewUser("instructor");
					break;
				case 3:
					admin.createNewUser("student");
					break;
				default:
					System.out.println("Option selected is incorrect. Please Try Add User again.");
					break;
				}

				break;

			case 4:
				LoginAuthentication.logout((user.getAuthenticationToken().getSessionID()));
				break;

			default:
				System.out.println("-----Invalid Option. Please try again-----");
				break;
			}

			System.out.println("-------------------------------------------------------------------------------");
		}
	}

	/**
	 * This method allows Instructors to choose an Operations
	 * 
	 * @param LoggedInAuthenitcatedUser
	 *            lu
	 * @throws IOException
	 */
	public static void InstructorMenu(LoggedInAuthenticatedUser user) throws IOException {
		int option;
		Scanner scan = new Scanner(System.in);

		while (true) {
			System.out.println("\nPlease select one of the following operations: ");

			System.out.println("1. Add mark for a student");
			System.out.println("2. Modify mark for a student");
			System.out.println("3. Calculate final grade for a student");
			System.out.println("4: Print class record");
			System.out.println("5. Logout");

			System.out.print("Your Option (1-5) ------> ");
			option = scan.nextInt();

			System.out.println("");
			System.out.println("-------------------------------------------------------------------------------");
			InstructorOperations inOp = new InstructorOperations();

			switch (option) {
			case 1:
				inOp.addMark();
				break;
			case 2:
				inOp.modifyMark();
				break;
			case 3:
				inOp.calcFinalGrade();
				break;
			case 4:
				inOp.printClassRecord();
				break;
			case 5:
				LoginAuthentication.logout((user.getAuthenticationToken().getSessionID()));
				break;
			default:
				System.out.println("\n-----Invalid Option. Please try again-----");
				break;

			}
			System.out.println("-------------------------------------------------------------------------------");

		}

	}

	/**
	 * This method allows Students to choose an Operations
	 * 
	 * @param LoggedInAuthenitcatedUser
	 *            lu
	 * @throws IOException
	 */
	public static void StudentMenu(LoggedInAuthenticatedUser user) throws IOException {

		int option;
		Scanner scan = new Scanner(System.in);
		while (true) {
			System.out.println("\nPlease select one of the following operations: ");
			System.out.println("1. Enroll in a Course");
			System.out.println("2. Select Notification Status and Preferences");
			System.out.println("3. Print course record");
			System.out.println("4. Logout");

			System.out.print("Your Option (1-4) ------> ");
			option = scan.nextInt();

			System.out.println("");
			System.out.println("-------------------------------------------------------------------------------");
			StudentOperations stuOp = new StudentOperations();
			switch (option) {
			case 1:
				stuOp.enrollInCourse(user);
				break;
			case 2:
				stuOp.AddNotificationPreference(user);
				break;
			case 3:
				stuOp.printStudentRecord(user);
				break;
			case 4:
				LoginAuthentication.logout((user.getAuthenticationToken().getSessionID()));
				break;
			default:
				System.out.println("\n-----Invalid Option. Please try again-----");
				break;
			}

			System.out.println("-------------------------------------------------------------------------------");

		}
	}

}
