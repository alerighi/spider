package it.alerighi.spider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Classe che rappresenta un mazzo contentente più mazzi di carte francesi.
 *
 * @author Alessandro Righi
 */
public class CardDeck {

    private final int numberOfSuits;
    private final int numberOfDecks;
    private final int numberOfCards;

    private ArrayList<Card> deck;

    public CardDeck(int numberOfSuits, int numberOfDecks) {
        this.numberOfDecks = numberOfDecks;
        this.numberOfSuits = numberOfSuits;
        this.numberOfCards = numberOfSuits * numberOfDecks * 13;
        buildDeck();
        shuffle();
    }

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

    private void shuffle() {
        Random rand = new Random();

        for (int i = 0; i < 1000; i++) {
            int a = rand.nextInt(numberOfCards);
            int b = rand.nextInt(numberOfCards);
            swap(a, b);
        }
    }

    private void swap(int a, int b) {
        Card tmp = deck.get(a);
        deck.set(a, deck.get(b));
        deck.set(b, tmp);
    }
}
