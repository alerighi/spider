package it.alerighi.spider;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static it.alerighi.spider.Util.log;

/**
 * Classe che va a gestire il pannello di gioco
 *
 * @author Alessandro Righi
 */
public class GamePanel
        extends JPanel
        implements MouseListener, KeyListener, MouseMotionListener {

    private Deck[] topDecks = new Deck[10];

    private Card[][] decks = new Card[5][10];
    private int remainingDecks = 0;

    private Deck draggingDeck = null;

    private Stack<Deck> removedDecks = new Stack<>();
    private Stack<Move> moves = new Stack<>();

    private int score = 0;

    private List<Move> possibleMoves;

    private int numberOfSuits;

    private int offsetX, offsetY;
    boolean visible;


    public GamePanel() {
        log("Questo è Spider, v0.0.1");
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);
        requestFocus();
    }

    public void startNewGame(int numberOfSuits) {
        log("Inizio nuova partita " + numberOfSuits);
        this.numberOfSuits = numberOfSuits;
        buildDecks(numberOfSuits);
        removedDecks.clear();
        moves.clear();
        remainingDecks = 5;
        repaint();
        possibleMoves = getPossibleMoves();
    }

    private void buildDecks(int numberOfSuits) {
        CardDeck cardDeck = new CardDeck(numberOfSuits, 8 / numberOfSuits);

        for (int i = 0; i < 5; i++) {
            decks[i] = cardDeck.getCards(i * 10, 10).toArray(decks[i]);
        }

        for (int i = 0; i < 4; i++) {
            topDecks[i] = new Deck(cardDeck.getCards(50 + i * 6, 6));
            topDecks[i].setIndex(i);
        }

        for (int i = 0; i < 6; i++) {
            topDecks[i + 4] = new Deck(cardDeck.getCards(74 + i * 5, 5));
            topDecks[i + 4].setIndex(i + 4);
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.setColor(new Color(36, 124, 33));
        graphics.fillRect(0, 0, getWidth(), getHeight());

        if (remainingDecks == 0) {
            graphics.setColor(Color.BLACK);
            graphics.drawRect(getWidth() - 20 - Card.CARD_WIDTH, getHeight() - 20 - Card.CARD_HEIGHT, Card.CARD_WIDTH, Card.CARD_HEIGHT);
        } else {
            for (int i = 0; i < remainingDecks; i++) {
                Card.drawCardBack(getWidth() - i * 10 - 20 - Card.CARD_WIDTH, getHeight() - 20 - Card.CARD_HEIGHT, graphics);
            }
        }

        int x = getWidth() / 2 - 125;
        int y = getHeight() - 20 - 125;
        graphics.setColor(new Color(35, 104, 32));
        graphics.fillRect(x, y, 250, 125);
        graphics.setColor(Color.black);
        graphics.drawRect(x, y, 250, 125);
        graphics.setFont(graphics.getFont().deriveFont(graphics.getFont().getSize() * 1.4F));
        if (numberOfSuits == 0) {
            graphics.drawString("Select Game Mode!", x + 23, y + 45);
        } else if (isEnded()) {
            graphics.drawString("Congratulations, you won!", x + 10, y + 45);
        } else {
            graphics.drawString("Score: " + score, x + 60, y + 45);
            graphics.drawString("Moves: " + moves.size(), x + 60, y + 75);
        }

        if (numberOfSuits == 0) return; // gioco non ancora inizzializzato;;

        int spaceBetweenCards = (getWidth() - 10 * Card.CARD_WIDTH) / 11;
        for (int i = 0; i < 10; i++) {
            topDecks[i].setPosition(spaceBetweenCards * (i + 1) + Card.CARD_WIDTH * i, 20);
            topDecks[i].paint(graphics);
        }

        x = 20;
        y = getHeight() - Card.CARD_HEIGHT - 20;
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
        check();
        possibleMoves = getPossibleMoves();
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getX() > getWidth() / 2 - 125 && mouseEvent.getX() < getWidth() / 2 + 125
                && mouseEvent.getY() > getHeight() - 145 && mouseEvent.getY() < getHeight() - 20) {
            if (isEnded()) startNewGame(numberOfSuits);
            else getHint();
        }
        if (mouseEvent.getX() > getWidth() - 200 && mouseEvent.getY() > getHeight() - 200 && remainingDecks > 0) {
            dealCards();
            return;
        }
        Deck deck = popDeckOnLocation(mouseEvent.getX(), mouseEvent.getY());
        if (deck != null) {
            offsetX = mouseEvent.getX() - deck.getPositionX();
            offsetY = mouseEvent.getY() - deck.getPositionY();
            draggingDeck = deck;
            visible = topDecks[deck.getIndex()].getTopCard() != null && topDecks[deck.getIndex()].getTopCard().isVisible();
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if (draggingDeck != null) {
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
                    possibleMoves = getPossibleMoves();
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

    private List<Move> getPossibleMoves() {
        List<Move> possibleMoves = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < topDecks[i].size(); j++) {
                if (topDecks[i].isOrderdered(j) && topDecks[i].get(j).isVisible()) {
                    for (int a = 0; a < 10; a++) {
                        if (i != a && validMove(topDecks[a].getTopCard(), topDecks[i].get(j))) {
                            possibleMoves.add(new Move(i, a, topDecks[i].size() - j));
                        }
                    }
                }
            }
        }
        return possibleMoves;
    }

    private void getHint() {
        if (!possibleMoves.isEmpty()) {
            try {
                Move move = possibleMoves.remove(0);
                possibleMoves.add(move);
                topDecks[move.from].get(topDecks[move.from].size() - move.cards).setFlagged(true);
                paintComponent(getGraphics());
                Thread.sleep(300);
                topDecks[move.from].get(topDecks[move.from].size() - move.cards).setFlagged(false);
                paintComponent(getGraphics());
                if (topDecks[move.to].getTopCard() != null) {
                    topDecks[move.to].getTopCard().setFlagged(true);
                    paintComponent(getGraphics());
                } else {
                    getGraphics().fillRect(topDecks[move.to].getPositionX(), topDecks[move.to].getPositionY(),
                            Card.CARD_WIDTH, Card.CARD_HEIGHT);
                }
                Thread.sleep(300);
                if (topDecks[move.to].getTopCard() != null) topDecks[move.to].getTopCard().setFlagged(false);
                paintComponent(getGraphics());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
                if (topDecks[i].getTopCard() != null) topDecks[i].getTopCard().setVisible(true);
            }
            remainingDecks++;
            repaint();
            return;
        }
        Deck from = topDecks[toUndo.to];
        Deck to = topDecks[toUndo.from];
        if (!visible && to.getTopCard() != null) to.getTopCard().setVisible(false);
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
            repaint();
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
                && (keyEvent.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0
                && keyEvent.getKeyCode() == KeyEvent.VK_Z)
            undo();
        if (!possibleMoves.isEmpty()
                && (keyEvent.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0
                && keyEvent.getKeyCode() == KeyEvent.VK_H)
            getHint();

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (draggingDeck != null) {
            Point position = getMousePosition();
            if (position != null) {
                int mouseX = (int) getMousePosition().getX() - offsetX;
                int mouseY = (int) getMousePosition().getY() - offsetY;
                if (draggingDeck != null) draggingDeck.setPosition(mouseX, mouseY);
                repaint();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
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
        }
    }
}
