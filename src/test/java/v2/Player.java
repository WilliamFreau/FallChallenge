package v2;

import java.util.Scanner;


/**
 * Re-transcription du code C en Java
 *
 * Pas de stream ou autres
 **/
public class Player {
    
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
            Univers.checkOpponentBrew();
            
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            Solver solver = new Solver();
            solver.solve();
            Path path = PathChooser.getPath(solver);
            
            if(Univers.INFO){
                if(Univers.currentDestination != null) {
                    System.err.println("Current destination: " + Univers.currentDestination.actionId);
                }
            }
            
            // in the first league: BREW <id> | WAIT; later: BREW <id> | CAST <id> [<times>] | LEARN <id> | REST | WAIT
            Timer.printElapsed();
            if(path == null){
                if(Univers.myself.castable.stream().anyMatch(receipt -> !receipt.castable))
                    System.out.println("REST 01");
                else {
                    if(Univers.learnable.size() > 0){
                        //Learn if Possible
                        for(Receipt learn : Univers.learnable) {
                            if(Univers.myself.delta.t1 < learn.tome_index)
                                continue;
                            System.out.println(learn.action + " " + learn.actionId + " 01");
                            break;
                        }
                    } else {
                        System.out.println("WAIT 01");
                    }
                }
            } else {
                System.out.println(path.nodes.get(0).asAction());
            }
        }
    }
}


