package dataAccess;
import model.UserData;

import java.sql.SQLException;

public interface UserDAO {

    boolean isUser(UserData userData) throws DataAccessException;

    UserData getUser(String username);

    void createUser(UserData userData) throws DataAccessException;

    void clear() throws DataAccessException;

}
