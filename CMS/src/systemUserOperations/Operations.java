package systemUserOperations;

import java.io.IOException;

import authenticatedUsers.LoggedInAuthenticatedUser;

public interface Operations {
//	LoggedInAuthenticatedUser performOp() throws IOException;
	void performOp(LoggedInAuthenticatedUser lu);
	
}
