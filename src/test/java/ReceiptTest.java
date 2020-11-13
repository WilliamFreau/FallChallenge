import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ReceiptTest {
    
    @Test
    public void testConstruct() {
        Receipt receipt = new Receipt(1, new Delta(-1, -2, -3, -4), 10, "BREW", 11, 12, true, false);
        
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
        Receipt receipt = new Receipt(1, new Delta(-1, -2, -3, -4), 10, "BREW", 11, 12, true, false);
        
        assertEquals(receipt.toAction(), "BREW 1");
    }
    
    @Test
    public void testActionTwo() {
        Receipt receipt = new Receipt(45, new Delta(-1, -2, -3, -4), 10, "CAST", 11, 12, true, false);
        
        assertEquals(receipt.toAction(), "CAST 45");
    }
    
    @Test
    public void testReceiptStepNothingToDo() {
        Receipt receipt = new Receipt(41, new Delta(-1, 0, 0, 0), 10, "BREW", 0, 0, false, false);
        Inventory inv = new Inventory(new Delta(3, 0, 0, 0), 0);
        inv.cast.add(new Receipt(1, new Delta(2, 0, 0, 0), 10, "CAST", 0, 0, true, false));
        inv.cast.add(new Receipt(2, new Delta(-1, 1, 0, 0), 10, "CAST", 0, 0, true, false));
        inv.cast.add(new Receipt(3, new Delta(0, -1, 1, 0), 10, "CAST", 0, 0, true, false));
        inv.cast.add(new Receipt(4, new Delta(0, 0, -1, 1), 10, "CAST", 0, 0, true, false));
        
        List<Receipt> solution = receipt.requiredCast(inv);
        assertEquals(0, solution.size());
    }
    
    @Test
    public void testReceiptStepOneCast() {
        Receipt receipt = new Receipt(41, new Delta(0, -1, 0, 0), 10, "BREW", 0, 0, false, false);
        Inventory inv = new Inventory(new Delta(3, 0, 0, 0), 0);
        inv.cast.add(new Receipt(1, new Delta(2, 0, 0, 0), 10, "CAST", 0, 0, true, false));
        inv.cast.add(new Receipt(2, new Delta(-1, 1, 0, 0), 10, "CAST", 0, 0, true, false));
        inv.cast.add(new Receipt(3, new Delta(0, -1, 1, 0), 10, "CAST", 0, 0, true, false));
        inv.cast.add(new Receipt(4, new Delta(0, 0, -1, 1), 10, "CAST", 0, 0, true, false));
        
        List<Receipt> solution = receipt.requiredCast(inv);
        assertEquals(1, solution.size());
    }
    
    @Test
    public void testReceiptStepTwoCast() {
        Receipt receipt = new Receipt(41, new Delta(-3, -1, 0, 0), 10, "BREW", 0, 0, false, false);
        Inventory inv = new Inventory(new Delta(3, 0, 0, 0), 0);
        inv.cast.add(new Receipt(1, new Delta(2, 0, 0, 0), 10, "CAST", 0, 0, true, false));
        inv.cast.add(new Receipt(2, new Delta(-1, 1, 0, 0), 10, "CAST", 0, 0, true, false));
        inv.cast.add(new Receipt(3, new Delta(0, -1, 1, 0), 10, "CAST", 0, 0, true, false));
        inv.cast.add(new Receipt(4, new Delta(0, 0, -1, 1), 10, "CAST", 0, 0, true, false));
        
        List<Receipt> solution = receipt.requiredCast(inv);
        assertEquals(2, solution.size());
    }
    
    @Test
    public void testReceiptStepThreeCast() {
        Receipt receipt = new Receipt(41, new Delta(-5, -1, 0, 0), 10, "BREW", 0, 0, false, false);
        Inventory inv = new Inventory(new Delta(3, 0, 0, 0), 0);
        inv.cast.add(new Receipt(1, new Delta(2, 0, 0, 0), 10, "CAST", 0, 0, true, false));
        inv.cast.add(new Receipt(2, new Delta(-1, 1, 0, 0), 10, "CAST", 0, 0, true, false));
        inv.cast.add(new Receipt(3, new Delta(0, -1, 1, 0), 10, "CAST", 0, 0, true, false));
        inv.cast.add(new Receipt(4, new Delta(0, 0, -1, 1), 10, "CAST", 0, 0, true, false));
        
        List<Receipt> solution = receipt.requiredCast(inv);
        assertEquals(3, solution.size());
    }
    
    @Test
    public void testReceiptStepThritySixCast() {
        Receipt receipt = new Receipt(41, new Delta(0, -1, 0, -5), 10, "BREW", 0, 0, false, false);
        Inventory inv = new Inventory(new Delta(3, 0, 0, 0), 0);
        inv.cast.add(new Receipt(1, new Delta(2, 0, 0, 0), 10, "CAST", 0, 0, true, false));
        inv.cast.add(new Receipt(2, new Delta(-1, 1, 0, 0), 10, "CAST", 0, 0, true, false));
        inv.cast.add(new Receipt(3, new Delta(0, -1, 1, 0), 10, "CAST", 0, 0, true, false));
        inv.cast.add(new Receipt(4, new Delta(0, 0, -1, 1), 10, "CAST", 0, 0, true, false));
        
        List<Receipt> solution = receipt.requiredCast(inv);
        assertEquals(36, solution.size());
    }
    
    
    /*
    Receipt{id=73, required=[-1, -1, -1, -1], price=12, action='BREW', tomeIndex=-1, taxCount=-1, castable=false, repeatable=false}
Receipt{id=51, required=[-2, 0, -3, 0], price=11, action='BREW', tomeIndex=-1, taxCount=-1, castable=false, repeatable=false}
Receipt{id=48, required=[0, -2, -2, 0], price=10, action='BREW', tomeIndex=-1, taxCount=-1, castable=false, repeatable=false}
Receipt{id=69, required=[-2, -2, -2, 0], price=13, action='BREW', tomeIndex=-1, taxCount=-1, castable=false, repeatable=false}
Receipt{id=56, required=[0, -2, -3, 0], price=13, action='BREW', tomeIndex=-1, taxCount=-1, castable=false, repeatable=false}
Receipt{id=78, required=[2, 0, 0, 0], price=0, action='CAST', tomeIndex=-1, taxCount=-1, castable=true, repeatable=false}
Receipt{id=79, required=[-1, 1, 0, 0], price=0, action='CAST', tomeIndex=-1, taxCount=-1, castable=true, repeatable=false}
Receipt{id=80, required=[0, -1, 1, 0], price=0, action='CAST', tomeIndex=-1, taxCount=-1, castable=true, repeatable=false}
Receipt{id=81, required=[0, 0, -1, 1], price=0, action='CAST', tomeIndex=-1, taxCount=-1, castable=true, repeatable=false}
Receipt{id=82, required=[2, 0, 0, 0], price=0, action='OPPONENT_CAST', tomeIndex=-1, taxCount=-1, castable=true, repeatable=false}
Receipt{id=83, required=[-1, 1, 0, 0], price=0, action='OPPONENT_CAST', tomeIndex=-1, taxCount=-1, castable=true, repeatable=false}
Receipt{id=84, required=[0, -1, 1, 0], price=0, action='OPPONENT_CAST', tomeIndex=-1, taxCount=-1, castable=true, repeatable=false}
Receipt{id=85, required=[0, 0, -1, 1], price=0, action='OPPONENT_CAST', tomeIndex=-1, taxCount=-1, castable=true, repeatable=false}
New best receipt: Receipt{id=69, required=[-2, -2, -2, 0], price=13, action='BREW', tomeIndex=-1, taxCount=-1, castable=false, repeatable=false}
New cast list compute ask for: Receipt{id=69, required=[-2, -2, -2, 0], price=13, action='BREW', tomeIndex=-1, taxCount=-1, castable=false, repeatable=false} with inv: Inventory{cast=[Receipt{id=78, required=[2, 0, 0, 0], price=0, action='CAST', tomeIndex=-1, taxCount=-1, castable=true, repeatable=false}, Receipt{id=79, required=[-1, 1, 0, 0], price=0, action='CAST', tomeIndex=-1, taxCount=-1, castable=true, repeatable=false}, Receipt{id=80, required=[0, -1, 1, 0], price=0, action='CAST', tomeIndex=-1, taxCount=-1, castable=true, repeatable=false}, Receipt{id=81, required=[0, 0, -1, 1], price=0, action='CAST', tomeIndex=-1, taxCount=-1, castable=true, repeatable=false}], inv=[3, 0, 0, 0], rupees=0}

     */
    
    @Test
    public void testReceiptStepFromCGCast() {
        Receipt receipt = new Receipt(41, new Delta(-2, -2, -2, 0), 10, "BREW", 0, 0, false, false);
        
        Inventory inv = new Inventory(new Delta(3, 0, 0, 0), 0);
        inv.cast.add(new Receipt(1, new Delta(2, 0, 0, 0), 10, "CAST", 0, 0, true, false));
        inv.cast.add(new Receipt(2, new Delta(-1, 1, 0, 0), 10, "CAST", 0, 0, true, false));
        inv.cast.add(new Receipt(3, new Delta(0, -1, 1, 0), 10, "CAST", 0, 0, true, false));
        inv.cast.add(new Receipt(4, new Delta(0, 0, -1, 1), 10, "CAST", 0, 0, true, false));
    
        List<Receipt> solution = receipt.requiredCast(inv);
        assertEquals(14, solution.size());
    }
    
}
