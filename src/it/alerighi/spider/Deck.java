package it.alerighi.spider;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class that reppresent a game deck
 *
 * @author Alessandro Righi
 */
public final class Deck {
    private static final Logger logger = Logger.getGlobal();

    public static final int SPACE_BETWEEN_CARDS = 25;
    public static final int SPACE_BETWEEN_CARDS_COVERED = 10;

    private Point position;
    private int index;
    private int flaggedCardIndex;

    private final List<Card> cards;

    /**
     * Create a new card deck
     *
     * @param list list of cards to insert in the deck
     */
    public Deck(List<Card> list) {
        cards = new ArrayList<>(list);
        getTopCard().setVisible(true);
    }

    /**
     * Get the deck index
     *
     * @return deck index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set the deck index
     *
     * @param index indice del mazzo
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Get the first card of the deck
     *
     * @return first card of the deck, null if deck is empty
     */
    public Card getFirstCard() {
        return cards.isEmpty() ? null : cards.get(0);
    }

    /**
     * Get the top card of the deck
     *
     * @return top card of the deck, null if deck empty
     */
    public Card getTopCard() {
        return cards.isEmpty() ? null : cards.get(cards.size() - 1);
    }

    /**
     * Draws the card deck in the specified position
     *
     * @param graphics graphics area
     * @param position deck position
     */
    public void paint(Graphics graphics, Point position) {
        setPosition(position);
        if (cards.isEmpty()) {
            graphics.setColor(GamePanel.SCORE_BOX_COLOR);
            graphics.fillRect(position.x, position.y, Card.WIDTH, Card.HEIGHT);
            graphics.setColor(Color.BLACK);
            graphics.drawRect(position.x, position.y, Card.WIDTH, Card.HEIGHT);
        } else {
            for (int i = 0; i < cards.size(); i++) {
                Card card = cards.get(i);
                card.paint(graphics, position);
                int space = i == flaggedCardIndex ? 20 : 0;
                position.translate(0, card.isVisible() ? SPACE_BETWEEN_CARDS + space : SPACE_BETWEEN_CARDS_COVERED);
            }
        }
    }

    /**
     * Draws the card deck in the current position
     *
     * @param graphics graphics area
     */
    public void paint(Graphics graphics) {
        paint(graphics, position);
    }

    /**
     * Check if the deck is ordered from an index to the bottom (so it can be moved)
     *
     * @param index start index
     * @return true only if is ordered
     */
    public boolean isOrderdered(int index) {
        for (int i = index; i < cards.size() - 1; i++) {
            if (cards.get(i).suit != cards.get(i + 1).suit ||
                    cards.get(i).value != cards.get(i + 1).value + 1)
                return false;
        }
        return true;
    }

    /**
     * Get a subdeck from n to top, onby if it's ordered
     *
     * @param index starting index
     * @param pop   if true removes the subdeck from the deck
     * @return the subdeck, if impossible to get null
     */
    public Deck getSubDeck(int index, boolean pop) {
        if (!isOrderdered(index))
            return null;
        Deck deck = new Deck(cards.subList(index, cards.size()));
        if (pop)
            cards.subList(index, cards.size()).clear();
        return deck;
    }

    private int getCardIndexFromLocation(Point location) {
        Card topCard = getTopCard();
        if (topCard == null)
            return -1;
        int y = topCard.getPosition().y;
        int x = position.x;
        for (int i = cards.size() - 1; i >= 0; i--) {
            Card card = cards.get(i);
            if (location.x > x
                    && location.x < x + Card.WIDTH
                    && location.y > y
                    && location.y < y + Card.HEIGHT && card.isVisible())
                return i;
            y -= card.isVisible() ? SPACE_BETWEEN_CARDS : SPACE_BETWEEN_CARDS_COVERED;
        }
        return -1;
    }

    public void flagLocation(Point location) {
        flaggedCardIndex = getCardIndexFromLocation(location);
    }

    public void unFlagLocation() {
        flaggedCardIndex = -1;
    }

    /**
     * Get the subdeck from (x,y) and the end, if exists
     *
     * @param getPosition position
     * @param pop         if true also remove subdeck from deck
     * @return the deck if exists, else null
     */
    public Deck selectSubDeck(Point getPosition, boolean pop) {
        if (cards.isEmpty()) {
            if (getPosition.x > position.x
                    && getPosition.x < position.x + Card.WIDTH
                    && getPosition.y > position.y
                    && getPosition.y < Card.HEIGHT + position.y)
                return this;
            else
                return null;
        }
        int i = getCardIndexFromLocation(getPosition);
        if (i < 0)
            return null;
        Card card = getCardByIndex(i);
        Deck deck = getSubDeck(i, pop);

        if (deck != null) {
            deck.setPosition(card.getPosition());
            deck.setIndex(index);
        }

        return deck;
    }

    /**
     * Set deck positoin
     *
     * @param position new position
     */
    public void setPosition(Point position) {
        this.position = new Point(position);
    }

    /**
     * Get X coordinate
     *
     * @return position
     */
    public Point getPosition() {
        return new Point(position);
    }

    /**
     * Get the card at the specified index
     *
     * @param index index of the card
     * @return card
     */
    public Card getCardByIndex(int index) {
        return cards.get(index);
    }

    /**
     * Get the number of cards in the deck
     *
     * @return number of card in the deck
     */
    public int numberOfCards() {
        return cards.size();
    }

    /**
     * Check if the deck is empty
     *
     * @return true only if deck is empty
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Add a card to the deck
     *
     * @param card card to add
     */
    public void addCard(Card card) {
        cards.add(card);
    }

    /**
     * Add a deck at the end of the deck
     *
     * @param deck deck to add
     */
    public void appendDeck(Deck deck) {
        if (deck == null)
            return;
        cards.addAll(deck.cards);
    }
}

