package Systems;

import Components.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;

public class PropSystem extends EntitySystem {
    ArrayList<Component> dealerProps = new ArrayList<>(8);
    ArrayList<Component> playerProps = new ArrayList<>(8);
    ArrayList<Class<?>> allPropsClasses = new ArrayList<>();
    Engine engine;

    public PropSystem(Engine engine) {
        this.engine = engine;
        allPropsClasses.add(BeerComponent.class);
        allPropsClasses.add(CigaretteComponent.class);
        allPropsClasses.add(MagnifierComponent.class);
        allPropsClasses.add(HandsawComponent.class);
        allPropsClasses.add(ConverterComponent.class);
        allPropsClasses.add(PhoneComponent.class);
        allPropsClasses.add(Adrenaline.class);
        allPropsClasses.add(MedicineComponent.class);
    }

    public void beer() {
        AmmoSystem ammoSystem = (AmmoSystem) engine.getSystem(AmmoSystem.class);
        Component bullet = ammoSystem.nextBullet();
        if (bullet instanceof BlankComponent) {
            System.out.println("BLANK");
        } else {
            System.out.println("BALL");
        }
    }

    public void cigarette() {
        TurnSystem turnSystem = (TurnSystem) engine.getSystem(TurnSystem.class);
        if (turnSystem.isPlayerTurn()) {
            PersonSystem player = (PersonSystem) engine.getSystem(PersonSystem.class);
            if (player.isWounded()) player.heal();
        } else if (turnSystem.isDealerTurn()) {
            PersonSystem dealer = (PersonSystem) engine.getSystem(PersonSystem.class);
            if (dealer.isWounded()) dealer.heal();
        }
    }

    public void magnifier() {
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

    public void phone() {
        TurnSystem turnSystem = (TurnSystem) engine.getSystem(TurnSystem.class);
        AmmoSystem ammoSystem = (AmmoSystem) engine.getSystem(AmmoSystem.class);
        if (turnSystem.isPlayerTurn()) {
            int totalAmount = ammoSystem.getTotalAmount();
            int index = engine.rand.nextInt((int) Math.floor(totalAmount / 2.0)) + (int) Math.floor(totalAmount / 2.0);
            Component bullet = ammoSystem.checkBulletByPhone(index);
            System.out.print("NUMBER " + (index + 1) + " BULLET IS ");
            if (bullet instanceof BlankComponent) {
                System.out.println("BLANK");
            } else {
                System.out.println("BALL");
            }
        }
    }

    public void converter() {
        AmmoSystem ammoSystem = (AmmoSystem) engine.getSystem(AmmoSystem.class);
        ammoSystem.convertCurrentBullet();
    }

    public void handsaw() {
        ShotgunSystem shotgunSystem = (ShotgunSystem) engine.getSystem(ShotgunSystem.class);
        shotgunSystem.sawBarrel();
    }

    public void adrenaline() {
        TurnSystem turnSystem = (TurnSystem) engine.getSystem(TurnSystem.class);
        if (turnSystem.isPlayerTurn()) {
            System.out.println("TYPE INDEX TO STEAL");
            int choice = Integer.parseInt(engine.input.nextLine()) - 1;
            stealPropByIndex(choice, turnSystem);
        }
    }

    public void medicine() {
        int chance = engine.rand.nextInt(2);
        //chance==0 then damage, 1 then heal
        TurnSystem turnSystem = (TurnSystem) engine.getSystem(TurnSystem.class);
        PersonSystem personSystem = (PersonSystem) engine.getSystem(PersonSystem.class);
        if (chance == 0) {
            personSystem.harm();
        } else {
            personSystem.heal();
            personSystem.heal();
        }
    }

    public void stealPropByIndex(int index, TurnSystem turnSystem) {
        if (turnSystem.isPlayerTurn()) {
            playerProps.add(dealerProps.remove(index));
        }
    }

    public void showProps() {
        System.out.println("DEALER PROPS" + dealerProps.toString());
        System.out.println(" YOUR  PROPS" + playerProps.toString());
    }

    public void usePropByIndex(int index, TurnSystem turnSystem) {
        Component prop;
        if (turnSystem.isDealerTurn()) {
            prop = dealerProps.get(index);
            dealerProps.remove(index);
        } else {
            prop = playerProps.get(index);
            playerProps.remove(index);
        }
        if (prop instanceof BeerComponent) {
            beer();
        } else if (prop instanceof CigaretteComponent) {
            cigarette();
        } else if (prop instanceof MagnifierComponent) {
            magnifier();
        } else if (prop instanceof HandsawComponent) {
            handsaw();
        } else if (prop instanceof ConverterComponent) {
            converter();
        } else if (prop instanceof PhoneComponent) {
            phone();
        } else if (prop instanceof Adrenaline) {
            adrenaline();
        } else if (prop instanceof MedicineComponent) {
            medicine();
        }
    }

    public void spawnProps()
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        int numberOfProps = engine.rand.nextInt(2, 6);
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
