package it.alerighi.spider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import static it.alerighi.spider.Util.log;

/**
 * "Lasciate ogne speranza, voi ch' entrate"
 *
 * @author Alessandro Righi
 */
public class GamePanel extends JPanel implements MouseListener, KeyListener {

    private final int numberOfSuits;

    private Deck[] topDecks = new Deck[10];

    private Card[][] decks = new Card[5][];
    private int remainingDecks = 5;

    private static final int GAME_HEIGHT = 800;
    private static final int GAME_WIDTH = 1500;

    private Deck draggingDeck = null;

    private Timer timer;

    private Stack<Deck> removedDecks = new Stack<>();
    private Stack<Move> moves = new Stack<>();


    public GamePanel(int numberOfSuits) {
        log("Questo è Spider, v0.0.1");
        setSize(GAME_WIDTH, GAME_HEIGHT);
        this.numberOfSuits = numberOfSuits;
        buildDecks();
        addMouseListener(this);
        addKeyListener(this);
        setFocusable(true);
        requestFocus();
        log("Gioco inizzializzato");
    }

    private void buildDecks() {
        CardDeck cardDeck = new CardDeck(numberOfSuits, 8 / numberOfSuits);

        for (int i = 0; i < 5; i++) {
            decks[i] = cardDeck.getCards(i * 10, 10);
        }

        for (int i = 0; i < 4; i++) {
            topDecks[i] = new Deck(Arrays.asList(cardDeck.getCards(50 + i * 6, 6)));
            topDecks[i].setIndex(i);
        }

        for (int i = 0; i < 6; i++) {
            topDecks[i + 4] = new Deck(Arrays.asList(cardDeck.getCards(74 + i * 5, 5)));
            topDecks[i + 4].setIndex(i + 4);
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.setColor(new Color(36, 124, 33));
        graphics.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        int spaceBetweenCards = (GAME_WIDTH - 10 * Card.CARD_WIDTH) / 11;
        for (int i = 0; i < 10; i++) {
            topDecks[i].setPosition(spaceBetweenCards * (i + 1) + Card.CARD_WIDTH * i, 20);
            topDecks[i].paint(graphics);
        }

        for (int i = 0; i < remainingDecks; i++) {
            Card.drawCardBack(GAME_WIDTH - i * 10 - 20 - Card.CARD_WIDTH, GAME_HEIGHT - 20 - Card.CARD_HEIGHT, graphics);
        }

        int x = 20;
        int y = GAME_HEIGHT - Card.CARD_HEIGHT - 20;
        for (Deck d : removedDecks) {
            d.getFirstCard().setPosition(x, y);
            d.getFirstCard().drawCard(graphics);
            x += 20;
        }

        if (draggingDeck != null) {
            draggingDeck.paint(graphics);
        }

    }

    private Deck popDeckOnLocation(int x, int y) {
        Deck deck = null;
        for (int i = 0; i < 10 && deck == null; i++) {
            deck = topDecks[i].popSubDeck(x, y);
        }
        return deck;
    }

    private Deck selectDeckOnLocation(int x, int y) {
        Deck deck = null;
        for (int i = 0; i < 10 && deck == null; i++) {
            deck = topDecks[i].selectSubDeck(x, y);
        }
        return deck;
    }


    private boolean validMove(Card card1, Card card2) {
        return card1 == null || card1.getValue() == card2.getValue() + 1;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    private void dealCards() {
        for (Deck d : topDecks) {
            if (d.isEmpty()) {
                log("Non posso dispensare le carte. È presente un buco!");
                return;
            }
        }
        remainingDecks--;
        log("Dispenso carte: mazzi rimanenti " + remainingDecks);
        for (int i = 0; i < 10; i++) {
            decks[remainingDecks][i].setVisible(true);
            topDecks[i].add(decks[remainingDecks][i]);
        }
        moves.add(new Move());
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getX() > GAME_WIDTH - 200 && mouseEvent.getY() > GAME_HEIGHT - 200 && remainingDecks > 0) {
            dealCards();
            return;
        }
        Deck deck = popDeckOnLocation(mouseEvent.getX(), mouseEvent.getY());
        if (deck != null) {
            int offsetX = mouseEvent.getX() - deck.getPositionX();
            int offsetY = mouseEvent.getY() - deck.getPositionY();
            draggingDeck = deck;
            CardDrag dragTask = new CardDrag(this, deck, offsetX, offsetY);
            timer = new Timer();
            timer.schedule(dragTask, 0, 10);
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if (draggingDeck != null) {
            timer.cancel();
            timer = null;
            Deck deck = selectDeckOnLocation(mouseEvent.getX(), mouseEvent.getY());
            if (deck != null) {
                deck = topDecks[deck.getIndex()];
                if (validMove(deck.getTopCard(), draggingDeck.getFirstCard())) {
                    Card c = topDecks[draggingDeck.getIndex()].getTopCard();
                    if (c != null) c.setVisible(true);
                    deck.addAll(draggingDeck);
                    moves.push(new Move(draggingDeck.getIndex(), deck.getIndex(), draggingDeck.size()));
                    repaint();
                    check();
                    draggingDeck = null;
                    return;
                }
            }
            topDecks[draggingDeck.getIndex()].addAll(draggingDeck);
        }
        draggingDeck = null;
        repaint();
    }

    public boolean isEnded() {
        return removedDecks.size() == 8;
    }

    private void undo() {
        Move toUndo = moves.pop();
        if (toUndo.removedDeck > 0) {
            if (!toUndo.visible) {
                topDecks[toUndo.removedDeck].getTopCard().setVisible(false);
            }
            topDecks[toUndo.removedDeck].addAll(removedDecks.pop());
            toUndo = moves.pop();
        }
        if (toUndo.dealCards) {
            for (int i = 0; i < 10; i++) {
                topDecks[i].getSubDeck(topDecks[i].size() - 1, true);
            }
            remainingDecks++;
            repaint();
            return;
        }
        Deck from = topDecks[toUndo.to];
        Deck to = topDecks[toUndo.from];
        if (to.getTopCard() != null) to.getTopCard().setVisible(false);
        Deck toMove = from.getSubDeck(from.size() - toUndo.cards, true);
        to.addAll(toMove);
        repaint();
    }

    private boolean check() {
        for (int j = 0; j < 10; j++) {
            Deck d = topDecks[j];
            if (d.getTopCard() != null && d.getTopCard().getValue() != 1) continue;
            for (int i = 0; i < d.size(); i++) {
                Card c = d.get(i);
                if (c.isVisible() && c.getValue() == 13 && d.isOrderdered(i)) {
                    removedDecks.push(d.getSubDeck(i, true));
                    moves.push(new Move(j, d.size() <= 0 || d.getTopCard().isVisible()));
                    Card c2 = d.getTopCard();
                    if (c2 != null) c2.setVisible(true);
                    log("Congratulazioni. Rimosso un mazzetto.");
                }
            }
        }
        if (removedDecks.size() == 8) {
            log("Partita terminata");
            return true;
        }
        return false;
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (!moves.isEmpty()
                && (keyEvent.getModifiers() & KeyEvent.CTRL_MASK) != 0
                && keyEvent.getKeyCode() == KeyEvent.VK_Z)
            undo();
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    private class CardDrag extends TimerTask {
        private JPanel panel;
        private Deck deck;
        private int offsetX, offsetY;

        public CardDrag(JPanel panel, Deck deck, int x, int y) {
            this.panel = panel;
            this.deck = deck;
            this.offsetX = x;
            this.offsetY = y;
        }

        @Override
        public void run() {
            Point position = panel.getMousePosition();
            if (position != null) {
                int mouseX = (int) panel.getMousePosition().getX() - offsetX;
                int mouseY = (int) panel.getMousePosition().getY() - offsetY;
                deck.setPosition(mouseX, mouseY);
                panel.repaint();
            }
        }
    }

    private class Move {
        private int from;
        private int to;
        private int cards;
        private boolean dealCards = false;
        private int removedDeck = -1;
        private boolean visible = false;

        public Move() {
            dealCards = true;
        }

        public Move(int indice, boolean visible) {
            removedDeck = indice;
            this.visible = visible;
        }

        public Move(int from, int to, int cards) {
            this.from = from;
            this.to = to;
            this.cards = cards;
            log("Sposto " + cards + " cart" + (cards == 1 ? "a" : "e") + " dal mazzo: " +
                    (from + 1) + " al mazzo: " + (to + 1));

        }
    }
}
