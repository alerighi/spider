package it.alerighi.spider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe che rappresenta un mazzo contentente più mazzi di carte.
 *
 * @author Alessandro Righi
 */
public class CardDeck {

    /**
     * numero di semi contenuti nel mazzo
     */
    private final int numberOfSuits;

    /**
     * numero di mazzi di carte
     */
    private final int numberOfDecks;

    /**
     * numero di carte totale
     */
    private final int numberOfCards;

    /**
     * array delle carte del mazzo
     */
    private ArrayList<Card> deck;

    /**
     * Crea un nuovo mazzo di carte
     *
     * @param numberOfSuits numero di semi nel mazzo
     * @param numberOfDecks numero di mazzi di carte
     */
    public CardDeck(int numberOfSuits, int numberOfDecks) {
        this.numberOfDecks = numberOfDecks;
        this.numberOfSuits = numberOfSuits;
        this.numberOfCards = numberOfSuits * numberOfDecks * 13;
        buildDeck();
        shuffle();
    }

    /**
     * Ottiene una lista di carte compresa fra start ed end
     *
     * @param start indice di inizio
     * @param end indice di fine
     * @return lista di carte fra strat ed end
     */
    public List<Card> getCards(int start, int end) {
        return deck.subList(start, start+end);
    }

    @Override
    public String toString() {
        String result = "";
        for (Card card : deck) {
            result += card.toString() + "; ";
        }
        return result;
    }

    /**
     * Costruisce il mazzo di carte
     */
    private void buildDeck() {
        deck = new ArrayList<>(numberOfCards);

        for (int n = 0; n < numberOfDecks; n++) {
            for (int suit = 0; suit < numberOfSuits; suit++) {
                for (int value = 1; value < 14; value++) {
                    deck.add(new Card(suit, value));
                }
            }
        }
    }

    /**
     * Mescola il mazzo di carte
     */
    private void shuffle() {
        Random rand = new Random();

        for (int i = 0; i < 1000; i++) {
            int a = rand.nextInt(numberOfCards);
            int b = rand.nextInt(numberOfCards);
            swap(a, b);
        }
    }

    /**
     * Scambia la posizione di due carte
     *
     * @param a indice prima carta da scambiare
     * @param b indice seconda carta da scambiare
     */
    private void swap(int a, int b) {
        Card tmp = deck.get(a);
        deck.set(a, deck.get(b));
        deck.set(b, tmp);
    }
}
