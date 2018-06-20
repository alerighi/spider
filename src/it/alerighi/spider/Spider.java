package it.alerighi.spider;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Main game window
 *
 * @author Alessandro Righi
 */
public final class Spider extends JFrame {
    private static final Logger logger = Logger.getGlobal();
    private static final boolean IS_MAC = System.getProperty("os.name").startsWith("Mac");

    public static final String APPLICATION_VERSION = "1.0.0";
    public static final String APPLICATION_NAME = "Spider";

    private static final String WIN_TITLE = APPLICATION_NAME + " v" + APPLICATION_VERSION;
    private static final int WIN_WIDTH = 1500;
    private static final int WIN_HEIGHT = 800;
    private static final Image WIN_ICON_IMAGE;

    private final GamePanel gamePanel = new GamePanel();


    static {
        /* load game icon image */
        Image img = null;
        try {
            img = ImageIO.read(Spider.class.getResourceAsStream("spider.png"));
        } catch (IOException e) {
            logger.severe("Cannot load game icon");
        }
        WIN_ICON_IMAGE = img;
    }

    public Spider() {
        setTitle(WIN_TITLE);
        setSize(WIN_WIDTH, WIN_HEIGHT);
        setIconImage(WIN_ICON_IMAGE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setJMenuBar(buildMenuBar());
        setContentPane(gamePanel);
        setLocationRelativeTo(null);
        setVisible(true);
        showNewGameDialog();
    }

    /**
     * Show new game dialog
     */
    private void showNewGameDialog() {
        logger.info("prompting game mode selection");
        final String[] GAME_MODES = {
                "1 suit  (easy)",
                "2 suits (medium)",
                "4 suits (hard)"
        };

        String selection = (String) JOptionPane.showInputDialog(null, "Choose game difficulty",
                "New Game", JOptionPane.PLAIN_MESSAGE, null, GAME_MODES, GAME_MODES[1]);
        if (selection == null)
            System.exit(0);
        int suits = Character.getNumericValue(selection.charAt(0));
        logger.info( "starting new game with " + suits + " suits");
        gamePanel.startNewGame(suits);
    }

    /**
     * Build the menu bar
     *
     * @return JMenuBar object
     */
    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        if (!IS_MAC) {
            JMenu fileMenu = new JMenu("File");

            fileMenu.addSeparator();

            JMenuItem itemAbout = new JMenuItem("About");
            //itemAbout.addActionListener(a -> new AboutWindow());
            fileMenu.add(itemAbout);

            JMenuItem itemExit = new JMenuItem("Quit");
            itemExit.addActionListener(a -> System.exit(0));
            fileMenu.add(itemExit);

            menuBar.add(fileMenu);
        }

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

        menuBar.add(gameMenu);
        return menuBar;
    }

    public static void main(String args[]) {
        logger.info(APPLICATION_NAME + " version " + APPLICATION_VERSION + " (c) 2016-2018 Alessandro Righi");

        /* set propriety to use system menu bar on MacOS */
        if (System.getProperty("os.name").startsWith("Mac")) {
            System.setProperty("apple.awt.application.name", Spider.APPLICATION_NAME);
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.warning("Cannot set UI system look and feel");
        }

        new Spider();
    }
}
