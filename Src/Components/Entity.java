package Components;

import java.util.ArrayList;

public class Entity {
    ArrayList<Component> components = new ArrayList<>();

    public void addComponent(Component component) {
        components.add(component);
    }
}
