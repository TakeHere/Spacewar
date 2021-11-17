package fr.takehere.spacewar;

import fr.takehere.ethereal.Game;
import fr.takehere.ethereal.display.GameWindow;
import fr.takehere.ethereal.utils.RessourcesManager;
import fr.takehere.ethereal.utils.SoundUtil;

import javax.sound.sampled.AudioInputStream;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Spacewar extends Game {

    private static Spacewar instance;

    public static void main(String[] args) {
        instance = new Spacewar("Spacewar", 1000,1000, 60);

        SoundUtil.playLoopSound(SoundUtil.getSoundRessource("sounds/song.wav", instance.getClass()), 1);
    }
    public Spacewar(String title, int height, int width, int targetFps) {
        super(title, height, width, targetFps);
    }

    public List<String> shootSounds;
    public List<String> explosionSounds;

    public float explosionSoundLevel = 0.5f;
    public float shootSoundLevel = 0.3f;

    @Override
    public void init() {
        shootSounds = new ArrayList<>();
        explosionSounds = new ArrayList<>();

        RessourcesManager.addImage("bullet", "images/bullet.png", getClass());
        RessourcesManager.addImage("player", "images/player.png", getClass());
        RessourcesManager.addImage("smoke", "images/smoke.png", getClass());
        RessourcesManager.addImage("fire", "images/fire.png", getClass());
        RessourcesManager.addImage("asteroid", "images/asteroid.png", getClass());
        RessourcesManager.addImage("menu", "images/menu.png", getClass());

        shootSounds.add("sounds/shoot1.wav");
        shootSounds.add("sounds/shoot2.wav");
        shootSounds.add("sounds/shoot3.wav");

        explosionSounds.add("sounds/explosion1.wav");
        explosionSounds.add("sounds/explosion2.wav");
        explosionSounds.add("sounds/explosion3.wav");
    }

    @Override
    public void gameLoop(double v) {
        Graphics2D g2d = gameWindow.getGraphics();

        g2d.drawImage(RessourcesManager.getImage("menu"), 0, 0, gameWindow.getWidth(), gameWindow.getHeight(), null);

        if (gameWindow.isPressed(KeyEvent.VK_CONTROL)){
            switchScene(new SpacewarGame());
        }
    }

    public static Spacewar getInstance() {
        return instance;
    }
}
