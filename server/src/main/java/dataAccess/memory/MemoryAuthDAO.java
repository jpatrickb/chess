package dataAccess.memory;

import dataAccess.AuthDAO;
import model.AuthData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private HashMap<String, String> authTokens = new HashMap<>();
    public void saveAuth(AuthData authData) {
        authTokens.put(authData.username(), authData.authToken());
    }

    public void clear() {
        authTokens.clear();
    }
}
