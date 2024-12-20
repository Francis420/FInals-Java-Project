import java.io.Serializable;

public class HighScore implements Serializable, Comparable<HighScore> {
    private static final long serialVersionUID = 1L; // Add a serialVersionUID
    private String playerName;
    private int score;

    public HighScore(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    @Override
    public int compareTo(HighScore other) {
        return Integer.compare(other.score, this.score); // Sort in descending order
    }
}