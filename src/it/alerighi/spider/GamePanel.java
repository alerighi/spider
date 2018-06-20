package it.alerighi.spider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static it.alerighi.spider.Util.*;

/**
 * Classe that handles the game logic
 *
 * TODO: separate class into smaller classes
 *
 * @author Alessandro Righi
 */
public class GamePanel extends JPanel {

    public static final Color BACKGROUND_COLOR = new Color(35, 104, 32);
    public static final Color SCORE_BOX_COLOR = new Color(33, 79, 33);
    public static final Color HINT_COLOR = new Color(10, 27, 50);

    /**
     * Array of the top 10 decks of the game
     */
    private Deck[] topDecks = new Deck[10];

    /**
     * Array of the decks of cards to deal
     */
    private Card[][] decks = new Card[5][10];

    /**
     * number of remaining decks to distribute [0, 5]
     */
    private int remainingDecks;

    /**
     * deck that the player is moving (if any)
     */
    private Deck draggingDeck = null;

    /**
     * completed and removed decks
     */
    private Stack<Deck> removedDecks = new Stack<>();

    /**
     * stack of moves done by the player
     */
    private Stack<Move> moves = new Stack<>();

    /**
     * game score
     */
    private int score;

    /**
     * list of possible valid moves
     */
    private List<Move> possibleMoves;

    /**
     * number of suits of the game
     */
    private int numberOfSuits;

    /**
     * indicate the offset X of the moving deck
     *
     * TODO: this doesn't belong here
     */
    private int offsetX;

    /**
     * indicate the offset Y of the moving deck
     *
     * TODO: this doesn't belong here
     */
    private int offsetY;

    /**
     * Indicate if the upper card is visible or not
     *
     * TODO: this doesn't belong here
     */
    private boolean visible;

    public GamePanel() {
        addMouseListener(new GameMouseListener());
        addMouseMotionListener(new GameMouseMotionListener());
        addKeyListener(new GameKeyListener());
        setFocusable(true);
        requestFocus();
        new Card(1, 1); // this is done to trigger static initialization of the Card class
    }

    /**
     * Start a new game
     *
     * @param numberOfSuits number of suits of the game
     */
    public void startNewGame(int numberOfSuits) {
        debug("GamePanel","Starting new game with " + numberOfSuits);
        this.numberOfSuits = numberOfSuits;
        buildDecks();
        removedDecks.clear();
        moves.clear();
        remainingDecks = 5;
        score = 500;
        repaint();
        possibleMoves = getPossibleMoves();
    }

    /**
     * Initializes card decks
     */
    private void buildDecks() {
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

    /**
     * Draws the score area and the UNDO button
     *
     * @param graphics graphics area
     */
    private void drawScoreArea(Graphics2D graphics) {
        int x = getWidth() / 2 - 125;
        int y = getHeight() - 145;
        graphics.setColor(SCORE_BOX_COLOR);
        graphics.fillRect(x, y, 250, 125);
        graphics.setColor(Color.black);
        graphics.drawRect(x, y, 250, 125);
        graphics.setFont(graphics.getFont().deriveFont(graphics.getFont().getSize() * 1.4F));
        if (numberOfSuits == 0) {
            graphics.drawString("Select Game Mode!", x + 23, y + 45);
        } else if (isEnded()) {
            graphics.drawString("Congratulations, you won!", x + 10, y + 45);
            graphics.drawString("Final score: " + score, x + 20, y + 75);

        } else {
            graphics.drawString("Score: " + score, x + 60, y + 45);
            graphics.drawString("Moves: " + moves.size(), x + 60, y + 75);
        }

        y += 30;
        x = (getWidth() + x) / 2;
        graphics.setColor(SCORE_BOX_COLOR);
        graphics.fillRect(x, y, 120, 60);
        graphics.setColor(Color.BLACK);
        graphics.drawRect(x, y, 120, 60);
        graphics.drawString("UNDO", x + 30, y + 35);

    }

    /**
     * Check if the specified position is in the UNDO area
     *
     * @param x X
     * @param y Y
     * @return true only if (x,y) is UNDO
     */
    private boolean isInUndoBox(int x, int y) {
        int startX = ((getWidth() / 2) - 125 + getWidth()) / 2;
        int startY = getHeight() - 115;
        int width = 120;
        int height = 60;
        return x > startX && x < startX + width && y > startY && y < startY + height;
    }

    /**
     * Check if the specified position is in the score area
     *
     * @param x X
     * @param y Y
     * @return true only if (x,y) is in score area
     */
    private boolean isInScoreBox(int x, int y) {
        return x > getWidth() / 2 - 125
                && x < getWidth() / 2 + 125
                && y > getHeight() - 145
                && y < getHeight() - 20;
    }

    /**
     * Draw the decks of cards to deal in the lower right corner
     *
     * @param graphics graphics area
     */
    void drawExtraDecks(Graphics2D graphics) {
        if (remainingDecks == 0) {
            graphics.setColor(SCORE_BOX_COLOR);
            graphics.fillRect(getWidth() - 20 - Card.WIDTH, getHeight() - 20 - Card.HEIGHT, Card.WIDTH, Card.HEIGHT);
            graphics.setColor(Color.BLACK);
            graphics.drawRect(getWidth() - 20 - Card.WIDTH, getHeight() - 20 - Card.HEIGHT, Card.WIDTH, Card.HEIGHT);
        } else {
            for (int i = 0; i < remainingDecks; i++)
                Card.drawCardBack(getWidth() - i * 10 - 20 - Card.WIDTH, getHeight() - 20 - Card.HEIGHT, graphics);
        }
    }

    /**
     * Draw the upper decks
     *
     * @param graphics graphics area
     */
    void drawCardDecks(Graphics2D graphics) {
        int spaceBetweenCards = (getWidth() - 10 * Card.WIDTH) / 11;
        for (int i = 0; i < 10; i++) {
            topDecks[i].setPosition(spaceBetweenCards * (i + 1) + Card.WIDTH * i, 20);
            topDecks[i].paint(graphics);
        }

        /* disegna i mazzi rimossi */
        int x = 20;
        int y = getHeight() - Card.HEIGHT - 20;
        for (Deck d : removedDecks) {
            d.getFirstCard().drawCard(graphics, x, y);
            x += 20;
        }
    }

    /**
     * Draws the game area
     *
     * @param g graphics area
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;

        graphics.setColor(BACKGROUND_COLOR);
        graphics.fillRect(0, 0, getWidth(), getHeight());

        drawScoreArea(graphics);
        drawExtraDecks(graphics);

        /* se il gioco è inizializzato */
        if (numberOfSuits != 0)
            drawCardDecks(graphics);

        /* disegna eventualmente il mazzo che si sta trascinando */
        if (draggingDeck != null)
            draggingDeck.paint(graphics);
    }

    /**
     * Gets a subdeck from the current mouse position if present,
     * and removes it from the game area.
     *
     * @param x X
     * @param y Y
     * @return subdeck if present, else null
     */
    private Deck popDeckOnLocation(int x, int y) {
        Deck deck = null;

        for (int i = 0; i < 10 && deck == null; i++)
            deck = topDecks[i].popSubDeck(x, y);

        return deck;
    }

    /**
     * Select a subdeck from the current mouse position if present.
     *
     * @param x X
     * @param y Y
     * @return subdeck if present, else null
     */
    private Deck selectDeckOnLocation(int x, int y) {
        Deck deck = null;

        for (int i = 0; i < 10 && deck == null; i++)
            deck = topDecks[i].selectSubDeck(x, y);

        return deck;
    }

    /**
     * Check if a move is valid. A move is valid if the lower card
     * have a value that is equal to the value of the upper card - 1,
     * if exists.
     *
     * @param upperCard upper card
     * @param lowerCard lower card
     * @return true only if the move is valid
     */
    private boolean validMove(Card upperCard, Card lowerCard) {
        return upperCard == null || upperCard.getValue() - lowerCard.getValue() == 1;
    }

    /**
     * Deal cards
     */
    public void dealCards() {
        if (remainingDecks <= 0)
            return; /* non ci sono più massi da distribuire */
        for (Deck d : topDecks) {
            if (d.isEmpty()) {
                debug("Non posso dispensare le carte. È presente un buco!");
                return;
            }
        }
        remainingDecks -= 1;
        score -= 1;
        debug("Dispenso carte: mazzi rimanenti " + remainingDecks);
        for (int i = 0; i < 10; i++) {
            decks[remainingDecks][i].setVisible(true);
            topDecks[i].addCard(decks[remainingDecks][i]);
        }
        moves.add(Move.dealCards());
        checkAndRemoveDecks();
        possibleMoves = getPossibleMoves();
        repaint();
    }

    /**
     * Indicate if the game is ended (all decks removed)
     *
     * @return true only if the game is ended
     */
    public boolean isEnded() {
        return removedDecks.size() == 8;
    }

    /**
     * Get a list of possible moves
     *
     * @return list of possible moves
     */
    private List<Move> getPossibleMoves() {
        List<Move> badMoves = new ArrayList<>();
        List<Move> goodMoves = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < topDecks[i].numberOfCards(); j++)
                if (topDecks[i].isOrderdered(j) && topDecks[i].getCardByIndex(j).isVisible())
                    for (int a = 0; a < 10; a++)
                        if (i != a && validMove(topDecks[a].getTopCard(), topDecks[i].getCardByIndex(j)))
                            if (topDecks[a].getTopCard() == null
                                    || topDecks[i].getCardByIndex(j) != null
                                    || topDecks[a].getTopCard().getSuit() == topDecks[i].getCardByIndex(j).getSuit())
                                goodMoves.add(Move.moveDeck(i, a, topDecks[i].numberOfCards() - j, false));
                            else
                                badMoves.add(Move.moveDeck(i, a, topDecks[i].numberOfCards() - j, false));

        goodMoves.addAll(badMoves);
        return goodMoves;
    }

    /**
     * Get game hint
     */
    public void getHint() {
        if (possibleMoves.isEmpty())
            return; /* no possibile moves */
        try {
            Move move = possibleMoves.remove(0);
            possibleMoves.add(move);

            Graphics2D g = (Graphics2D) getGraphics();
            g.setColor(HINT_COLOR);

            Stroke oldStroke = g.getStroke();
            Stroke highWidthStroke = new BasicStroke(5);
            g.setStroke(highWidthStroke);

            Deck d = topDecks[move.from];
            Card c = d.getCardByIndex(d.numberOfCards() - move.cards);
            g.drawRect(c.getPositionX(), c.getPositionY(), Card.WIDTH, Card.HEIGHT + move.cards * Deck.SPACE_BETWEEN_CARDS - Deck.SPACE_BETWEEN_CARDS);

            Thread.sleep(400);
            g.setStroke(oldStroke);
            paint(g);
            g.setStroke(highWidthStroke);
            g.setColor(HINT_COLOR);

            d = topDecks[move.to];
            c = d.getTopCard();

            if (c != null)
                g.drawRect(c.getPositionX(), c.getPositionY(), Card.WIDTH, Card.HEIGHT);
            else
                g.drawRect(d.getPositionX(), d.getPositionY(), Card.WIDTH, Card.HEIGHT);

            Thread.sleep(400);
            g.setStroke(oldStroke);
            paint(g);
        } catch (InterruptedException e) {
            die("This shouldn't have ever happened!");
        }
    }

    /**
     * Undo last move
     */
    private void undoLastMove() {
        if (moves.empty())
            return; /* nessuna mossa da annullare */
        Move toUndo = moves.pop();
        switch (toUndo.moveType) {
            case DECK_REMOVED:
                if (!toUndo.visible)
                    topDecks[toUndo.removedDeck].getTopCard().setVisible(false);
                topDecks[toUndo.removedDeck].appendDeck(removedDecks.pop());
                undoLastMove(); /* annulla un altra mossa */
                break;
            case DEAL_CARDS:
                score -= 1;
                for (int i = 0; i < 10; i++) {
                    topDecks[i].getSubDeck(topDecks[i].numberOfCards() - 1, true);
                    if (topDecks[i].getTopCard() != null)
                        topDecks[i].getTopCard().setVisible(true);
                }
                remainingDecks++;
                repaint();
                break;
            case MOVE_DECK:
                score -= 1;
                Deck from = topDecks[toUndo.to];
                Deck to = topDecks[toUndo.from];
                if (!toUndo.visible && to.getTopCard() != null)
                    to.getTopCard().setVisible(false);
                Deck toMove = from.getSubDeck(from.numberOfCards() - toUndo.cards, true);
                to.appendDeck(toMove);
                repaint();
        }
    }

    /**
     * Check if is possible to remove a deck and if it is remove it
     */
    private void checkAndRemoveDecks() {
        for (int j = 0; j < 10; j++) {
            Deck d = topDecks[j];
            if (d.getTopCard() != null && d.getTopCard().getValue() != 1)
                continue; /* la carta infondo ad un mazzetto non è un asse: non posso rimuoverlo */
            for (int i = 0; i < d.numberOfCards(); i++) {
                Card c = d.getCardByIndex(i);
                if (c.isVisible() && c.getValue() == 13 && d.isOrderdered(i)) {
                    /* trovato mazzetto removibile */
                    removedDecks.push(d.getSubDeck(i, true));
                    moves.push(Move.deckRemoved(j, d.numberOfCards() <= 0 || d.getTopCard().isVisible()));
                    Card c2 = d.getTopCard();
                    if (c2 != null)
                        c2.setVisible(true); /* rende visibile la carta superiore sotto il mazzetto rimosso */
                    score += 100;
                    debug("Congratulazioni. Rimosso un mazzetto.");
                }
            }
        }

        /* se ho rimosso 8 mazzetti, la partita è terminata */
        if (removedDecks.size() == 8) {
            debug("Partita terminata");
            repaint();
        }
    }

    /**
     * Class to handle mouse events
     *
     * TODO: move it in another file ?
     */
    private class GameMouseListener implements MouseListener {

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            int x = mouseEvent.getX();
            int y = mouseEvent.getY();

            /* click nel pulsante UNDO */
            if (isInUndoBox(x, y))
                undoLastMove();

            /* click nel quadrato dei punteggi */
            if (isInScoreBox(x, y)) {
                if (isEnded()) /* un click nel riquadro a gioco finito fa iniziare una nuova partita */
                    startNewGame(numberOfSuits);
                else /* normalmente mostro un suggerimento */
                    getHint();
            }

            /* click sul mazzo di carte da distribuire */
            if (x > getWidth() - 200
                    && y > getHeight() - 200
                    && remainingDecks > 0) {
                dealCards();
                return;
            }

            /* se ho cliccato su un mazzo di carte, avvio il trascinamento */
            Deck deck = popDeckOnLocation(x, y);
            if (deck != null) {
                offsetX = x - deck.getPositionX();
                offsetY = y - deck.getPositionY();
                draggingDeck = deck;
                visible = topDecks[deck.getIndex()].getTopCard() != null && topDecks[deck.getIndex()].getTopCard().isVisible();
            }
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            if (draggingDeck == null)
                return; /* non sto trascinando nulla */

            Deck deck = selectDeckOnLocation(mouseEvent.getX(), mouseEvent.getY());
            if (deck != null) {
                deck = topDecks[deck.getIndex()];
                if (validMove(deck.getTopCard(), draggingDeck.getFirstCard())) {
                    Card c = topDecks[draggingDeck.getIndex()].getTopCard();
                    if (c != null)
                        c.setVisible(true); /* scopre la carta sopra il mazzo vecchio da cui si trascina */
                    deck.appendDeck(draggingDeck);
                    Move m = Move.moveDeck(draggingDeck.getIndex(), deck.getIndex(), draggingDeck.numberOfCards(), visible);
                    moves.push(m);
                    repaint();
                    checkAndRemoveDecks();
                    possibleMoves = getPossibleMoves();
                    score -= 1;
                    draggingDeck = null;
                    return;
                }
            }
            topDecks[draggingDeck.getIndex()].appendDeck(draggingDeck);
            draggingDeck = null;
            repaint();
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
        }
    }

    /**
     * Class to handle mouse movement
     *
     * TODO: move it in another file ?
     */
    private class GameMouseMotionListener implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent e) {
            Point position = getMousePosition();

            if (draggingDeck == null || position == null)
                return; /* non sto trascinando nulla o il mouse è fuori dalla finestra */

            int mouseX = (int) position.getX() - offsetX;
            int mouseY = (int) position.getY() - offsetY;
            draggingDeck.setPosition(mouseX, mouseY);
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }

    /**
     * Classe to handle key events
     *
     * TODO: move it in another file ?
     */
    private class GameKeyListener implements KeyListener {

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            if (!moves.isEmpty()
                    && (keyEvent.getModifiersEx() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()) != 0
                    && keyEvent.getKeyCode() == KeyEvent.VK_Z)
                undoLastMove();
            if (!possibleMoves.isEmpty()
                    && (keyEvent.getModifiersEx() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()) != 0
                    && keyEvent.getKeyCode() == KeyEvent.VK_H)
                getHint();
        }

        @Override
        public void keyTyped(KeyEvent keyEvent) {
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
        }
    }
}
