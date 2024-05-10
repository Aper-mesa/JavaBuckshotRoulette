package Systems;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Engine {
    public Scanner input = new Scanner(System.in);
    public Random rand = new Random();
    private final ArrayList<ComponentSystem> systems = new ArrayList<>();

    public void addSystem(ComponentSystem system) {
        systems.add(system);
    }

    public ComponentSystem getSystem(Class<?> systemClass) {
        for (ComponentSystem system : systems) {
            if (systemClass == system.getClass()) {
                return system;
            }
        }
        return null;
    }
}
