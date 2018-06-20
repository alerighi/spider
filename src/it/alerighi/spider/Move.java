package it.alerighi.spider;

/**
 * Data class that represent a move of the game
 *
 * @author Alessandro Righi
 */
public final class Move {

    public static final int DEAL_CARDS = 0;
    public static final int DECK_REMOVED = 1;
    public static final int MOVE_DECK = 2;

    public final int moveType;
    public final int to;
    public final int from;
    public final int cards;
    public final int removedDeck;
    public final boolean visible;

    /**
     * Create a deal cards move
     *
     * @return dealCards Move
     */
    public static Move dealCards() {
        return new Move(DEAL_CARDS, 0, 0, 0, 0, false);
    }

    /**
     * Create a new deck remove move
     *
     * @param index index of the upper deck where it was the removed deck
     * @param visible indicate if the card under the removed deck was visible
     * @return removedDeck Move
     */
    public static Move deckRemoved(int index, boolean visible) {
        return new Move(DECK_REMOVED, 0, 0, 0, index, visible);
    }

    /**
     * Create a new deck move move
     *
     * @param from source index
     * @param to destination index
     * @param numberOfCards number of cards moved
     * @param visible indicate if the card under the moved deck was visible
     * @return moveDeck Move
     */
    public static Move moveDeck(int from, int to, int numberOfCards, boolean visible) {
        return new Move(MOVE_DECK, from, to, numberOfCards, 0, visible);
    }

    private Move(int type, int from, int to, int cards, int removedDeck, boolean visible) {
        this.moveType = type;
        this.from = from;
        this.to = to;
        this.cards = cards;
        this.removedDeck = removedDeck;
        this.visible = visible;
    }

    @Override
    public String toString() {
        switch (moveType) {
            case MOVE_DECK:
                return "Move " + cards + " card" + (cards == 1 ? "" : "s") + " from " + from + " to " + to;
            case DEAL_CARDS:
                return "Dealed cards";
            case DECK_REMOVED:
                return "Removed deck from " + removedDeck + " with first card visible = " + visible;
        }
        return null;
    }
}
