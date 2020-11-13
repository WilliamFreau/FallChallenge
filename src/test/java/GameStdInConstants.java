import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class GameStdInConstants {
    
    public static String SIMPLE_INPUT =
            "2" + "\n" +
            "45" + "\n" +
            "BREW" + "\n" +
            "1" + "\n" +
            "2" + "\n" +
            "3" + "\n" +
            "4" + "\n" +
            "10" + "\n" +
            "0" + "\n" +
            "0" + "\n" +
            "0" + "\n" +
            "0" + "\n" +
            "46" + "\n" +
            "CAST" + "\n" +
            "1" + "\n" +
            "2" + "\n" +
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
