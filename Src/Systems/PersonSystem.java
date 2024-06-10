package Systems;

import Components.HealthComponent;
import Components.NameComponent;
import Core.Entity;

import static Core.Engine.*;

public class PersonSystem extends ComponentSystem {
    public static Entity player2 = new Entity();
    public static Entity player1 = new Entity();

    public void setPlayer1Name(String name) {
        NameComponent nameComponent = (NameComponent) player1.getComponent(NameComponent.class);
        nameComponent.name = name;
    }

    public void setPlayer2Name(String name) {
        NameComponent nameComponent = (NameComponent) player2.getComponent(NameComponent.class);
        nameComponent.name = name;
    }

    public String player1Name() {
        NameComponent nameComponent = (NameComponent) player1.getComponent(NameComponent.class);
        return nameComponent.name;
    }

    public String player2Name() {
        NameComponent nameComponent = (NameComponent) player2.getComponent(NameComponent.class);
        return nameComponent.name;
    }

    public void heal() {
        HealthComponent healthComponent = turnSystem.isPlayer1Turn() ?
                (HealthComponent) player1.getComponent(HealthComponent.class)
                : (HealthComponent) player2.getComponent(HealthComponent.class);
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
        HealthComponent playerHealth = (HealthComponent) player1.getComponent(HealthComponent.class);
        HealthComponent dealerHealth = (HealthComponent) player2.getComponent(HealthComponent.class);
        int playerAmount = playerHealth.amount;
        int dealerAmount = dealerHealth.amount;
        System.out.print("\t\t\t" + personSystem.player2Name() + "血量：");
        for (int i = 0; i < dealerAmount; i++) {
            System.out.print("⬛");
        }
        System.out.println();
        System.out.print("\t\t\t" + personSystem.player1Name() + "血量：");
        for (int i = 0; i < playerAmount; i++) {
            System.out.print("⬛");
        }
        System.out.println();
    }

    public boolean isPlayer1Dead() {
        HealthComponent healthComponent = (HealthComponent) player1.getComponent(HealthComponent.class);
        return healthComponent.amount == 0;
    }

    public boolean isPlayer2Dead() {
        HealthComponent healthComponent = (HealthComponent) player2.getComponent(HealthComponent.class);
        return healthComponent.amount == 0;
    }

    public void setHealth(int amount) {
        HealthComponent playerHealth = (HealthComponent) player1.getComponent(HealthComponent.class);
        HealthComponent dealerHealth = (HealthComponent) player2.getComponent(HealthComponent.class);
        dealerHealth.amount = amount;
        playerHealth.amount = amount;
        playerHealth.maxHealth = amount;
        dealerHealth.maxHealth = amount;
    }

    public boolean isWounded() {
        HealthComponent healthComponent = turnSystem.isPlayer1Turn() ?
                (HealthComponent) player1.getComponent(HealthComponent.class)
                : (HealthComponent) player2.getComponent(HealthComponent.class);
        return healthComponent.amount != healthComponent.maxHealth;
    }
}
