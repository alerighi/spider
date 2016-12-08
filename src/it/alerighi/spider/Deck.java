package it.alerighi.spider;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Righi
 */
public class Deck extends ArrayList<Card> {

    private int x = 0;
    private int y = 0;
    private int index = 0;

    public Deck(List<Card> list) {
        super(list);
        getTopCard().setVisible(true);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int i) {
        index = i;
    }

    public Card getFirstCard() {
        return size() > 0 ? get(0) : null;
    }

    public Card getTopCard() {
        if (size() > 0)
            return this.get(size() - 1);
        return null;
    }

    public void paint(Graphics graphics, int x, int y) {
        setPosition(x, y);
        if (isEmpty()) {
            graphics.setColor(Color.BLACK);
            graphics.drawRect(x, y, Card.CARD_WIDTH, Card.CARD_HEIGHT);
        } else {
            for (Card card : this) {
                card.setPosition(x, y);
                card.drawCard(graphics);
                y += card.isVisible() ? 30 : 10;
            }
        }
    }

    public void paint(Graphics graphics) {
        paint(graphics, x, y);
    }

    public boolean isOrderdered(int n) {
        for (int i = n; i < size() - 1; i++) {
            if (get(i).getSuit() != get(i + 1).getSuit() ||
                    get(i).getValue() != get(i + 1).getValue() + 1)
                return false;
        }
        return true;
    }

    public Deck getSubDeck(int n, boolean pop) {
        if (isOrderdered(n)) {
            Deck deck = new Deck(this.subList(n, size()));
            if (pop) this.removeRange(n, size());
            return deck;
        } else {
            return null;
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Deck selectSubDeck(int a, int b, boolean pop) {
        if (this.size() == 0) {
            if (a > x && a < x + Card.CARD_WIDTH && b > 20 && b < Card.CARD_HEIGHT + 20)
                return this;
            else
                return null;
        }
        int y = this.getTopCard().getPositionY();
        int x = this.x;
        int i;
        for (i = size() - 1; i >= 0; i--) {
            Card card = this.get(i);
            if (a > x && a < x + Card.CARD_WIDTH && b > y && b < y + Card.CARD_HEIGHT && card.isVisible())
                break;
            y -= card.isVisible() ? 30 : 10;
        }
        if (i < 0) return null;
        Deck deck = getSubDeck(i, pop);
        if (deck == null) return null;
        deck.setPosition(x, y);
        deck.setIndex(index);
        return deck;
    }

    public Deck popSubDeck(int a, int b) {
        return selectSubDeck(a, b, true);
    }

    public Deck selectSubDeck(int a, int b) {
        return selectSubDeck(a, b, false);
    }

    public int getPositionX() {
        return x;
    }

    public int getPositionY() {
        return y;
    }
}

