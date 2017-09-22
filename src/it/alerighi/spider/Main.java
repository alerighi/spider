package it.alerighi.spider;


import javax.swing.*;

import static it.alerighi.spider.Util.info;


/**
 * Classe Main dell'applicazione
 *
 * @author Alessandro Righi
 */
public class Main {

    private Main() {}

    /**
     * indica se in esecuzione su un MAC
     */
    public static final boolean IS_MAC = System.getProperty("os.name").startsWith("Mac");

    /**
     * Entry point del programma
     *
     * @param args argomenti da riga di comando
     */
    public static void main(String args[]) {
        info("This is " + Spider.NAME + " version " + Spider.VERSION);

        if (IS_MAC) {
            /* proprietà necessarie per la barra dei menù in macOS */
            System.setProperty("apple.awt.application.name", Spider.NAME);
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            com.apple.eawt.Application app = com.apple.eawt.Application.getApplication();
            app.setDockIconImage(Spider.ICON_IMAGE);
        }

        /* set della grafica nativa del sistema */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* avvio l'applicazione */
        new Spider();
    }
}
