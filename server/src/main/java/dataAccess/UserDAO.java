package dataAccess;

import exception.ResponseException;
import model.UserData;

public interface UserDAO {

    boolean isUser(UserData userData) throws ResponseException;

    UserData getUser(String username);

    void createUser(UserData userData);

    void clear();

}
