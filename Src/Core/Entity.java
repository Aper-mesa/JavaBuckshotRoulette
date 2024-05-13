package Core;

import Components.Component;

import java.util.ArrayList;

public class Entity {
    public ArrayList<Component> components = new ArrayList<>();

    public void addComponent(Component component) {
        components.add(component);
    }

    public <E> Component getComponent(Class<E> componentClass) {
        for (Component component : components) {
            if (componentClass == component.getClass()) return component;
        }
        return null;
    }
}
