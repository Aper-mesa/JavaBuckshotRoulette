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
            if (player.isWounded()) player.incrementHealth();
        } else if (turnSystem.isDealerTurn()) {
            PersonSystem dealer = (PersonSystem) engine.getSystem(PersonSystem.class);
            if (dealer.isWounded()) dealer.incrementHealth();
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
