package it.alerighi.spider;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import static it.alerighi.spider.Util.*;

/**
 * Classe che rappresenta una carta da gioco francese.
 *
 * @author Alessandro Righi
 */
public class Card {

    // dimensioni di una carta
    public static final int HEIGHT = 145;
    public static final int WIDTH = 100;

    // nomi dei semi
    private static final String[] SUITS = {"spades", "hearts", "clubs", "diamonds"};
    private static final String[] VALUES = {"ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king"};

    // Immagini delle carte
    private static Image[] cardsImages = new Image[52];
    private static Image back;

    // variabile che indica se la classe è inizzializzata
    private static boolean initialized = false;

    // valore della carta, da 0 a 52
    private final int value;

    // posizione della carta sullo schermo
    private int positionX;
    private int positionY;

    // indica se la carta è visibile o coperta
    private boolean visible = false;

    /**
     * Metodo statico che inizzializza la classe, caricando le immagini delle carte nel rispettivo array.
     * Deve essere richiamato prima di utilizzare la classe (il costruttore lo richiama comunque)
     */
    public static void loadCardImages() {
        info("Loading images from resource files...");
        try {

            // carico le immagini delle carte normali nell'array cardsImages, una ad una
            int i = 0;
            for (String suit : SUITS) {
                for (String value : VALUES) {
                    cardsImages[i] = ImageIO.read(Card.class.getResourceAsStream(value + "_of_" + suit + ".png"));
                    i++;
                }
            }

            // carico l'immagine del dorsetto della carta
            back = ImageIO.read(Card.class.getResourceAsStream("back.png"));

        } catch (IOException e) {
            err("Errore nel caricamento immagini delle carte!");
        }

        initialized = true;
        info("Done loading card images");
    }

    /**
     * Costruttore di una carta
     *
     * @param suit valore del seme (0-3)
     * @param value valore numerico della carta (1-11)
     */
    public Card(int suit, int value) {
        if (!initialized) {
            loadCardImages();
        }
        if (value < 1 || value > 13 || suit < 0 || suit > 3) {
            throw new IllegalArgumentException("Suit or value out of bounds!");
        }
        this.value = suit * 13 + value - 1;
    }

    /**
     * Metodo statico che disegna il retro di una carta alla posizione specificata
     *
     * @param x coordinata X dove disegnata
     * @param y coordinata Y dove disegnare
     * @param graphics l'oggetto grafico su cui disegnare
     */
    public static void drawCardBack(int x, int y, Graphics graphics) {
        if (!initialized)
            loadCardImages();
        graphics.drawImage(back, x, y, WIDTH, HEIGHT, null);
    }

    /**
     * Metodo che disegna una carta nell'area grafica specificata
     *
     * @param graphics l'area grafica su cui disegnare
     */
    public void drawCard(Graphics graphics) {
        if (visible) {
            graphics.drawImage(cardsImages[value], positionX, positionY, WIDTH, HEIGHT, null);
        } else {
            drawCardBack(positionX, positionY, graphics);
        }
    }

    @Override
    public String toString() {
        return getValueAsString() + " of " + getSuitAsString();
    }

    /*
     * Getters/setters vari
     */

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPosition(int x, int y) {
        this.positionX = x;
        this.positionY = y;
    }

    public int getSuit() {
        return this.value / 13;
    }

    public String getSuitAsString() {
        return SUITS[getSuit()];
    }

    public int getValue() {
        return this.value % 13 + 1;
    }

    public String getValueAsString() {
        return VALUES[getValue() - 1];
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean val) {
        this.visible = val;
    }

}
