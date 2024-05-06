package Systems;

import Components.Entity;

import java.util.ArrayList;

public class EntitySystem {
    public ArrayList<Entity> entities = new ArrayList<>();

    public void addEntity(Entity entity) {
        entities.add(entity);
    }
}
