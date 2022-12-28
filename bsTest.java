import static org.junit.Assert.*;
import org.junit.Test;

public class bsTest{
    @Test
    public void checkUserTest(){
        String input = "A10";
        boolean result = battleship.checkUser(input);
        assertEquals(true, result);
    }
}