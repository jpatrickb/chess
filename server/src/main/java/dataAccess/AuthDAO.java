package dataAccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    public void clear();

    public AuthData createAuth(UserData userData);

    public boolean authExists(String authToken);

    public boolean deleteAuth(String authToken);
}
