package dataAccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    public void clear();

    public void createAuth(AuthData authData);

    public boolean getAuth(AuthData authData);

    public void deleteAuth(AuthData authData);
}
