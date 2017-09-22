package it.alerighi.spider;

/**
 * Classe che rappresenta una mossa di gioco
 *
 * @author Alessandro Righi
 */
public class Move {

    /**
     * ultimo id assegnato
     */
    private static int lastId;
    /**
     * indica il tipo di mossa effettuata
     */
    private final MoveType moveType;
    /**
     * identification number of the move
     */
    private final int id = ++lastId;
    /**
     * indice dal quale il mazzo viene spostato
     */
    private int from;
    /**
     * indice al quale il mazzo viene spostato
     */
    private int to;
    /**
     * numero di carte spostate
     */
    private int cards;
    /**
     * indica l'indice de mazzetto rimosso nel caso di mossa rimozione mazzetti
     */
    private int removedDeck;
    /**
     * indica se la carta superiore all'eventuale mazzetto rimosso era visibile, o meno
     */
    private boolean visible;

    /**
     * Crea una nuova mossa di distribuzione di carte
     */
    public Move() {
        moveType = MoveType.DEAL_CARDS;
    }

    /**
     * Crea una mossa di rimozione di un mazzetto
     *
     * @param indice  indice del mazzetto rimosso
     * @param visible indica se la carta superiore del mazzetto era visibile o meno
     */
    public Move(int indice, boolean visible) {
        moveType = MoveType.DECK_REMOVED;
        removedDeck = indice;
        this.visible = visible;
    }

    /**
     * Indica una mossa di spostamento di un mazzetto di carte
     *
     * @param from  indice del mazzo dal quale le carte sono spsotate
     * @param to    indice del mazzo sul quale le carte sono spostate
     * @param cards numero di carte spostate
     */
    public Move(int from, int to, int cards) {
        moveType = MoveType.MOVE_DECK;
        this.from = from;
        this.to = to;
        this.cards = cards;
    }

    /**
     * Indica una mossa di spostamento di un mazzetto di carte
     *
     * @param from    indice del mazzo dal quale le carte sono spsotate
     * @param to      indice del mazzo sul quale le carte sono spostate
     * @param cards   numero di carte spostate
     * @param visible indica se la carta prima era visibile o meno
     */
    public Move(int from, int to, int cards, boolean visible) {
        this(from, to, cards);
        this.visible = visible;
    }

    /**
     * Ritorna l'indice del deck di origine
     *
     * @return indice del deck di origine
     */
    public int getFrom() {
        return from;
    }

    /**
     * Ritorna l'indice del deck di destinazione
     *
     * @return indice del deck di destinazione
     */
    public int getTo() {
        return to;
    }

    /**
     * Ritorna il numero di carte spostate da from a to
     *
     * @return numero di carte spostate
     */
    public int getCards() {
        return cards;
    }

    /**
     * Ritorna l'indice del deck rimosso
     *
     * @return indice del deck rimosso
     */
    public int getRemovedDeck() {
        return removedDeck;
    }

    /**
     * Ritorna se la carta sopra mazzetto rimosso era visibile o meno
     *
     * @return true se era visibile, false altrimenti
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Ritorna il tipo di mossa
     *
     * @return tipo di mossa
     */
    public MoveType getMoveType() {
        return moveType;
    }

    /**
     * Ottiene l'id della mossa
     *
     * @return id della mossa
     */
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        switch (moveType) {
            case MOVE_DECK:
                return "Move #" + id + " : move " + cards + " card" + (cards == 1 ? "" : "s") + " from " + from + " to " + to;
            case DEAL_CARDS:
                return "Move #" + id + " : Dealed cards";
            case DECK_REMOVED:
                return "Move #" + id + " : Removed deck from " + removedDeck + " with first card visible = " + visible;
        }
        return null;
    }

    /**
     * Rappresenta il tipo di mossa eseguita
     */
    public enum MoveType {
        DEAL_CARDS,
        DECK_REMOVED,
        MOVE_DECK
    }
}
