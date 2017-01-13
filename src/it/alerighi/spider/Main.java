package it.alerighi.spider;

import javax.swing.UIManager;

/**
 * Classe Main dell'applicazione
 *
 * @author Alessandro Righi
 */
public class Main {
    private Main() {}

    /**
     * Entry point del programma
     *
     * @param args argomenti da riga di comando
     */
    public static void main(String args[]) {

        // proprietà necessarie per la barra dei menù in macOS
        System.setProperty("apple.awt.application.name", "Spider");
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        // set della grafica nativa del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // avvio l'applicazione
        new Spider();
    }
}
