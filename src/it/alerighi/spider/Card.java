package it.alerighi.spider;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Class that represent a card
 *
 * @author Alessandro Righi
 */
public final class Card {

    public static final int HEIGHT = 145;
    public static final int WIDTH = 100;

    private static final String[] SUITS = {"spades", "hearts", "clubs", "diamonds"};
    private static final String[] VALUES = {"ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king"};

    private static final Logger logger = Logger.getGlobal();

    private static Image[] cardsImages = new Image[52];
    private static Image back;

    public final int value;
    public final int suit;

    private Point position = new Point(0, 0);

    private boolean isVisible;

    static {
        logger.info("loading images from resource files...");
        try {
            int i = 0;
            for (String suit : SUITS) {
                for (String value : VALUES) {
                    cardsImages[i++] = ImageIO.read(Card.class.getResourceAsStream(value + "_of_" + suit + ".png"));
                }
            }

            back = ImageIO.read(Card.class.getResourceAsStream("back.png"));
        } catch (IOException e) {
            logger.severe("Error loading card images!");
        }
    }

    /**
     * Card constructor
     *
     * @param suit  suit value (0-3)
     * @param value card value (1-11)
     */
    public Card(int suit, int value) {
        if (value < 1 || value > 13 || suit < 0 || suit > 3) {
            throw new IllegalArgumentException("Suit or value out of bounds!");
        }
        this.suit = suit;
        this.value = value;
    }

    /**
     * Draws the card back
     *
     * @param position position
     * @param graphics l'oggetto grafico su cui disegnare
     */
    public static void drawCardBack(Point position, Graphics graphics) {
        graphics.drawImage(back, position.x, position.y, WIDTH, HEIGHT, null);
    }

    /**
     * Set the position of the specified card, and draws it
     *
     * @param graphics area grafica in cui disegnare la carta
     * @param position position of the card
     */
    public void paint(Graphics graphics, Point position) {
        setPosition(position);
        if (isVisible) {
            graphics.drawImage(cardsImages[suit * 13 + value - 1], position.x, position.y, WIDTH, HEIGHT, null);
        } else {
            drawCardBack(position, graphics);
        }
    }

    @Override
    public String toString() {
        return SUITS[suit] + " of " + VALUES[value - 1];
    }

    /**
     * Get the card position
     *
     * @return card position
     */
    public Point getPosition() {
        return new Point(position);
    }

    /**
     * Set the card position
     *
     * @param position new position
     */
    public void setPosition(Point position) {
        this.position = new Point(position);
    }

    /**
     * Check if the card is visible
     *
     * @return true only if visible
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Set card visibility
     *
     * @param val visibility
     */
    public void setVisible(boolean val) {
        this.isVisible = val;
    }

}
