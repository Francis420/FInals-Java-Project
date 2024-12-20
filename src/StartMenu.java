import javax.swing.*;
import java.awt.*;

public class StartMenu extends JPanel {
    private AudioPlayer audioPlayer;
    private BackgroundPanel background; // Make background an instance variable

    public StartMenu(Runnable startGameRunnable) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Initialize the audio player
        audioPlayer = new AudioPlayer();

        // Load background image
        background = new BackgroundPanel("/background.jpg");
        background.setLayout(new BorderLayout());
        add(background);

        JLabel titleLabel = new JLabel("Pac-Man Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.YELLOW);
        background.add(titleLabel, BorderLayout.NORTH);

        JTextArea instructions = new JTextArea(
            "Instructions:\n" +
            "- Use arrow keys to move Pac-Man.\n" +
            "- Collect all the food to advance to the next level.\n" +
            "- Avoid the ghosts or use power-ups to survive.\n" +
            "- Speed power-up doubles your speed for a short time.\n" +
            "- Invisibility power-up makes you invisible to ghosts for a short time.\n" +
            "- You have 3 lives. The game ends when you lose all lives."
        );
        instructions.setFont(new Font("Arial", Font.PLAIN, 18));
        instructions.setForeground(Color.WHITE);
        instructions.setBackground(Color.BLACK);
        instructions.setEditable(false);
        background.add(instructions, BorderLayout.CENTER);

        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.setBackground(Color.YELLOW);
        startButton.setForeground(Color.BLACK);
        startButton.addActionListener(_ -> {
            startGameRunnable.run();
            stopMusic();
        });
        background.add(startButton, BorderLayout.SOUTH);

        // Additional buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setLayout(new GridLayout(1, 2));

        JButton settingsButton = new JButton("Settings");
        settingsButton.setFont(new Font("Arial", Font.BOLD, 24));
        settingsButton.setBackground(Color.GRAY);
        settingsButton.setForeground(Color.WHITE);
        buttonPanel.add(settingsButton);

        JButton highScoresButton = new JButton("High Scores");
        highScoresButton.setFont(new Font("Arial", Font.BOLD, 24));
        highScoresButton.setBackground(Color.GRAY);
        highScoresButton.setForeground(Color.WHITE);
        highScoresButton.addActionListener(_ -> showHighScores());
        buttonPanel.add(highScoresButton);

        background.add(buttonPanel, BorderLayout.NORTH);
    }

    public void showHighScores() {
        HighScoreManager highScoreManager = new HighScoreManager();
        StringBuilder highScoresText = new StringBuilder("High Scores:\n");
        for (HighScore highScore : highScoreManager.getHighScores()) {
            highScoresText.append(highScore.getPlayerName()).append(": ").append(highScore.getScore()).append("\n");
        }
        JOptionPane.showMessageDialog(this, highScoresText.toString(), "High Scores", JOptionPane.INFORMATION_MESSAGE);
    }

    public void playMusic() {
        audioPlayer.play("/sounds/startmenu.wav");
    }

    public void stopMusic() {
        audioPlayer.stop();
    }
}