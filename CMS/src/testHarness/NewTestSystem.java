package testHarness;

import java.io.IOException;

import authenticationServer.LoginAuthentication;
import authenticationServer.sessionGenerator;
import customDatatypes.SystemState;

public class NewTestSystem {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		// New test system using a system start and initialization

		// System Initialization: Initializes system data with administrators on the
		// database
		SystemState.initialize("admin_user_list.txt");
		sessionGenerator.initialize();

		LoginAuthentication.interact();
		
	}

}
