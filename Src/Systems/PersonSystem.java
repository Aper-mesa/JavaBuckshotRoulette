package Systems;

import Components.Entity;
import Components.HealthComponent;

public class PersonSystem extends ComponentSystem {
    Entity dealer;
    public Entity player;
    TurnSystem turnSystem;
    Engine engine;

    public PersonSystem(Engine engine, Entity dealer, Entity player, TurnSystem turnSystem) {
        this.dealer = dealer;
        this.player = player;
        this.turnSystem = turnSystem;
        this.engine = engine;
    }

    public void heal() {
        HealthComponent healthComponent = turnSystem.isPlayerTurn() ?
                (HealthComponent) player.getComponent(HealthComponent.class)
                : (HealthComponent) dealer.getComponent(HealthComponent.class);
        if (healthComponent.amount < healthComponent.maxHealth)
            healthComponent.amount = healthComponent.amount + 1;
    }

    public void harm(Entity person) {
        HealthComponent dealerHealth = (HealthComponent) person.getComponent(HealthComponent.class);
        if (dealerHealth.amount > 0)
            dealerHealth.amount = dealerHealth.amount - 1;
        ShotgunSystem shotgunSystem = (ShotgunSystem) engine.getSystem(ShotgunSystem.class);
        if (shotgunSystem.isBarrelSawed() && dealerHealth.amount > 0) {
            dealerHealth.amount = dealerHealth.amount - 1;
            shotgunSystem.respawnBarrel();
        }
    }

    public void printHealth() {
        HealthComponent playerHealth = (HealthComponent) player.getComponent(HealthComponent.class);
        HealthComponent dealerHealth = (HealthComponent) dealer.getComponent(HealthComponent.class);
        int playerAmount = playerHealth.amount;
        int dealerAmount = dealerHealth.amount;
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
        return healthComponent.amount == 0;
    }

    public boolean isDealerDead() {
        HealthComponent healthComponent = (HealthComponent) dealer.getComponent(HealthComponent.class);
        return healthComponent.amount == 0;
    }

    public void setHealth(int amount) {
        HealthComponent playerHealth = (HealthComponent) player.getComponent(HealthComponent.class);
        HealthComponent dealerHealth = (HealthComponent) dealer.getComponent(HealthComponent.class);
        dealerHealth.amount = amount;
        playerHealth.amount = amount;
        playerHealth.maxHealth = amount;
        dealerHealth.maxHealth = amount;
    }

    public boolean isWounded() {
        HealthComponent healthComponent = turnSystem.isPlayerTurn() ?
                (HealthComponent) player.getComponent(HealthComponent.class)
                : (HealthComponent) dealer.getComponent(HealthComponent.class);
        return healthComponent.amount != healthComponent.maxHealth;
    }

}
