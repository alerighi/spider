package it.alerighi.spider;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

    private static final String TITLE = "Spider";
    private static final int WIN_WIDTH = 1500;
    private static final int WIN_HEIGHT = 800;
    private GamePanel game;

    private static final String[] GAME_MODES = {
            "1 suit  (easy)",
            "2 suits (medium)",
            "4 suits (hard)"
    };

    public Spider() {
        super(TITLE);
        setSize(WIN_WIDTH, WIN_HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setJMenuBar(buildMenuBar());
        game = new GamePanel();
        setContentPane(game);
        setLocationRelativeTo(null);
        setVisible(true);
        showNewGameDialog();
    }

    private void showNewGameDialog() {
        String selection = (String) JOptionPane.showInputDialog(null, "Choose game difficulty",
                "New Game", JOptionPane.DEFAULT_OPTION, null, GAME_MODES, GAME_MODES[0]);
        if (selection == null) System.exit(0);
        int suits = Integer.parseInt(selection.split(" ")[0]);
        game.startNewGame(suits);
    }

    private void loadGame() {
        throw new NotImplementedException();
    }

    private void saveGame() {
        throw new NotImplementedException();
    }

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("Game");

        JMenuItem menuNewGame = new JMenu("New Game");

        JMenuItem itemOneSuit = new JMenuItem("One suit");
        itemOneSuit.addActionListener(a -> game.startNewGame(1));
        menuNewGame.add(itemOneSuit);

        JMenuItem itemTwoSuits = new JMenuItem("Two suits");
        itemTwoSuits.addActionListener(a -> game.startNewGame(2));
        menuNewGame.add(itemTwoSuits);

        JMenuItem itemFourSuits = new JMenuItem("Four suits");
        itemFourSuits.addActionListener(a -> game.startNewGame(4));
        menuNewGame.add(itemFourSuits);

        gameMenu.add(menuNewGame);

        gameMenu.addSeparator();

        JMenuItem itemSaveGame = new JMenuItem("Save Game");
        itemSaveGame.addActionListener(a -> saveGame());
        gameMenu.add(itemSaveGame);

        JMenuItem itemLoadGame = new JMenuItem("Load Game");
        itemLoadGame.addActionListener(a -> loadGame());
        gameMenu.add(itemLoadGame);

        gameMenu.addSeparator();

        JMenuItem itemExit = new JMenuItem("Quit");
        itemExit.addActionListener(a -> System.exit(0));
        gameMenu.add(itemExit);

        menuBar.add(gameMenu);
        return menuBar;
    }

}
