package phase3Tests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class ExampleTest {

    @Test
    public void MyTest() {
        if (true) {
            Assertions.assertTrue(true);
        } else {
            fail("bad");
        }
    }
}
