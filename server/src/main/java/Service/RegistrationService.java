package Service;

import dataAccess.memory.MemoryAuthDAO;
import dataAccess.memory.MemoryUserDAO;
import exception.ResponseException;
import handlers.RegistrationRequest;
import model.AuthData;
import model.UserData;

import java.util.*;

public class RegistrationService {

    private final MemoryUserDAO memoryUserDAO;
    private final MemoryAuthDAO memoryAuthDAO;

    public RegistrationService(MemoryUserDAO memoryUserDAO, MemoryAuthDAO memoryAuthDAO) {
        this.memoryUserDAO = memoryUserDAO;
        this.memoryAuthDAO = memoryAuthDAO;
    }


    /**
     * Registers the user by checking for duplicates, saving the user into the database,
     * creating an authentication token, saving that to the database, and return it.
     *
     * @param userRequest The user object to be registered
     * @return the AuthToken object that has been created
     */
    public AuthData registerUser(RegistrationRequest userRequest) throws ResponseException {
//        Ensure valid request
        if (userRequest.username() == null || userRequest.password() == null || userRequest.email() == null){
            throw new ResponseException(400, "error: bad request");
        }

//        Make sure the username is unique
        UserData userData = new UserData(userRequest.username(), userRequest.password(), userRequest.email());
        if (memoryUserDAO.isUser(userData)) {
            throw new ResponseException(403, "error: already taken");
        } else {
//            Add the user to the database if it's all new
            memoryUserDAO.createUser(userData);

            return memoryAuthDAO.createAuth(userData);
        }
    }

    private static AuthData generateAuth(UserData userData) {
        return new AuthData(userData.username(), UUID.randomUUID().toString());
    }

}
