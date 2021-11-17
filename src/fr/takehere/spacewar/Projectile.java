package fr.takehere.spacewar;

import fr.takehere.ethereal.Scene;
import fr.takehere.ethereal.display.GameWindow;
import fr.takehere.ethereal.objects.Actor;
import fr.takehere.ethereal.utils.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Projectile extends Actor {

    public static List<Projectile> projectiles = new ArrayList<>();

    public Projectile(Vector2 location, Dimension dimension, Image texture, String name, Scene scene) {
        super(location, dimension, texture, name, scene);

        projectiles.add(this);
    }

    public void destroy(){
        GameWindow.runNextFrame(() -> {
            projectiles.remove(this);
        });

        super.destroy();
    }
}
