package Core;

import Components.BlankComponent;
import Components.Component;
import Components.HealthComponent;
import Components.NameComponent;

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
        setNames();
        setInitialHealth();
        System.out.println("-----第 " + roundSystem.getRound() + " 回合-----");
        ammoSystem.reload();
        printChamber();
        personSystem.printHealth();
        play();
    }

    public void setNames() {
        System.out.println("玩家1输入姓名：");
        personSystem.setPlayer1Name(input.nextLine().toUpperCase());
        System.out.println("玩家2输入姓名：");
        personSystem.setPlayer2Name(input.nextLine().toUpperCase());
    }

    private void setInitialHealth() {
        initialHealth = rand.nextInt(3) + 2;
        personSystem.setHealth(initialHealth);
    }

    private void addPeople() {
        HealthComponent player2Health = new HealthComponent();
        NameComponent player2Name = new NameComponent();
        player2.addComponent(player2Health);
        player2.addComponent(player2Name);
        HealthComponent playerHealth = new HealthComponent();
        NameComponent player1Name = new NameComponent();
        player1.addComponent(playerHealth);
        player1.addComponent(player1Name);
    }

    private void printChamber() {
        int blankAmmo = ammoSystem.getBlankAmount();
        int ballAmmo = ammoSystem.getBallAmount();
        System.out.println("\t\t\t" + blankAmmo + " 空  " + ballAmmo + " 实");
    }

    private void play()
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        propSystem.spawnPropsInNewRound();
        //if this random number is 0 then player2 first; otherwise player1
        int initTurn = rand.nextInt(2);
        if (initTurn == 0) {
            turnSystem.player2Turn();
        }
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
            System.out.println(personSystem.player1Name() + "死了，" + personSystem.player2Name() + "赢了此回合");
            nextRound();
        } else if (personSystem.isPlayer2Dead()) {
            System.out.println(personSystem.player2Name() + "死了，" + personSystem.player1Name() + "赢了此回合");
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
        if (personSystem.isPlayer1Dead()) turnSystem.player1Turn();
        else turnSystem.player2Turn();
        setInitialHealth();
        personSystem.printHealth();
        printChamber();
        propSystem.clearProps();
        propSystem.spawnPropsInNewRound();
    }

    private void player1Turn() {
        propSystem.showProps();
        System.out.println("\t\t\t\u001B[31m" + personSystem.player1Name() + "回合\u001B[0m");
        System.out.println("""
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
        System.out.println("\t\t\t\u001B[31m" + personSystem.player2Name() + "回合\u001B[0m");
        System.out.println("""
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
        if (turnSystem.notHandcuffed()) {
            turnSystem.player1Turn();
        }
        turnSystem.noHandcuff();
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
        if (turnSystem.notHandcuffed()) {
            turnSystem.player2Turn();
        }
        turnSystem.noHandcuff();
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
