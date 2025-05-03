package org.example.harrypotterthedeatheatersattack;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.*;

public class Controller {
    @FXML private ImageView harry;
    @FXML private Pane gamePane;
    @FXML private ProgressBar healthbar;
    @FXML private Label hpLabel;
    @FXML private ProgressBar experiencebar;
    @FXML private Label xpLabel;
    @FXML private Label levelLabel;
    @FXML private Label timer;
    @FXML private Label endLabel;
    @FXML private ImageView depulsoIcon;
    @FXML private Label depulsoLabel;
    @FXML private ImageView leviosoIcon;
    @FXML private Label leviosoLabel;
    @FXML private ImageView protegoIcon;
    @FXML private Label protegoLabel;
    @FXML private ImageView episkeyIcon;
    @FXML private Label episkeyLabel;
    @FXML private ImageView bombardaIcon;
    @FXML private Label bombardaLabel;

    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final Random random = new Random();
    private final Set<ImageView> enemies = new HashSet<>();
    private HealthManager healthManager;
    private ExperienceManager experienceManager;
    private SpellManager spellManager;

    private Timeline spawnTimeline;
    private Timeline movementTimeline;
    private Timeline attackTimeline;
    private Timeline bossCheckTimeline;

    private boolean gameOver = false;
    private double moveAmount = 2.0;
    private double directionX = 0;
    private double directionY = 0;

    private double harryDamage = 10;
    private double harryAttackspeed = 3;
    private double harryHealth = 100;
    private double xp = 100;
    private double harryMaxHealth = 100;
    private final Set<Boss.BossType> spawnedBosses = new HashSet<>();
    private final Set<Boss> activeBosses = new HashSet<>();

    @FXML
    public void initialize() {
        ImageView background = new ImageView(new Image(getClass().getResource("/images/background.png").toString()));
        background.setFitWidth(1920);
        background.setFitHeight(1080);
        gamePane.getChildren().add(0, background);

        harry.setImage(new Image(getClass().getResource("/images/harry.png").toString()));
        harry.setFitHeight(96);
        harry.setFitWidth(88);

        bombardaIcon.setImage(new Image(getClass().getResource("/images/spells/bombardaIcon.png").toString()));
        bombardaIcon.setFitHeight(81);
        bombardaIcon.setFitWidth(77);

        depulsoIcon.setImage(new Image(getClass().getResource("/images/spells/depulsoIcon.png").toString()));
        depulsoIcon.setFitHeight(81);
        depulsoIcon.setFitWidth(77);

        leviosoIcon.setImage(new Image(getClass().getResource("/images/spells/leviosoIcon.png").toString()));
        leviosoIcon.setFitHeight(81);
        leviosoIcon.setFitWidth(77);

        episkeyIcon.setImage(new Image(getClass().getResource("/images/spells/episkeyIcon.png").toString()));
        episkeyIcon.setFitHeight(81);
        episkeyIcon.setFitWidth(77);

        protegoIcon.setImage(new Image(getClass().getResource("/images/spells/protegoIcon.png").toString()));
        protegoIcon.setFitHeight(81);
        protegoIcon.setFitWidth(77);

        timer.setUserData(new int[]{0, 0});
        startTimer(timer);

        experienceManager = new ExperienceManager(0, xp, experiencebar, levelLabel, harryDamage, harryAttackspeed,
                harryHealth, harryMaxHealth, 15, 10, 10, 10, 15, 30, 0.5, 500, 25);
        healthManager = new HealthManager(experienceManager, healthbar, endLabel);
        healthbar.setStyle("-fx-accent: green;");

        spellManager = new SpellManager(gamePane, harry, enemies, healthManager, experienceManager,
                episkeyIcon, episkeyLabel, protegoIcon, protegoLabel, leviosoIcon, leviosoLabel,
                depulsoIcon, depulsoLabel, bombardaIcon, bombardaLabel);
        experienceManager.setSpellManager(spellManager);

        setupTimelines();
        setupInitialUI();
        harry.requestFocus();
    }

    private void setupTimelines() {
        spawnTimeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> spawnEnemy()));
        spawnTimeline.setCycleCount(Timeline.INDEFINITE);
        spawnTimeline.play();

        movementTimeline = new Timeline(new KeyFrame(Duration.millis(16), event -> moveEnemies()));
        movementTimeline.setCycleCount(Timeline.INDEFINITE);
        movementTimeline.play();

        attackTimeline = new Timeline(new KeyFrame(Duration.seconds(experienceManager.getAttackSpeed()), event -> attack()));
        attackTimeline.setCycleCount(Timeline.INDEFINITE);
        attackTimeline.play();

        bossCheckTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            if (Boss.shouldSpawnBoss()) {
                spawnBoss();
            }
        }));
        bossCheckTimeline.setCycleCount(Timeline.INDEFINITE);
        bossCheckTimeline.play();
    }

    private void setupInitialUI() {
        episkeyIcon.setOpacity(0);
        episkeyLabel.setOpacity(0);
        leviosoIcon.setOpacity(0);
        leviosoLabel.setOpacity(0);
        depulsoIcon.setOpacity(0);
        depulsoLabel.setOpacity(0);
        bombardaIcon.setOpacity(0);
        bombardaLabel.setOpacity(0);
    }

    private void startTimer(Label timerLabel) {
        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            int[] time = (int[]) timerLabel.getUserData();
            time[1]++;
            if (time[1] >= 60) {
                time[0]++;
                time[1] = 0;
            }
            timerLabel.setText(String.format("%02d:%02d", time[0], time[1]));
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    @FXML
    void KeyPressed(KeyEvent event) {
        if (!gameOver) {
            pressedKeys.add(event.getCode());
            updateDirection();
            spellManager.setDirection(directionX, directionY);
        }
    }

    @FXML
    void KeyReleased(KeyEvent event) {
        if (!gameOver) {
            pressedKeys.remove(event.getCode());
            switch (event.getCode()) {
                case E -> {
                    //if (spellManager.isEpiskeyUnlocked()) {
                        spellManager.castHealSpell();
                    //}
                }
                case Q -> spellManager.castProtegoSpell();
                case J -> {
                    //if (spellManager.isLeviosoUnlocked()) {
                        spellManager.castLeviosoSpell();
                    //}
                }
                case K -> {
                //    if (spellManager.isDepulsoUnlocked()) {
                        spellManager.castDepulsoSpell();
                 //   }
                }
                case L -> {
                   // if (spellManager.isBombardaUnlocked()) {
                        spellManager.castBombardaSpell();
                    //}
                }
            }
        }
    }

    private void updateDirection() {
        if (pressedKeys.contains(KeyCode.W)) {
            directionY = -1;
        } else if (pressedKeys.contains(KeyCode.S)) {
            directionY = 1;
        } else {
            directionY = 0;
        }

        if (pressedKeys.contains(KeyCode.A)) {
            directionX = -1;
        } else if (pressedKeys.contains(KeyCode.D)) {
            directionX = 1;
        } else {
            directionX = 0;
        }
    }

    public void updateHarryPosition() {
        if (gameOver) return;

        if (pressedKeys.contains(KeyCode.W)) harry.setLayoutY(harry.getLayoutY() - moveAmount);
        if (pressedKeys.contains(KeyCode.S)) harry.setLayoutY(harry.getLayoutY() + moveAmount);
        if (pressedKeys.contains(KeyCode.A)) harry.setLayoutX(harry.getLayoutX() - moveAmount);
        if (pressedKeys.contains(KeyCode.D)) harry.setLayoutX(harry.getLayoutX() + moveAmount);
    }

    private void spawnEnemy() {
        if (!gameOver) {
            Enemies randomEnemy = new Enemies(experienceManager);
            ImageView enemy = randomEnemy.getImageView();
            gamePane.getChildren().add(enemy);
            enemies.add(enemy);
        }
    }

    private void spawnBoss() {
        if (Boss.isCurrentBossSpawned()) {
            return;
        }

        Boss.BossType nextBossType = determineNextBossType();
        Boss boss = new Boss(experienceManager, gamePane, harry, healthManager, nextBossType, spellManager);
        ImageView bossView = boss.getImageView();
        gamePane.getChildren().add(bossView);
        enemies.add(bossView);
        activeBosses.add(boss);
        Boss.setCurrentBossSpawned(true);
    }


    private Boss.BossType determineNextBossType() {
        if (!spawnedBosses.contains(Boss.BossType.DRACO)) return Boss.BossType.DRACO;
        if (!spawnedBosses.contains(Boss.BossType.LUCIUS)) return Boss.BossType.LUCIUS;
        if (!spawnedBosses.contains(Boss.BossType.TOM_RIDDLE)) return Boss.BossType.TOM_RIDDLE;
        if (!spawnedBosses.contains(Boss.BossType.BELLATRIX)) return Boss.BossType.BELLATRIX;
        return Boss.BossType.VOLDEMORT;
    }

    private void moveEnemies() {
        if (gameOver) return;

        for (ImageView enemy : enemies) {
            Enemies enemyData = (Enemies) enemy.getUserData();
            if (enemyData != null && !enemyData.isMoving()) continue;

            double enemyX = enemy.getLayoutX();
            double enemyY = enemy.getLayoutY();
            double harryX = harry.getLayoutX();
            double harryY = harry.getLayoutY();

            double dx = harryX - enemyX;
            double dy = harryY - enemyY;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (enemyData != null && enemyData.getLastAttackTime() + 1 <= System.currentTimeMillis() / 1000) {
                if (distance < 30 && !spellManager.isProtegoActive()) {
                    healthManager.takeDamage(enemyData.getDamage());
                    enemyData.setLastAttackTime(System.currentTimeMillis() / 1000);
                }
            }

            if (distance > 0) {
                enemy.setLayoutX(enemyX + (dx / distance));
                enemy.setLayoutY(enemyY + (dy / distance));
            }
        }

        checkGameOver();
        updateHealthUI();
    }

    private void attack() {
        if (gameOver || enemies.isEmpty()) return;

        ImageView closestEnemy = findClosestEnemy();
        if (closestEnemy != null) {
            performAttack(closestEnemy);
        }

        updateExperienceUI();
    }

    private ImageView findClosestEnemy() {
        ImageView closestEnemy = null;
        double closestDistance = Double.MAX_VALUE;
        double harryX = harry.getLayoutX();
        double harryY = harry.getLayoutY();

        for (ImageView enemy : enemies) {
            Enemies enemyData = (Enemies) enemy.getUserData();
            if (enemyData == null || enemyData.getHealth() <= 0) continue;

            double distance = calculateDistance(harryX, harryY, enemy.getLayoutX(), enemy.getLayoutY());
            if (distance < closestDistance) {
                closestDistance = distance;
                closestEnemy = enemy;
            }
        }

        return closestEnemy;
    }

    private double calculateDistance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void performAttack(ImageView enemy) {
        Enemies enemyData = (Enemies) enemy.getUserData();
        if (enemyData != null && enemyData.getHealth() > 0) {
            double damageAmount = experienceManager.getDamage();
            enemyData.decreaseHealth(damageAmount);

            ImageView attackImg = new ImageView(new Image(getClass().getResource("/images/spells/attack.png").toString()));
            attackImg.setFitWidth(14);
            attackImg.setFitHeight(10);
            attackImg.setLayoutX(enemy.getLayoutX() + 20);
            attackImg.setLayoutY(enemy.getLayoutY() + 20);
            gamePane.getChildren().add(attackImg);

            Timeline attackEnemyTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                gamePane.getChildren().remove(attackImg);
                if (enemyData.getHealth() <= 0) {
                    handleEnemyDeath(enemy, enemyData);
                }
            }));
            attackEnemyTimeline.setCycleCount(1);
            attackEnemyTimeline.play();
        }
    }

    private void handleEnemyDeath(ImageView enemy, Enemies enemyData) {
        if (enemyData instanceof Boss) {
            Boss boss = (Boss) enemyData;
            Boss.BossType bossType = boss.getBossType();
            if (bossType != null) {
                spawnedBosses.add(bossType);
                activeBosses.remove(boss);

                switch (bossType) {
                    case DRACO -> {
                        spellManager.unlockEpiskey();
                        episkeyIcon.setOpacity(1);
                        episkeyLabel.setOpacity(1);
                    }
                    case LUCIUS -> {
                        spellManager.unlockLevioso();
                        leviosoIcon.setOpacity(1);
                        leviosoLabel.setOpacity(1);
                    }
                    case TOM_RIDDLE -> {
                        spellManager.unlockDepulso();
                        depulsoIcon.setOpacity(1);
                        depulsoLabel.setOpacity(1);
                    }
                    case BELLATRIX -> {
                        spellManager.unlockBombarda();
                        bombardaIcon.setOpacity(1);
                        bombardaLabel.setOpacity(1);
                    }
                }
            }
        }


        enemies.remove(enemy);
        gamePane.getChildren().remove(enemy);
        enemyData.die();
    }

    private void updateHealthUI() {
        int currentHP = (int)healthManager.getHp();
        int maxHP = (int)experienceManager.getMaxHealth();
        hpLabel.setText(currentHP + "/" + maxHP);

    }

    private void updateExperienceUI() {
        int currentXP = experienceManager.getExperience();
        int maxXP = (int)experienceManager.getMaxExperience();
        xpLabel.setText(currentXP + "/" + maxXP);
    }

    public void checkGameOver() {
        if (healthManager.getHp() <= 0) {
            gameOver = true;
            spawnTimeline.stop();
            movementTimeline.stop();
            attackTimeline.stop();
            spellManager.stopAllTimelines();
            endLabel.setVisible(true);
            bossCheckTimeline.stop();
        }
    }
}
