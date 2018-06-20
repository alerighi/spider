package it.alerighi.spider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

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

    public GamePanel() {
        GameEventListener eventListener = new GameEventListener();
        addMouseListener(eventListener);
        addMouseMotionListener(eventListener);
        addKeyListener(eventListener);
        setFocusable(true);
        requestFocus();
        new Card(1, 1);
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

        ArrayList<Card> deck = new ArrayList<>(8 * 4 * 13);

        for (int n = 0; n < 8 / numberOfSuits; n++) {
            for (int suit = 0; suit < numberOfSuits; suit++) {
                for (int value = 1; value < 14; value++) {
                    deck.add(new Card(suit, value));
                }
            }
        }

        Collections.shuffle(deck);

        for (int i = 0; i < 5; i++) {
            decks[i] = deck.subList(i * 10, i * 10 + 10).toArray(decks[i]);
        }

        for (int i = 0; i < 4; i++) {
            topDecks[i] = new Deck(deck.subList(50 + i * 6, 50 + i * 6 + 6));
            topDecks[i].setIndex(i);
        }

        for (int i = 0; i < 6; i++) {
            topDecks[i + 4] = new Deck(deck.subList(74 + i * 5, 74 + i * 5 + 5));
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
            for (int i = 0; i < remainingDecks; i++) {
                Card.drawCardBack(new Point(getWidth() - i * 10 - 20 - Card.WIDTH, getHeight() - 20 - Card.HEIGHT), graphics);
            }
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
            topDecks[i].paint(graphics, new Point(spaceBetweenCards * (i + 1) + Card.WIDTH * i, 20));
        }

        /* disegna i mazzi rimossi */
        int x = 20;
        int y = getHeight() - Card.HEIGHT - 20;
        for (Deck d : removedDecks) {
            Card c = d.getFirstCard();
            if (c != null)
                c.paint(graphics, new Point(x, y));
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
     * @param position position
     * @return subdeck if present, else null
     */
    private Deck popDeckOnLocation(Point position) {
        Deck deck = null;

        for (int i = 0; i < 10 && deck == null; i++)
            deck = topDecks[i].selectSubDeck(position, true);

        return deck;
    }

    /**
     * Select a subdeck from the current mouse position if present.
     *
     * @param position position
     * @return subdeck if present, else null
     */
    private Deck selectDeckOnLocation(Point position) {
        Deck deck = null;

        for (int i = 0; i < 10 && deck == null; i++)
            deck = topDecks[i].selectSubDeck(position, false);

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
        return upperCard == null || upperCard.value - lowerCard.value == 1;
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
     *
     * TODO: this method is shit
     */
    private List<Move> getPossibleMoves() {
        LinkedList<Move> moves = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < topDecks[i].numberOfCards(); j++) {
                if (topDecks[i].isOrderdered(j) && topDecks[i].getCardByIndex(j).isVisible()) {
                    for (int a = 0; a < 10; a++) {
                        if (i != a && validMove(topDecks[a].getTopCard(), topDecks[i].getCardByIndex(j))) {
                            Move move = Move.moveDeck(i, a, topDecks[i].numberOfCards() - j, false);
                            Card topCard = topDecks[a].getTopCard();
                            if (topCard == null
                                    || topDecks[i].getCardByIndex(j) != null
                                    || topCard.suit == topDecks[i].getCardByIndex(j).suit)
                                moves.addFirst(move);
                            else
                                moves.addLast(move);
                        }
                    }
                }
            }
        }

        return moves;
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
            g.drawRect(c.getPosition().x, c.getPosition().y, Card.WIDTH, Card.HEIGHT + move.cards * Deck.SPACE_BETWEEN_CARDS - Deck.SPACE_BETWEEN_CARDS);

            Thread.sleep(400);
            g.setStroke(oldStroke);
            paint(g);
            g.setStroke(highWidthStroke);
            g.setColor(HINT_COLOR);

            d = topDecks[move.to];
            c = d.getTopCard();

            if (c != null)
                g.drawRect(c.getPosition().x, c.getPosition().y, Card.WIDTH, Card.HEIGHT);
            else
                g.drawRect(d.getPosition().x, d.getPosition().y, Card.WIDTH, Card.HEIGHT);

            Thread.sleep(400);
            g.setStroke(oldStroke);
            paint(g);
        } catch (InterruptedException e) {
            die("This shouldn't have ever happened!");
        }
    }

    private void undoMoveDeck(Move toUndo) {
        score -= 1;
        Deck from = topDecks[toUndo.to];
        Deck to = topDecks[toUndo.from];
        if (!toUndo.visible && to.getTopCard() != null)
            to.getTopCard().setVisible(false);
        Deck toMove = from.getSubDeck(from.numberOfCards() - toUndo.cards, true);
        to.appendDeck(toMove);
        repaint();
    }

    private void undoDealCards(Move toUndo) {
        score -= 1;
        for (int i = 0; i < 10; i++) {
            topDecks[i].getSubDeck(topDecks[i].numberOfCards() - 1, true);
            Card topCard = topDecks[i].getTopCard();
            if (topCard != null)
                topCard.setVisible(true);
        }
        remainingDecks++;
        repaint();
    }

    private void undoDeckRemoved(Move toUndo) {
        if (!toUndo.visible) {
            Card topCard = topDecks[toUndo.removedDeck].getTopCard();
            if (topCard != null)
                topCard.setVisible(false);
        }
        topDecks[toUndo.removedDeck].appendDeck(removedDecks.pop());
        undoLastMove(); /* undo another move */
    }

    /**
     * Undo last move
     */
    private void undoLastMove() {
        if (moves.empty())
            return;
        Move toUndo = moves.pop();
        switch (toUndo.moveType) {
            case Move.DECK_REMOVED:
                undoDeckRemoved(toUndo);
                undoLastMove(); /* need to undo another move! */
                break;
            case Move.DEAL_CARDS:
                undoDealCards(toUndo);
                break;
            case Move.MOVE_DECK:
                undoMoveDeck(toUndo);
                break;
        }
    }

    /**
     * Check if is possible to remove a deck and if it is remove it
     */
    private void checkAndRemoveDecks() {
        for (int j = 0; j < 10; j++) {
            Deck d = topDecks[j];
            if (d.getTopCard() != null && d.getTopCard().value != 1)
                continue; /* la carta infondo ad un mazzetto non è un asse: non posso rimuoverlo */
            for (int i = 0; i < d.numberOfCards(); i++) {
                Card c = d.getCardByIndex(i);
                if (c.isVisible() && c.value == 13 && d.isOrderdered(i)) {
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
    }

    /**
     * Class to handle mouse events
     *
     * TODO: move it in another file ?
     */
    private class GameEventListener implements MouseListener, MouseMotionListener, KeyListener {

        private Point offset;
        private boolean visible;

        private boolean mouseIsInUndoBox(Point mousePosition) {
            int startX = ((getWidth() / 2) - 125 + getWidth()) / 2;
            int startY = getHeight() - 115;
            int width = 120;
            int height = 60;
            return mousePosition.x > startX
                    && mousePosition.x < startX + width
                    && mousePosition.y > startY
                    && mousePosition.y < startY + height;
        }

        private boolean mouseIsInScoreBox(Point mousePosition) {
            return mousePosition.x > getWidth() / 2 - 125
                    && mousePosition.x < getWidth() / 2 + 125
                    && mousePosition.y > getHeight() - 145
                    && mousePosition.y < getHeight() - 20;
        }

        private boolean mouseIsInDealCardsPosition(Point mousePosition) {
            return mousePosition.x > getWidth() - 200
                    && mousePosition.y > getHeight() - 200
                    && remainingDecks > 0;
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            Point mousePosition = mouseEvent.getPoint();

            if (mouseIsInUndoBox(mousePosition))
                undoLastMove();

            if (mouseIsInScoreBox(mousePosition)) {
                if (isEnded()) /* if game ended start a new game */
                    startNewGame(numberOfSuits);
                else
                    getHint();
            }

            if (mouseIsInDealCardsPosition(mousePosition))
                dealCards();

            Deck deck = popDeckOnLocation(mousePosition);
            if (deck != null) {
                offset = deck.getPosition();
                offset.translate(-mousePosition.x, -mousePosition.y);
                draggingDeck = deck;
                Card topCard = topDecks[deck.getIndex()].getTopCard();
                visible = topCard != null && topCard.isVisible();
            }
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            if (draggingDeck == null)
                return; /* non sto trascinando nulla */

            Deck deck = selectDeckOnLocation(mouseEvent.getPoint());
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
        public void mouseDragged(MouseEvent e) {
            Point position = getMousePosition();

            if (draggingDeck == null || position == null)
                return;

            position.translate(offset.x, offset.y);
            draggingDeck.setPosition(position);
            repaint();
        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            /* CTRL-Z - undo last move */
            if ((keyEvent.getModifiersEx() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()) != 0
                    && keyEvent.getKeyCode() == KeyEvent.VK_Z)
                undoLastMove();
            /* CTRL-H - get hint */
            if ((keyEvent.getModifiersEx() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()) != 0
                    && keyEvent.getKeyCode() == KeyEvent.VK_H)
                getHint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
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

        @Override
        public void keyTyped(KeyEvent keyEvent) {
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
        }
    }
}
