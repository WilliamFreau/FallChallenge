package v5;

import java.util.Objects;

/**
 * Une Receipt donc une action du jeux
 */
public class Receipt {
    public static Receipt wait_receipt = new Receipt(Univers.SPECIAL_RECEIPT_ID, Univers.WAIT, Delta.ZERO, 0, 0, 0, true, false);
    public static Receipt rest_receipt = new Receipt(Univers.SPECIAL_RECEIPT_ID, Univers.REST, Delta.ZERO, 0, 0, 0, true, false);
    
    public int actionId;
    public String action;
    public Delta delta;
    public int price;
    public int tome_index;
    public int tax_count;
    public boolean castable;
    public boolean repeatable;
    
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
     *
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
