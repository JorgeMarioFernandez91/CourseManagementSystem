package systemUserModelFactories;

import java.io.BufferedReader;
import java.io.IOException;

import offerings.ICourseOffering;
import registrar.ModelRegister;
import systemUsers.AdminModel;

public class AdminModelFactory implements SystemUserModelFactory {
	/**
	 * This method takes a text file of admin users and adds them to the model register
	 * It implements the general SystemUserModelFactory interface as with the instructor and student factories
	 * @param br	the BufferReader initialized to the admin database file
	 * @param course	this is present due to the interface implemented, but since administrators aren't attached to any courses, should just be null
	 */
	public AdminModel createSystemUserModel(BufferedReader br, ICourseOffering course) {
		// make new AdminModel object to read into
		AdminModel newAdmin = new AdminModel();

		// read one line (one user) from user to add to register
		try {
			// read line
			String line = br.readLine();

			// test for presence in register
			if (!ModelRegister.getInstance().checkIfUserHasAlreadyBeenCreated(line.split("\t")[2])) {
				// create new admin user
				newAdmin.setName(line.split("\t")[0]);
				newAdmin.setSurname(line.split("\t")[1]);
				newAdmin.setID(line.split("\t")[2]);

				// add to modelRegister
				ModelRegister.getInstance().registerUser(newAdmin.getID(), newAdmin);
			}
			return newAdmin;
		} catch (IOException e) {
			// TODO: handle exception

			// IO error encountered
			System.out.println(e.getMessage());
			return null;
		}

	}

	// ----------------------------------------------
	public static void main(String[] args) {
		// AdminModelFactory factory = new AdminModelFactory();
		// AdminModel am = factory.createSystemUserModel(null, null);
		// am.getID();
	}

}
