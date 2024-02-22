package dataAccess;

import exception.ResponseException;
import model.UserData;

public interface UserDAO {

    public boolean getUser(UserData userData) throws ResponseException;

    public void createUser(UserData userData);

}
