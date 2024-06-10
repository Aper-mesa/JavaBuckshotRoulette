package Systems;

import Components.*;
import Core.Engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;

import static Core.Engine.*;

public class PropSystem extends ComponentSystem {
    public ArrayList<Component> player2Props = new ArrayList<>(8);
    public ArrayList<Component> player1Props = new ArrayList<>(8);
    ArrayList<Class<?>> allPropsClasses = new ArrayList<>();
    ArrayList<Integer> player1Indices = new ArrayList<>();
    public ArrayList<Integer> player2Indexes = new ArrayList<>();

    public PropSystem() {
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
        System.out.println(ammoSystem.nextBullet());
    }

    public void cigarette() {
        if (personSystem.isWounded()) personSystem.heal();
    }

    public void magnifier() {
        Component bullet = ammoSystem.checkBullet();
        System.out.println(bullet);
    }

    public void phone() {
        int index = -1;
        int totalAmount = ammoSystem.getTotalAmount();
        ArrayList<Integer> indices = turnSystem.isPlayer1Turn() ? player1Indices : player2Indexes;
        while (true) {
            if (indices.isEmpty()) break;
            index = Engine.rand.nextInt((int) Math.floor(totalAmount / 2.0)) + (int) Math.floor(totalAmount / 2.0);
            if (indices.contains(index)) {
                continue;
            }
            indices.add(index);
            break;
        }
        if (indices.isEmpty()) {
            index = Engine.rand.nextInt((int) Math.floor(totalAmount / 2.0)) + (int) Math.floor(totalAmount / 2.0);
            indices.add(index);
        }
        Component bullet = ammoSystem.checkBulletByPhone(index);
        System.out.println("第 " + (index + 1) + " 发子弹是" + bullet);
    }

    public void clearPhoneIndexes() {
        player1Indices.clear();
        player2Indexes.clear();
    }

    public void converter() {
        ammoSystem.convertCurrentBullet();
    }

    public void handsaw() {
        shotgunSystem.sawBarrel();
    }

    public void adrenaline() {
        System.out.println("输入要偷的道具序号");
        String inputString;
        while (true) {
            inputString = input.nextLine();
            if (inputString.matches("[1-8]")) {
                int choice = Integer.parseInt(inputString);
                if (choice <= propSystem.player2Props.size()) break;
                else {
                    System.out.println("重新输入");
                }
            } else {
                System.out.println("重新输入");
            }
        }
        usePropByStealing(Integer.parseInt(inputString) - 1);
    }

    public void medicine() {
        int chance = Engine.rand.nextInt(2);
        //chance==0 then damage, 1 then heal
        if (chance == 1) {
            personSystem.heal();
            personSystem.heal();
            return;
        }
        if (turnSystem.isPlayer1Turn()) {
            personSystem.harm(PersonSystem.player1);
        } else {
            personSystem.harm(PersonSystem.player2);
        }
    }

    public void handcuff() {
        //attempting to use an extra handcuff when a handcuff is in use results in wasting this extra handcuff
        if (!turnSystem.notHandcuffed()) {
            System.out.println("不能重复使用手铐，该手铐报废");
            return;
        }
        turnSystem.handcuff();
    }

    public void showProps() {
        System.out.println(personSystem.player2Name() + "道具" + player2Props.toString());
        System.out.println(personSystem.player1Name() + "道具" + player1Props.toString());
    }

    public void usePropByIndex(int index) {
        Component prop = turnSystem.isPlayer2Turn() ? player2Props.get(index) : player1Props.get(index);
        useProp(prop);
        if (turnSystem.isPlayer2Turn()) {
            player2Props.remove(index);
        } else {
            player1Props.remove(index);
        }
    }

    public void usePropByStealing(int index) {
        Component prop = turnSystem.isPlayer2Turn() ? player1Props.get(index) : player2Props.get(index);
        if (prop instanceof AdrenalineComponent) {
            System.out.println("不能偷肾上腺素，道具报废");
            return;
        }
        if (turnSystem.isPlayer2Turn()) {
            player1Props.remove(index);
        } else {
            player2Props.remove(index);
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
        int numberOfProps = Engine.rand.nextInt(2, 6);
        for (int i = 0; i < numberOfProps; i++) {
            spawnProps();
        }
    }

    public void spawnPropsInReload()
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        int propsNumberBeforeReload = player2Props.size() + player1Props.size();
        if (propsNumberBeforeReload == 8) return;
        for (int i = 0; i < 2; i++) {
            spawnProps();
        }
    }

    private void spawnProps()
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Collections.shuffle(allPropsClasses);
        Constructor<?> constructor = allPropsClasses.getFirst().getConstructor();
        if (player2Props.size() < 8) {
            player2Props.add((Component) constructor.newInstance());
        }
        Collections.shuffle(allPropsClasses);
        constructor = allPropsClasses.getFirst().getConstructor();
        if (player1Props.size() < 8) {
            player1Props.add((Component) constructor.newInstance());
        }
    }

    public void clearProps() {
        player2Props.clear();
        player1Props.clear();
    }

    public boolean noProp() {
        ArrayList<Component> props = turnSystem.isPlayer2Turn() ? player2Props : player1Props;
        return props.isEmpty();
    }
}
