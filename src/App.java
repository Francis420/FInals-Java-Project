import javax.swing.JFrame;
import java.awt.CardLayout;
import javax.swing.JPanel;

public class App {
    public static void main(String[] args) throws Exception {
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        JFrame frame = new JFrame("PacMan Reinmagined");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the main panel with CardLayout
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        // Create the PacMan game panel
        PacMan pacmanGame = new PacMan();
        mainPanel.add(pacmanGame.mainPanel, "Game");

        // Create the start menu panel
        StartMenu startMenu = new StartMenu(_ -> {
            cardLayout.show(mainPanel, "Game");
            pacmanGame.requestFocusInWindow();
            pacmanGame.startGame();
        });
        mainPanel.add(startMenu, "StartMenu");

        // Add the main panel to the frame
        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);

        // Show the start menu initially
        cardLayout.show(mainPanel, "StartMenu");
    }
}