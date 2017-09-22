package it.alerighi.spider;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static it.alerighi.spider.Util.err;

/**
 * Finestra principale del programma
 *
 * @author Alessandro Righi
 */
public class Spider extends JFrame {

    /**
     * versione dell'applicazione
     */
    public static final String VERSION = "v1.0.0";

    /**
     * nome dell'applicazione
     */
    public static final String NAME = "Spider";
    /**
     * icona del gioco
     */
    public static final Image ICON_IMAGE;
    /** titolo della finestra */
    private static final String TITLE = NAME + " " + VERSION;
    /** larghezza della finestra */
    private static final int WIN_WIDTH = 1500;
    /** altezza della finestra */
    private static final int WIN_HEIGHT = 800;

    static {
        Image img = null;
        try {
            img = ImageIO.read(Spider.class.getResourceAsStream("spider.png"));
        } catch (IOException e) {
            err("Cannot load game icon");
        }
        ICON_IMAGE = img;
    }

    /**
     * oggetto che rappresenta il pannello di gioco
     */
    private GamePanel gamePanel = new GamePanel();

    /**
     * Costruttore di una finestra gioco spider
     */
    public Spider() {
        setTitle(TITLE);
        setSize(WIN_WIDTH, WIN_HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setJMenuBar(buildMenuBar());
        setContentPane(gamePanel);
        setLocationRelativeTo(null);
        setIconImage(ICON_IMAGE);
        setVisible(true);
        showNewGameDialog();
    }

    /**
     * Mostra il dialogo che consente di selezionare la modalità di gioco
     */
    private void showNewGameDialog() {
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
        gamePanel.startNewGame(suits);
    }

    /**
     * Costruisce la barra dei menu
     *
     * @return barra dei menu
     */
    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        if (!Main.IS_MAC) {
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

}
