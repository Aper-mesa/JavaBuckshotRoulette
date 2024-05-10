package Systems;

import Components.Entity;
import Components.HealthComponent;

public class PersonSystem extends EntitySystem {
    Entity dealer;
    Entity player;
    TurnSystem turnSystem;
    Engine engine;

    public PersonSystem(Engine engine, Entity dealer, Entity player, TurnSystem turnSystem) {
        this.dealer = dealer;
        this.player = player;
        this.turnSystem = turnSystem;
        this.engine = engine;
    }

    private HealthComponent getCorrectHealthComponent() {
        HealthComponent healthComponent;
        if (turnSystem.isHandcuffed()) {
            healthComponent = turnSystem.isPlayerTurn() ?
                    (HealthComponent) dealer.getComponent(HealthComponent.class)
                    : (HealthComponent) player.getComponent(HealthComponent.class);
            turnSystem.noHandcuff();
        } else {
            healthComponent = turnSystem.isPlayerTurn() ?
                    (HealthComponent) player.getComponent(HealthComponent.class)
                    : (HealthComponent) dealer.getComponent(HealthComponent.class);
        }
        return healthComponent;
    }

    public void heal() {
        HealthComponent healthComponent = getCorrectHealthComponent();
        if (healthComponent.getAmount() < healthComponent.maxHealth)
            healthComponent.setAmount(healthComponent.getAmount() + 1);
    }

    public void harm() {
        HealthComponent healthComponent = getCorrectHealthComponent();
        if (healthComponent.getAmount() > 0)
            healthComponent.setAmount(healthComponent.getAmount() - 1);
        ShotgunSystem shotgunSystem = (ShotgunSystem) engine.getSystem(ShotgunSystem.class);
        if (shotgunSystem.isBarrelSawed()) {
            healthComponent.setAmount(healthComponent.getAmount() - 1);
            shotgunSystem.respawnBarrel();
        }
    }

    public void printHealth() {
        HealthComponent playerHealth = (HealthComponent) player.getComponent(HealthComponent.class);
        HealthComponent dealerHealth = (HealthComponent) dealer.getComponent(HealthComponent.class);
        int playerAmount = playerHealth.getAmount();
        int dealerAmount = dealerHealth.getAmount();
        System.out.print("\t\t\tDEALER HEALTH:\t");
        for (int i = 0; i < dealerAmount; i++) {
            System.out.print("⬛");
        }
        System.out.println();
        System.out.print("\t\t\tYOUR HEALTH:\t");
        for (int i = 0; i < playerAmount; i++) {
            System.out.print("⬛");
        }
        System.out.println();
    }

    public boolean isPlayerDead() {
        HealthComponent healthComponent = (HealthComponent) player.getComponent(HealthComponent.class);
        return healthComponent.getAmount() == 0;
    }

    public boolean isDealerDead() {
        HealthComponent healthComponent = (HealthComponent) dealer.getComponent(HealthComponent.class);
        return healthComponent.getAmount() == 0;
    }

    public void setHealth(int amount) {
        HealthComponent playerHealth = (HealthComponent) player.getComponent(HealthComponent.class);
        HealthComponent dealerHealth = (HealthComponent) dealer.getComponent(HealthComponent.class);
        dealerHealth.setAmount(amount);
        playerHealth.setAmount(amount);
        playerHealth.maxHealth = amount;
        dealerHealth.maxHealth = amount;
    }

    public boolean isWounded() {
        HealthComponent healthComponent = turnSystem.isPlayerTurn() ?
                (HealthComponent) player.getComponent(HealthComponent.class)
                : (HealthComponent) dealer.getComponent(HealthComponent.class);
        return healthComponent.getAmount() != healthComponent.maxHealth;
    }

}
