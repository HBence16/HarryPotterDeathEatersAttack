package org.example.harrypotterthedeatheatersattack;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.Label;

public class ExperienceManager {
    private int experience;
    private double maxExperience;
    private final ProgressBar experienceBar;
    private final Label levellabel;
    private int currentLevel;

    private double damage;
    private double attackSpeed;
    private double maxHealth;
    private double currentHealth;

    private double healCooldown;
    private double protegoCooldown;
    private double leviosoCooldown;
    private double depulsoCooldown;
    private double bombardaCooldown;

    private double healAmount;
    private double leviosoAirTime;
    private double depulsoRadius;
    private double bombardaDamage;

    private SpellManager spellManager;

    public ExperienceManager(int initialExperience, double maxExperience, ProgressBar experienceBar, Label levellabel,
                             double harryDamage, double harryAttackSpeed, double harryHealth, double harryMaxHealth,
                             double healCooldown, double protegoCooldown, double leviosoCooldown, double depulsoCooldown,
                             double bombardaCooldown, double healAmount, double leviosoAirTime, double depulsoRadius,
                             double bombardaDamage) {

        this.spellManager = null;

        this.experience = initialExperience;
        this.maxExperience = maxExperience;
        this.experienceBar = experienceBar;
        this.levellabel = levellabel;
        this.currentLevel = 1;

        this.damage=harryDamage;
        this.attackSpeed = harryAttackSpeed;
        this.maxHealth = harryMaxHealth;
        this.currentHealth = harryHealth;

        this.healCooldown = healCooldown;
        this.protegoCooldown = protegoCooldown;
        this.leviosoCooldown = leviosoCooldown;
        this.depulsoCooldown = depulsoCooldown;
        this.bombardaCooldown = bombardaCooldown;

        this.healAmount = healAmount;
        this.leviosoAirTime = leviosoAirTime;
        this.depulsoRadius = depulsoRadius;
        this.bombardaDamage = bombardaDamage;

        updateExperienceBar();
        updateLevelLabel();
    }
    public void setSpellManager(SpellManager spellManager) {
        this.spellManager = spellManager;
    }

    public void gainExperience(int amount) {
        experience += amount;
        while (experience >= maxExperience) {
            experience -= maxExperience;
            currentLevel++;

            updateLevelLabel();
            increaseAttack();
            increaseHealth();
            increaseAbilities();

            if (currentLevel % 5 == 0) {
                maxExperience *= 2;
                decreaseCooldowns();
            }
        }
        updateExperienceBar();
    }

    private void increaseHealth() {
        maxHealth *= 1.1;
        currentHealth *= 1.1;
    }

    private void increaseAttack() {
        attackSpeed *= 0.9;
        damage *= 1.1;
    }

    private void increaseAbilities() {
        if (spellManager != null) {
            if (spellManager.isEpiskeyUnlocked()) {
                healAmount *= 1.1;
            }
            if (spellManager.isLeviosoUnlocked()) {
                leviosoAirTime *= 1.1;
            }
            if (spellManager.isDepulsoUnlocked()) {
                depulsoRadius *= 1.1;
            }
            if (spellManager.isBombardaUnlocked()) {
                bombardaDamage *= 1.1;
            }
        }
    }

    private void decreaseCooldowns() {
        if (spellManager != null) {
            protegoCooldown *= 0.9;
            if (spellManager.isEpiskeyUnlocked()) {
                healCooldown *= 0.9;
            }
            if (spellManager.isLeviosoUnlocked()) {
                leviosoCooldown *= 0.9;
            }
            if (spellManager.isDepulsoUnlocked()) {
                depulsoCooldown *= 0.9;
            }
            if (spellManager.isBombardaUnlocked()) {
                bombardaCooldown *= 0.9;
            }
        }
    }

    private void updateExperienceBar() {
        if (experienceBar != null) {
            experienceBar.setProgress((double) experience / maxExperience);
        }
    }

    private void updateLevelLabel() {
        if (levellabel != null) {
            String levelText = currentLevel < 10 ? "0" + currentLevel : String.valueOf(currentLevel);
            levellabel.setText(levelText);
        }
    }

    public double getDamage(){return damage;}
    public double getAttackSpeed() {
        return attackSpeed;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getCurrentHealth() {
        return currentHealth;
    }

    public double getHealCooldown() {
        return healCooldown;
    }

    public double getProtegoCooldown() {
        return protegoCooldown;
    }

    public double getLeviosoCooldown() {
        return leviosoCooldown;
    }

    public double getDepulsoCooldown() {
        return depulsoCooldown;
    }

    public double getBombardaCooldown() {
        return bombardaCooldown;
    }

    public double getHealAmount() {
        return healAmount;
    }

    public double getLeviosoAirTime() {
        return leviosoAirTime;
    }

    public double getDepulsoRadius() {
        return depulsoRadius;
    }

    public double getBombardaDamage() {
        return bombardaDamage;
    }
    public void setCurrentHealth(double currentHealth) {
        this.currentHealth = currentHealth;
    }

    public int getExperience() {
        return experience;
    }
    public double getMaxExperience() {
        return maxExperience;
    }
}
