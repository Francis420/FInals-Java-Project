import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

class PowerUp {
    int x;
    int y;
    int width;
    int height;
    Image image;
    String type; // "speed", "invisibility"
    

    PowerUp(Image image, int x, int y, int width, int height, String type) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
    }
}

public class PacMan extends JPanel implements ActionListener, KeyListener {
    
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;
        boolean isInvisible = false;
    
        int startX;
        int startY;
        char direction = 'U'; // U D L R
        int velocityX = 0;
        int velocityY = 0;
    
        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }
    
        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }
    
        void updateVelocity() {
            int speed = DEFAULT_SPEED;
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -speed;
            } else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = speed;
            } else if (this.direction == 'L') {
                this.velocityX = -speed;
                this.velocityY = 0;
            } else if (this.direction == 'R') {
                this.velocityX = speed;
                this.velocityY = 0;
            }
        }
    
        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    int animationFrame = 0;
    char bufferedDirection = ' ';
    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;
    private int currentLevel = 0;
    private static final int INVISIBILITY_DURATION = 10000;
    private static final int DEFAULT_SPEED = 8; // Default speed
    private static final double SPEED_MULTIPLIER = 2.0; // Speed multiplier for power-up    

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;
    private Image cyanGhostImage;
    private Image speedImage;
    private Image invisibilityImage;
    private Image heartFullImage;
    private Image heartEmptyImage;
    public CardLayout cardLayout;
    public JPanel mainPanel;
    private StartMenu startMenu;


    private Image[] pacmanUpImages;
    private Image[] pacmanDownImages;
    private Image[] pacmanLeftImages;
    private Image[] pacmanRightImages;
    

    // X = wall, O = skip, P = pac man, ' ' = food
    // Ghosts: b = blue, o = orange, p = pink, r = red, c = cyan
    // Power-ups: S = speed, I = invisibility
    private String[][] tileMaps = {
        {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXXcXXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X      I X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X     S           X",
            "XXXXXXXXXXXXXXXXXXX"
        },
        {
            "XXXXXXXXXXXXXXXXXXX",
            "X bpo    X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXXcXXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O                 O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X      I X",
            "X XX XXX X XXX XX X",
            "X  X           X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X     S         P X",
            "XXXXXXXXXXXXXXXXXXX"
        }
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'}; //up down left right
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    private AudioPlayer audioPlayer;
    private String[] levelMusic = {
        "/sounds/level1.wav",
        "/sounds/level2.wav"
    };

    private HighScoreManager highScoreManager;

    PacMan() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize the start menu with a method reference
        startMenu = new StartMenu(this::startGame);

        mainPanel.add(startMenu, "StartMenu");
        mainPanel.add(this, "Game");

        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        highScoreManager = new HighScoreManager();

        // Initialize the audio player
        audioPlayer = new AudioPlayer();

        // Start the background music for the start menu
        startMenu.playMusic();

        // Load images
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        cyanGhostImage = new ImageIcon(getClass().getResource("./cyanGhost.png")).getImage();
        speedImage = new ImageIcon(getClass().getResource("./speed.png")).getImage();
        invisibilityImage = new ImageIcon(getClass().getResource("./invisibility.png")).getImage();
        heartFullImage = new ImageIcon(getClass().getResource("./heart_full.png")).getImage();
        heartEmptyImage = new ImageIcon(getClass().getResource("./heart_blank.png")).getImage();

        pacmanUpImages = new Image[] {
            new ImageIcon(getClass().getResource("./pacmanUp1.png")).getImage(),
            new ImageIcon(getClass().getResource("./pacmanUp2.png")).getImage(),
            new ImageIcon(getClass().getResource("./pacmanUp3.png")).getImage()
        };
        pacmanDownImages = new Image[] {
            new ImageIcon(getClass().getResource("./pacmanDown1.png")).getImage(),
            new ImageIcon(getClass().getResource("./pacmanDown2.png")).getImage(),
            new ImageIcon(getClass().getResource("./pacmanDown3.png")).getImage()
        };
        pacmanLeftImages = new Image[] {
            new ImageIcon(getClass().getResource("./pacmanLeft1.png")).getImage(),
            new ImageIcon(getClass().getResource("./pacmanLeft2.png")).getImage(),
            new ImageIcon(getClass().getResource("./pacmanLeft3.png")).getImage()
        };
        pacmanRightImages = new Image[] {
            new ImageIcon(getClass().getResource("./pacmanRight1.png")).getImage(),
            new ImageIcon(getClass().getResource("./pacmanRight2.png")).getImage(),
            new ImageIcon(getClass().getResource("./pacmanRight3.png")).getImage()
        };

        loadMap();
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        // How long it takes to start timer, milliseconds gone between frames
        gameLoop = new Timer(50, this); // 20fps (1000/50)
        gameLoop.start();
    }

    private void handleGameOver() {
        String playerName = JOptionPane.showInputDialog(this, "Game Over! Enter your name:", "Game Over", JOptionPane.PLAIN_MESSAGE);
        if (playerName != null && !playerName.trim().isEmpty()) {
            highScoreManager.addHighScore(playerName, score);
        }
        int option = JOptionPane.showOptionDialog(this, "Would you like to restart the game or go to the home menu?", "Game Over",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Restart", "Home Menu"}, "Home Menu");
        if (option == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            cardLayout.show(mainPanel, "StartMenu");
        }
    }

    private void restartGame() {
        score = 0;
        lives = 3;
        gameOver = false;
        currentLevel = 0;
        loadMap();
        resetPositions();
        playLevelMusic();
        gameLoop.start();
    }

    private void playLevelMusic() {
        if (audioPlayer != null) {
            audioPlayer.stop();
        }
        // Alternate between the two music files
        String musicFile = levelMusic[currentLevel % levelMusic.length];
        audioPlayer.play(musicFile);
    }

    public void startGame() {
        cardLayout.show(mainPanel, "Game");
        requestFocusInWindow(); // Ensure the game panel has focus
        gameLoop.start();

        // Stop the start menu music
        startMenu.stopMusic();
        playLevelMusic();
    }

    HashSet<PowerUp> powerUps;

    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();
        powerUps = new HashSet<PowerUp>();
        
    

        String[] tileMap = tileMaps[currentLevel];

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c * tileSize;
                int y = r * tileSize;

                if (tileMapChar == 'X') {
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                } else if (tileMapChar == 'b') {
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'o') {
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'p') {
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'c') {
                    Block ghost = new Block(cyanGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'r') {
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'P') {
                    pacman = new Block(pacmanRightImages[0], x, y, tileSize, tileSize);
                } else if (tileMapChar == ' ') {
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                } else if (tileMapChar == 'S') { // Speed power-up
                    PowerUp powerUp = new PowerUp(speedImage, x, y, tileSize, tileSize, "speed");
                    powerUps.add(powerUp);
                } else if (tileMapChar == 'I') { // Invisibility power-up
                    PowerUp powerUp = new PowerUp(invisibilityImage, x, y, tileSize, tileSize, "invisibility");
                    powerUps.add(powerUp);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);
    
        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }
    
        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }
    
        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }
    
        for (PowerUp powerUp : powerUps) {
            g.drawImage(powerUp.image, powerUp.x, powerUp.y, powerUp.width, powerUp.height, null);
        }
    
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), tileSize / 2, tileSize / 2);
        } else {
            g.drawString(" Score: " + String.valueOf(score), tileSize / 2, tileSize / 2);
        }

        int heartX = tileSize / 2;
        int heartY = tileSize / 2;
            for (int i = 0; i < 3; i++) {
            if (i < lives) {
                g.drawImage(heartFullImage, heartX + (i * 32), heartY, 32, 32, null);
            } else {
                g.drawImage(heartEmptyImage, heartX + (i * 32), heartY, 32, 32, null);
            }
    }

    }

    public boolean collision(Block a, PowerUp b) {
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
    }

    public void move() {
        if (isValidMove(bufferedDirection)) {
            pacman.updateDirection(bufferedDirection);
            bufferedDirection = ' ';
        }
    
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;
    
        animationFrame = (animationFrame + 1) % 3;
        updatePacmanImage();
    
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }
    
        for (Block ghost : ghosts) {
            if (collision(ghost, pacman) && !pacman.isInvisible) {
                lives -= 1;
                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }
    
            if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }
    
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);
    
        PowerUp powerUpCollected = null;
        for (PowerUp powerUp : powerUps) {
            if (collision(pacman, powerUp)) {
                powerUpCollected = powerUp;
                applyPowerUp(powerUp);
            }
        }
        powerUps.remove(powerUpCollected);
    
        if (foods.isEmpty()) {
            currentLevel = (currentLevel + 1) % tileMaps.length;
            loadMap();
            resetPositions();
            playLevelMusic(); // Ensure music changes when level changes
        }
    }
    
    public void applyPowerUp(PowerUp powerUp) {
        if (powerUp.type.equals("speed")) {
            pacman.velocityX *= SPEED_MULTIPLIER;
            pacman.velocityY *= SPEED_MULTIPLIER;
            Timer speedTimer = new Timer(5000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pacman.velocityX /= SPEED_MULTIPLIER;
                    pacman.velocityY /= SPEED_MULTIPLIER;
                    System.out.println("Speed power-up ended");
                }
            });
            speedTimer.setRepeats(false);
            speedTimer.start();
        } else if (powerUp.type.equals("invisibility")) {
            pacman.isInvisible = true;
            pacman.image = null; // Make Pac-Man invisible
            System.out.println("Invisibility power-up activated");

            // Timer to flash Pac-Man's image before invisibility ends
            Timer flashTimer = new Timer(INVISIBILITY_DURATION - 2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Timer flashEffectTimer = new Timer(200, new ActionListener() {
                        private int flashCount = 0;

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (flashCount < 5) {
                                pacman.image = (pacman.image == null) ? pacmanRightImages[0] : null;
                                flashCount++;
                            } else {
                                ((Timer) e.getSource()).stop();
                            }
                            repaint();
                        }
                    });
                    flashEffectTimer.start();
                }
            });
            flashTimer.setRepeats(false);
            flashTimer.start();

            Timer invisibilityTimer = new Timer(INVISIBILITY_DURATION, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pacman.isInvisible = false;
                    updatePacmanImage(); // Restore Pac-Man's image
                    System.out.println("Invisibility power-up ended");
                }
            });
            invisibilityTimer.setRepeats(false);
            invisibilityTimer.start();
        }
    }

    public void updatePacmanImage() {
        if (pacman.direction == 'U') {
            pacman.image = pacmanUpImages[animationFrame];
        } else if (pacman.direction == 'D') {
            pacman.image = pacmanDownImages[animationFrame];
        } else if (pacman.direction == 'L') {
            pacman.image = pacmanLeftImages[animationFrame];
        } else if (pacman.direction == 'R') {
            pacman.image = pacmanRightImages[animationFrame];
        }
    }

    public boolean collision(Block a, Block b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public boolean isValidMove(char direction) {
        int testX = pacman.x;
        int testY = pacman.y;
    
        if (direction == 'U') {
            testY -= tileSize / 4;
        } else if (direction == 'D') {
            testY += tileSize / 4;
        } else if (direction == 'L') {
            testX -= tileSize / 4;
        } else if (direction == 'R') {
            testX += tileSize / 4;
        }
    
        for (Block wall : walls) {
            if (collision(new Block(null, testX, testY, pacman.width, pacman.height), wall)) {
                return false;
            }
        }
        return true;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
            audioPlayer.stop();
            handleGameOver();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}


    @Override
public void keyPressed(KeyEvent e) {
    if (gameOver) {
        return;
    }
    switch (e.getKeyCode()) {
        case KeyEvent.VK_UP:
        case KeyEvent.VK_W:
            bufferedDirection = 'U';
            break;
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_S:
            bufferedDirection = 'D';
            break;
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_A:
            bufferedDirection = 'L';
            break;
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_D:
            bufferedDirection = 'R';
            break;
    }
}
    
    @Override
    public void keyReleased(KeyEvent e) {

    }
}
