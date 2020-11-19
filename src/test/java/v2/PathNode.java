package v2;

import java.util.List;

/**
 * Un noeud du path pouvant être n'importe qu'elle action et contient aussi les états utilisé pour le solver
 * Gére le repeatable
 */
public class PathNode {
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
        
        if (Univers.LEARN.equals(this.step.action)) {
            this.currentDelta.t1 -= (this.step.tome_index - 1);
            this.currentDelta.t1 += this.step.tax_count;
            this.learnable.remove(this.step);
            Receipt newCast = this.step.clone();
            newCast.action = Univers.CASTS;
            newCast.castable = true;
            this.castables.add(newCast);
        } else if (Univers.REST.equals(this.step.action)) {
            this.castables.forEach(receipt -> receipt.castable = true);
        } else if (Univers.CASTS.equals(this.step.action)) {
            for (Receipt c : castables) {
                if (c.actionId == this.step.actionId)
                    c.castable = false;
            }
            this.currentDelta.sum(cast.delta);
        }
    }
    
    public PathNode(PathNode parent, Receipt cast, int repeat) {
        this(parent, cast);
        this.repeat = repeat;
        if (Univers.CASTS.equals(this.step.action)) {
            //already take one
            for (int i = 0; i < repeat; i++) {
                this.currentDelta.sum(cast.delta);
            }
        }
    }
    
    /**
     * Less is best
     *
     * @param destination
     * @return d.abs().sum()*depth - castables.size()*2.0f;
     */
    public double weight(Receipt destination) {
        return Heuristique.compute(this, destination);
    }
    
    public String asAction() {
        if (repeat > 0)
            return step.action + " " + step.actionId + " " + repeat;
        
        if (step.actionId == Univers.SPECIAL_RECEIPT_ID)
            return step.action;
        
        return step.action + " " + step.actionId;
    }
    
    public int cdCast() {
        int ret = 0;
        for (Receipt cast : castables)
            if (!cast.castable)
                ret++;
        
        return ret;
    }
}
