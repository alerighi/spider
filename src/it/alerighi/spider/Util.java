package it.alerighi.spider;

/**
 * Classe statica comprendente funzioni di utilità
 *
 * @author Alessandro Righi
 */
public class Util {
    private Util() {
    }

    /**
     * indica se è attiva la modalità debug
     */
    private static final boolean DEBUG = true;

    static {
        if (DEBUG)
            warn("Debug mode activated!");
    }

    /**
     * Logga un messaggio di informazione
     *
     * @param message messaggio di informazione da loggare
     */
    public static void info(String message) {
        System.out.println("[Info] " + message);

    }

    /**
     * Logga un messaggio di debug
     *
     */
    public static void debug(String message) {
        if (DEBUG)
            System.out.println("[Debug] " + message);
    }

    /**
     * Logga un messaggio di warning
     *
     * @param message messaggio di warning da loggare
     */
    public static void warn(String message) {
        System.err.println("[Warn] " + message);
    }

    /**
     * Logga un messaggio di errore, quindi termina il programma
     *
     * @param message messaggio di errore da loggare
     */
    public static void err(String message) {
        System.err.println("[Err] " + message);
        System.exit(1);
    }

}
