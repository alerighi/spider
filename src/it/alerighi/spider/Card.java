package it.alerighi.spider;


import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

/**
 * Classe che rappresenta una carta da pocker, jolly esclusi.
 *
 * @author Alessandro Righi
 */
public class Card {

    public static final int CARD_HEIGHT = 145;
    public static final int CARD_WIDTH = 100;
    private static final String[] SUITS = {"spades", "hearts", "clubs", "diamonds"};
    private static final String[] VALUES = {"ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king"};
    private static Image[] cardsImages = new Image[52];
    private static Image back;

    static {
        int i = 0;
        for (String suit : SUITS) {
            for (String value : VALUES) {
                InputStream cardInputstream = Card.class.getResourceAsStream(value + "_of_" + suit + ".png");
                try {
                    cardsImages[i] = ImageIO.read(cardInputstream);
                    i++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            back = ImageIO.read(Card.class.getResourceAsStream("back.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final int value;
    private int x, y;
    private boolean isVisible = false;
    private boolean flagged = false;

    public Card(int suit, int value) {
        if (value < 1 || value > 13 || suit < 0 || suit > 3) {
            throw new IllegalArgumentException("Suit or value out of bounds!");
        }
        this.value = suit * 13 + value - 1;
    }

    public static void drawCardBack(int x, int y, Graphics graphics) {
        graphics.drawImage(back, x, y, CARD_WIDTH, CARD_HEIGHT, null);
    }

    @Override
    public String toString() {
        return getValueAsString() + " of " + getSuitAsString();
    }

    @Override
    public int hashCode() {
        return this.value;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof Card) && (other.hashCode() == this.hashCode());
    }

    public int getPositionX() {
        return x;
    }

    public int getPositionY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
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

    public void setFlagged(boolean flag) {
        this.flagged = flag;
    }

    public void drawCard(Graphics graphics) {
        if (isVisible) {
            graphics.drawImage(cardsImages[value], x, y, CARD_WIDTH, CARD_HEIGHT, null);
            if (flagged) {
                graphics.setColor(Color.BLACK);
                graphics.fillRect(x, y, CARD_WIDTH, CARD_HEIGHT);
            }
        } else {
            drawCardBack(x, y, graphics);
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean val) {
        this.isVisible = val;
    }

}
