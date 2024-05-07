package Systems;

import Components.*;

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
        allPropsClasses.add(MagnifierComponent.class);
        allPropsClasses.add(HandsawComponent.class);
        allPropsClasses.add(ConverterComponent.class);
        allPropsClasses.add(PhoneComponent.class);
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
        TurnSystem turnSystem = (TurnSystem) engine.getSystem(TurnSystem.class);
        if (turnSystem.isPlayerTurn()) {
            PersonSystem player = (PersonSystem) engine.getSystem(PersonSystem.class);
            if (player.isWounded()) player.incrementHealth();
        } else if (turnSystem.isDealerTurn()) {
            PersonSystem dealer = (PersonSystem) engine.getSystem(PersonSystem.class);
            if (dealer.isWounded()) dealer.incrementHealth();
        }
    }

    public void magnifier(Engine engine) {
        TurnSystem turnSystem = (TurnSystem) engine.getSystem(TurnSystem.class);
        if (turnSystem.isPlayerTurn()) {
            AmmoSystem ammoSystem = (AmmoSystem) engine.getSystem(AmmoSystem.class);
            Component bullet = ammoSystem.checkBullet();
            if (bullet instanceof BlankComponent) {
                System.out.println("BLANK");
            } else {
                System.out.println("BALL");
            }
        }
    }

    public void phone(Engine engine) {
        TurnSystem turnSystem = (TurnSystem) engine.getSystem(TurnSystem.class);
        AmmoSystem ammoSystem = (AmmoSystem) engine.getSystem(AmmoSystem.class);
        if (turnSystem.isPlayerTurn()) {
            int totalAmount = ammoSystem.getTotalAmount();
            int index = r.nextInt((int) Math.floor(totalAmount / 2.0)) + (int) Math.floor(totalAmount / 2.0);
            Component bullet = ammoSystem.checkBulletByPhone(index);
            System.out.print("NUMBER " + (index + 1) + " BULLET IS ");
            if (bullet instanceof BlankComponent) {
                System.out.println("BLANK");
            } else {
                System.out.println("BALL");
            }
        }
    }

    public void converter(Engine engine) {
        AmmoSystem ammoSystem = (AmmoSystem) engine.getSystem(AmmoSystem.class);
        ammoSystem.convertCurrentBullet();
    }

    public void handsaw(Engine engine) {
        ShotgunSystem shotgunSystem = (ShotgunSystem) engine.getSystem(ShotgunSystem.class);
        shotgunSystem.sawBarrel();
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
        } else if (prop instanceof CigaretteComponent) {
            cigarette(engine);
        } else if (prop instanceof MagnifierComponent) {
            magnifier(engine);
        } else if (prop instanceof HandsawComponent) {
            handsaw(engine);
        } else if (prop instanceof ConverterComponent) {
            converter(engine);
        } else if (prop instanceof PhoneComponent) {
            phone(engine);
        }
    }

    public void spawnProps()
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        int numberOfProps = r.nextInt(1, 3) * 2;
        for (int i = 0; i < numberOfProps; i++) {
            Collections.shuffle(allPropsClasses);
            Constructor<?> constructor = allPropsClasses.getFirst().getDeclaredConstructor();
            dealerProps.add((Component) constructor.newInstance());
            Collections.shuffle(allPropsClasses);
            constructor = allPropsClasses.getFirst().getDeclaredConstructor();
            playerProps.add((Component) constructor.newInstance());
        }
    }
}
