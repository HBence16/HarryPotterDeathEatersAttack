package org.example.harrypotterthedeatheatersattack;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Boss extends Enemies {
    private final BossType bossType;
    private static final int SCREEN_WIDTH = 1920;
    private static final int SCREEN_HEIGHT = 1080;
    private static final Random random = new Random();
    private static final Set<BossType> spawnedBosses = new HashSet<>();
    private Timeline movementTimeline;
    private Timeline attackTimeline;
    private ImageView projectileImageView;
    private SpellManager spellManager;
    private Pane gamePane;

    public enum BossType {
        DRACO(50, 25, 200, "/images/enemies/bosses/draco.png"),
        LUCIUS(100, 50, 300, "/images/enemies/bosses/lucius.png"),
        TOM_RIDDLE(200, 75, 400, "/images/enemies/bosses/tom.png"),
        BELLATRIX(250, 100, 500, "/images/enemies/bosses/bellatrix.png"),
        VOLDEMORT(1000, 150, 1000, "/images/enemies/bosses/voldemort.png");

        final int health;
        final int damage;
        final int xp;
        final String imagePath;

        BossType(int health, int damage, int xp, String imagePath) {
            this.health = health;
            this.damage = damage;
            this.xp = xp;
            this.imagePath = imagePath;
        }
    }

    public Boss(ExperienceManager experienceManager, Pane gamePane, ImageView harryView,
                HealthManager healthManager, BossType type, SpellManager spellManager) {
        super(experienceManager);
        this.bossType = type;
        this.spellManager = spellManager;
        this.gamePane = gamePane;
        setupBoss(type, gamePane, harryView, healthManager);
    }

    private void setupBoss(BossType bossType, Pane gamePane, ImageView harryView, HealthManager healthManager) {
        Image bossImage = new Image(getClass().getResource(bossType.imagePath).toString());
        getImageView().setImage(bossImage);
        getImageView().setFitWidth(60);
        getImageView().setFitHeight(80);
        this.health = bossType.health;

        int edge = random.nextInt(4);
        double startX, startY;

        switch (edge) {
            case 0:
                startX = random.nextDouble() * SCREEN_WIDTH;
                startY = -getImageView().getFitHeight();
                break;
            case 1:
                startX = SCREEN_WIDTH;
                startY = random.nextDouble() * SCREEN_HEIGHT;
                break;
            case 2:
                startX = random.nextDouble() * SCREEN_WIDTH;
                startY = SCREEN_HEIGHT;
                break;
            default:
                startX = -getImageView().getFitWidth();
                startY = random.nextDouble() * SCREEN_HEIGHT;
                break;
        }

        getImageView().setLayoutX(startX);
        getImageView().setLayoutY(startY);

        initializeMovement(gamePane, harryView, healthManager);
        initializeAttack(gamePane, harryView, healthManager, bossType.damage);
    }

    private void initializeMovement(Pane gamePane, ImageView harryView, HealthManager healthManager) {
        double teleportInterval = switch (bossType) {
            case DRACO -> 7.0;
            case LUCIUS -> 6.0;
            case TOM_RIDDLE -> 5.0;
            case BELLATRIX -> 4.0;
            case VOLDEMORT -> 3.0;
        };

        movementTimeline = new Timeline(
                new KeyFrame(Duration.seconds(teleportInterval - 0.5), event -> {
                    ImageView flooPowder = new ImageView(new Image(getClass().getResource("/images/spells/flooPowder.png").toString()));
                    flooPowder.setFitWidth(80);
                    flooPowder.setFitHeight(80);
                    flooPowder.setLayoutX(getImageView().getLayoutX() - 10);
                    flooPowder.setLayoutY(getImageView().getLayoutY() - 10);
                    gamePane.getChildren().add(flooPowder);

                    Timeline removeFlooTimeline = new Timeline(
                            new KeyFrame(Duration.seconds(0.5), e -> gamePane.getChildren().remove(flooPowder))
                    );
                    removeFlooTimeline.play();
                }),
                new KeyFrame(Duration.seconds(teleportInterval), event -> {
                    double newX = random.nextDouble() * (SCREEN_WIDTH - getImageView().getFitWidth());
                    double newY = random.nextDouble() * (SCREEN_HEIGHT - getImageView().getFitHeight());
                    getImageView().setLayoutX(newX);
                    getImageView().setLayoutY(newY);
                })
        );

        movementTimeline.setCycleCount(Timeline.INDEFINITE);
        movementTimeline.play();
    }

    private void initializeAttack(Pane gamePane, ImageView harryView, HealthManager healthManager, int damage) {
        projectileImageView = new ImageView(new Image(getClass().getResource("/images/spells/attack.png").toString()));
        projectileImageView.setFitWidth(30);
        projectileImageView.setFitHeight(30);
        projectileImageView.setVisible(false);

        attackTimeline = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> launchAttack(harryView, healthManager, gamePane, damage))
        );
        attackTimeline.setCycleCount(Timeline.INDEFINITE);
        attackTimeline.play();
    }

    private Set<ImageView> activeProjectiles = new HashSet<>();

    private void launchAttack(ImageView harryView, HealthManager healthManager, Pane gamePane, int damage) {
        double startX = getImageView().getLayoutX();
        double startY = getImageView().getLayoutY();
        double targetX = harryView.getLayoutX();
        double targetY = harryView.getLayoutY();

        projectileImageView = new ImageView(new Image(getClass().getResource("/images/spells/attack.png").toString()));
        projectileImageView.setFitWidth(30);
        projectileImageView.setFitHeight(30);
        projectileImageView.setLayoutX(startX);
        projectileImageView.setLayoutY(startY);
        gamePane.getChildren().add(projectileImageView);
        activeProjectiles.add(projectileImageView);

        double deltaX = targetX - startX;
        double deltaY = targetY - startY;
        double angle = Math.atan2(deltaY, deltaX);

        double endX, endY;
        double slope = deltaY / deltaX;

        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            endX = deltaX > 0 ? SCREEN_WIDTH : 0;
            endY = startY + slope * (endX - startX);
        } else {
            endY = deltaY > 0 ? SCREEN_HEIGHT : 0;
            endX = startX + (endY - startY) / slope;
        }

        deltaX = endX - startX;
        deltaY = endY - startY;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        double speed = 5;
        double dx = (deltaX / distance) * speed;
        double dy = (deltaY / distance) * speed;

        final long startTime = System.currentTimeMillis();
        final long MAX_PROJECTILE_DURATION = 3000;

        Timeline projectileTimeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(16), e -> {
            if (System.currentTimeMillis() - startTime > MAX_PROJECTILE_DURATION) {
                cleanupProjectile(projectileImageView, gamePane, projectileTimeline);
                return;
            }

            projectileImageView.setLayoutX(projectileImageView.getLayoutX() + dx);
            projectileImageView.setLayoutY(projectileImageView.getLayoutY() + dy);

            if (projectileImageView.getBoundsInParent().intersects(harryView.getBoundsInParent())) {
                if (!spellManager.isProtegoActive()) {
                    healthManager.takeDamage(damage);
                }
                cleanupProjectile(projectileImageView, gamePane, projectileTimeline);
            }

            if (isProjectileOutOfBounds(projectileImageView)) {
                cleanupProjectile(projectileImageView, gamePane, projectileTimeline);
            }
        });

        projectileTimeline.getKeyFrames().add(keyFrame);
        projectileTimeline.setCycleCount(Timeline.INDEFINITE);
        projectileTimeline.play();
    }

    private void cleanupProjectile(ImageView projectile, Pane gamePane, Timeline timeline) {
        timeline.stop();
        gamePane.getChildren().remove(projectile);
        activeProjectiles.remove(projectile);
    }
    private static boolean currentBossSpawned = false;


    public static void setCurrentBossSpawned(boolean value) {
        currentBossSpawned = value;
    }

    public static boolean isCurrentBossSpawned() {
        return currentBossSpawned;
    }

    @Override
    public void die() {
        if (movementTimeline != null) {
            movementTimeline.stop();
        }
        if (attackTimeline != null) {
            attackTimeline.stop();
        }

        for (ImageView projectile : new ArrayList<>(activeProjectiles)) {
            gamePane.getChildren().remove(projectile);
        }
        activeProjectiles.clear();
        spawnedBosses.add(bossType);
        currentBossSpawned = false;
        super.die();
    }


    private boolean isProjectileOutOfBounds(ImageView projectile) {
        return projectile.getLayoutX() < -projectile.getFitWidth() ||
                projectile.getLayoutX() > SCREEN_WIDTH + projectile.getFitWidth() ||
                projectile.getLayoutY() < -projectile.getFitHeight() ||
                projectile.getLayoutY() > SCREEN_HEIGHT + projectile.getFitHeight();
    }

    public BossType getBossType() {
        return bossType;
    }

    private static long lastCheckTime = 0;

    public static boolean shouldSpawnBoss() {
        long currentTime = System.currentTimeMillis() / 1000;


        if (currentTime - lastCheckTime < 1) {
            return false;
        }
        lastCheckTime = currentTime;

        long elapsedMinutes = (currentTime - getGameStartTime()) / 60;

        if (elapsedMinutes >= 1 && !spawnedBosses.contains(BossType.DRACO) && spawnedBosses.isEmpty()) {
            return true;
        }
        if (elapsedMinutes >= 2 && !spawnedBosses.contains(BossType.LUCIUS) && spawnedBosses.contains(BossType.DRACO)) {
            return true;
        }
        if (elapsedMinutes >= 3 && !spawnedBosses.contains(BossType.TOM_RIDDLE) && spawnedBosses.contains(BossType.LUCIUS)) {
            return true;
        }
        if (elapsedMinutes >= 4 && !spawnedBosses.contains(BossType.BELLATRIX) && spawnedBosses.contains(BossType.TOM_RIDDLE)) {
            return true;
        }
        if (elapsedMinutes >= 5 && !spawnedBosses.contains(BossType.VOLDEMORT) && spawnedBosses.contains(BossType.BELLATRIX)) {
            return true;
        }
        return false;
    }

}
