package v4;

import java.util.ArrayList;
import java.util.List;

/**
 * Reprécente un inventaire d'un joueur
 */
public class Inventory {
    public Delta delta;
    public int score;
    public List<Receipt> castable;
    
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
