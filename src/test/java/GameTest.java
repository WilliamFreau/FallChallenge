import org.junit.Test;


import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GameTest {
    
    @Test
    public void testSimpleInputRead() {
        Game game = new Game(new DebugScanner(new Scanner(GameStdInConstants.asInputStream(GameStdInConstants.SIMPLE_INPUT))));
        game.readInput();
        
        assertEquals(game.receipts.size(), 1);
        assertEquals(game.myInventory.cast.size(), 1);
        assertEquals(game.receipts.get(0).toAction(), "BREW 45");
        assertEquals(game.myInventory.cast.get(0).toAction(), "CAST 46");
    }
    
    @Test
    public void testSimpleTwoInputRead() {
        Game game = new Game(new DebugScanner(new Scanner(GameStdInConstants.asInputStream(GameStdInConstants.INPUT_TWO))));
        game.readInput();
        
        game.getMostScoreReceipt();
        
    }
    
    
    
    @Test
    public void testFindMostScoreReceiptWithTwo() {
        Inventory my = new Inventory(new Delta(2, 0, 0, 0), 0);
        Inventory opp = new Inventory(new Delta(2, 0, 0, 0), 0);
        
        Receipt feasable = new Receipt(1, new Delta(-1, 0, 0, 0), 2, "BREW", 0, 0, false, false);
        Receipt notFeasable = new Receipt(2, new Delta(-1, 0, 0, 10), 1, "BREW", 0, 0, false, false);
        
        Game game = new Game();
        game.receipts.add(feasable);
        game.receipts.add(notFeasable);
        game.myInventory = my;
        game.opponentInventory = opp;
        
        Receipt find = game.getMostScoreReceipt();
        assertEquals(feasable, find);
    }
}
