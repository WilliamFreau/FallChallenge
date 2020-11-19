import com.codingame.gameengine.runner.MultiplayerGameRunner;

import java.io.IOException;

public class Fall2020Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

        //Choose league level
        gameRunner.setLeagueLevel(3);

        //Add players
        gameRunner.addAgent(v5.Player.class, "Kotake");
        gameRunner.addAgent(v5.Player.class, "Koume");

        //Set game seed
        gameRunner.setSeed(5842184981578562716L);

        //Run game and start viewer on 'http://localhost:8888/'
        gameRunner.start(8888);
    }
}
