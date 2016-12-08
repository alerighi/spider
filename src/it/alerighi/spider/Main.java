package it.alerighi.spider;

import javax.swing.*;

/**
 * Created by ale on 08/12/16.
 */
public class Main {
    public static void main(String args[]) {
        System.setProperty("apple.awt.application.name", "Spider");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Spider();
    }
}
