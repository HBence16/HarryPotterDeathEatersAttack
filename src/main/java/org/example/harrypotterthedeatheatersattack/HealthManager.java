package org.example.harrypotterthedeatheatersattack;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class HealthManager {
    private double hp;
    private double maxHealth;
    private final ProgressBar healthBar;
    private final Label endLabel;
    private final ExperienceManager experienceManager;

    public HealthManager(ExperienceManager experienceManager, ProgressBar healthBar, Label endLabel) {
        this.experienceManager = experienceManager;
        this.maxHealth = experienceManager.getMaxHealth();
        this.hp = experienceManager.getCurrentHealth();
        this.healthBar = healthBar;
        this.endLabel = endLabel;
        updateHealthBar();
    }

    public void takeDamage(int damage) {
        hp -= damage;
        if (hp < 0) {
            hp = 0;
        }
        updateHealthBar();

        if (hp <= 0) {
            endLabel.setVisible(true);
        }
    }

    private void updateHealthBar() {
        if (healthBar != null) {
            double oldMaxHealth = maxHealth;
            maxHealth = experienceManager.getMaxHealth();

            if (oldMaxHealth != maxHealth) {
                double healthRatio = hp / oldMaxHealth;
                hp = maxHealth * healthRatio;
            }
            syncHealthWithExperienceManager();
            healthBar.setProgress(hp / maxHealth);
        }
    }

    public double getHp() {
        return hp;
    }

    public void heal(double amount) {
        double oldMaxHealth = maxHealth;
        maxHealth = experienceManager.getMaxHealth();

        if (oldMaxHealth != maxHealth) {
            double healthRatio = hp / oldMaxHealth;
            hp = maxHealth * healthRatio;
        }

        hp += amount;
        if (hp > maxHealth) {
            hp = maxHealth;
        }
        updateHealthBar();
    }

    public void syncHealthWithExperienceManager() {
        experienceManager.setCurrentHealth(hp);
    }
}
