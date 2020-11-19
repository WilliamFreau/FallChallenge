package v4;

import java.util.Objects;

/**
 * Delta correspond à ce que demande ou posséde une recept
 */
public class Delta {
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
    
    public Delta clone() {
        return new Delta(this);
    }
    
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
     *
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
     *
     * @param delta
     * @return
     */
    public boolean feasable(Delta delta) {
        if (delta.t1 < 0 && this.t1 < Math.abs(delta.t1))
            return false;   //Not enought T1
        if (delta.t2 < 0 && this.t2 < Math.abs(delta.t2))
            return false;   //Not enought T2
        if (delta.t3 < 0 && this.t3 < Math.abs(delta.t3))
            return false;   //Not enought T3
        if (delta.t4 < 0 && this.t4 < Math.abs(delta.t4))
            return false;   //Not enought T4
        return
                this.t1 + delta.t1
                        + this.t2 + delta.t2
                        + this.t3 + delta.t3
                        + this.t4 + delta.t4
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
        ret.t1 = (delta.t1 < 0) ? this.t1 + delta.t1 : 0;
        ret.t2 = (delta.t2 < 0) ? this.t2 + delta.t2 : 0;
        ret.t3 = (delta.t3 < 0) ? this.t3 + delta.t3 : 0;
        ret.t4 = (delta.t4 < 0) ? this.t4 + delta.t4 : 0;
        return ret;
    }
    
    public Delta multiply(int factor) {
        return new Delta(this.t1 * factor, this.t2 * factor, this.t3 * factor, this.t4 * factor);
    }
    
    public int getEndScoreBonus() {
        return this.sum() - this.t1;
    }
}
