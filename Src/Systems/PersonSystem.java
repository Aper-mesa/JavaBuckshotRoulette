package Systems;

import Core.Entity;
import Components.HealthComponent;

import static Core.Engine.*;

public class PersonSystem extends ComponentSystem {
    public static Entity dealer = new Entity();
    public static Entity player = new Entity();

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
        System.out.print("\t\t\t大哥血量：\t");
        for (int i = 0; i < dealerAmount; i++) {
            System.out.print("⬛");
        }
        System.out.println();
        System.out.print("\t\t\t你的血量：\t");
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
