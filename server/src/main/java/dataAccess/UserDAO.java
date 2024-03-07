package dataAccess;
import model.UserData;


public interface UserDAO {

    boolean isUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void createUser(UserData userData) throws DataAccessException;

    void clear() throws DataAccessException;

}
