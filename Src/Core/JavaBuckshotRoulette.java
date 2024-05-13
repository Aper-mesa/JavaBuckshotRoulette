package Core;

import Components.BlankComponent;
import Components.Component;
import Components.HealthComponent;

import java.lang.reflect.InvocationTargetException;

import static Core.Engine.*;
import static Systems.PersonSystem.dealer;
import static Systems.PersonSystem.player;

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
        System.out.println("-----第 " + roundSystem.getRound() + "回合-----");
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
        dealer.addComponent(dealerHealth);
        HealthComponent playerHealth = new HealthComponent();
        player.addComponent(playerHealth);
    }

    private void printChamber() {
        int blankAmmo = ammoSystem.getBlankAmount();
        int ballAmmo = ammoSystem.getBallAmount();
        System.out.println("\t\t\t" + blankAmmo + " 实\t" + ballAmmo +" 空");
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
                turnSystem.playerTurn();
                personSystem.printHealth();
                printChamber();
            }
            inTurn();
        }
    }

    private void inTurn()
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if (turnSystem.isPlayerTurn()) {
            playerTurn();
        } else {
            dealerTurn();
        }
        if (personSystem.isPlayerDead()) {
            System.out.println("你死了，游戏结束");
            System.exit(-1);
        } else if (personSystem.isDealerDead()) {
            System.out.println("大哥死了，你赢了此回合");
            nextRound();
        }
    }

    private void nextRound()
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        roundSystem.nextRound();
        if (roundSystem.noMoreRound()) return;
        System.out.println("-----第 " + roundSystem.getRound() + "回合-----");
        ammoSystem.reload();
        propSystem.clearPhoneIndexes();
        turnSystem.playerTurn();
        setInitialHealth();
        personSystem.printHealth();
        printChamber();
        propSystem.clearProps();
        propSystem.spawnPropsInNewRound();
    }

    private void playerTurn() {
        propSystem.showProps();
        System.out.println("""
                \t\t\t⚠️⚠️⚠️你的回合⚠️⚠️⚠️
                \t\t\t输 1 打大哥
                \t\t\t输 2 打自己
                \t\t\t输 3 用道具""");
        String command = input.nextLine();
        switch (command) {
            case "1" -> shootDealer();
            case "2" -> shootPlayer();
            case "3" -> useProps();
            default -> System.out.println("无效指令");
        }
        //ammoSystem.cheat();
        personSystem.printHealth();
    }

    private void dealerTurn() {
        System.out.println("\t\t\t⚠️⚠️⚠️️️大哥回合⚠️⚠️⚠️");
        DealerAI.useProp();
        if (!ammoSystem.noBullet() && DealerAI.shootSelfByBulletNumbers() && !DealerAI.nextBall) {
            System.out.println("大哥打他自己");
            shootDealer();
        } else if (!ammoSystem.noBullet()) {
            System.out.println("大哥打你");
            shootPlayer();
        }
        //ammoSystem.cheat();
        personSystem.printHealth();
    }

    private void shootDealer() {
        Component nextBullet = ammoSystem.nextBullet();
        if (nextBullet instanceof BlankComponent) {
            shotgunSystem.respawnBarrel();
            System.out.println("空弹");
            if (turnSystem.isPlayerTurn()) {
                turnSystem.dealerTurn();
                turnSystem.noHandcuff();
            }
            return;
        }
        System.out.println("BOOM!");
        personSystem.harm(dealer);
        if (turnSystem.isPlayerTurn() && turnSystem.notHandcuffed()) {
            turnSystem.dealerTurn();
            return;
        }
        turnSystem.noHandcuff();
        turnSystem.playerTurn();
    }

    private void shootPlayer() {
        Component nextBullet = ammoSystem.nextBullet();
        if (nextBullet instanceof BlankComponent) {
            shotgunSystem.respawnBarrel();
            System.out.println("空弹");
            if (turnSystem.isDealerTurn()) {
                turnSystem.playerTurn();
                turnSystem.noHandcuff();
            }
            return;
        }
        System.out.println("BOOM!");
        personSystem.harm(player);
        if (turnSystem.isDealerTurn() && turnSystem.notHandcuffed()) {
            turnSystem.playerTurn();
            return;
        }
        turnSystem.noHandcuff();
        turnSystem.dealerTurn();
    }

    private void useProps() {
        if (propSystem.playerNoProp()) {
            System.out.println("你没有道具");
            return;
        }
        System.out.println("输入道具序号");
        int choice = Integer.parseInt(input.nextLine()) - 1;
        propSystem.usePropByIndex(choice, turnSystem);
    }
}
