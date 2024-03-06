package phase4Tests;

import dataAccess.DataAccess;
import exception.ResponseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UserDAOTests {

    @BeforeAll
    static void createDatabase() {
        try {
            DataAccess dataAccess = new DataAccess(DataAccess.DataLocation.SQL);
        } catch (ResponseException ex) {
            System.out.printf("Unable to connect to database: %s%n", ex.getMessage());
        }
    }

    @Test
    void testIsUser() {

    }
}
