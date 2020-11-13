import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
    
    
    
}
