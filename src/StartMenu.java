import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class StartMenu extends JPanel {
    StartMenu(ActionListener startGameListener) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("Pac-Man Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.YELLOW);
        add(titleLabel, BorderLayout.NORTH);

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
        add(instructions, BorderLayout.CENTER);

        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.addActionListener(startGameListener);
        add(startButton, BorderLayout.SOUTH);
    }
}