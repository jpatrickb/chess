package dataAccess.memory;

import dataAccess.UserDAO;
import exception.ResponseException;
import model.UserData;

import java.util.Collection;
import java.util.HashSet;

public class MemoryUserDAO implements UserDAO {

    Collection<String> allUsers = new HashSet<>();

    public boolean getUser(UserData userData) throws ResponseException {
        return allUsers.contains(userData.username());
    }

    public void createUser(UserData userData) {
        allUsers.add(userData.username());
    }

    public void clear() {
        allUsers.clear();
    }
}
