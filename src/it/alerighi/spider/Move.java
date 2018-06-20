package it.alerighi.spider;

/**
 * Data class that represent a move of the game
 *
 * @author Alessandro Righi
 */
public abstract class Move {

    public static final class DealCardsMove extends Move {

    }

    public static final class RemoveDeckMove extends Move {
        public final int index;
        public final boolean visible;

        /**
         * Create a new deck remove move
         *
         * @param index index of the upper deck where it was the removed deck
         * @param visible indicate if the card under the removed deck was visible
         */
        public RemoveDeckMove(int index, boolean visible) {
            this.index = index;
            this.visible = visible;
        }
    }

    public static final class MoveDeckMove extends Move {
        public final int to;
        public final int from;
        public final int cards;
        public final boolean visible;

        /**
         * Create a new deck move move
         *
         * @param from source index
         * @param to destination index
         * @param numberOfCards number of cards moved
         * @param visible indicate if the card under the moved deck was visible
         */
        public MoveDeckMove(int from, int to, int numberOfCards, boolean visible) {
            this.to = to;
            this.from = from;
            this.cards = numberOfCards;
            this.visible = visible;
        }
    }
}
