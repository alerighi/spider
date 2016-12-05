package it.alerighi.spider;

import java.util.Arrays;
import java.util.Random;

/**
 * Classe che rappresenta un mazzo di carte da pocker
 *
 * @author Alessandro Righi
 */
public class CardDeck {

    private final int numberOfSuits;
    private final int numberOfDecks;
    private final int numberOfCards;

    private Card[] deck;

    public CardDeck(int numberOfSuits, int numberOfDecks) {
        this.numberOfDecks = numberOfDecks;
        this.numberOfSuits = numberOfSuits;
        this.numberOfCards = numberOfSuits * numberOfDecks * 13;
        buildDeck();
        shuffle(1000);
    }

    public CardDeck(int numberOfSuits, int numberOfCards, Card deck[]) {
        this.numberOfSuits = numberOfSuits;
        this.numberOfDecks = 1;
        this.numberOfCards = numberOfCards;
        this.deck = deck;
    }

    public Card getCard(int index) {
        return deck[index];
    }

    public Card[] getCards(int start, int end) {
        return Arrays.copyOfRange(this.deck, start, start + end);
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
        deck = new Card[numberOfCards];

        int i = 0;
        for (int n = 0; n < numberOfDecks; n++) {
            for (int suit = 0; suit < numberOfSuits; suit++) {
                for (int value = 1; value < 14; value++) {
                    deck[i] = new Card(suit, value);
                    i += 1;
                }
            }
        }
    }

    private void shuffle(int passages) {
        Random rand = new Random();

        for (int i = 0; i < passages; i++) {
            int a = rand.nextInt(numberOfCards);
            int b = rand.nextInt(numberOfCards);
            swap(a, b);
        }
    }

    private void swap(int a, int b) {
        Card tmp = deck[a];
        deck[a] = deck[b];
        deck[b] = tmp;
    }
}
