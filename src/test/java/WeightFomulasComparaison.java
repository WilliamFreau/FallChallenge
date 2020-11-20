import com.codingame.gameengine.runner.MultiplayerGameRunner;
import com.codingame.gameengine.runner.simulate.GameResult;
import org.junit.Test;

import java.util.*;
import java.util.stream.IntStream;


public class WeightFomulasComparaison {
    
    public static final Class AI[] = {v1.Player.class, v2.Player.class, v3.Player.class, v4.Player.class, v5.Player.class};
    
    /**
     * Each round is with a different seed and between round, player is interverted
     */

    public static void main(String[] args) throws InterruptedException {
        
        
        Random random = new Random();
        List<CombatThread> threads = new ArrayList<>();
        for(int i = 0 ; i < AI.length ; i++)
            for(int j = 0 ; j < AI.length ; j++) {
                if(i==j)
                    continue;
                CombatThread combatThread = new CombatThread(i, j);
                Thread thread = new Thread(combatThread);
                thread.start();
                threads.add(combatThread);
            }
        
        while(threads.stream().anyMatch(combatThread -> !combatThread.end)) {
            System.out.println("Thread remain: " + threads.stream().filter(combatThread -> !combatThread.end).count());
            Thread.sleep(1000);
        }
        
        int[][] victory = new int[AI.length][AI.length];
        int[][] draw = new int[AI.length][AI.length];
        
        for(CombatThread thread : threads){
            for(Combat combat : thread.combats){
                if(combat.result.scores.get(0) > combat.result.scores.get(1)) {
                    victory[thread.indexP1][thread.indexP2]++;
                } else if(combat.result.scores.get(0) < combat.result.scores.get(1)) {
                    victory[thread.indexP2][thread.indexP1]++;
                } else {
                    draw[thread.indexP2][thread.indexP1]++;
                }
            }
        }
        System.out.println("Victory");
        printArray(victory);
        System.out.println("\n");
    
        System.out.println("Draw");
        printArray(draw);
        System.out.println("\n");
    }
    
    static void printArray(int[][] result) {
        for(int i = 0 ; i < result.length ; i++) {
            if(i == 0) {
                System.out.print("\t\t\t");
                for(int j = 0 ; j < result[i].length ; j++) {
                    System.out.print("\t|\t");
                    System.out.print(AI[j].getName());
                }
                System.out.print("\t|");
                System.out.println("\n----------------|---------------|---------------|---------------|---------------|---------------|");
            }
            for(int j = 0 ; j < result[i].length; j++) {
                if(j == 0) {
                    System.out.print("\t");
                    System.out.print(AI[i].getName());
                    System.out.print("\t|\t\t");
                }
                if(i!=j)
                    System.out.print(result[i][j]);
                else
                    System.out.print("");
                System.out.print("\t\t|\t\t");
            }
            //--------------------
            System.out.println("\n----------------|---------------|---------------|---------------|---------------|---------------|");
        }
    }
}


class Combat {
    public Class player1;
    public Class player2;
    GameResult result;
}


class CombatThread implements Runnable {
    private static final int NUMBER_ROUND = 100;
    
    public List<Combat> combats = new ArrayList<>();
    private Class p1, p2;
    public int indexP1, indexP2;
    public boolean end = false;
    private Random random = new Random();
    
    public CombatThread(int indexP1, int indexP2) {
        this.indexP1 = indexP1;
        this.indexP2 = indexP2;
        this.p1 = WeightFomulasComparaison.AI[this.indexP1];
        this.p2 = WeightFomulasComparaison.AI[this.indexP2];
    }
    
    @Override
    public void run() {
        for (int i = 0; i < NUMBER_ROUND; ++i) {
            Combat combat = new Combat();
            if(i%2==0) {
                combat.player1 = p1;
                combat.player2 = p2;
            } else {
                combat.player1 = p2;
                combat.player2 = p1;
            }
            MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
            gameRunner.setSeed(random.nextLong());
            gameRunner.addAgent(combat.player1);
            gameRunner.addAgent(combat.player2);
            combat.result= gameRunner.simulate();
            combats.add(combat);
        }
        end = true;
    }
}