import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

public class bsTest{
    @Test
    public void checkUserTest(){
        String input = "A10";
        boolean result = battleship.checkUser(input, new LinkedList<String>());
        assertEquals(true, result);
    }
}