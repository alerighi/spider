package it.alerighi.spider;

/**
 * @author Alessandro Righi
 */
public class Util {
    private Util() {
    }

    private static final boolean LOG_ENABLED = true;

    public static void log(String message) {
        if (LOG_ENABLED) {
            System.out.println(message);
        }
    }
}
