import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreManager {
    private List<HighScore> highScores;
    private static final String FILE_PATH = "highscores.dat";

    public HighScoreManager() {
        highScores = new ArrayList<>();
        loadHighScores();
    }

    public void addHighScore(String playerName, int score) {
        highScores.add(new HighScore(playerName, score));
        Collections.sort(highScores);
        if (highScores.size() > 10) { // Keep only top 10 scores
            highScores.remove(highScores.size() - 1);
        }
        saveHighScores();
    }

    public List<HighScore> getHighScores() {
        return highScores;
    }

    private void saveHighScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            System.err.println("Error saving high scores: " + e.getMessage());
        }
    }

    private void loadHighScores() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("High scores file does not exist. Creating a new one.");
            return; // No need to load if the file doesn't exist
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                List<?> list = (List<?>) obj;
                if (!list.isEmpty() && list.get(0) instanceof HighScore) {
                    highScores = new ArrayList<>();
                    for (Object item : list) {
                        if (item instanceof HighScore) {
                            highScores.add((HighScore) item);
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading high scores: " + e.getMessage());
        }
    }
}