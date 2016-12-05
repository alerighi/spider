package it.alerighi.spider;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.*;
import java.awt.*;

/**
 * Finestra principale del programma
 *
 * @author Alessandro Righi
 */
public class MainWindow extends JFrame {

    private static final String TITLE = "Spider";
    private GamePanel game;

    public MainWindow() {
        super(TITLE);
        setLayout(new GridLayout());
        setJMenuBar(buildMenuBar());
        newGame(1);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(((int) game.getSize().getWidth()), (int) game.getSize().getHeight() + 40);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void newGame(int numberOfSuits) {
        game = new GamePanel(numberOfSuits);
        getContentPane().removeAll();
        getContentPane().add(game);
        game.setFocusable(true);
        game.requestFocus();
        update(getGraphics());
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
        itemOneSuit.addActionListener(a -> newGame(1));
        menuNewGame.add(itemOneSuit);

        JMenuItem itemTwoSuits = new JMenuItem("Two suits");
        itemTwoSuits.addActionListener(a -> newGame(2));
        menuNewGame.add(itemTwoSuits);

        JMenuItem itemFourSuits = new JMenuItem("Four suits");
        itemFourSuits.addActionListener(a -> newGame(4));
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
