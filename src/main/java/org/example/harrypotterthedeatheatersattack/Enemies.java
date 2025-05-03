package org.example.harrypotterthedeatheatersattack;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Random;

public class Enemies {
    private long lastAttackTime;
    private ImageView imageView;
    public int health;
    private int damage;
    private boolean moving;
    private final Random random = new Random();
    private final int screenWidth = 1920;
    private final int screenHeight = 1080;
    private static long gameStartTime = System.currentTimeMillis() / 1000;

    private final String[] imagePaths = {
            "/images/enemies/spider.png",
            "/images/enemies/snake.png",
            "/images/enemies/werewolf.png",
            "/images/enemies/dementor.png"
    };

    private int currentImageIndex;
    private static int currentTypeIndex = 0;
    private static long elapsedTime = 0;
    private int xp;

    private ExperienceManager experienceManager;

    public Enemies(ExperienceManager experienceManager) {
        this.experienceManager = experienceManager;
        this.moving = true;

        long currentTime = System.currentTimeMillis() / 1000;
        elapsedTime = currentTime - gameStartTime;

        if (elapsedTime < 60) {
            currentTypeIndex = 0;
        } else if (elapsedTime < 120) {
            currentTypeIndex = random.nextInt(2);
        } else if (elapsedTime < 180) {
            currentTypeIndex = random.nextInt(3);
        } else {
            currentTypeIndex = random.nextInt(4);
        }

        setEnemyStats(currentTypeIndex);
        currentImageIndex = currentTypeIndex;

        Image enemyImage = new Image(getClass().getResource(imagePaths[currentImageIndex]).toString());
        this.imageView = new ImageView(enemyImage);

        imageView.setFitWidth(50);
        imageView.setFitHeight(70);

        int edge = random.nextInt(4);
        double spawnX = 0;
        double spawnY = 0;

        switch (edge) {
            case 0:
                spawnX = random.nextInt(screenWidth);
                spawnY = 0;
                break;
            case 1:
                spawnX = random.nextInt(screenWidth);
                spawnY = screenHeight - 50;
                break;
            case 2:
                spawnX = 0;
                spawnY = random.nextInt(screenHeight);
                break;
            case 3:
                spawnX = screenWidth - 50;
                spawnY = random.nextInt(screenHeight);
                break;
        }

        imageView.setLayoutX(spawnX);
        imageView.setLayoutY(spawnY);
        imageView.setUserData(this);
    }


    public ImageView getImageView() {
        return imageView;
    }

    public void decreaseHealth(double amount) {
        health -= amount;
        if (health <= 0) {
            die();
        }
    }

    public long getLastAttackTime() {
        return lastAttackTime;
    }

    public void setLastAttackTime(long lastAttackTime) {
        this.lastAttackTime = lastAttackTime;
    }

    public void die() {
        moving = false;
        awardXP();
        remove(imageView);
    }

    private void awardXP() {
        experienceManager.gainExperience(xp);
    }

    public void startMovement() {
        moving = true;
    }

    public void stopMovement() {
        moving = false;
    }

    public boolean isMoving() {
        return moving;
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }
    public static long getGameStartTime() {
        return gameStartTime;
    }


    public void remove(ImageView enemy) {
        imageView.setVisible(false);
    }

    private void setEnemyStats(int index) {
        switch (index) {
            case 0: // Spider
                health = 5;
                damage = 5;
                xp = 10;
                break;
            case 1: // Snake
                health = 15;
                damage = 15;
                xp = 25;
                break;
            case 2: // Werewolf
                health = 30;
                damage = 30;
                xp = 50;
                break;
            case 3: // Dementor
                health = 50;
                damage = 50;
                xp = 100;
                break;
        }
    }
}
