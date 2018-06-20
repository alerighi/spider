package it.alerighi.spider;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;

import static it.alerighi.spider.Util.*;

/**
 * Classe Main dell'applicazione
 *
 * @author Alessandro Righi
 */
public class Main {

    /**
     * Classe non instanziabile
     */
    private Main() {}

    /**
     * Entry point del programma
     *
     * @param args argomenti da riga di comando
     */
    public static void main(String args[]) {
        info("This is " + Spider.NAME + " version " + Spider.VERSION);

        if (Util.IS_MAC) {
            /* proprietà necessarie per la barra dei menù in macOS */
            System.setProperty("apple.awt.application.name", Spider.NAME);
            System.setProperty("apple.laf.useScreenMenuBar", "true");

            /* setto l'icona del dock usando la reflection (per compatibilità con OS non MAC) */
            try {
                Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
                Method getApplication = applicationClass.getMethod("getApplication");
                Method setDockIconImage = applicationClass.getMethod("setDockIconImage", Image.class);
                Object applicationObject = getApplication.invoke(null);
                setDockIconImage.invoke(applicationObject, Spider.ICON_IMAGE);
            } catch (Exception e) {
                warn("Cannot set dock icon");
            }
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
