import org.junit.Test;

import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class SolverTest {
    
    @Test
    public void testSimple() {
        Univers.clean();
        Univers.brewable.add(new Receipt(76, "BREW", new Delta(-1, -1, -3, -1), 18, 0, 0, false, false));
        Univers.myself.delta = new Delta(3, 0, 0, 0);
        Univers.myself.castable.add(new Receipt(78, "CAST", new Delta(2, 0, 0, 0), 0, 0, 0, true, false));
        Univers.myself.castable.add(new Receipt(79, "CAST", new Delta(-1, 1, 0, 0), 0, 0, 0, true, false));
        Univers.myself.castable.add(new Receipt(80, "CAST", new Delta(0, -1, 1, 0), 0, 0, 0, true, false));
        Univers.myself.castable.add(new Receipt(81, "CAST", new Delta(0, 0, -1, 1), 0, 0, 0, true, false));
        
        Univers.learnable.add(new Receipt(26, "LEARN", new Delta(1, 1, 1, -1), 0, 0, 0, true, true));
        Univers.learnable.add(new Receipt(29, "LEARN", new Delta(-5, 0, 0, 2), 0, 1, 0, true, true));
        Univers.learnable.add(new Receipt(41, "LEARN", new Delta(0, 0, 2, -1), 0, 2, 0, true, false));
        Univers.learnable.add(new Receipt(15, "LEARN", new Delta(0, 2, 0, 0), 0, 3, 0, true, true));
        Univers.learnable.add(new Receipt(32, "LEARN", new Delta(1, 1, 3, -2), 0, 4, 0, true, true));
        Univers.learnable.add(new Receipt(33, "LEARN", new Delta(-5, 0, 0, 3), 0, 5, 0, true, true));
        
        Solver solver = new Solver();
        Timer.start_turn();
        solver.solve();
    }
    
    
    @Test
    public void testRest() {
        Univers.clean();
        Univers.brewable.add(new Receipt(55, "BREW", new Delta(0, -3, -2, 0), 15, 0, 0, false, false));
        Univers.brewable.add(new Receipt(73, "BREW", new Delta(-1, -1, -1, -1), 13, 0, 0, false, false));
        Univers.brewable.add(new Receipt(43, "BREW", new Delta(-3, -2, 0, 0), 7, 0, 0, false, false));
        Univers.brewable.add(new Receipt(66, "BREW", new Delta(-2, -1, 0, -1), 9, 0, 0, false, false));
        Univers.brewable.add(new Receipt(47, "BREW", new Delta(-3, 0, 0, -2), 9, 0, 0, false, false));
        Univers.myself.delta = new Delta(3, 0, 0, 0);
        Univers.myself.castable.add(new Receipt(78, "CAST", new Delta(2, 0, 0, 0), 0, 0, 0, true, false));
        Univers.myself.castable.add(new Receipt(79, "CAST", new Delta(-1, 1, 0, 0), 0, 0, 0, true, false));
        Univers.myself.castable.add(new Receipt(80, "CAST", new Delta(0, -1, 1, 0), 0, 0, 0, true, false));
        Univers.myself.castable.add(new Receipt(81, "CAST", new Delta(0, 0, -1, 1), 0, 0, 0, true, false));
        
        Univers.learnable.add(new Receipt(27, "LEARN", new Delta(1, 2, 0, -1), 0, 0, 0, true, true));
        Univers.learnable.add(new Receipt(5, "LEARN",  new Delta(2, 3, 0, -2), 0, 1, 0, true, true));
        Univers.learnable.add(new Receipt(32, "LEARN", new Delta(1, 1, 3, -2), 0, 2, 0, true, false));
        Univers.learnable.add(new Receipt(22, "LEARN", new Delta(0, 2, -2, 1), 0, 3, 0, true, true));
        Univers.learnable.add(new Receipt(28, "LEARN", new Delta(4, 1, -1, 0), 0, 4, 0, true, true));
        Univers.learnable.add(new Receipt(3, "LEARN",  new Delta(0, 0, 0, 1), 0, 5, 0, true, true));
        
        Solver solver = new Solver();
        Timer.start_turn();
        solver.solve();
    }
    
}
