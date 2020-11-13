import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {
    
    @Test
    public void testConstruction() {
        Inventory inv = new Inventory(new Delta(1, 2, 3, 4), 5);
        
        assertEquals(inv.inv.d1, 1);
        assertEquals(inv.inv.d2, 2);
        assertEquals(inv.inv.d3, 3);
        assertEquals(inv.inv.d4, 4);
        assertEquals(inv.rupees, 5);
    }
    
    
    @Test
    public void testNotFeaseable() {
        Receipt receipt = new Receipt(41, new Delta(-2, -2, -2, 0), 10, "BREW", 0, 0, false, false);
        Inventory inv = new Inventory(new Delta(3, 0, 0, 0), 5);
        assertFalse(inv.isFeasable(receipt));
    }
    
    @Test
    public void testFeaseable() {
        Receipt receipt = new Receipt(41, new Delta(-3, 0, 0, 0), 10, "BREW", 0, 0, false, false);
        Inventory inv = new Inventory(new Delta(3, 0, 0, 0), 5);
        assertTrue(inv.isFeasable(receipt));
    }
    
    
}
