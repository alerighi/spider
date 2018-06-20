package it.alerighi.spider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static it.alerighi.spider.Util.*;

/**
 * Classe che gestisce il pannello del gioco
 *
 * @author Alessandro Righi
 */
public class GamePanel extends JPanel {

    /**
     * colore dello sfondo del gioco (verdino)
     */
    public static final Color BACKGROUND_COLOR = new Color(35, 104, 32);

    /**
     * colore dello sfondo del riquadro punteggi (verdino più scuro)
     */
    public static final Color SCORE_BOX_COLOR = new Color(33, 79, 33);

    /**
     * colore con cui sono evidenziate le carte dei suggerimenti
     */
    public static final Color HINT_COLOR = new Color(10, 27, 50);

    /**
     * array dei 10 mazzi superiori del gioco
     */
    private Deck[] topDecks = new Deck[10];

    /**
     * array dei 5 mazzi di carte extra da distribuire durente il gioco
     */
    private Card[][] decks = new Card[5][10];

    /**
     * numero di deck di carte da distribuire rimanenti
     */
    private int remainingDecks;

    /**
     * deck di carte che il giocatore sta spostando
     */
    private Deck draggingDeck = null;

    /**
     * deck completati e rimossi dal gioco
     */
    private Stack<Deck> removedDecks = new Stack<>();

    /**
     * lista delle mosse effettuate dal giocatore
     */
    private Stack<Move> moves = new Stack<>();

    /**
     * punteggio di gioco
     */
    private int score;

    /**
     * lista delle mosse possibili da effettuare
     */
    private List<Move> possibleMoves;

    /**
     * numero di semi del gioco
     */
    private int numberOfSuits;

    /**
     * indica l'offset X del mazzetto che si trascina
     */
    private int offsetX;

    /**
     * indica l'offset Y del mazzetto che si trascina
     */
    private int offsetY;

    /**
     * indica se la carta sopra il mazzetto prima del trascinamento era visibile
     */
    private boolean visible;

    /**
     * Costruttore di un nuovo pannello di gioco
     */
    public GamePanel() {
        debug("GamePanel", "initializing game panel");
        addMouseListener(new GameMouseListener());
        addMouseMotionListener(new GameMouseMotionListener());
        addKeyListener(new GameKeyListener());
        setFocusable(true);
        requestFocus();
        new Card(1, 1);
    }

    /**
     * Avvia un nuovo gioco con il numero di semi specificato
     *
     * @param numberOfSuits numero di semi del gioco
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
     * Inizializza i vari deck di carte per il gioco
     */
    private void buildDecks() {
        debug("GamePanel", "building card decks");
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
     * Disegna l'area del punteggio e il pulsante UNDO
     *
     * @param graphics area grafica in cui disegnare
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
     * Controlla se la posizione specificata si trova nel riquadro di UNDO
     *
     * @param x coordinata X da controllare
     * @param y coordinata Y da controllare
     * @return true se (x,y) in UNDO, false altrimenti
     */
    private boolean isInUndoBox(int x, int y) {
        int startX = ((getWidth() / 2) - 125 + getWidth()) / 2;
        int startY = getHeight() - 115;
        int width = 120;
        int height = 60;
        return x > startX && x < startX + width && y > startY && y < startY + height;
    }

    /**
     * Controlla se la posizione specificata si trova nel riguardo punteggio
     *
     * @param x coordinata X da controllare
     * @param y coordinata Y da controllare
     * @return true se (x,y) in riquadro punteggi, false altrimenti
     */
    private boolean isInScoreBox(int x, int y) {
        return x > getWidth() / 2 - 125
                && x < getWidth() / 2 + 125
                && y > getHeight() - 145
                && y < getHeight() - 20;
    }

    /**
     * Disegna il mazzo extra in basso a destra
     *
     * @param graphics area grafica in cui disegnare
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
     * Disegna i mazzi di carte
     *
     * @param graphics area grafica in cui disegnare
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
     * Disegna il campo di gioco
     *
     * @param g area grafica in cui disegnare
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
     * Prende un sottomazzo nella posizione attuale del mouse, e lo rimuove dal mazzo
     *
     * @param x coordinata X
     * @param y coordinata Y
     * @return sottomazzo nella posizione del mouse se esiste, null altrimenti
     */
    private Deck popDeckOnLocation(int x, int y) {
        Deck deck = null;

        for (int i = 0; i < 10 && deck == null; i++)
            deck = topDecks[i].popSubDeck(x, y);

        return deck;
    }

    /**
     * Seleziona un sottomazzo nella posizione attuale del mouse, e NON lo rimuove dal mazzo
     *
     * @param x coordinata X
     * @param y coordinata Y
     * @return sottomazzo nella posizione del mouse se esiste, null altrimenti
     */
    private Deck selectDeckOnLocation(int x, int y) {
        Deck deck = null;

        for (int i = 0; i < 10 && deck == null; i++)
            deck = topDecks[i].selectSubDeck(x, y);

        return deck;
    }

    /**
     * Controlla se una mossa è valida (?)
     *
     * @param card1 carta inferiore
     * @param card2 carta superiore
     * @return true se la mossa è valida, false altrimenti
     */
    private boolean validMove(Card card1, Card card2) {
        return card1 == null || card1.getValue() == card2.getValue() + 1;
    }

    /**
     * distribuisce le carte da uno dei mazzi inferiori
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
        moves.add(new Move());
        checkAndRemoveDecks();
        possibleMoves = getPossibleMoves();
        repaint();
    }

    /**
     * Indica se il gioco è finito o meno
     *
     * @return true se il gico è terminato, false altrimenti
     */
    public boolean isEnded() {
        return removedDecks.size() == 8;
    }

    /**
     * Ottiene una lista di possibili mosse
     *
     * @return lista di possibili mosse
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
                                goodMoves.add(new Move(i, a, topDecks[i].numberOfCards() - j));
                            else
                                badMoves.add(new Move(i, a, topDecks[i].numberOfCards() - j));

        goodMoves.addAll(badMoves);
        return goodMoves;
    }

    /**
     * Mostra un suggerimento di gioco
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

            Deck d = topDecks[move.getFrom()];
            Card c = d.getCardByIndex(d.numberOfCards() - move.getCards());
            g.drawRect(c.getPositionX(), c.getPositionY(), Card.WIDTH, Card.HEIGHT + move.getCards() * Deck.SPACE_BETWEEN_CARDS - Deck.SPACE_BETWEEN_CARDS);

            Thread.sleep(400);
            g.setStroke(oldStroke);
            paint(g);
            g.setStroke(highWidthStroke);
            g.setColor(HINT_COLOR);

            d = topDecks[move.getTo()];
            c = d.getTopCard();

            if (c != null)
                g.drawRect(c.getPositionX(), c.getPositionY(), Card.WIDTH, Card.HEIGHT);
            else
                g.drawRect(d.getPositionX(), d.getPositionY(), Card.WIDTH, Card.HEIGHT);

            Thread.sleep(400);
            g.setStroke(oldStroke);
            paint(g);
        } catch (InterruptedException e) {
            err("This shouldn't have ever happened!");
        }
    }

    /**
     * Annulla l'ultima mossa
     */
    private void undoLastMove() {
        if (moves.empty())
            return; /* nessuna mossa da annullare */
        Move toUndo = moves.pop();
        switch (toUndo.getMoveType()) {
            case DECK_REMOVED:
                if (!toUndo.isVisible())
                    topDecks[toUndo.getRemovedDeck()].getTopCard().setVisible(false);
                topDecks[toUndo.getRemovedDeck()].appendDeck(removedDecks.pop());
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
                Deck from = topDecks[toUndo.getTo()];
                Deck to = topDecks[toUndo.getFrom()];
                if (!toUndo.isVisible() && to.getTopCard() != null)
                    to.getTopCard().setVisible(false);
                Deck toMove = from.getSubDeck(from.numberOfCards() - toUndo.getCards(), true);
                to.appendDeck(toMove);
                repaint();
        }
    }

    /**
     * Controlla se è possibile rimuovere un mazzetto
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
                    moves.push(new Move(j, d.numberOfCards() <= 0 || d.getTopCard().isVisible()));
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
     * Classe per gestire gli eventi del mouse
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
                    Move m = new Move(draggingDeck.getIndex(), deck.getIndex(), draggingDeck.numberOfCards(), visible);
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
     * Classe per gestire gli eventi di movimento del mouse
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
     * Classe per gestire gi eventi della tastiera
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
