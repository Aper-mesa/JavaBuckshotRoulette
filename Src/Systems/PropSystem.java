package Systems;

import Components.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;

public class PropSystem extends ComponentSystem {
    public ArrayList<Component> dealerProps = new ArrayList<>(8);
    public ArrayList<Component> playerProps = new ArrayList<>(8);
    ArrayList<Class<?>> allPropsClasses = new ArrayList<>();
    ArrayList<Integer> usedIndexes = new ArrayList<>();
    AmmoSystem ammoSystem;
    TurnSystem turnSystem;
    PersonSystem personSystem;
    Engine engine;

    public PropSystem(Engine engine) {
        this.engine = engine;
        ammoSystem = (AmmoSystem) engine.getSystem(AmmoSystem.class);
        turnSystem = (TurnSystem) engine.getSystem(TurnSystem.class);
        personSystem = (PersonSystem) engine.getSystem(PersonSystem.class);
        allPropsClasses.add(PhoneComponent.class);
        allPropsClasses.add(BeerComponent.class);
        allPropsClasses.add(CigaretteComponent.class);
        allPropsClasses.add(MagnifierComponent.class);
        allPropsClasses.add(HandsawComponent.class);
        allPropsClasses.add(ConverterComponent.class);
        allPropsClasses.add(AdrenalineComponent.class);
        allPropsClasses.add(MedicineComponent.class);
        allPropsClasses.add(HandcuffComponent.class);
    }

    public void beer() {
        Component bullet = ammoSystem.nextBullet();
        if (bullet instanceof BlankComponent) {
            System.out.println("BLANK");
        } else {
            System.out.println("BALL");
        }
    }

    public void cigarette() {
        if (personSystem.isWounded()) personSystem.heal();
    }

    public Component magnifier() {
        Component bullet = ammoSystem.checkBullet();
        if (turnSystem.isPlayerTurn()) {
            if (bullet instanceof BlankComponent) {
                System.out.println("BLANK");
            } else {
                System.out.println("BALL");
            }
        }
        return bullet;
    }

    public void phone() {
        int index = -1;
        if (turnSystem.isPlayerTurn()) {
            int totalAmount = ammoSystem.getTotalAmount();
            while (true) {
                if (usedIndexes.isEmpty()) break;
                index = engine.rand.nextInt((int) Math.floor(totalAmount / 2.0)) + (int) Math.floor(totalAmount / 2.0);
                if (usedIndexes.contains(index)) {
                    continue;
                }
                usedIndexes.add(index);
                break;
            }
            if (usedIndexes.isEmpty()) {
                index = engine.rand.nextInt((int) Math.floor(totalAmount / 2.0)) + (int) Math.floor(totalAmount / 2.0);
                usedIndexes.add(index);
            }
            Component bullet = ammoSystem.checkBulletByPhone(index);
            System.out.print("NUMBER " + (index + 1) + " BULLET IS ");
            if (bullet instanceof BlankComponent) {
                System.out.println("BLANK");
            } else {
                System.out.println("BALL");
            }
        }
    }

    public void clearPhoneIndexes() {
        usedIndexes.clear();
    }

    public void converter() {
        ammoSystem.convertCurrentBullet();
    }

    public void handsaw() {
        ShotgunSystem shotgunSystem = (ShotgunSystem) engine.getSystem(ShotgunSystem.class);
        shotgunSystem.sawBarrel();
    }

    public void adrenaline() {
        if (turnSystem.isPlayerTurn()) {
            System.out.println("TYPE INDEX TO STEAL");
            int choice = Integer.parseInt(engine.input.nextLine()) - 1;
            usePropByStealing(choice, turnSystem);
        }
    }

    public void dealerAdrenaline(Class<?> prop) {
        for (Component playerProp : playerProps) {
            if (playerProp.getClass() == prop) {
                removePlayerProp(prop);
                return;
            }
        }
    }

    public void medicine() {
        int chance = engine.rand.nextInt(2);
        //chance==0 then damage, 1 then heal
        if (chance == 1) {
            personSystem.heal();
            personSystem.heal();
            return;
        }
        if (turnSystem.isPlayerTurn()) {
            personSystem.harm(personSystem.player);
        } else {
            personSystem.harm(personSystem.dealer);
        }
    }

    public void handcuff() {
        //attempting to use an extra handcuff when a handcuff is in use results in wasting this extra handcuff
        if (!turnSystem.notHandcuffed()) {
            System.out.println("CANNOT USE MORE HANDCUFFS, PROP WASTED");
            return;
        }
        turnSystem.handcuff();
    }

    public void showProps() {
        System.out.println("DEALER PROPS" + dealerProps.toString());
        System.out.println(" YOUR  PROPS" + playerProps.toString());
    }

    public void usePropByIndex(int index, TurnSystem turnSystem) {
        Component prop = turnSystem.isDealerTurn() ? dealerProps.get(index) : playerProps.get(index);
        useProp(prop);
        if (turnSystem.isDealerTurn()) {
            dealerProps.remove(index);
        } else {
            playerProps.remove(index);
        }
    }

    public void usePropByStealing(int index, TurnSystem turnSystem) {
        Component prop = turnSystem.isDealerTurn() ? playerProps.get(index) : dealerProps.get(index);
        if (prop instanceof AdrenalineComponent) {
            if (turnSystem.isPlayerTurn()) {
                System.out.println("CANNOT STEAL ADRENALINE, PROP WASTED");
            }
            return;
        }
        if (turnSystem.isDealerTurn()) {
            playerProps.remove(index);
        } else {
            dealerProps.remove(index);
        }
        useProp(prop);
    }

    private void useProp(Component prop) {
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
        } else if (prop instanceof AdrenalineComponent) {
            adrenaline();
        } else if (prop instanceof MedicineComponent) {
            medicine();
        } else if (prop instanceof HandcuffComponent) {
            handcuff();
        }
    }

    public void spawnPropsInNewRound()
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
//        int numberOfProps = engine.rand.nextInt(2, 6);
        int numberOfProps = 8;
        for (int i = 0; i < numberOfProps; i++) {
            spawnProps();
        }
    }

    public void spawnPropsInReload()
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        int propsNumberBeforeReload = dealerProps.size() + playerProps.size();
        if (propsNumberBeforeReload == 8) return;
        for (int i = 0; i < 2; i++) {
            spawnProps();
        }
    }

    private void spawnProps() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Collections.shuffle(allPropsClasses);
        Constructor<?> constructor = allPropsClasses.getFirst().getDeclaredConstructor();
        if (dealerProps.size() < 8) {
            dealerProps.add((Component) constructor.newInstance());
        }
        Collections.shuffle(allPropsClasses);
        constructor = allPropsClasses.getFirst().getDeclaredConstructor();
        if (playerProps.size() < 8) {
            playerProps.add((Component) constructor.newInstance());
        }
    }

    public void clearProps() {
        dealerProps.clear();
        playerProps.clear();
    }

    public boolean dealerHasProp(Class<? extends Component> prop) {
        for (Component dealerProp : dealerProps) {
            if (dealerProp.getClass() == prop) {
                return true;
            }
        }
        return false;
    }

    public boolean playerHasProp(Class<? extends Component> prop) {
        for (Component dealerProp : playerProps) {
            if (dealerProp.getClass() == prop) {
                return true;
            }
        }
        return false;
    }

    public void removeDealerProp(Class<? extends Component> prop) {
        for (Component dealerProp : dealerProps) {
            if (dealerProp.getClass() == prop) {
                dealerProps.remove(dealerProp);
                return;
            }
        }
    }

    public void removePlayerProp(Class<?> prop) {
        for (Component dealerProp : playerProps) {
            if (dealerProp.getClass() == prop) {
                playerProps.remove(dealerProp);
                return;
            }
        }
    }
}
