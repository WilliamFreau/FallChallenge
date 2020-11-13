import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class GameStdInConstants {
    
    public static final String SIMPLE_INPUT =
            "2" + "\n" +
            "45" + "\n" +
            "BREW" + "\n" +
            "-1" + "\n" +
            "-2" + "\n" +
            "-3" + "\n" +
            "-4" + "\n" +
            "10" + "\n" +
            "0" + "\n" +
            "0" + "\n" +
            "0" + "\n" +
            "0" + "\n" +
            "46" + "\n" +
            "CAST" + "\n" +
            "-1" + "\n" +
            "-2" + "\n" +
            "0" + "\n" +
            "0" + "\n" +
            "2" + "\n" +
            "0" + "\n" +
            "0" + "\n" +
            "0" + "\n" +
            "0" + "\n" +
                    //INVENTORY myself as first
            "1" + "\n" +    //d1
            "2" + "\n" +    //d2
            "0" + "\n" +    //d3
            "0" + "\n" +    //d4
            "0" + "\n" +    //score
                    // INVENTORY oponent as first
            "1" + "\n" +    //d1
            "2" + "\n" +    //d2
            "0" + "\n" +    //d3
            "0" + "\n" +    //d4
            "0" + "\n" +    //score
            "";
    
    public static final String INPUT_TWO =
            "13\n" +
                    "56\n" +
                    "BREW\n" +
                    "0\n" +
                    "-3\n" +
                    "-2\n" +
                    "0\n" +
                    "13\n" +
                    "-1\n" +
                    "-1\n" +
                    "0\n" +
                    "0\n" +
                    "50\n" +
                    "BREW\n" +
                    "-2\n" +
                    "0\n" +
                    "0\n" +
                    "-2\n" +
                    "10\n" +
                    "-1\n" +
                    "-1\n" +
                    "0\n" +
                    "0\n" +
                    "59\n" +
                    "BREW\n" +
                    "-2\n" +
                    "0\n" +
                    "0\n" +
                    "-3\n" +
                    "14\n" +
                    "-1\n" +
                    "-1\n" +
                    "0\n" +
                    "0\n" +
                    "69\n" +
                    "BREW\n" +
                    "-2\n" +
                    "-2\n" +
                    "-2\n" +
                    "0\n" +
                    "13\n" +
                    "-1\n" +
                    "-1\n" +
                    "0\n" +
                    "0\n" +
                    "51\n" +
                    "BREW\n" +
                    "-2\n" +
                    "-3\n" +
                    "0\n" +
                    "0\n" +
                    "11\n" +
                    "-1\n" +
                    "-1\n" +
                    "0\n" +
                    "0\n" +
                    "78\n" +
                    "CAST\n" +
                    "2\n" +
                    "0\n" +
                    "0\n" +
                    "0\n" +
                    "0\n" +
                    "-1\n" +
                    "-1\n" +
                    "1\n" +
                    "0\n" +
                    "79\n" +
                    "CAST\n" +
                    "-1\n" +
                    "1\n" +
                    "0\n" +
                    "0\n" +
                    "0\n" +
                    "-1\n" +
                    "-1\n" +
                    "1\n" +
                    "0\n" +
                    "80\n" +
                    "CAST\n" +
                    "0\n" +
                    "-1\n" +
                    "1\n" +
                    "0\n" +
                    "0\n" +
                    "-1\n" +
                    "-1\n" +
                    "1\n" +
                    "0\n" +
                    "81\n" +
                    "CAST\n" +
                    "0\n" +
                    "0\n" +
                    "-1\n" +
                    "1\n" +
                    "0\n" +
                    "-1\n" +
                    "-1\n" +
                    "1\n" +
                    "0\n" +
                    "82\n" +
                    "OPPONENT_CAST\n" +
                    "2\n" +
                    "0\n" +
                    "0\n" +
                    "0\n" +
                    "0\n" +
                    "-1\n" +
                    "-1\n" +
                    "1\n" +
                    "0\n" +
                    "83\n" +
                    "OPPONENT_CAST\n" +
                    "-1\n" +
                    "1\n" +
                    "0\n" +
                    "0\n" +
                    "0\n" +
                    "-1\n" +
                    "-1\n" +
                    "1\n" +
                    "0\n" +
                    "84\n" +
                    "OPPONENT_CAST\n" +
                    "0\n" +
                    "-1\n" +
                    "1\n" +
                    "0\n" +
                    "0\n" +
                    "-1\n" +
                    "-1\n" +
                    "1\n" +
                    "0\n" +
                    "85\n" +
                    "OPPONENT_CAST\n" +
                    "0\n" +
                    "0\n" +
                    "-1\n" +
                    "1\n" +
                    "0\n" +
                    "-1\n" +
                    "-1\n" +
                    "1\n" +
                    "0\n" +
                    "3\n" +
                    "0\n" +
                    "0\n" +
                    "0\n" +
                    "0\n" +
                    "3\n" +
                    "0\n" +
                    "0\n" +
                    "0\n" +
                    "0\n" +
                    "";
    
    
    
    
    
    
    
    
    
    /**
     * Permet de transformer une String en inputStream.
     *
     * @param string n'importe la quelle, mais surtout utile pour les cas de jeux au dessus
     * @return
     */
    public static InputStream asInputStream(String string) {
        return new ByteArrayInputStream(string.getBytes());
    }
}
