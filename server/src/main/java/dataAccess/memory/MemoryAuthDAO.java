package dataAccess.memory;

import dataAccess.AuthDAO;
import model.AuthData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private HashMap<String, String> authTokens = new HashMap<>();

    public void clear() {
        authTokens.clear();
    }

    @Override
    public void createAuth(AuthData authData) {
        authTokens.put(authData.authToken(), authData.username());
    }

    @Override
    public boolean getAuth(AuthData authData) {
        return authTokens.containsKey(authData.authToken());
    }

    @Override
    public void deleteAuth(AuthData authData) {
        authTokens.remove(authData.authToken());
    }
}
