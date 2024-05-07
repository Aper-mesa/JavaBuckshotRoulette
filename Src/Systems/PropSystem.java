package Systems;

import Components.BeerComponent;
import Components.BlankComponent;
import Components.CigaretteComponent;
import Components.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PropSystem extends EntitySystem {
    Random r = new Random();
    ArrayList<Component> dealerProps = new ArrayList<>(8);
    ArrayList<Component> playerProps = new ArrayList<>(8);
    ArrayList<Class<?>> allPropsClasses = new ArrayList<>();

    public PropSystem() {
        allPropsClasses.add(BeerComponent.class);
        allPropsClasses.add(CigaretteComponent.class);
    }

    public void beer(Engine engine) {
        AmmoSystem ammoSystem = (AmmoSystem) engine.getSystem(AmmoSystem.class);
        Component bullet = ammoSystem.nextBullet();
        if (bullet instanceof BlankComponent) {
            System.out.println("BLANK");
        } else {
            System.out.println("BALL");
        }
    }

    public void cigarette(Engine engine) {

    }

    public void showProps() {
        System.out.println("DEALER PROPS" + dealerProps.toString());
        System.out.println(" YOUR  PROPS" + playerProps.toString());
    }

    public void usePropByIndex(int index, Engine engine, TurnSystem turnSystem) {
        Component prop;
        if (turnSystem.isDealerTurn()) {
            prop = dealerProps.get(index);
            dealerProps.remove(index);
        } else {
            prop = playerProps.get(index);
            playerProps.remove(index);
        }
        if (prop instanceof BeerComponent) {
            beer(engine);
        }
    }

    public void spawnProps()
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        int numberOfProps = r.nextInt(1, 3) * 2;
        for (int i = 0; i < numberOfProps; i++) {
            Collections.shuffle(allPropsClasses);
            Constructor<?> constructor = allPropsClasses.getFirst().getDeclaredConstructor();
            dealerProps.add((Component) constructor.newInstance());
            playerProps.add((Component) constructor.newInstance());
        }
    }
}
