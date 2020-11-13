import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        
        Game game = new Game(in);
        
        // game loop
        while (true) {
            game.readInput();
            
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            
            
            // in the first league: BREW <id> | WAIT; later: BREW <id> | CAST <id> [<times>] | LEARN <id> | REST | WAIT
            //System.out.println("BREW 0");
            Receipt possibleReceipt = game.getBestPossibleReceipt();
            if(possibleReceipt != null) {
                System.out.println(possibleReceipt.toAction());
            }
            else {
                System.out.println("WAIT");
            }
        }
    }
}

class Game {
    private Scanner in;
    
    List<Receipt> receipts;
    Inventory myInventory, opponentInventory;
    
    public Game() {
        this.receipts = new ArrayList<>();
    }
    
    public Game(Scanner in) {
        this();
        this.in = in;
    }
    
    public void readInput() {
        this.receipts.clear();
        this.myInventory = null;
        this.opponentInventory = null;
        
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
            this.receipts.add(r);
        }
        for (int i = 0; i < 2; i++) {
            int inv0 = in.nextInt(); // tier-0 ingredients in inventory
            int inv1 = in.nextInt();
            int inv2 = in.nextInt();
            int inv3 = in.nextInt();
            int score = in.nextInt(); // amount of rupees
            
            Inventory inv = new Inventory(new Delta(inv0, inv1, inv2, inv3), score);
            if(this.myInventory == null) {
                this.myInventory = inv;
            }
            else {
                this.opponentInventory = inv;
            }
        }
    }
    
    public Receipt getBestPossibleReceipt() {
        return this.receipts.stream().sorted(Receipt::compareTo).filter(receipt -> this.myInventory.inv.more(receipt.required)).findFirst().orElse(null);
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
    }
    
    public String toAction() {
        return this.action + " " + this.id;
    }
    
    @Override
    public int compareTo(Receipt o) {
        return o.price-this.price;
    }
}

class Inventory {
    public Delta inv;
    public int rupees;
    
    public Inventory(Delta inv, int rupees) {
        this.inv = inv;
        this.rupees = rupees;
    }
}

class Delta {
    public int d1,d2,d3,d4;
    
    public Delta() {
    }
    
    public Delta(int d1, int d2, int d3, int d4) {
        this.d1 = d1;
        this.d2 = d2;
        this.d3 = d3;
        this.d4 = d4;
    }
    
    /**
     * Compare this delta with parameter delta
     * @param delta
     * @return
     */
    public boolean more(Delta delta) {
        return  this.d1 >= delta.d1 &&
                this.d2 >= delta.d2 &&
                this.d3 >= delta.d3 &&
                this.d4 >= delta.d4 ;
    }
}