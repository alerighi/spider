package it.alerighi.spider;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta un mazzo di carte.
 *
 * @author Alessandro Righi
 */
public class Deck extends ArrayList<Card> {

    /**
     * spazio in pixel fra carte scoperte
     */
    public static final int SPACE_BETWEEN_CARDS = 25;
    /**
     * spazio in pixel fra carte coperte
     */
    public static final int SPACE_BETWEEN_CARDS_COVERED = 10;
    /**
     * posizone X del mazzo di carte
     */
    private int positionX;
    /**
     * posizione Y del mazzo di carte
     */
    private int positionY;
    /**
     * indice del mazzo di carte
     */
    private int index;

    /**
     * Crea un nuovo mazzo di carte
     *
     * @param list lista di carte da inserire nel mazzo
     */
    public Deck(List<Card> list) {
        super(list);
        getTopCard().setVisible(true);
    }

    /**
     * Ritorna l'indice del mazzo
     *
     * @return indice del mazzo
     */
    public int getIndex() {
        return index;
    }

    /**
     * Setta l'indice del mazzo
     *
     * @param i indice del mazzo
     */
    public void setIndex(int i) {
        index = i;
    }

    /**
     * Ritorna la prima carta del mazzo, se esiste
     *
     * @return prima carta del mazzo se esiste, altrimenti null
     */
    public Card getFirstCard() {
        return size() > 0 ? get(0) : null;
    }

    /**
     * Ritorna la carta sopra i mazzo (più in basso), se esiste
     *
     * @return carta sopra il mazzo se esiste, altrimenti null
     */
    public Card getTopCard() {
        if (size() > 0)
            return this.get(size() - 1);
        return null;
    }

    /**
     * Disegna il mazzo di carte
     *
     * @param graphics area grafica su cui disegnare
     * @param x        coordinata x
     * @param y        coordinata y
     */
    public void paint(Graphics graphics, int x, int y) {
        setPosition(x, y);
        if (isEmpty()) {
            graphics.setColor(GamePanel.SCORE_BOX_COLOR);
            graphics.fillRect(x, y, Card.WIDTH, Card.HEIGHT);
            graphics.setColor(Color.BLACK);
            graphics.drawRect(x, y, Card.WIDTH, Card.HEIGHT);
        } else {
            for (Card card : this) {
                card.setPosition(x, y);
                card.drawCard(graphics);
                y += card.isVisible() ? SPACE_BETWEEN_CARDS : SPACE_BETWEEN_CARDS_COVERED;
            }
        }
    }

    /**
     * Disegna il mazzo di carte
     *
     * @param graphics area grafica in cui disegnare il mazzo
     */
    public void paint(Graphics graphics) {
        paint(graphics, positionX, positionY);
    }

    /**
     * Controlla se il mazzo è ordinato nell'intervallo n..size()
     *
     * @param n indice iniziale
     * @return true se ordinato, flase altrimenti
     */
    public boolean isOrderdered(int n) {
        for (int i = n; i < size() - 1; i++) {
            if (get(i).getSuit() != get(i + 1).getSuit() ||
                    get(i).getValue() != get(i + 1).getValue() + 1)
                return false;
        }
        return true;
    }

    /**
     * Ottiene un sottomazzo fra n e size(), se questo è ordinato
     *
     * @param n indice di partenza
     * @param pop indica se rimuovere o meno le carte dal mazzo
     * @return il sottomazzo se esiste, null altrimenti
     */
    public Deck getSubDeck(int n, boolean pop) {
        if (!isOrderdered(n))
            return null;
        Deck deck = new Deck(this.subList(n, size()));
        if (pop)
            this.removeRange(n, size());
        return deck;
    }

    /**
     * Setta la posizione del mazzo
     *
     * @param x coordinata X
     * @param y coordinata Y
     */
    public void setPosition(int x, int y) {
        this.positionX = x;
        this.positionY = y;
    }

    /**
     * Ottiene il sottomazzo fra la posizione (x,y) e la fine, se esiste
     *
     * @param a coordinata X
     * @param b coordinata Y
     * @param pop indica se rimuovere il mazzo, o meno
     * @return il mazzo se esiste, null altrimenti
     * TODO: riscrivere questo metodo
     */
    public Deck selectSubDeck(int a, int b, boolean pop) {
        if (this.size() == 0) {
            if (a > positionX && a < positionX + Card.WIDTH && b > positionY && b < Card.HEIGHT + positionY)
                return this;
            else
                return null;
        }
        int y = this.getTopCard().getPositionY();
        int x = this.positionX;
        int i;
        for (i = size() - 1; i >= 0; i--) {
            Card card = this.get(i);
            if (a > x && a < x + Card.WIDTH && b > y && b < y + Card.HEIGHT && card.isVisible())
                break;
            y -= card.isVisible() ? SPACE_BETWEEN_CARDS : SPACE_BETWEEN_CARDS_COVERED;
        }
        if (i < 0)
            return null;
        Deck deck = getSubDeck(i, pop);
        if (deck == null)
            return null;
        deck.setPosition(x, y);
        deck.setIndex(index);
        return deck;
    }

    /**
     * Rimuove il sottomazzo fra la posizione (x,y) e la fine, se esiste
     *
     * @param x coordinata X
     * @param y coordinata Y
     * @return il sottomazzo se esiste, null altrimenti
     */
    public Deck popSubDeck(int x, int y) {
        return selectSubDeck(x, y, true);
    }

    /**
     * Ritorna il sottomazzo fra la posizione (x,y) e la fine, se esiste, senza rimuoverlo dal mazzo
     *
     * @param x coordinata X
     * @param y coordinata Y
     * @return il sottomazzo se esiste, null altrimenti
     */
    public Deck selectSubDeck(int x, int y) {
        return selectSubDeck(x, y, false);
    }

    /**
     * Ottiene la posizione X del mazzo
     *
     * @return posizone X del mazzo
     */
    public int getPositionX() {
        return positionX;
    }

    /**
     * Ottiene la posizione Y del mazzo
     *
     * @return posizione Y del mazzo
     */
    public int getPositionY() {
        return positionY;
    }
}

