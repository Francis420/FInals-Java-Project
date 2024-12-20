import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {
    private Clip clip;

    public void play(String audioFilePath) {
        new Thread(() -> {
            try {
                if (clip != null && clip.isRunning()) {
                    clip.stop();
                    clip.close();
                }
                File audioFile = new File(getClass().getResource(audioFilePath).getFile());
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println("Error playing audio: " + e.getMessage());
            }
        }).start();
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}