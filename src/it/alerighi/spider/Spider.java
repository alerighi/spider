package it.alerighi.spider;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.*;

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

    public Spider() {
        super(TITLE);
        setSize(WIN_WIDTH, WIN_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setJMenuBar(buildMenuBar());
        game = new GamePanel();
        game.startNewGame(1);
        setContentPane(game);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String args[]) {
        new Spider();
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

        JMenuItem itemExit = new JMenuItem("Exit");
        itemExit.addActionListener(a -> System.exit(0));
        gameMenu.add(itemExit);

        menuBar.add(gameMenu);
        return menuBar;
    }

}
