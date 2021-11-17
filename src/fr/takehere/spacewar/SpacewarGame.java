package fr.takehere.spacewar;

import fr.takehere.ethereal.Scene;
import fr.takehere.ethereal.display.GameWindow;
import fr.takehere.ethereal.objects.Actor;
import fr.takehere.ethereal.objects.GameObject;
import fr.takehere.ethereal.objects.ParticleGenerator;
import fr.takehere.ethereal.objects.Title;
import fr.takehere.ethereal.utils.*;
import fr.takehere.spacewar.enemies.Enemy;
import fr.takehere.spacewar.enemies.EnemyGenerator;

import javax.sound.sampled.AudioInputStream;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

public class SpacewarGame extends Scene {

    private static SpacewarGame instance;

    public Actor player;

    public int score = 0;

    float rotationSpeed = 5;
    float speedAddition = 0.7f;
    float velocityDeceleration = (float) (1/1.05);

    float projectileSpeed = 15;
    float playerRecoil = 3;

    Spacewar spacewar = Spacewar.getInstance();
    GameWindow gameWindow = spacewar.gameWindow;

    ParticleGenerator explosionParticleGenerator = new ParticleGenerator(Vector2.ZERO, new Dimension(15,15), RessourcesManager.getImage("fire"), false, 10, 1, 2, 1000, this);

    @Override
    public void init() {
        instance = this;

        new EnemyGenerator();

        Title tutorialTitle = new Title("Press space to shoot !", new Vector2(100,200), new Color(224, 216, 71), new Font("Colibri", Font.BOLD, 40), 3000, this);
        player = new Actor(new Vector2(gameWindow.getWidth()/2, gameWindow.getHeight()/2), new Dimension(50,50), RessourcesManager.getImage("player"), "player", this);

        ParticleGenerator particleGenerator = new ParticleGenerator(Vector2.ZERO, new Dimension(15,15), RessourcesManager.getImage("smoke"), false, 10, 1, 3, 1000, this);
        particleGenerator.rotationSpeed = 5;

        //----< Player shoot >----
        gameWindow.canvas.addKeyListener(new KeyAdapter() {
            boolean isSpaceAlreadyPressed = false;

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE){
                    if (isSpaceAlreadyPressed == false && gameWindow.currentScene == SpacewarGame.this){
                        isSpaceAlreadyPressed = true;

                        //Generate Particles
                        particleGenerator.location = MathUtils.getCenterOfPawn(player);
                        particleGenerator.generate();

                        //Shoot projectile
                        Projectile projectile = new Projectile(MathUtils.getCenterOfPawn(player), new Dimension(10,20), RessourcesManager.getImage("bullet"), "projectile", SpacewarGame.this);
                        projectile.rotation = player.rotation;
                        projectile.velocity = MathUtils.getForwardVector(projectile.rotation).multiply(projectileSpeed);

                        //Play shoot sound
                        SoundUtil.playSound(SoundUtil.getSoundRessource(spacewar.shootSounds.get(MathUtils.random.nextInt(spacewar.shootSounds.size())), spacewar.getClass()), spacewar.shootSoundLevel);

                        //Player recoil
                        player.velocity = player.velocity.add(MathUtils.getForwardVector(player.rotation).multiply(-1).multiply(playerRecoil));
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE){
                    isSpaceAlreadyPressed = false;
                }
            }
        });
    }

    Rectangle screenRectangle = new Rectangle(0,0, gameWindow.getWidth(), gameWindow.getHeight());


    @Override
    public void gameLoop(double v) {
        //----< Drawing Background >----
        Graphics2D g2d = gameWindow.getGraphics();
        g2d.setColor(new Color(26, 33, 84));
        g2d.fillRect(0,0, gameWindow.getWidth(), gameWindow.getHeight());

        //----< Drawing Score UI >----
        g2d.setFont(new Font("Bahnschrift", Font.BOLD, 48));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Score: " + score, 100,100);

        //----< Player friction >----
        player.velocity = player.velocity.multiply(velocityDeceleration);

        //----< Player rotation >----
        if (gameWindow.isPressed(KeyEvent.VK_LEFT)){
            player.rotation -= rotationSpeed;
        }else if (gameWindow.isPressed(KeyEvent.VK_RIGHT)){
            player.rotation += rotationSpeed;
        }

        //----< Player Speed >----
        if (gameWindow.isPressed(KeyEvent.VK_UP)){
            player.velocity = player.velocity.add(MathUtils.getForwardVector(player.rotation).multiply(speedAddition));
        }else if (gameWindow.isPressed(KeyEvent.VK_DOWN)){
            player.velocity = player.velocity.add(MathUtils.getForwardVector(player.rotation).multiply(-1).multiply(speedAddition));
        }

        //----< Player teleportation if oob >----
        if (player.location.x > gameWindow.canvas.getWidth()) player.location.x = 0;
        if (player.location.x < 0) player.location.x = gameWindow.canvas.getWidth();

        if (player.location.y > gameWindow.canvas.getHeight()) player.location.y = 0;
        if (player.location.y < 0) player.location.y = gameWindow.canvas.getHeight();

        //----< Projectile deletion if oob >----
        for (Iterator<Projectile> iterator = Projectile.projectiles.iterator(); iterator.hasNext();) {
            Projectile projectile = iterator.next();

            if (!MathUtils.isColliding(projectile.boundingBox.getBounds(), screenRectangle)){
                projectile.destroy();
            }
        }

        //----< Destroy enemy when shoot >----
        try {
            for (Iterator<Enemy> EnemyIterator = Enemy.enemies.iterator(); EnemyIterator.hasNext();) {
                Enemy enemy = EnemyIterator.next();

                for (Iterator<Projectile> ProjectileIterator = Projectile.projectiles.iterator(); ProjectileIterator.hasNext();) {
                    Projectile projectile = ProjectileIterator.next();

                    if (MathUtils.isColliding(enemy.boundingBox.getBounds(), projectile.boundingBox.getBounds())){
                        score += 100;
                        new Title("+100", MathUtils.getCenterOfPawn(enemy), Color.WHITE, new Font("Bahnschrift", Font.BOLD, 20), 1000, this);

                        SoundUtil.playSound(SoundUtil.getSoundRessource(spacewar.explosionSounds.get(MathUtils.random.nextInt(spacewar.explosionSounds.size())), spacewar.getClass()), spacewar.explosionSoundLevel);

                        explosionParticleGenerator.location = MathUtils.getCenterOfPawn(enemy);
                        explosionParticleGenerator.generate();

                        enemy.destroy();
                        projectile.destroy();
                    }
                }
            }
        }catch (ConcurrentModificationException e){}

        //----< player death if he touches an asteroid >----
        for (Iterator<Enemy> EnemyIterator = Enemy.enemies.iterator(); EnemyIterator.hasNext();) {
            Enemy enemy = EnemyIterator.next();

            g2d.setColor(Color.RED);
            Rectangle enemyRectangle = new Rectangle((int) enemy.location.x, (int) enemy.location.y, enemy.dimension.width, enemy.dimension.height);
            Rectangle playerRectangle = new Rectangle((int) player.location.x, (int) player.location.y, player.dimension.width, player.dimension.height);

            if (MathUtils.isColliding(enemyRectangle, playerRectangle)){
                SoundUtil.playSound(SoundUtil.getSoundRessource("sounds/death.wav", spacewar.getClass()), 0.6f);

                Enemy.enemies.forEach(enemy1 -> enemy1.destroy());
                Projectile.projectiles.forEach(projectile -> projectile.destroy());
                EnemyGenerator.cancelTimer();

                Spacewar.getInstance().switchScene(Spacewar.getInstance());
            }
        }
    }

    public static SpacewarGame getInstance() {
        return instance;
    }
}