package it.alerighi.spider;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 * Finestra principale del programma
 *
 * @author Alessandro Righi
 */
public class Spider extends JFrame {

    // stringhe che indicano versione e nome del programma
    public static final String VERSION = "v1.0.0";
    public static final String NAME = "Spider";

    // dati riguardanti la finestra
    private static final String TITLE = NAME + " " + VERSION;
    private static final int WIN_WIDTH = 1500;
    private static final int WIN_HEIGHT = 800;

    // l'oggetto che rappresenta il pannello di gioco
    private GamePanel gamePanel;

    public Spider() {
        new Thread(Card::loadCardImages).start(); // carica le carte in un thread separato, velocizza avvio!
        setTitle(TITLE);
        setSize(WIN_WIDTH, WIN_HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setJMenuBar(buildMenuBar());
        gamePanel = new GamePanel();
        setContentPane(gamePanel);
        setLocationRelativeTo(null);
        setVisible(true);
        showNewGameDialog();
    }

    private void showNewGameDialog() {
        final String[] GAME_MODES = {
                "1 suit  (easy)",
                "2 suits (medium)",
                "4 suits (hard)"
        };

        String selection = (String) JOptionPane.showInputDialog(null, "Choose game difficulty",
                "New Game", JOptionPane.DEFAULT_OPTION, null, GAME_MODES, GAME_MODES[0]);
        if (selection == null) System.exit(0);
        int suits = Character.getNumericValue(selection.charAt(0));
        gamePanel.startNewGame(suits);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("Game");

        JMenuItem menuNewGame = new JMenu("New Game");

        JMenuItem itemOneSuit = new JMenuItem("One suit");
        itemOneSuit.addActionListener(a -> gamePanel.startNewGame(1));
        menuNewGame.add(itemOneSuit);

        JMenuItem itemTwoSuits = new JMenuItem("Two suits");
        itemTwoSuits.addActionListener(a -> gamePanel.startNewGame(2));
        menuNewGame.add(itemTwoSuits);

        JMenuItem itemFourSuits = new JMenuItem("Four suits");
        itemFourSuits.addActionListener(a -> gamePanel.startNewGame(4));
        menuNewGame.add(itemFourSuits);

        gameMenu.add(menuNewGame);

        gameMenu.addSeparator();

        JMenuItem itemSaveGame = new JMenuItem("Deal cards");
        itemSaveGame.addActionListener(a -> gamePanel.dealCards());
        gameMenu.add(itemSaveGame);

        JMenuItem itemLoadGame = new JMenuItem("Hint");
        itemLoadGame.addActionListener(a -> gamePanel.getHint());
        gameMenu.add(itemLoadGame);

        gameMenu.addSeparator();

        JMenuItem itemExit = new JMenuItem("Quit");
        itemExit.addActionListener(a -> System.exit(0));
        gameMenu.add(itemExit);

        menuBar.add(gameMenu);
        return menuBar;
    }

}
