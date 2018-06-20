package it.alerighi.spider;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

import static it.alerighi.spider.Util.*;

/**
 * Classe che rappresenta una carta da gioco.
 *
 * @author Alessandro Righi
 */
public class Card {

    /**
     * card HEIGHT
     */
    public static final int HEIGHT = 145;

    /**
     * card WIDTH
     */
    public static final int WIDTH = 100;

    /**
     * array dei nomi dei semi
     */
    private static final String[] SUITS = {"spades", "hearts", "clubs", "diamonds"};

    /**
     * array dei valori delle carte
     */
    private static final String[] VALUES = {"ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king"};

    /**
     * immagini delle carte
     */
    private static Image[] cardsImages = new Image[52];

    /**
     * immagine del retro di una carta
     */
    private static Image back;

    /**
     * valore della carta, da 0 a 52
     */
    private final int value;

    /**
     * posizione X della carta su schermo
     */
    private int positionX;

    /**
     * posizione Y della carta su schermo
     */
    private int positionY;

    /**
     * indica se la carta è visibile o coperta
     */
    private boolean isVisible;

    static {
        debug("Card", "loading images from resource files...");
        try {
            /* carico le immagini delle carte normali nell'array cardsImages */
            int i = 0;
            for (String suit : SUITS)
                for (String value : VALUES)
                    cardsImages[i++] = ImageIO.read(Card.class.getResourceAsStream(value + "_of_" + suit + ".png"));

            // carico l'immagine del dorsetto della carta
            back = ImageIO.read(Card.class.getResourceAsStream("back.png"));
        } catch (IOException e) {
            err("Error loading card images!");
        }
    }

    /**
     * Costruttore di una carta
     *
     * @param suit valore del seme (0-3)
     * @param value valore numerico della carta (1-11)
     */
    public Card(int suit, int value) {
        if (value < 1 || value > 13 || suit < 0 || suit > 3) {
            throw new IllegalArgumentException("Suit or value out of bounds!");
        }
        this.value = suit * 13 + value - 1;
    }

    /**
     * Metodo statico che disegna il retro di una carta alla posizione specificata
     *
     * @param x coordinata X dove disegnare
     * @param y coordinata Y dove disegnare
     * @param graphics l'oggetto grafico su cui disegnare
     */
    public static void drawCardBack(int x, int y, Graphics graphics) {
        graphics.drawImage(back, x, y, WIDTH, HEIGHT, null);
    }

    /**
     * Metodo che disegna una carta nell'area grafica specificata
     *
     * @param graphics l'area grafica su cui disegnare
     */
    public void drawCard(Graphics graphics) {
        drawCard(graphics, positionX, positionY);
    }

    /**
     * Imposta la posizione della carta a quella specificata, e la disegna
     *
     * @param graphics area grafica in cui disegnare la carta
     * @param x coordinata X
     * @param y coordinata Y
     */
    public void drawCard(Graphics graphics, int x, int y) {
        setPosition(x, y);
        if (isVisible) {
            graphics.drawImage(cardsImages[value], x, y, WIDTH, HEIGHT, null);
        } else {
            drawCardBack(x, y, graphics);
        }
    }

    @Override
    public String toString() {
        return getValueAsString() + " of " + getSuitAsString();
    }

    /**
     * Ottiene la posizione X della carta
     *
     * @return posizione X della carta
     */
    public int getPositionX() {
        return positionX;
    }

    /**
     * Ottiene la posizione Y della carta
     *
     * @return posizione Y della carta
     */
    public int getPositionY() {
        return positionY;
    }

    /**
     * Setta la posizione della carta
     *
     * @param x coordinata X
     * @param y coordinata Y
     */
    public void setPosition(int x, int y) {
        this.positionX = x;
        this.positionY = y;
    }

    /**
     * Ottiene il seme della carta
     *
     * @return seme della carta
     */
    public int getSuit() {
        return this.value / 13;
    }

    /**
     * Ottiene il seme della carta come stringa
     *
     * @return seme della carta come stringa
     */
    public String getSuitAsString() {
        return SUITS[getSuit()];
    }

    /**
     * Ottiene il valore della carta
     *
     * @return valore della carta
     */
    public int getValue() {
        return this.value % 13 + 1;
    }

    /**
     * Ottiene il valore della carta come stringa
     *
     * @return valore della carta come stringa
     */
    public String getValueAsString() {
        return VALUES[getValue() - 1];
    }

    /**
     * Ritorna true se la carta è visibile
     *
     * @return true se la carta è visibile
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Setta la carta visibile in base al valore specificato
     *
     * @param val true setta la carta visibile, false la nasconde
     */
    public void setVisible(boolean val) {
        this.isVisible = val;
    }

}
