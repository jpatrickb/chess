package Service;

import dataAccess.memory.MemoryAuthDAO;
import dataAccess.memory.MemoryGameDAO;
import dataAccess.memory.MemoryUserDAO;

public class ClearService {
    private static final MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    private static final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
    private static final MemoryGameDAO memoryGameDAO = new MemoryGameDAO();


    public static void clearDatabase() {
        memoryUserDAO.clear();
        memoryAuthDAO.clear();
        memoryGameDAO.clear();
    }
}
