import java.util.*;
import java.util.stream.Collectors;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    
    public static boolean ENABLE_INPUT_OUTPUT = false;
    public static boolean ENABLE_DEBUG = false;
    
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        
        Game game = new Game(new DebugScanner(in));
        
        // game loop
        while (true) {
            game.readInput();
            
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            
            
            // in the first league: BREW <id> | WAIT; later: BREW <id> | CAST <id> [<times>] | LEARN <id> | REST | WAIT
            //System.out.println("BREW 0");
            System.out.println(game.getNextAction());
        }
    }
}

class Game {
    private DebugScanner in;
    
    List<Receipt> receipts;
    Inventory myInventory, opponentInventory;
    
    private Receipt bestReceipt;
    private List<Receipt> castList;
    private List<Integer> castedSinceLastRest;
    
    
    public Game() {
        this.myInventory = new Inventory();
        this.opponentInventory = new Inventory();
        this.receipts = new ArrayList<>();
        this.castedSinceLastRest = new ArrayList<>();
    }
    
    public Game(DebugScanner in) {
        this();
        this.in = in;
    }
    
    public void readInput() {
        this.myInventory.cast.clear();
        this.opponentInventory.cast.clear();
        this.receipts.clear();
        
        int actionCount = in.nextInt(); // the number of spells and recipes in play
        for (int i = 0; i < actionCount; i++) {
            int actionId = in.nextInt(); // the unique ID of this spell or recipe
            String actionType = in.next(); // in the first league: BREW; later: CAST, OPPONENT_CAST, LEARN, BREW
            int delta0 = in.nextInt(); // tier-0 ingredient change
            int delta1 = in.nextInt(); // tier-1 ingredient change
            int delta2 = in.nextInt(); // tier-2 ingredient change
            int delta3 = in.nextInt(); // tier-3 ingredient change
            int price = in.nextInt(); // the price in rupees if this is a potion
            int tomeIndex = in.nextInt(); // in the first two leagues: always 0; later: the index in the tome if this is a tome spell, equal to the read-ahead tax
            int taxCount = in.nextInt(); // in the first two leagues: always 0; later: the amount of taxed tier-0 ingredients you gain from learning this spell
            boolean castable = in.nextInt() != 0; // in the first league: always 0; later: 1 if this is a castable player spell
            boolean repeatable = in.nextInt() != 0; // for the first two leagues: always 0; later: 1 if this is a repeatable player spell
            Receipt r = new Receipt(actionId, new Delta(delta0, delta1, delta2, delta3), price, actionType, tomeIndex, taxCount, castable, repeatable);
            this.addNewReceipt(r);
        }
        for (int i = 0; i < 2; i++) {
            int inv0 = in.nextInt(); // tier-0 ingredients in inventory
            int inv1 = in.nextInt();
            int inv2 = in.nextInt();
            int inv3 = in.nextInt();
            int score = in.nextInt(); // amount of rupees
            
            if(i == 0) {
                this.myInventory.update(new Delta(inv0, inv1, inv2, inv3), score);
            }
            else {
                this.opponentInventory.update(new Delta(inv0, inv1, inv2, inv3), score);
            }
        }
    }
    
    private void addNewReceipt(Receipt r) {
        switch (r.action) {
            case "BREW":
                this.receipts.add(r);
                break;
            case "CAST":
                this.myInventory.cast.add(r);
                break;
            case "OPPONENT_CASE":
                this.opponentInventory.cast.add(r);
        }
    }
    
    public Receipt getMostScoreReceipt() {
        return this.receipts.stream().max(Receipt::compareTo).orElse(null);
    }
    
    public Receipt findPossible() {
        return this.receipts.stream().filter(r -> this.myInventory.isFeasable(r)).findFirst().orElse(null);
    }
    
    public String getNextAction() {
        do {
            if (this.bestReceipt == null || !this.receipts.contains(this.bestReceipt)) {
                this.bestReceipt = this.getMostScoreReceipt();
                if (Player.ENABLE_DEBUG)
                    System.err.println("New best receipt found: " + this.bestReceipt);
            }
            if (this.castList == null) {
                this.castList = this.bestReceipt.requiredCast(this.myInventory);
            }
        } while(this.bestReceipt == null || this.castList == null);
        
        System.err.println("Target receipt id: " + this.bestReceipt.id);
        System.err.print("Remain step [");
        for(Receipt r : this.castList) {
            System.err.print(" " + r.id + " ");
        }
        System.err.println("]");
        
        if(this.castList.size() == 0 || this.myInventory.isFeasable(this.bestReceipt)) {
            //Arrivé au bout, donc receipt done
            String action = this.bestReceipt.toAction();
            this.bestReceipt = null;
            this.castList = null;
            return action;
        }
        else {
            //Jouer le cast suivant.
            // S'il n'est pas  castable alors on rest
            if(!this.castedSinceLastRest.contains(this.castList.get(0).id)) {
                String action = this.castList.get(0).toAction();
                this.castedSinceLastRest.add(this.castList.get(0).id);
                this.castList.remove(this.castList.get(0));
                return action;
            }
            else {
                this.castedSinceLastRest.clear();
                return "REST";
            }
        }
    }
}

class Receipt implements Comparable<Receipt> {
    public int id;
    
    public Delta required;
    public int price;
    public String action;
    public int tomeIndex;
    public int taxCount;
    public boolean castable;
    public boolean repeatable;
    
    public Receipt(int id, Delta required, int price, String action, int tomeIndex, int taxCount, boolean castable, boolean repeatable) {
        this.id = id;
        this.required = required;
        this.price = price;
        this.action = action;
        this.tomeIndex = tomeIndex;
        this.taxCount = taxCount;
        this.castable = castable;
        this.repeatable = repeatable;
        
        if(action.equals("BREW"))
        {
            this.required = this.required.negate();
        }
        
        if(Player.ENABLE_DEBUG)
            System.err.println(this.toString());
    }
    
    public String toAction() {
        return this.action + " " + this.id;
    }
    
    @Override
    public int compareTo(Receipt o) {
        return o.price-this.price;
    }
    
    /**
     * Take the inventory and compute the list of cast to be at the correct step
     * @param inventory The base inventory
     * @return  List of casts cast is called n times so need to add the correct rest
     */
    public List<Receipt> requiredCast(Inventory inventory) {
        if(Player.ENABLE_DEBUG)
            System.err.println("New cast list compute ask for: " + this + " with inv: " + inventory);
        Delta current = new Delta(inventory.inv);
        Map<Delta, List<Receipt>> requirement = new HashMap<>();
        
        requirement.put(current, new ArrayList<>());
        List<Delta> unCompute = new LinkedList<>();
        unCompute.add(current);
        
        List<Receipt> reverseCasts = new ArrayList<>(inventory.cast);
        Collections.reverse(inventory.cast);
        //While we are not arrived
        while(requirement.keySet().stream().noneMatch(delta -> delta.more(required))){
            if(unCompute.size() == 0)
                break;
            Map.Entry<Delta, List<Receipt>> minMove = requirement.entrySet().stream().filter(deltaListEntry -> deltaListEntry.getKey().equals(unCompute.get(0))).findFirst().get();
            
            //For all cast, apply and keep change in mind
            for (Receipt cast : reverseCasts) {
                if (requirement.keySet().stream().anyMatch(delta -> delta.more(required)))
                    break;
                Delta nextStep = minMove.getKey().sum(cast.required);
                List<Receipt> parentList = requirement.getOrDefault(nextStep, requirement.getOrDefault(minMove.getKey(), new ArrayList<>()));
                List<Receipt> currentList = new ArrayList<>(parentList);
                if (!nextStep.isValid()) continue;   //Impossible move, overstack the inventory
                if (requirement.containsKey(nextStep) && requirement.get(nextStep).size() > currentList.size()) {
                    //Évite les cas dupliqué, avec un chemin plus court
                    requirement.remove(nextStep);
                } else {
                    currentList.add(cast);
                    requirement.put(nextStep, currentList);
                    
                    if(!unCompute.contains(nextStep))
                        unCompute.add(nextStep);
                }
            }
            unCompute.remove(0);
            
        }
        
        //We have are good in state
        Delta key = requirement.keySet().stream().filter(delta -> delta.more(required)).findFirst().orElse(null);
        if(key == null)
            return null;
        return requirement.get(key);
    }
    
    @Override
    public String toString() {
        return "Receipt{" +
                "id=" + id +
                ", required=" + required +
                ", price=" + price +
                ", action='" + action + '\'' +
                ", tomeIndex=" + tomeIndex +
                ", taxCount=" + taxCount +
                ", castable=" + castable +
                ", repeatable=" + repeatable +
                '}';
    }
}

class Inventory implements Cloneable {
    public List<Receipt> cast;
    
    public Delta inv;
    public int rupees;
    
    public Inventory(Delta inv, int rupees) {
        this();
        this.inv = inv;
        this.rupees = rupees;
        
        if(Player.ENABLE_DEBUG)
            System.err.println(this.toString());
    }
    
    public Inventory() {
        this.cast = new ArrayList<>();
    }
    
    public void update(Delta newDelta, int rupees) {
        this.inv = newDelta;
        this.rupees = rupees;
    }
    
    @Override
    public String toString() {
        return "Inventory{" +
                "cast=" + cast +
                ", inv=" + inv +
                ", rupees=" + rupees +
                '}';
    }
    
    public boolean isFeasable(Receipt receipt) {
        Delta d = this.inv.sum(receipt.required.negate());
        return d.isValid() && d.more(Delta.ZERO);
    }
    
    public Receipt bestCast(Receipt possibleReceipt) {
        Delta goal = this.inv.sum(possibleReceipt.required);
        Delta goalMissing = goal.missing();
        System.err.println("Goal: " + goal);
        System.err.println("Goal missing: " + goalMissing);
        List<Receipt> castable = this.cast.stream().filter(receipt -> receipt.castable && this.isFeasable(receipt)).collect(Collectors.toList());
        
        Receipt best = null;
        int bestWeight = Integer.MIN_VALUE;
        for(Receipt r : castable) {
            Delta afterMissing = goalMissing.sum(r.required);
            Delta after = goal.sum(r.required);
            //Pour chaque castable
            if(bestWeight <= afterMissing.weight()) {
                bestWeight = afterMissing.weight();
                best = r;
            }
        }
        return (bestWeight>=goalMissing.weight())?best: null;
    }
}

class Delta implements Cloneable {
    public static final Delta ZERO = new Delta(0, 0, 0, 0);
    
    public int d1,d2,d3,d4;
    
    public Delta(int d1, int d2, int d3, int d4) {
        this.d1 = d1;
        this.d2 = d2;
        this.d3 = d3;
        this.d4 = d4;
    }
    
    public Delta(Delta inv) {
        this.d1 = inv.d1;
        this.d2 = inv.d2;
        this.d3 = inv.d3;
        this.d4 = inv.d4;
    }
    
    /**
     * Compare this delta with parameter delta
     * @param delta the compared delta
     * @return  True if all value is greater than the other delta
     */
    public boolean more(Delta delta) {
        return  this.d1 >= delta.d1 &&
                this.d2 >= delta.d2 &&
                this.d3 >= delta.d3 &&
                this.d4 >= delta.d4 ;
    }
    
    /**
     * Return a new delta corresponding to the sum between this and d
     * @param d The delta who be added to this
     * @return  Return a new instance of Delta
     */
    public Delta sum(Delta d) {
        return new Delta(this.d1 + d.d1, this.d2+d.d2, this.d3+d.d3, this.d4+d.d4);
    }
    
    public int weight() {
        int w = 0;
        w += d1;
        w += d2 *2;
        w += d3 *2*2;
        w += d4 *2*2*2;
        return w;
    }
    
    /**
     * Return the missing part of the current delta
     **/
    public Delta missing() {
        return new Delta(d1<0?d1:0, d2<0?d2:0, d3<0?d3:0,d4<0?d4:0);
    }
    
    @Override
    public String toString() {
        return "[" +
                "" + d1 +
                ", " + d2 +
                ", " + d3 +
                ", " + d4 +
                ']';
    }
    
    public boolean isValid() {
        return this.d1 + this.d2 + this.d3 + this.d4 <= 10 && this.d1 >= 0 && this.d2 >= 0 && this.d3 >= 0 && this.d4 >= 0;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Delta delta = (Delta) o;
        return d1 == delta.d1 &&
                d2 == delta.d2 &&
                d3 == delta.d3 &&
                d4 == delta.d4;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(d1, d2, d3, d4);
    }
    
    public int distTo(Delta dest) {
        return
                Math.abs(this.d1)-Math.abs(dest.d1) +
                        Math.abs(this.d2)-Math.abs(dest.d2) +
                        Math.abs(this.d3)-Math.abs(dest.d3) +
                        Math.abs(this.d4)-Math.abs(dest.d4) ;
    }
    
    public Delta negate() {
        return new Delta(d1*-1, d2*-1, d3*-1, d4*-1);
    }
}



class DebugScanner {
    
    private final Scanner source;
    
    public DebugScanner(Scanner source) {
        this.source = source;
    }
    
    public int nextInt() {
        int value = source.nextInt();
        if(Player.ENABLE_INPUT_OUTPUT)
            System.err.println(value);
        return value;
    }
    
    public String next() {
        String value = source.next();
        if(Player.ENABLE_INPUT_OUTPUT)
            System.err.println(value);
        return value;
    }
}