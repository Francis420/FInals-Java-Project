import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class AudioPlayer {
    private Clip clip;
    private FloatControl volumeControl;

    public void play(String filePath) {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
        try {
            if (clip == null || !clip.isOpen()) {
                URL url = getClass().getResource(filePath);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                }
            }
            clip.setFramePosition(0); // Rewind to the beginning
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void loop(String filePath) {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
        try {
            if (clip == null || !clip.isOpen()) {
                URL url = getClass().getResource(filePath);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                }
            }
            clip.setFramePosition(0); // Rewind to the beginning
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.flush(); // Clear the buffer
        }
    }

    public void setVolume(double volume) {
        if (volumeControl != null) {
            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();
            float newVolume = (float) (min + (max - min) * volume);
            volumeControl.setValue(newVolume);
        }
    }
}
