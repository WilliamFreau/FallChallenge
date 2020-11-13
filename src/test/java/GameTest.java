import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GameTest {
    
    @Test
    public void testSimpleInputRead() {
        Game game = new Game(new Scanner(GameStdInConstants.asInputStream(GameStdInConstants.SIMPLE_INPUT)));
        game.readInput();
        
        assertEquals(game.receipts.size(), 2);
        assertEquals(game.receipts.get(0).toAction(), "BREW 45");
        assertEquals(game.receipts.get(1).toAction(), "CAST 46");
    }
    
    
    
    @Test
    public void testFindFeasableReceiptWithOneFeasable() {
        Inventory my = new Inventory(new Delta(2, 0, 0, 0), 0);
        Inventory opp = new Inventory(new Delta(2, 0, 0, 0), 0);
        
        Receipt feasable = new Receipt(1, new Delta(1, 0, 0, 0), 1, "BREW", 0, 0, false, false);
        Receipt notFeasable = new Receipt(2, new Delta(1, 0, 0, 10), 1, "BREW", 0, 0, false, false);
        
        Game game = new Game();
        game.receipts.add(feasable);
        game.receipts.add(notFeasable);
        game.myInventory = my;
        game.opponentInventory = opp;
        
        Receipt find = game.getBestPossibleReceipt();
        assertEquals(feasable, find);
    }
    
    @Test
    public void testFindFeasableReceiptWithTwoFeasable() {
        Inventory my = new Inventory(new Delta(2, 0, 0, 0), 0);
        Inventory opp = new Inventory(new Delta(2, 0, 0, 0), 0);
        
        Receipt feasable = new Receipt(1, new Delta(1, 0, 0, 0), 1, "BREW", 0, 0, false, false);
        Receipt feasableBest = new Receipt(2, new Delta(1, 0, 0, 0), 10, "BREW", 0, 0, false, false);
        
        Game game = new Game();
        game.receipts.add(feasable);
        game.receipts.add(feasableBest);
        game.myInventory = my;
        game.opponentInventory = opp;
        
        Receipt find = game.getBestPossibleReceipt();
        assertEquals(feasableBest, find);
    }
    
    @Test
    public void testFindFeasableReceiptWithNoFeasable() {
        Inventory my = new Inventory(new Delta(2, 0, 0, 0), 0);
        Inventory opp = new Inventory(new Delta(2, 0, 0, 0), 0);
        
        Receipt feasable = new Receipt(1, new Delta(1, 0, 0, 10), 1, "BREW", 0, 0, false, false);
        Receipt feasableBest = new Receipt(2, new Delta(1, 0, 0, 10), 10, "BREW", 0, 0, false, false);
        
        Game game = new Game();
        game.receipts.add(feasable);
        game.receipts.add(feasableBest);
        game.myInventory = my;
        game.opponentInventory = opp;
        
        Receipt find = game.getBestPossibleReceipt();
        assertNull(find);
    }
}
