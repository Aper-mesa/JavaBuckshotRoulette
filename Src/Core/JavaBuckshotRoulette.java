package Core;

import Components.BlankComponent;
import Components.Component;
import Components.HealthComponent;

import java.lang.reflect.InvocationTargetException;

import static Core.Engine.*;
import static Systems.PersonSystem.player1;
import static Systems.PersonSystem.player2;

public class JavaBuckshotRoulette {
    int initialHealth = 0;

    public static void main(String[] args)
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        new JavaBuckshotRoulette();
    }

    public JavaBuckshotRoulette()
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        addPeople();
        setInitialHealth();
        System.out.println("-----第 " + roundSystem.getRound() + " 回合-----");
        printChamber();
        personSystem.printHealth();
        play();
    }

    private void setInitialHealth() {
        initialHealth = rand.nextInt(3) + 2;
        personSystem.setHealth(initialHealth);
    }

    private void addPeople() {
        HealthComponent dealerHealth = new HealthComponent();
        player2.addComponent(dealerHealth);
        HealthComponent playerHealth = new HealthComponent();
        player1.addComponent(playerHealth);
    }

    private void printChamber() {
        int blankAmmo = ammoSystem.getBlankAmount();
        int ballAmmo = ammoSystem.getBallAmount();
        System.out.println("\t\t\t" + blankAmmo + " 实  " + ballAmmo + " 空");
    }

    private void play()
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        propSystem.spawnPropsInNewRound();
        while (true) {
            if (roundSystem.noMoreRound()) {
                System.out.println("游戏结束");
                System.exit(-1);
            }
            if (ammoSystem.noBullet()) {
                System.out.println("\t\t\t--------------");
                ammoSystem.reload();
                propSystem.clearPhoneIndexes();
                propSystem.spawnPropsInReload();
                turnSystem.noHandcuff();
                turnSystem.player1Turn();
                personSystem.printHealth();
                printChamber();
            }
            inTurn();
        }
    }

    private void inTurn()
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if (turnSystem.isPlayer1Turn()) {
            player1Turn();
        } else {
            player2Turn();
        }
        if (personSystem.isPlayer1Dead()) {
            System.out.println("玩家1死了，玩家2赢了此回合");
            nextRound();
        } else if (personSystem.isPlayer2Dead()) {
            System.out.println("玩家2死了，玩家1赢了此回合");
            nextRound();
        }
    }

    private void nextRound()
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        roundSystem.nextRound();
        if (roundSystem.noMoreRound()) return;
        System.out.println("-----第 " + roundSystem.getRound() + " 回合-----");
        ammoSystem.reload();
        propSystem.clearPhoneIndexes();
        turnSystem.player1Turn();
        setInitialHealth();
        personSystem.printHealth();
        printChamber();
        propSystem.clearProps();
        propSystem.spawnPropsInNewRound();
    }

    private void player1Turn() {
        propSystem.showProps();
        System.out.println("""
                \t\t\t⚠️玩家1回合⚠️
                \t\t\t输 1 打对面
                \t\t\t输 2 打自己
                \t\t\t输 3 用道具""");
        String command = input.nextLine();
        switch (command) {
            case "1" -> shootPlayer2();
            case "2" -> shootPlayer1();
            case "3" -> useProps();
            default -> System.out.println("无效指令");
        }
        ammoSystem.cheat();
        personSystem.printHealth();
    }

    private void player2Turn() {
        propSystem.showProps();
        System.out.println("""
                \t\t\t😨玩家2回合😨
                \t\t\t输 1 打对面
                \t\t\t输 2 打自己
                \t\t\t输 3 用道具""");
        String command = input.nextLine();
        switch (command) {
            case "1" -> shootPlayer1();
            case "2" -> shootPlayer2();
            case "3" -> useProps();
            default -> System.out.println("无效指令");
        }
        ammoSystem.cheat();
        personSystem.printHealth();
    }

    private void shootPlayer2() {
        Component nextBullet = ammoSystem.nextBullet();
        if (nextBullet instanceof BlankComponent) {
            shotgunSystem.respawnBarrel();
            System.out.println("空弹");
            if (turnSystem.isPlayer1Turn()) {
                turnSystem.player2Turn();
                turnSystem.noHandcuff();
            }
            return;
        }
        System.out.println("BOOM!");
        personSystem.harm(player2);
        if (turnSystem.isPlayer1Turn() && turnSystem.notHandcuffed()) {
            turnSystem.player2Turn();
            return;
        }
        turnSystem.noHandcuff();
        turnSystem.player1Turn();
    }

    private void shootPlayer1() {
        Component nextBullet = ammoSystem.nextBullet();
        if (nextBullet instanceof BlankComponent) {
            shotgunSystem.respawnBarrel();
            System.out.println("空弹");
            if (turnSystem.isPlayer2Turn()) {
                turnSystem.player1Turn();
                turnSystem.noHandcuff();
            }
            return;
        }
        System.out.println("BOOM!");
        personSystem.harm(player1);
        if (turnSystem.isPlayer2Turn() && turnSystem.notHandcuffed()) {
            turnSystem.player1Turn();
            return;
        }
        turnSystem.noHandcuff();
        turnSystem.player2Turn();
    }

    private void useProps() {
        if (propSystem.noProp()) {
            System.out.println("没有道具可以用");
            return;
        }
        System.out.println("输入道具序号");
        String inputString;
        while (true) {
            int size = turnSystem.isPlayer1Turn() ? propSystem.player1Props.size() : propSystem.player2Props.size();
            inputString = input.nextLine();
            if (inputString.matches("[1-8]")) {
                int choice = Integer.parseInt(inputString);
                if (choice <= size) break;
                else {
                    System.out.println("重新输入");
                }
            } else {
                System.out.println("重新输入");
            }
        }
        propSystem.usePropByIndex(Integer.parseInt(inputString) - 1);
    }
}
