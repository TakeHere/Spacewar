package fr.takehere.spacewar.enemies;

import fr.takehere.ethereal.display.GameWindow;
import fr.takehere.ethereal.objects.Title;
import fr.takehere.ethereal.utils.ImageUtil;
import fr.takehere.ethereal.utils.MathUtils;
import fr.takehere.ethereal.utils.RessourcesManager;
import fr.takehere.ethereal.utils.Vector2;
import fr.takehere.spacewar.Spacewar;
import fr.takehere.spacewar.SpacewarGame;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class EnemyGenerator {

    float circleRadius;
    float offsetRadius = 100;

    private static Timer spawnTimer;
    public int time = 1500;

    public EnemyGenerator() {
        spawnTimer = new Timer();
        GameWindow gameWindow = Spacewar.getInstance().gameWindow;

        float width = gameWindow.getWidth()/2;
        float height = gameWindow.getHeight()/2;

        circleRadius = (float) (Math.sqrt(Math.pow(width,2) + Math.pow(height,2)) + offsetRadius);

        spawnTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (time >= 50){
                    time -= 15;
                }

                SpacewarGame.getInstance().score += 10;

                generateEnemy();
            }
        }, 500,time);
    }

    public static void cancelTimer(){
        spawnTimer.cancel();
    }

    int minEnemySpeed = 6;
    int manEnemySpeed = 10;

    private void generateEnemy(){
        GameWindow gameWindow = Spacewar.getInstance().gameWindow;
        int angle = MathUtils.randomNumberBetween(0,360);

        Vector2 randomLocation = new Vector2(
                Math.sin(Math.toRadians(angle)) * circleRadius,
                Math.cos(Math.toRadians(angle)) * circleRadius * -1
        ).add(new Vector2(gameWindow.getWidth()/2, gameWindow.getHeight()/2));

        Enemy enemy = new Enemy(randomLocation, new Dimension(100,100), RessourcesManager.getImage("asteroid"), "enemy", SpacewarGame.getInstance());
        enemy.rotation = MathUtils.randomNumberBetween(0,360);
        enemy.velocity = SpacewarGame.getInstance().player.location.subtract(enemy.location).normalize().multiply(MathUtils.randomNumberBetween(minEnemySpeed, manEnemySpeed));
    }
}
