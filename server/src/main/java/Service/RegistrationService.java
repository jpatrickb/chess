package Service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import dataAccess.memory.MemoryAuthDAO;
import dataAccess.memory.MemoryUserDAO;
import exception.ResponseException;
import handlers.RegistrationRequest;
import model.AuthData;
import model.UserData;

import java.util.*;

public class RegistrationService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegistrationService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
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
        if (userDAO.isUser(userData)) {
            throw new ResponseException(403, "error: already taken");
        } else {
//            Add the user to the database if it's all new
            userDAO.createUser(userData);

            return authDAO.createAuth(userData);
        }
    }

    private static AuthData generateAuth(UserData userData) {
        return new AuthData(userData.username(), UUID.randomUUID().toString());
    }

}
