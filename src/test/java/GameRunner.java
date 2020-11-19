import com.codingame.gameengine.runner.MultiplayerGameRunner;
import com.codingame.gameengine.runner.simulate.GameResult;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GameRunner {


    @Test
    public void gameRune() {
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
        gameRunner.setLeagueLevel(3);
        gameRunner.setSeed(4171177010753416700L);
        gameRunner.addAgent(Player.class);
        gameRunner.addAgent(Player.class);
        gameRunner.start();
        while(true) {
        
        }
    }

    @Test
    public void tests() {
        for (int i = 0; i < 100; ++i) {
            MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
            gameRunner.setSeed((long) (i * 100));
            gameRunner.addAgent(Player.class);
            gameRunner.addAgent(Player.class);
            GameResult result = gameRunner.simulate();
        }
    }

}
