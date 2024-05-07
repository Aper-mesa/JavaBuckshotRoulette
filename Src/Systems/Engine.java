package Systems;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Engine {
    public Scanner input = new Scanner(System.in);
    public Random rand = new Random();
    private final ArrayList<EntitySystem> systems = new ArrayList<>();

    public void addSystem(EntitySystem system) {
        systems.add(system);
    }

    public EntitySystem getSystem(Class<?> systemClass) {
        for (EntitySystem system : systems) {
            if (systemClass == system.getClass()) {
                return system;
            }
        }
        return null;
    }
}
