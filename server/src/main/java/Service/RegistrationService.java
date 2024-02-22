package Service;

import dataAccess.memory.MemoryAuthDAO;
import dataAccess.memory.MemoryUserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.*;

public class RegistrationService {

    private final MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    private final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();



    /**
     * Registers the user by checking for duplicates, saving the user into the database,
     * creating an authentication token, saving that to the database, and return it.
     *
     * @param userData The user object to be registered
     * @return the AuthToken object that has been created
     */
    public AuthData registerUser(UserData userData) throws ResponseException {
        AuthData authData = new AuthData("0", "0");
        if (memoryUserDAO.getUser(userData)) {
            System.out.println("Caught exception");
            throw new ResponseException(403, "already taken");
        } else {
            memoryUserDAO.createUser(userData);
            authData = generateAuth(userData);
            memoryAuthDAO.createAuth(authData);
        }
        return authData;
    }

    private static AuthData generateAuth(UserData userData) {
        return new AuthData(userData.username(), UUID.randomUUID().toString());
    }

}
