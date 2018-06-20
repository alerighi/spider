package it.alerighi.spider;

import javax.swing.*;

/**
 * Main application class
 *
 * @author Alessandro Righi
 */
public enum Main {
    ;

    public static void main(String args[]) {
        if (System.getProperty("os.name").startsWith("Mac")) {
            /* set propriety to use system meny bar on MacOS */
            System.setProperty("apple.awt.application.name", Spider.APPLICATION_NAME);
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Spider();
    }
}
