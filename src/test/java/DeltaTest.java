import org.junit.Test;

import static org.junit.Assert.*;

public class DeltaTest {
    
    @Test
    public void testContructor() {
        Delta delta = new Delta(1, 2, 3, 4);
        
        assertEquals(delta.d1, 1);
        assertEquals(delta.d2, 2);
        assertEquals(delta.d3, 3);
        assertEquals(delta.d4, 4);
    }
    
    @Test
    public void testMoreThanOK() {
        Delta base = new Delta(2, 2, 2, 2);
        Delta compare = new Delta(1, 2, 1, 0);
        assertTrue(base.more(compare));
    }
    
    @Test
    public void testMoreThanKOOne() {
        Delta base = new Delta(2, 2, 2, 2);
        Delta compare = new Delta(1, 3, 1, 0);
        assertFalse(base.more(compare));
    }
    @Test
    public void testMoreThanKOAll() {
        Delta base = new Delta(2, 2, 2, 2);
        Delta compare = new Delta(3, 5, 4, 9);
        assertFalse(base.more(compare));
    }
}
