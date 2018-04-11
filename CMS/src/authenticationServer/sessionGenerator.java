package authenticationServer;

public class sessionGenerator {
	
	/**	Variables:	*/
	private static int sessionTracker;	// using a persistent counter to keep track the users logged in during a server life time
	
	// Methods:
	
	/**
	 * The method to initialize the class to allow creation of sessions. 
	 * It zeroes the value of the sessionTracker, so that it may start assigning ID's to authenticated users.
	 */
	public static void initialize() {
		sessionTracker = 0;
	}
	
	/**
	 * This method resets and shuts down the sessionGenerator.
	 * It resets Tracker value to -1, indicating it is offline, so no sessions may be generated.
	 */
	public static void reset() {
		sessionTracker = -1;
	}
	
	/**
	 * This method is used to create a session code (ID) for the factory creating a new session for a newly logged in user.
	 * It increments the tracker value and returns it to the calling function
	 * @return the incremented value of sessionTracker. This value is also the amount of session requests made in this server life time
	 */
	public static int createSession() {
		sessionTracker++;
		return sessionTracker;
	}
	
	/**
	 * this method returns the number of sessions created in this server's lifetime
	 * @return the amount of session requests made in this server life time
	 */
	public static int sessionsInitialized() {
		return sessionTracker;
	}
}
