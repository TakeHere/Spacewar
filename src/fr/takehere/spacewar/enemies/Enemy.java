package fr.takehere.spacewar.enemies;

import fr.takehere.ethereal.Scene;
import fr.takehere.ethereal.display.GameWindow;
import fr.takehere.ethereal.objects.Actor;
import fr.takehere.ethereal.utils.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Enemy extends Actor {

    public static List<Enemy> enemies = new ArrayList<>();

    public Enemy(Vector2 location, Dimension dimension, Image texture, String name, Scene scene) {
        super(location, dimension, texture, name, scene);

        enemies.add(this);
    }

    public void destroy(){
        GameWindow.runNextFrame(() -> {
            enemies.remove(this);
        });

        super.destroy();
    }
}
