import java.util.*;
import java.util.stream.IntStream;

/**
 * Static class Univers to keep track info between turn
 */
class Univers {
    public static boolean DEBUG = false;
    public static boolean INFO = true;
    
    public static final String WAIT = "WAIT";
    public static final String REST  = "REST";
    public static final String BREW = "BREW";
    public static final String LEARN  = "LEARN";
    public static final String CASTS  = "CAST";
    public static final String OPP_CAST  = "OPPONENT_CAST";
    public static final int SPECIAL_RECEIPT_ID = 255;
    
    
    public static final int BUFFER = 256;
    
    public static Inventory myself = new Inventory();           //My inventory
    public static Inventory opponent = new Inventory();         //Opponent Inventory
    public static List<Receipt> learnable = new ArrayList<>();       //Keep tack of learn
    public static List<Receipt> brewable = new ArrayList<>();       //Keep track of brewables
    
    public static Receipt currentDestination = null;
    
    
    public static List<Receipt> cloneList(List<Receipt> old) {
        List<Receipt> ret = new ArrayList<>(old.size());
        old.forEach(receipt -> ret.add(receipt.clone()));
        return ret;
    }
    
    
    
    public static void clean() {
        myself.castable.clear();
        opponent.castable.clear();
        learnable.clear();
        brewable.clear();
    }
    
    public static boolean destNoMoreBrewable() {
        for(Receipt brew : brewable) {
            if(currentDestination.actionId == brew.actionId)
                return false;
        }
        return true;
    }
}

/**
 * Re-transcription du code C en Java
 *
 * Pas de stream ou autres
 **/
class Player {
    
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        
        // game loop
        while (true) {
            
            //Cleaning Univers
            Univers.clean();
            
            
            
            int actionCount = in.nextInt(); // the number of spells and recipes in play
            Timer.start_turn();
            for (int i = 0; i < actionCount; i++) {
                int actionId = in.nextInt(); // the unique ID of this spell or recipe
                String actionType = in.next(); // in the first league: BREW; later: CAST, OPPONENT_CAST, LEARN, BREW
                int delta0 = in.nextInt(); // tier-0 ingredient change
                int delta1 = in.nextInt(); // tier-1 ingredient change
                int delta2 = in.nextInt(); // tier-2 ingredient change
                int delta3 = in.nextInt(); // tier-3 ingredient change
                int price = in.nextInt(); // the price in rupees if this is a potion
                int tomeIndex = in.nextInt(); // in the first two leagues: always 0; later: the index in the tome if this is a tome spell, equal to the read-ahead tax; For brews, this is the value of the current urgency bonus
                int taxCount = in.nextInt(); // in the first two leagues: always 0; later: the amount of taxed tier-0 ingredients you gain from learning this spell; For brews, this is how many times you can still gain an urgency bonus
                boolean castable = in.nextInt() != 0; // in the first league: always 0; later: 1 if this is a castable player spell
                boolean repeatable = in.nextInt() != 0; // for the first two leagues: always 0; later: 1 if this is a repeatable player spell
                
                new Receipt(actionId, actionType, new Delta(delta0, delta1, delta2, delta3), price, tomeIndex, taxCount, castable, repeatable);
            }
            for (int i = 0; i < 2; i++) {
                int inv0 = in.nextInt(); // tier-0 ingredients in inventory
                int inv1 = in.nextInt();
                int inv2 = in.nextInt();
                int inv3 = in.nextInt();
                int score = in.nextInt(); // amount of rupees
                if(i == 0) {
                    //my info
                    Univers.myself.delta = new Delta(inv0, inv1, inv2, inv3);
                    Univers.myself.score = score;
                }
                else {
                    //opponent info
                    Univers.opponent.delta = new Delta(inv0, inv1, inv2, inv3);
                    Univers.opponent.score = score;
                }
            }
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            Solver solver = new Solver();
            solver.solve();
            Path path = null;
            if(Univers.currentDestination == null || Univers.destNoMoreBrewable() || solver.paths.get(Univers.currentDestination) == null) {
                OptionalDouble avg = solver.paths.entrySet().stream().flatMapToInt(receiptPathEntry -> IntStream.of(receiptPathEntry.getValue().nodes.size())).average();
                double min = Double.MAX_VALUE;
                for (Path p : solver.paths.values()) {
                    if ((p.nodes.size() - avg.getAsDouble()) < min) {
                        path = p;
                        min = (p.nodes.size() - avg.getAsDouble());
                    }
                }
                if (path != null) {
                    Univers.currentDestination = path.destination;
                    if (Univers.INFO) {
                        System.err.println("New destination: " + path.destination.actionId);
                    }
                }
            } else {
                path = solver.paths.get(Univers.currentDestination);
            }
            
            if(Univers.INFO){
                System.err.println("Current destination: " + Univers.currentDestination.actionId);
            }
            
            // in the first league: BREW <id> | WAIT; later: BREW <id> | CAST <id> [<times>] | LEARN <id> | REST | WAIT
            Timer.printElapsed();
            if(path == null){
                System.out.println("WAIT path null");
            } else {
                System.out.println(path.nodes.get(0).asAction());
            }
        }
    }
}

/**
 * Permit to solve all Brewable from my inventory
 */
class Solver {
    private static final int DEPTH_LIMIT = 50;
    private static long mean_required_solve_time = 50;
    private static long number_solve_turn = 1;
    
    private final Inventory myself;
    private final List<Receipt> learnable;
    private final List<Receipt> brewable;
    
    public Map<Receipt, Path> paths;
    
    /**
     * Create a copy of the universe and Receipt to test and simulate the resolution
     */
    public Solver() {
        this.paths = new HashMap<>();
        
        this.myself = Univers.myself.clone();
        this.learnable = Univers.cloneList(Univers.learnable);
        this.brewable = Univers.cloneList(Univers.brewable);
    }
    
    /**
     *  Run the solver
     */
    public void solve() {
        //Solve for all
        for(Receipt brew : brewable) {
            if(Univers.INFO) {
                System.err.println("Receipt " + brew.actionId);
            }
            Path path = this.solve(brew, myself.clone());
            if(path != null) {
                if(Univers.INFO) {
                    System.err.println("|   Path len: " + path.nodes.size());
                }
                this.paths.put(brew, path);
            } else {
                if(Univers.INFO) {
                    System.err.println("|   No path");
                }
            }
        }
        if(Univers.INFO) {
            System.err.println("Solver end");
            System.err.println("Mean solve per turn time [ " + mean_required_solve_time/number_solve_turn + " nano ]\n");
        }
    }
    
    /**
     * Permit to solve
     * @param destination
     * @param inventory
     * @return
     */
    private Path solve(Receipt destination, Inventory inventory) {
        List<PathNode> openList = new ArrayList<>(Univers.BUFFER);      //Allocate many space
        PathNode start = new PathNode();
        start.currentDelta = inventory.delta.clone();
        start.castables = Univers.cloneList(inventory.castable);
        start.learnable = Univers.cloneList(learnable);
        openList.add(start);
        
        PathNode arrival = null;
        //Tant qu'on à un élément dans la liste
        while(openList.size() > 0 && Timer.hasTime(mean_required_solve_time/number_solve_turn, 1000*1000)) {
            long startTime = Timer.currentElapsed();
            PathNode current = null;
            double minW = Double.MAX_VALUE;
            for(PathNode node : openList){
                double w = node.weight(destination);
                if(minW > w) {
                    current = node;
                    minW = w;
                }
            }
            openList.remove(current);
            
            if(Univers.DEBUG) {
                System.err.println("|   " + current.currentDelta.toString() + " depth: " + current.depth + " w: " + minW);
            }
            if(current.currentDelta.greaterThan(destination.delta.abs())) {
                //Arrived Need to create Brew PathNode
                arrival = new PathNode(current, destination);
                break;
            }
            
            //Create subnode for Learnable
            for(Receipt learn : current.learnable) {
                if(current.currentDelta.t1 < learn.tome_index)
                    continue;
                
                boolean addCurrent = true;
                PathNode nextNode = new PathNode(current, learn);
                
                //Look if it's not a duplicated state with better depth
                keepOpenListClean(openList, addCurrent, nextNode);
            }
            
            //Create subnode for cast
            for(Receipt cast : current.castables) {
                boolean addCurrent = true;
                PathNode nextNode;
                if(!cast.castable) {        //Look if need to rest
                    nextNode = new PathNode(current, Receipt.res_receipt.clone());
                }
                else {
    
                    //Need to check if cast is feasable
                    if (!current.currentDelta.feasable(cast.delta))
                        continue;
    
                    nextNode = new PathNode(current, cast);
                }
                if(nextNode.depth > DEPTH_LIMIT)                        //Stop at depth
                    break;
    
                //Look if it's not a duplicated state with better depth
                keepOpenListClean(openList, addCurrent, nextNode);
            }
            
            mean_required_solve_time += (Timer.currentElapsed()-startTime);
            number_solve_turn ++;
        }
        
        if(arrival != null) {
            //Arrived at the end
            //Need to rebuild the path
            return new Path(destination, arrival);
        }
        return null;
    }
    
    /**
     * Keep the openList clean
     * @param openList  The open node list
     * @param addCurrent    If it's required to add the current node
     * @param nextNode      The next node
     */
    private void keepOpenListClean(List<PathNode> openList, boolean addCurrent, PathNode nextNode) {
        List<PathNode> toRemove = new ArrayList<>();            //Node with more depth and same state
        for(PathNode node : openList) {
            if(nextNode.currentDelta.equals(node.currentDelta) && nextNode.cdCast()<node.cdCast() )
                if(nextNode.depth < node.depth)
                    toRemove.add(node);
                else
                    addCurrent = false;
        }
        for(PathNode node : toRemove)
            openList.remove(node);
        
        
        if(addCurrent) {                 //Have found the same state with a better depth
            openList.add(0, nextNode);
        }
    }
}


class Path {
    public Receipt destination;
    public List<PathNode> nodes;

    public Path(Receipt destination, PathNode terminal) {
        this.destination = destination;
        this.nodes = new LinkedList<>();
        
        rebuildPath(terminal);
    }
    
    private void rebuildPath(PathNode p) {
        PathNode current = p;
        while(current.parent != null) {
            this.nodes.add(0, current);
            current = current.parent;
        }
    }
}

class PathNode {
    public PathNode parent;
    public Receipt step;
    public int repeat;
    public int depth;
    public Delta currentDelta;
    public List<Receipt> castables; //Keep copy of castable and
    public List<Receipt> learnable;
    
    public PathNode() {
        this.depth = 0;
    }
    
    public PathNode(PathNode parent) {
        this.parent = parent;
        this.currentDelta = parent.currentDelta.clone();
        this.depth = this.parent.depth + 1;
        this.castables = Univers.cloneList(this.parent.castables);
        this.learnable = Univers.cloneList(this.parent.learnable);
    }
    
    public PathNode(PathNode parent, Receipt cast) {
        this(parent);
        this.step = cast;
        this.step.castable = false;
        
        if(Univers.LEARN.equals(this.step.action)) {
            this.currentDelta.t1 -= this.step.tome_index;
            this.currentDelta.t1 += this.step.tax_count;
            this.learnable.remove(this.step);
            Receipt newCast = this.step.clone();
            newCast.action = Univers.CASTS;
            this.castables.add(newCast);
        } else if(Univers.REST.equals(this.step.action))    {
            this.castables.forEach(receipt -> receipt.castable = true);
        } else if(Univers.CASTS.equals(this.step.action)){
            for (Receipt c : castables) {
                if (c.actionId == this.step.actionId)
                    c.castable = false;
            }
            this.currentDelta.sum(cast.delta);
        }
        
    }
    
    public double weight(Receipt destination) {
        Delta d = this.currentDelta.getMissings(destination.delta);
        return d.abs().sum()*3.0d - depth + castables.size();
    }
    
    public String asAction() {
        if(repeat > 0)
            return step.action + " " + step.actionId + " " + repeat;
        
        if(step.actionId == Univers.SPECIAL_RECEIPT_ID)
            return step.action;
        
        
        return step.action + " " + step.actionId;
    }
    
    public int cdCast() {
        int ret = 0;
        for(Receipt cast : castables)
            if(!cast.castable)
                ret ++;
        
        return ret;
    }
}




class Receipt {
    public static Receipt wait_receipt = new Receipt(Univers.SPECIAL_RECEIPT_ID, Univers.WAIT, Delta.ZERO, 0, 0, 0, true, false);
    public static Receipt res_receipt = new Receipt(Univers.SPECIAL_RECEIPT_ID, Univers.REST, Delta.ZERO, 0, 0, 0, true, false);
    
    int actionId;
    String action;
    Delta delta;
    int price;
    int tome_index;
    int tax_count;
    boolean castable;
    boolean repeatable;
    
    public Receipt(int actionId, String action, Delta delta, int price, int tome_index, int tax_count, boolean castable, boolean repeatable) {
        this.actionId = actionId;
        this.action = action;
        this.delta = delta;
        this.price = price;
        this.tome_index = tome_index;
        this.tax_count = tax_count;
        this.castable = castable;
        this.repeatable = repeatable;
    
        switch (action) {
            case Univers.LEARN:
                Univers.learnable.add(this);
                break;
            case Univers.BREW:
                Univers.brewable.add(this);
                break;
            case Univers.CASTS:
                Univers.myself.castable.add(this);
                break;
            case Univers.OPP_CAST:
                Univers.opponent.castable.add(this);
                break;
        }
    }
    
    /**
     * Clone contructor
     * @param old Object to clone
     */
    public Receipt(Receipt old) {
        this.actionId = Integer.valueOf(old.actionId);
        this.action = new String(old.action);
        this.delta = new Delta(old.delta);
        this.price = Integer.valueOf(old.price);
        this.tome_index = Integer.valueOf(old.tome_index);
        this.tax_count = Integer.valueOf(old.tax_count);
        this.castable = Boolean.valueOf(old.castable);
        this.repeatable = Boolean.valueOf(old.repeatable);
    }
    
    public Receipt clone() {
        return new Receipt(this);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Receipt receipt = (Receipt) o;
        return actionId == receipt.actionId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(actionId);
    }
}

class Delta {
    public static Delta ZERO = new Delta(0, 0, 0, 0);
    
    
    int t1, t2, t3, t4;
    
    public Delta(int t1, int t2, int t3, int t4) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
    }
    
    public Delta(Delta delta) {
        this.t1 = Integer.valueOf(delta.t1);
        this.t2 = Integer.valueOf(delta.t2);
        this.t3 = Integer.valueOf(delta.t3);
        this.t4 = Integer.valueOf(delta.t4);
    }
    
    public Delta clone() {return new Delta(this);}
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Delta delta = (Delta) o;
        return t1 == delta.t1 &&
                t2 == delta.t2 &&
                t3 == delta.t3 &&
                t4 == delta.t4;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(t1, t2, t3, t4);
    }
    
    /**
     * Add a to self
     * @param a
     */
    public void sum(Delta a) {
        this.t1 = this.t1 + a.t1;
        this.t2 = this.t2 + a.t2;
        this.t3 = this.t3 + a.t3;
        this.t4 = this.t4 + a.t4;
    }
    
    public int sum() {
        return this.t1 + this.t2 + this.t3 + this.t4;
    }
    
    @Override
    public String toString() {
        return "[" +
                " " + t1 +
                " " + t2 +
                " " + t3 +
                " " + t4 +
                ']';
    }
    
    /**
     * Need to check if the delta is possible (have all required item)
     * AND if the inventory will not be overflowed
     * @param delta
     * @return
     */
    public boolean feasable(Delta delta) {
        if(delta.t1 < 0 && this.t1 < Math.abs(delta.t1))
            return false;   //Not enought T1
        if(delta.t2 < 0 && this.t2 < Math.abs(delta.t2))
            return false;   //Not enought T2
        if(delta.t3 < 0 && this.t3 < Math.abs(delta.t3))
            return false;   //Not enought T3
        if(delta.t4 < 0 && this.t4 < Math.abs(delta.t4))
            return false;   //Not enought T4
        return
                this.t1+delta.t1
                        +this.t2+delta.t2
                        +this.t3+delta.t3
                        +this.t4+delta.t4
                <= 10;
        
    }
    
    public Delta abs() {
        return new Delta(Math.abs(this.t1), Math.abs(this.t2), Math.abs(this.t3), Math.abs(this.t4));
    }
    
    public boolean greaterThan(Delta delta) {
        return
                this.t1 >= delta.t1 &&
                this.t2 >= delta.t2 &&
                this.t3 >= delta.t3 &&
                this.t4 >= delta.t4;
    }
    
    public Delta getMissings(Delta delta) {
        Delta ret = new Delta(0, 0, 0, 0);
        ret.t1 = (delta.t1 < 0)? this.t1+delta.t1: 0;
        ret.t2 = (delta.t2 < 0)? this.t2+delta.t2: 0;
        ret.t3 = (delta.t3 < 0)? this.t3+delta.t3: 0;
        ret.t4 = (delta.t4 < 0)? this.t4+delta.t4: 0;
        return ret;
    }
}

class Inventory {
    Delta delta;
    int score;
    List<Receipt> castable;
    
    public Inventory() {
        this.castable = new ArrayList<>();
    }
    
    public Inventory(Delta delta, int score) {
        this();
        this.delta = delta;
        this.score = score;
    }
    
    /**
     * Clone contructor
     */
    public Inventory(Inventory old) {
        this.delta = new Delta(old.delta);
        this.score = Integer.valueOf(old.score);
        this.castable = Univers.cloneList(old.castable);
    }
    
    public void addToCast(Receipt receipt) {
        this.castable.add(receipt);
    }
    
    public Inventory clone() {
        return new Inventory(this);
    }
}

class Timer {
    public static final long FIRST_TURN_TIMEOUT = 1000 * 1000 * 1000;
    public static final long EACH_TURN_TIMEOUT = 50    * 1000 * 1000;
    
    private static long start_nano;
    public static int turn_number = 0;
    
    public static void start_turn() {
        turn_number ++;
        start_nano = System.nanoTime();
    }
    
    /**
     *
     * @param required in nano seconds
     * @return
     */
    public static boolean hasTime(long required) {
        return currentElapsed() + required < (turn_number==1 ?FIRST_TURN_TIMEOUT:EACH_TURN_TIMEOUT);
    }
    
    /**
     *
     * @param required in nano seconds
     * @param delay    Subscrat a delay from the end turn time
     * @return
     */
    public static boolean hasTime(long required, long delay) {
        return currentElapsed() + required < ((turn_number==1 ?FIRST_TURN_TIMEOUT:EACH_TURN_TIMEOUT) - delay);
    }
    
    public static long currentElapsed() {
        return System.nanoTime() - start_nano;
    }
    
    public static long currentElapsedMs() {
        return Timer.currentElapsed() / (1000 * 1000);
    }
    
    public static void printElapsed() {
        System.err.println("Elapsed: " + Timer.currentElapsedMs() + " ms [ " + Timer.currentElapsed() + " ]");
    }
}