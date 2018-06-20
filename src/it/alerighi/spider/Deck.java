package it.alerighi.spider;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta un mazzo di carte.
 *
 * @author Alessandro Righi
 */
public class Deck {

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
     * Lista di carte presenti nel mazzo
     */
    private final List<Card> cards;

    /**
     * Crea un nuovo mazzo di carte
     *
     * @param list lista di carte da inserire nel mazzo
     */
    public Deck(List<Card> list) {
        cards = new ArrayList<>(list);
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
        return cards.isEmpty() ? null : cards.get(0);
    }

    /**
     * Ritorna la carta sopra i mazzo (più in basso), se esiste
     *
     * @return carta sopra il mazzo se esiste, altrimenti null
     */
    public Card getTopCard() {
        return cards.isEmpty() ? null : cards.get(cards.size() - 1);
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
        if (cards.isEmpty()) {
            graphics.setColor(GamePanel.SCORE_BOX_COLOR);
            graphics.fillRect(x, y, Card.WIDTH, Card.HEIGHT);
            graphics.setColor(Color.BLACK);
            graphics.drawRect(x, y, Card.WIDTH, Card.HEIGHT);
        } else {
            for (Card card : cards) {
                card.drawCard(graphics, x, y);
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
     * Controlla se il mazzo è ordinato nell'intervallo n..numberOfCards()
     *
     * @param n indice iniziale
     * @return true se ordinato, flase altrimenti
     */
    public boolean isOrderdered(int n) {
        for (int i = n; i < cards.size() - 1; i++) {
            if (cards.get(i).getSuit() != cards.get(i + 1).getSuit() ||
                    cards.get(i).getValue() != cards.get(i + 1).getValue() + 1)
                return false;
        }
        return true;
    }

    /**
     * Ottiene un sottomazzo fra n e numberOfCards(), se questo è ordinato
     *
     * @param n indice di partenza
     * @param pop indica se rimuovere o meno le carte dal mazzo
     * @return il sottomazzo se esiste, null altrimenti
     */
    public Deck getSubDeck(int n, boolean pop) {
        if (!isOrderdered(n))
            return null;
        Deck deck = new Deck(cards.subList(n, cards.size()));
        if (pop)
            cards.subList(n, cards.size()).clear();
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
        if (cards.isEmpty()) {
            if (a > positionX && a < positionX + Card.WIDTH && b > positionY && b < Card.HEIGHT + positionY)
                return this;
            else
                return null;
        }
        int y = getTopCard().getPositionY();
        int x = positionX;
        int i;
        for (i = cards.size() - 1; i >= 0; i--) {
            Card card = cards.get(i);
            if (a > x && a < x + Card.WIDTH && b > y && b < y + Card.HEIGHT && card.isVisible())
                break; /* found card */
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

    /**
     * Ottiene la carta con l'indice specificato
     *
     * @param index indice della carta
     * @return carta con l'indice specificato
     */
     public Card getCardByIndex(int index) {
         return cards.get(index);
     }

    /**
     * Ottiene quante carte sono presenti nel mazzo
     *
     * @return numero di carte nel mazzo
     */
    public int numberOfCards() {
        return cards.size();
    }

    /**
     * Indica se il mazzo è vuoto o meno
     *
     * @return true se il mazzo è vuoto, false altrimenti
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Aggiunge una carta al mazzo
     *
     * @param card carta da aggiungere al mazzo
     */
    public void addCard(Card card) {
        cards.add(card);
    }

    /**
     * Aggiunge un mazzo alla fine del mazzo
     *
     * @param deck mazzo da aggiungere
     */
    public void appendDeck(Deck deck) {
        cards.addAll(deck.cards);
    }
}

