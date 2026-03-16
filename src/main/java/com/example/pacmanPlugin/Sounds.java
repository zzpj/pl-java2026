package com.example.pacmanPlugin;

import javax.sound.sampled.*;
import java.io.File;

public class Sounds {

    public static void playWin() {
        try {
            AudioInputStream audioStream =
                    AudioSystem.getAudioInputStream(
                            Sounds.class.getResource("/sounds/win.wav"));

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);


            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playMusic() {
        try {
            AudioInputStream audioStream =
                    AudioSystem.getAudioInputStream(
                            Sounds.class.getResource("/sounds/music.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            volume.setValue(-25.0f);
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playLose() {
        try {
            AudioInputStream audioStream =
                    AudioSystem.getAudioInputStream(
                            Sounds.class.getResource("/sounds/lose.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);


            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playCoin() {
        try {
            AudioInputStream audioStream =
                    AudioSystem.getAudioInputStream(
                            Sounds.class.getResource("/sounds/coin.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);


            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
