import org.junit.Test;

import static org.junit.Assert.*;

public class ReceiptTest {
    
    @Test
    public void testConstruct() {
        Receipt receipt = new Receipt(1, new Delta(1, 2, 3, 4), 10, "BREW", 11, 12, true, false);
        
        assertEquals(receipt.id, 1);
        assertEquals(receipt.required.d1, 1);
        assertEquals(receipt.required.d2, 2);
        assertEquals(receipt.required.d3, 3);
        assertEquals(receipt.required.d4, 4);
        assertEquals(receipt.price, 10);
        assertEquals(receipt.action, "BREW");
        assertEquals(receipt.tomeIndex, 11);
        assertEquals(receipt.taxCount, 12);
        assertTrue(receipt.castable);
        assertFalse(receipt.repeatable);
    }
    
    @Test
    public void testActionOne() {
        Receipt receipt = new Receipt(1, new Delta(1, 2, 3, 4), 10, "BREW", 11, 12, true, false);
        
        assertEquals(receipt.toAction(), "BREW 1");
    }
    
    @Test
    public void testActionTwo() {
        Receipt receipt = new Receipt(45, new Delta(1, 2, 3, 4), 10, "CAST", 11, 12, true, false);
        
        assertEquals(receipt.toAction(), "CAST 45");
    }
    
    
    
}
