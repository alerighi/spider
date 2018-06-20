package it.alerighi.spider;

import javax.swing.*;

import static it.alerighi.spider.Util.*;

/**
 * Main application class
 *
 * @author Alessandro Righi
 */
public enum Main {
    ;

    public static void main(String args[]) {
        info(Spider.APPLICATION_NAME + " version " + Spider.APPLICATION_VERSION);

        if (Util.IS_MAC) {
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
