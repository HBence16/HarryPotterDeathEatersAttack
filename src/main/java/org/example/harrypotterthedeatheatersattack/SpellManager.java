package org.example.harrypotterthedeatheatersattack;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SpellManager {
    private final Pane gamePane;
    private final ImageView harry;
    private final Set<ImageView> enemies;
    private final HealthManager healthManager;
    private final ExperienceManager experienceManager;

    private ImageView spellImg;
    private ImageView protegoImg;
    private ImageView leviosoBubbleImg;
    private ImageView healImg;
    private ImageView attackImg;

    private Timeline protegoFollow;
    private Timeline healFollow;

    private double spellVelocityX = 0;
    private double spellVelocityY = 0;
    private double directionX = 0;
    private double directionY = 0;

    private boolean protegoActive = false;
    private boolean healActive = false;

    private boolean isEpiskeyUnlocked = false;

    public boolean isEpiskeyUnlocked() {
        return isEpiskeyUnlocked;
    }

    public boolean isLeviosoUnlocked() {
        return isLeviosoUnlocked;
    }

    public boolean isDepulsoUnlocked() {
        return isDepulsoUnlocked;
    }

    public boolean isBombardaUnlocked() {
        return isBombardaUnlocked;
    }

    private boolean isLeviosoUnlocked = false;
    private boolean isDepulsoUnlocked = false;
    private boolean isBombardaUnlocked = false;

    private double healCooldownRemaining = 0;
    private double protegoCooldownRemaining = 0;
    private double leviosoCooldownRemaining = 0;
    private double depulsoCooldownRemaining = 0;
    private double bombardaCooldownRemaining = 0;

    private Timeline healCooldownTimer;
    private Timeline protegoCooldownTimer;
    private Timeline leviosoCooldownTimer;
    private Timeline depulsoCooldownTimer;
    private Timeline bombardaCooldownTimer;

    private final ImageView episkeyIcon;
    private final Label episkeyLabel;
    private final ImageView protegoIcon;
    private final Label protegoLabel;
    private final ImageView leviosoIcon;
    private final Label leviosoLabel;
    private final ImageView depulsoIcon;
    private final Label depulsoLabel;
    private final ImageView bombardaIcon;
    private final Label bombardaLabel;

    private Set<ImageView> damagedEnemies = new HashSet<>();
    private boolean explosionActive = false;


    public SpellManager(Pane gamePane, ImageView harry, Set<ImageView> enemies,
                        HealthManager healthManager, ExperienceManager experienceManager,
                        ImageView episkeyIcon, Label episkeyLabel,
                        ImageView protegoIcon, Label protegoLabel,
                        ImageView leviosoIcon, Label leviosoLabel,
                        ImageView depulsoIcon, Label depulsoLabel,
                        ImageView bombardaIcon, Label bombardaLabel) {
        this.gamePane = gamePane;
        this.harry = harry;
        this.enemies = enemies;
        this.healthManager = healthManager;
        this.experienceManager = experienceManager;
        this.episkeyIcon = episkeyIcon;
        this.episkeyLabel = episkeyLabel;
        this.protegoIcon = protegoIcon;
        this.protegoLabel = protegoLabel;
        this.leviosoIcon = leviosoIcon;
        this.leviosoLabel = leviosoLabel;
        this.depulsoIcon = depulsoIcon;
        this.depulsoLabel = depulsoLabel;
        this.bombardaIcon = bombardaIcon;
        this.bombardaLabel = bombardaLabel;
    }

    public void setDirection(double x, double y) {
        this.directionX = x;
        this.directionY = y;
    }


    public void unlockEpiskey() {
        isEpiskeyUnlocked = true;
    }

    public void unlockLevioso() {
        isLeviosoUnlocked = true;
    }

    public void unlockDepulso() {
        isDepulsoUnlocked = true;
    }

    public void unlockBombarda() {
        isBombardaUnlocked = true;
    }

    public void castHealSpell() {
        if (!isEpiskeyUnlocked) {
            return;
        }
        if (healCooldownRemaining == 0) {
            healthManager.heal(experienceManager.getHealAmount());
            healImg = new ImageView(new Image(getClass().getResource("/images/spells/heal.png").toString()));
            healImg.setFitWidth(140);
            healImg.setFitHeight(100);
            healImg.setLayoutX(harry.getLayoutX() - 25);
            healImg.setLayoutY(harry.getLayoutY());
            gamePane.getChildren().add(healImg);
            healActive = true;

            setupHealFollow();
            healCooldownRemaining = experienceManager.getHealCooldown();
            startHealCooldown();
        }
    }

    public void castProtegoSpell() {
        if (protegoCooldownRemaining == 0) {
            protegoImg = new ImageView(new Image(getClass().getResource("/images/spells/protego.png").toString()));
            protegoImg.setFitWidth(140);
            protegoImg.setFitHeight(100);
            protegoImg.setLayoutX(harry.getLayoutX());
            protegoImg.setLayoutY(harry.getLayoutY());
            gamePane.getChildren().add(protegoImg);
            protegoActive = true;

            setupProtegoFollow();
            protegoCooldownRemaining = experienceManager.getProtegoCooldown();
            startProtegoCooldown();
        }
    }

    public void castLeviosoSpell() {
        if (!isLeviosoUnlocked) {
           return;
        }
        if (leviosoCooldownRemaining == 0) {
            Image spellImage = new Image(getClass().getResource("/images/spells/leviosospell.png").toString());
            spellImg = new ImageView(spellImage);
            spellImg.setFitWidth(50);
            spellImg.setFitHeight(20);
            spellImg.setLayoutX(harry.getLayoutX() + harry.getFitWidth()/2);
            spellImg.setLayoutY(harry.getLayoutY() + harry.getFitHeight()/2);


            double magnitude = Math.sqrt(directionX * directionX + directionY * directionY);
            if (magnitude > 0) {
                spellVelocityX = (directionX / magnitude) * 10;
                spellVelocityY = (directionY / magnitude) * 10;
            } else {

                spellVelocityX = 10;
                spellVelocityY = 0;
            }

            gamePane.getChildren().add(spellImg);
            leviosoCooldownRemaining = experienceManager.getLeviosoCooldown();


            Timeline spellMovement = new Timeline(
                    new KeyFrame(Duration.millis(16), e -> moveLeviosoSpell())
            );
            spellMovement.setCycleCount(Timeline.INDEFINITE);
            spellMovement.play();

            startLeviosoCooldown();
        }
    }


    public void castDepulsoSpell() {
        if (!isDepulsoUnlocked) {
            return;
        }
        if (depulsoCooldownRemaining == 0) {
            double harryX = harry.getLayoutX();
            double harryY = harry.getLayoutY();
            double radius = experienceManager.getDepulsoRadius();

            ImageView depulsoImg = new ImageView(new Image(getClass().getResource("/images/spells/depulso.png").toString()));
            depulsoImg.setFitWidth(100);
            depulsoImg.setFitHeight(100);
            depulsoImg.setLayoutX(harryX);
            depulsoImg.setLayoutY(harryY);
            gamePane.getChildren().add(depulsoImg);

            Timeline growDepulso = new Timeline(
                    new KeyFrame(Duration.millis(50),
                            new KeyValue(depulsoImg.fitWidthProperty(), radius * 2),
                            new KeyValue(depulsoImg.fitHeightProperty(), radius * 2),
                            new KeyValue(depulsoImg.layoutXProperty(), harryX + 40 - radius),
                            new KeyValue(depulsoImg.layoutYProperty(), harryY + 50 - radius))
            );
            growDepulso.setCycleCount(5);
            growDepulso.setOnFinished(e -> gamePane.getChildren().remove(depulsoImg));
            growDepulso.play();

            applyDepulsoEffect(harryX, harryY, radius);
            depulsoCooldownRemaining = experienceManager.getDepulsoCooldown();
            startDepulsoCooldown();
        }
    }

    public void castBombardaSpell() {
        if (!isBombardaUnlocked) {
            return;
        }
        if (bombardaCooldownRemaining == 0) {
            explosionActive = false;
            double spellSize = 20;
            double spellSpeed = 4;
            double explosionRadius = 150;

            ImageView explosionSpell = new ImageView(new Image(getClass().getResource("/images/spells/bombardaSpell.png").toString()));
            explosionSpell.setFitWidth(spellSize);
            explosionSpell.setFitHeight(spellSize);
            explosionSpell.setLayoutX(harry.getLayoutX());
            explosionSpell.setLayoutY(harry.getLayoutY());
            gamePane.getChildren().add(explosionSpell);

            setupBombardaMovement(explosionSpell, spellSpeed, explosionRadius);
            bombardaCooldownRemaining = experienceManager.getBombardaCooldown();
            startBombardaCooldown();
        }
    }

    public void moveLeviosoSpell() {
        if (spellImg != null && spellImg.getParent() != null) {
            double newX = spellImg.getLayoutX() + spellVelocityX;
            double newY = spellImg.getLayoutY() + spellVelocityY;

            spellImg.setLayoutX(newX);
            spellImg.setLayoutY(newY);

            checkLeviosoCollision();
            checkLeviosoOutOfBounds();
        }
    }

    private void setupHealFollow() {
        if (healFollow == null) {
            healFollow = new Timeline(new KeyFrame(Duration.millis(16), event -> {
                healImg.setLayoutX(harry.getLayoutX() - 25);
                healImg.setLayoutY(harry.getLayoutY() - 10);
            }));
            healFollow.setCycleCount(Timeline.INDEFINITE);
            healFollow.play();
        }

        Timeline healTimer = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
            gamePane.getChildren().remove(healImg);
            healActive = false;
            healFollow.stop();
            healFollow = null;
        }));
        healTimer.setCycleCount(1);
        healTimer.play();
    }

    private void setupProtegoFollow() {
        if (protegoFollow == null) {
            protegoFollow = new Timeline(new KeyFrame(Duration.millis(16), event -> {
                protegoImg.setLayoutX(harry.getLayoutX() - 25);
                protegoImg.setLayoutY(harry.getLayoutY() - 10);
            }));
            protegoFollow.setCycleCount(Timeline.INDEFINITE);
            protegoFollow.play();
        }

        Timeline protegoTimer = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            gamePane.getChildren().remove(protegoImg);
            protegoActive = false;
            protegoFollow.stop();
            protegoFollow = null;
        }));
        protegoTimer.setCycleCount(1);
        protegoTimer.play();
    }

    private void applyDepulsoEffect(double harryX, double harryY, double radius) {
        for (ImageView enemy : new HashSet<>(enemies)) {
            double enemyX = enemy.getLayoutX();
            double enemyY = enemy.getLayoutY();
            double dx = enemyX - harryX;
            double dy = enemyY - harryY;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < radius) {
                double moveDistance = radius - distance + 50;
                double moveX = dx / distance * moveDistance;
                double moveY = dy / distance * moveDistance;

                Timeline enemyEscape = new Timeline(
                        new KeyFrame(Duration.millis(50),
                                new KeyValue(enemy.layoutXProperty(), enemyX + moveX),
                                new KeyValue(enemy.layoutYProperty(), enemyY + moveY))
                );
                enemyEscape.setCycleCount(5);
                enemyEscape.play();
            }
        }
    }

    private void setupBombardaMovement(ImageView explosionSpell, double spellSpeed, double explosionRadius) {
        double magnitude = Math.sqrt(directionX * directionX + directionY * directionY);
        double spellVelocityX = magnitude > 0 ? (directionX / magnitude) * spellSpeed : 0;
        double spellVelocityY = magnitude > 0 ? (directionY / magnitude) * spellSpeed : -spellSpeed;

        Timeline moveSpell = new Timeline(new KeyFrame(Duration.millis(16), event -> {
            explosionSpell.setLayoutX(explosionSpell.getLayoutX() + spellVelocityX);
            explosionSpell.setLayoutY(explosionSpell.getLayoutY() + spellVelocityY);

            checkBombardaCollision(explosionSpell, explosionRadius);
            checkBombardaOutOfBounds(explosionSpell);
        }));

        moveSpell.setCycleCount(Timeline.INDEFINITE);
        moveSpell.play();
    }

    private void checkLeviosoCollision() {
        for (ImageView enemy : new HashSet<>(enemies)) {
            if (spellImg.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                leviosoBubbleImg = new ImageView(new Image(getClass().getResource("/images/spells/leviosobubble.png").toString()));
                leviosoBubbleImg.setFitWidth(150);
                leviosoBubbleImg.setFitHeight(120);
                leviosoBubbleImg.setLayoutX(enemy.getLayoutX() - 50);
                leviosoBubbleImg.setLayoutY(enemy.getLayoutY() - 20);
                gamePane.getChildren().add(leviosoBubbleImg);

                Enemies enemies = (Enemies) enemy.getUserData();
                if (enemies != null) {
                    enemies.stopMovement();
                }

                Timeline pauseEnemyTimeline = new Timeline(new KeyFrame(Duration.seconds(experienceManager.getLeviosoAirTime()), event -> {
                    if (enemies != null) {
                        enemies.startMovement();
                        gamePane.getChildren().remove(leviosoBubbleImg);
                    }
                }));
                pauseEnemyTimeline.setCycleCount(1);
                pauseEnemyTimeline.play();

                gamePane.getChildren().remove(spellImg);
                spellImg = null;
                break;
            }
        }
    }

    private void checkLeviosoOutOfBounds() {
        if (spellImg != null && (spellImg.getLayoutX() < 0 || spellImg.getLayoutX() > gamePane.getWidth()
                || spellImg.getLayoutY() < 0 || spellImg.getLayoutY() > gamePane.getHeight())) {
            gamePane.getChildren().remove(spellImg);
            spellImg = null;
        }
    }

    private void checkBombardaCollision(ImageView explosionSpell, double explosionRadius) {
        if (explosionSpell.getParent() != null) {
            for (ImageView enemy : new ArrayList<>(enemies)) {
                if (explosionSpell.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                    gamePane.getChildren().remove(explosionSpell);
                    createBombardaExplosion(explosionSpell, explosionRadius);
                    return;
                }
            }
        }
    }


    private void checkBombardaOutOfBounds(ImageView explosionSpell) {
        if (explosionSpell.getLayoutX() < 0 || explosionSpell.getLayoutX() > gamePane.getWidth()
                || explosionSpell.getLayoutY() < 0 || explosionSpell.getLayoutY() > gamePane.getHeight()) {
            gamePane.getChildren().remove(explosionSpell);
        }
    }

    private void createBombardaExplosion(ImageView explosionSpell, double explosionRadius) {
        ImageView explosionEffect = new ImageView(new Image(getClass().getResource("/images/spells/bombardaCircle.png").toString()));
        explosionEffect.setFitWidth(10);
        explosionEffect.setFitHeight(10);
        explosionEffect.setLayoutX(explosionSpell.getLayoutX() - 5);
        explosionEffect.setLayoutY(explosionSpell.getLayoutY() - 5);
        gamePane.getChildren().add(explosionEffect);

        Timeline expandExplosion = new Timeline(
                new KeyFrame(Duration.millis(50),
                        new KeyValue(explosionEffect.fitWidthProperty(), explosionRadius * 2),
                        new KeyValue(explosionEffect.fitHeightProperty(), explosionRadius * 2),
                        new KeyValue(explosionEffect.layoutXProperty(), explosionSpell.getLayoutX() - explosionRadius),
                        new KeyValue(explosionEffect.layoutYProperty(), explosionSpell.getLayoutY() - explosionRadius)
                )
        );

        expandExplosion.setOnFinished(e -> {
            applyExplosionDamage(explosionEffect, explosionRadius);
            gamePane.getChildren().remove(explosionEffect);
        });

        expandExplosion.play();
    }

    private void applyExplosionDamage(ImageView explosionEffect, double explosionRadius) {
        double explosionCenterX = explosionEffect.getLayoutX() + explosionEffect.getFitWidth() / 2;
        double explosionCenterY = explosionEffect.getLayoutY() + explosionEffect.getFitHeight() / 2;

        for (ImageView targetEnemy : new ArrayList<>(enemies)) {
            double enemyCenterX = targetEnemy.getLayoutX() + targetEnemy.getFitWidth() / 2;
            double enemyCenterY = targetEnemy.getLayoutY() + targetEnemy.getFitHeight() / 2;

            double distance = Math.sqrt(
                    Math.pow(enemyCenterX - explosionCenterX, 2) +
                            Math.pow(enemyCenterY - explosionCenterY, 2)
            );

            if (distance <= explosionRadius) {
                Enemies enemyData = (Enemies) targetEnemy.getUserData();
                if (enemyData != null) {
                    enemyData.decreaseHealth(experienceManager.getBombardaDamage());
                }
            }
        }
    }

    private void startHealCooldown() {
        if (healCooldownTimer != null) {
            healCooldownTimer.stop();
        }

        healCooldownTimer = new Timeline(new KeyFrame(Duration.seconds(0.05), event -> {
            if (healCooldownRemaining > 0) {
                healCooldownRemaining = Math.max(0, healCooldownRemaining - 0.05);
                updateCooldownUI(episkeyIcon, episkeyLabel, healCooldownRemaining,
                        experienceManager.getHealCooldown(), "E");
            }
        }));
        healCooldownTimer.setCycleCount(Timeline.INDEFINITE);
        healCooldownTimer.play();
    }

    private void startProtegoCooldown() {
        if (protegoCooldownTimer != null) {
            protegoCooldownTimer.stop();
        }

        protegoCooldownTimer = new Timeline(new KeyFrame(Duration.seconds(0.05), event -> {
            if (protegoCooldownRemaining > 0) {
                protegoCooldownRemaining = Math.max(0, protegoCooldownRemaining - 0.05);
                updateCooldownUI(protegoIcon, protegoLabel, protegoCooldownRemaining,
                        experienceManager.getProtegoCooldown(), "Q");
            }
        }));
        protegoCooldownTimer.setCycleCount(Timeline.INDEFINITE);
        protegoCooldownTimer.play();
    }

    private void startLeviosoCooldown() {
        if (leviosoCooldownTimer != null) {
            leviosoCooldownTimer.stop();
        }

        leviosoCooldownTimer = new Timeline(new KeyFrame(Duration.seconds(0.05), event -> {
            if (leviosoCooldownRemaining > 0) {
                leviosoCooldownRemaining = Math.max(0, leviosoCooldownRemaining - 0.05);
                updateCooldownUI(leviosoIcon, leviosoLabel, leviosoCooldownRemaining,
                        experienceManager.getLeviosoCooldown(), "J");
            }
        }));
        leviosoCooldownTimer.setCycleCount(Timeline.INDEFINITE);
        leviosoCooldownTimer.play();
    }

    private void startDepulsoCooldown() {
        if (depulsoCooldownTimer != null) {
            depulsoCooldownTimer.stop();
        }

        depulsoCooldownTimer = new Timeline(new KeyFrame(Duration.seconds(0.05), event -> {
            if (depulsoCooldownRemaining > 0) {
                depulsoCooldownRemaining = Math.max(0, depulsoCooldownRemaining - 0.05);
                updateCooldownUI(depulsoIcon, depulsoLabel, depulsoCooldownRemaining,
                        experienceManager.getDepulsoCooldown(), "K");
            }
        }));
        depulsoCooldownTimer.setCycleCount(Timeline.INDEFINITE);
        depulsoCooldownTimer.play();
    }

    private void startBombardaCooldown() {
        if (bombardaCooldownTimer != null) {
            bombardaCooldownTimer.stop();
        }

        bombardaCooldownTimer = new Timeline(new KeyFrame(Duration.seconds(0.05), event -> {
            if (bombardaCooldownRemaining > 0) {
                bombardaCooldownRemaining = Math.max(0, bombardaCooldownRemaining - 0.05);
                updateCooldownUI(bombardaIcon, bombardaLabel, bombardaCooldownRemaining,
                        experienceManager.getBombardaCooldown(), "L");
            }
        }));
        bombardaCooldownTimer.setCycleCount(Timeline.INDEFINITE);
        bombardaCooldownTimer.play();
    }


    private void updateCooldownUI(ImageView icon, Label label, double remaining,
                                  double maxCooldown, String readyText) {
        if (remaining > 0) {
            double opacity = 0.25 + (1 - 0.25) * (1 - remaining / maxCooldown);
            icon.setOpacity(opacity);
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            label.setText(decimalFormat.format(remaining));
        } else {
            label.setText(readyText);
        }
    }

    public boolean isProtegoActive() {
        return protegoActive;
    }


    public void stopAllTimelines() {
        if (healCooldownTimer != null) healCooldownTimer.stop();
        if (protegoCooldownTimer != null) protegoCooldownTimer.stop();
        if (leviosoCooldownTimer != null) leviosoCooldownTimer.stop();
        if (depulsoCooldownTimer != null) depulsoCooldownTimer.stop();
        if (bombardaCooldownTimer != null) bombardaCooldownTimer.stop();
        if (protegoFollow != null) protegoFollow.stop();
        if (healFollow != null) healFollow.stop();
    }
}
