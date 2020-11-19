package v1;

import java.util.ArrayList;
import java.util.List;

/**
 * Static class Univers to keep track info between turn
 */
public class Univers {
    public static boolean DEBUG = false;            //Permet de change le niveau de log.
    public static boolean INFO = true;              //Désactiver tout pour le submit, cela fait gagner du temps!
    
    public static final String WAIT = "WAIT";
    public static final String REST  = "REST";
    public static final String BREW = "BREW";
    public static final String LEARN  = "LEARN";
    public static final String CASTS  = "CAST";
    public static final String OPP_CAST  = "OPPONENT_CAST";
    public static final int SPECIAL_RECEIPT_ID = 255;
    
    public static int heuristique_formula = 1;
    
    public static final int BUFFER = 256;
    public static final int NUMBER_BREW_TO_STOP = 6;
    
    public static int MY_BREW_NUMBER = 0;
    public static int OPPONENT_BREW_NUMBER = 0;
    
    public static Inventory myself = new Inventory();           //My inventory
    public static Inventory opponent = new Inventory();         //Opponent Inventory
    public static List<Receipt> learnable = new ArrayList<>();       //Keep tack of learn
    public static List<Receipt> brewable = new ArrayList<>();       //Keep track of brewables
    
    private static int lastOpponentScore;
    private static int lastScore;
    
    public static Receipt currentDestination = null;
    
    
    public static List<Receipt> cloneList(List<Receipt> old) {
        List<Receipt> ret = new ArrayList<>(old.size());
        old.forEach(receipt -> ret.add(receipt.clone()));
        return ret;
    }   //Usefull function used to clone a List
    
    public static void checkOpponentBrew() {                        //Keep track of number of Brew
        if(myself.score > lastScore) {
            MY_BREW_NUMBER++;
            currentDestination = null;
        }
        if(opponent.score > lastOpponentScore)
            OPPONENT_BREW_NUMBER++;
    }
    
    public static void clean() {                                //Partial reset the Univers between turn
        myself.castable.clear();
        opponent.castable.clear();
        learnable.clear();
        brewable.clear();
        lastOpponentScore = opponent.score;
        lastScore = myself.score;
    }
    
    public static boolean destNoMoreBrewable() {            //Look if destination is currently in brewable
        //In case of the opponent had brew before me
        for(Receipt brew : brewable) {
            if(currentDestination.actionId == brew.actionId)
                return false;
        }
        return true;
    }
}
