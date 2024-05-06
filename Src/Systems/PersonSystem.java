package Systems;

import Components.Entity;
import Components.HealthComponent;

public class PersonSystem extends EntitySystem {
    Entity person;

    public PersonSystem(Entity person) {
        this.person = person;
    }

    public void incrementHealth() {
        HealthComponent healthComponent = (HealthComponent) person.getComponent(HealthComponent.class);
        if (healthComponent.getAmount() < HealthComponent.MAX)
            healthComponent.setAmount(healthComponent.getAmount() + 1);
    }

    public void decrementHealth() {
        HealthComponent healthComponent = (HealthComponent) person.getComponent(HealthComponent.class);
        if (healthComponent.getAmount() > 0)
            healthComponent.setAmount(healthComponent.getAmount() - 1);
    }

    public int getHealth() {
        HealthComponent healthComponent = (HealthComponent) person.getComponent(HealthComponent.class);
        return healthComponent.getAmount();
    }

    public boolean isDead() {
        HealthComponent healthComponent = (HealthComponent) person.getComponent(HealthComponent.class);
        return healthComponent.getAmount() == 0;
    }

    public void setHealth(int amount) {
        HealthComponent healthComponent = (HealthComponent) person.getComponent(HealthComponent.class);
        healthComponent.setAmount(amount);
    }

}
