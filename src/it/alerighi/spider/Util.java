package it.alerighi.spider;

/**
 * Utility class
 *
 * @author Alessandro Righi
 */
public enum Util {
    ;

    /**
     * Set true for debug mode
     */
    public static final boolean DEBUG = true;

    /**
     * True if the OS is MacOS
     */
    public static final boolean IS_MAC = System.getProperty("os.name").startsWith("Mac");

    static {
        if (DEBUG)
            warn("Debug mode activated!");
    }

    /**
     * Log a info message
     *
     * @param message info message
     */
    public static void info(String message) {
        System.out.println("[Info] " + message);
    }

    /**
     * Log a debug message
     *
     * @param message debug message
     */
    public static void debug(String message) {
        if (DEBUG)
            System.out.println("[Debug] " + message);
    }

    /**
     * Log a debug message
     *
     * @param where class name
     * @param message message to debug
     */
    public static void debug(String where, String message) {
        debug(where + ": " + message);
    }

    /**
     * Log a warning message
     *
     * @param message warning message
     */
    public static void warn(String message) {
        System.err.println("[Warn] " + message);
    }

    /**
     * Log a fatal error message, then terminate the program
     *
     * @param message error message
     */
    public static void die(String message) {
        System.err.println("[Err] " + message);
        System.exit(1);
    }

}
