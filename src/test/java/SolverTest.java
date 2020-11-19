import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class SolverTest {
    
    @BeforeEach
    public void reset() {
        Univers.clean();
    }
    
    @Test
    public void testSimple() {
        Univers.clean();
        new Receipt(76, "BREW", new Delta(-1, -1, -3, -1), 18, 0, 0, false, false);
        Univers.myself.delta = new Delta(3, 0, 0, 0);
        new Receipt(78, "CAST", new Delta(2, 0, 0, 0), 0, 0, 0, true, false);
        new Receipt(79, "CAST", new Delta(-1, 1, 0, 0), 0, 0, 0, true, false);
        new Receipt(80, "CAST", new Delta(0, -1, 1, 0), 0, 0, 0, true, false);
        new Receipt(81, "CAST", new Delta(0, 0, -1, 1), 0, 0, 0, true, false);
        
        new Receipt(26, "LEARN", new Delta(1, 1, 1, -1), 0, 0, 0, true, true);
        new Receipt(29, "LEARN", new Delta(-5, 0, 0, 2), 0, 1, 0, true, true);
        new Receipt(41, "LEARN", new Delta(0, 0, 2, -1), 0, 2, 0, true, false);
        new Receipt(15, "LEARN", new Delta(0, 2, 0, 0), 0, 3, 0, true, true);
        new Receipt(32, "LEARN", new Delta(1, 1, 3, -2), 0, 4, 0, true, true);
        new Receipt(33, "LEARN", new Delta(-5, 0, 0, 3), 0, 5, 0, true, true);
        
        Solver solver = new Solver();
        Timer.start_turn();
        solver.solve();
    }
    
    
    @Test
    public void testRest() {
        Univers.clean();
        new Receipt(55, "BREW", new Delta(0, -3, -2, 0), 15, 0, 0, false, false);
        new Receipt(73, "BREW", new Delta(-1, -1, -1, -1), 13, 0, 0, false, false);
        new Receipt(43, "BREW", new Delta(-3, -2, 0, 0), 7, 0, 0, false, false);
        new Receipt(66, "BREW", new Delta(-2, -1, 0, -1), 9, 0, 0, false, false);
        new Receipt(47, "BREW", new Delta(-3, 0, 0, -2), 9, 0, 0, false, false);
        Univers.myself.delta = new Delta(3, 0, 0, 0);
        new Receipt(78, "CAST", new Delta(2, 0, 0, 0), 0, 0, 0, true, false);
        new Receipt(79, "CAST", new Delta(-1, 1, 0, 0), 0, 0, 0, true, false);
        new Receipt(80, "CAST", new Delta(0, -1, 1, 0), 0, 0, 0, true, false);
        new Receipt(81, "CAST", new Delta(0, 0, -1, 1), 0, 0, 0, true, false);
        
        new Receipt(27, "LEARN", new Delta(1, 2, 0, -1), 0, 0, 0, true, true);
        new Receipt(5, "LEARN",  new Delta(2, 3, 0, -2), 0, 1, 0, true, true);
        new Receipt(32, "LEARN", new Delta(1, 1, 3, -2), 0, 2, 0, true, false);
        new Receipt(22, "LEARN", new Delta(0, 2, -2, 1), 0, 3, 0, true, true);
        new Receipt(28, "LEARN", new Delta(4, 1, -1, 0), 0, 4, 0, true, true);
        new Receipt(3, "LEARN",  new Delta(0, 0, 0, 1), 0, 5, 0, true, true);
        
        Solver solver = new Solver();
        Timer.start_turn();
        solver.solve();
    }
    
    @Test
    //seed:    seed=-2828944963523520000
    public void testExceptionThreadMain() {
        Univers.clean();
        new Receipt(56, "BREW", new Delta(0, -2, -3, 0), 16, 0, 0, false, false);
        new Receipt(55, "BREW", new Delta(0, -3, -2, -0), 13, 0, 0, false, false);
        new Receipt(43, "BREW", new Delta(-3, -2, 0, 0), 7, 0, 0, false, false);
        new Receipt(66, "BREW", new Delta(-2, -1, 0, -1), 9, 0, 0, false, false);
        new Receipt(44, "BREW", new Delta(0, -4, 0, 0), 8, 0, 0, false, false);
        Univers.myself.delta = new Delta(3, 0, 0, 0);
        new Receipt(78, "CAST", new Delta(2, 0, 0, 0), 0, 0, 0, true, false);
        new Receipt(79, "CAST", new Delta(-1, 1, 0, 0), 0, 0, 0, true, false);
        new Receipt(80, "CAST", new Delta(0, -1, 1, 0), 0, 0, 0, true, false);
        new Receipt(81, "CAST", new Delta(0, 0, -1, 1), 0, 0, 0, true, false);
        
        new Receipt(14, "LEARN", new Delta(0, 0, 0, 1), 0, 0, 0, true, false);
        new Receipt(38, "LEARN", new Delta(-2, 2, 0, 0), 0, 1, 0, true, true);
        new Receipt(25, "LEARN", new Delta(0, -3, 0, 2), 0, 2, 0, true, true);
        new Receipt(32, "LEARN", new Delta(1, 1, 3, -2), 0, 3, 0, true, true);
        new Receipt(34, "LEARN", new Delta(-2, 0, -1, 2), 0, 4, 0, true, true);
        new Receipt(26, "LEARN", new Delta(1, 1, 1, -1), 0, 5, 0, true, true);
        
        Solver solver = new Solver();
        Timer.start_turn();
        solver.solve();
    }
    
}
