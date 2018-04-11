package customDatatypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import systemUserModelFactories.AdminModelFactory;

public class SystemState {
	private static int state;

	/**
	 * Initializes the system through initializing the state value to 0, and
	 * populates the register with the administrators
	 * from file
	 * 
	 * @param usersDB
	 *            the string literal of the name of the file holding administrators
	 */
	public static void initialize(String usersDB) {
		// initialize system
		state = 0;

		// read admins into register
		try {
			// initialize BufferReader with the provided file & factory to import the admin
			// users
			BufferedReader br = new BufferedReader(new FileReader(new File(usersDB)));
			AdminModelFactory AdminFact = new AdminModelFactory();

			// read first line to determine the number of administrators on file
			String line = br.readLine();

			// iterate through file to add all admins to ModelRegister
			for (int i = 0; i < Integer.parseInt(line.split("\t")[1]); i++) {
				AdminFact.createSystemUserModel(br, null);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	// setting system state
	/**
	 * Sets system state to on (1)
	 */
	public static void start() {
		state = 1;
	}

	/**
	 * Sets system state to off (0)
	 */
	public static void stop() {
		state = 0;
	}

	/**
	 * Retrieve current system state
	 */
	public static int getState() {
		return state;
	}
}
