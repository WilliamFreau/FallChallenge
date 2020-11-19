package v5;


/**
 * Class entierement static utilisé pour la gestion du temps
 */
public class Timer {
    public static final long FIRST_TURN_TIMEOUT = 1000 * 1000 * 1000;
    public static final long EACH_TURN_TIMEOUT = 50    * 1000 * 1000;
    
    private static long start_nano;
    public static int turn_number = 0;
    
    public static void start_turn() {
        turn_number ++;
        start_nano = System.nanoTime();
    }
    
    /**
     *
     * @param required in nano seconds
     * @return
     */
    public static boolean hasTime(long required) {
        return currentElapsed() + required < (turn_number==1 ?FIRST_TURN_TIMEOUT:EACH_TURN_TIMEOUT);
    }
    
    /**
     *
     * @param required in nano seconds
     * @param delay    Subscrat a delay from the end turn time
     * @return
     */
    public static boolean hasTime(long required, long delay) {
        return currentElapsed() + required < ((turn_number==1 ?FIRST_TURN_TIMEOUT:EACH_TURN_TIMEOUT) - delay);
    }
    
    public static long currentElapsed() {
        return System.nanoTime() - start_nano;
    }
    
    public static long currentElapsedMs() {
        return Timer.currentElapsed() / (1000 * 1000);
    }
    
    public static void printElapsed() {
        System.err.println("Elapsed: " + Timer.currentElapsedMs() + " ms [ " + Timer.currentElapsed() + " ]");
    }
}