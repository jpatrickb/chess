package Service;

import dataAccess.AuthDAO;
import exception.ResponseException;

public class AuthenticationService {
    private final AuthDAO authDAO;

    public AuthenticationService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }


    public void authenticate(String authToken) throws ResponseException {
        if (!this.authDAO.authExists(authToken)) {
            throw new ResponseException(401, "error: unauthorized");
        }
    }
}
