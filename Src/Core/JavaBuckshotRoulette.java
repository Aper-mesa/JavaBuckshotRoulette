package Core;

import Components.BlankComponent;
import Components.Component;
import Components.HealthComponent;

import java.lang.reflect.InvocationTargetException;

import static Core.Engine.*;
import static Systems.PersonSystem.*;

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
        System.out.println("-----ROUND " + roundSystem.getRound() + "-----");
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
        System.out.println("\t\t\tBLANK " + blankAmmo + "\t" + "BALL " + ballAmmo);
    }

    private void play()
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        propSystem.spawnPropsInNewRound();
        while (true) {
            if (roundSystem.noMoreRound()) {
                System.out.println("GAME OVER");
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
            System.out.println("YOU ARE DEAD, YOU LOSE");
            System.exit(-1);
        } else if (personSystem.isDealerDead()) {
            System.out.println("DEALER IS DEAD, YOU WIN THE ROUND");
            nextRound();
        }
    }

    private void nextRound()
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        roundSystem.nextRound();
        if (roundSystem.noMoreRound()) return;
        System.out.println("-----ROUND " + roundSystem.getRound() + "-----");
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
                \t\t\t⚠️⚠️⚠️YOUR TURN⚠️⚠️⚠️
                \t\t\tTYPE 1 TO SHOOT THE DEALER
                \t\t\tTYPE 2 TO SHOOT YOURSELF
                \t\t\tTYPE 3 TO USE PROPS""");
        String command = input.nextLine();
        switch (command) {
            case "1" -> shootDealer();
            case "2" -> shootPlayer();
            case "3" -> useProps();
            default -> System.out.println("INVALID COMMAND");
        }
        ammoSystem.cheat();
        personSystem.printHealth();
    }

    private void dealerTurn() {
        System.out.println("\t\t\t⚠️⚠️⚠️️️DEALER TURN⚠️⚠️⚠️");
        DealerAI.useProp();
        if (!ammoSystem.noBullet() && DealerAI.shootSelfByBulletNumbers() && !DealerAI.nextBall) {
            System.out.println("DEALER SHOT HIMSELF");
            shootDealer();
        } else if (!ammoSystem.noBullet()) {
            System.out.println("DEALER SHOT YOU");
            shootPlayer();
        }
        ammoSystem.cheat();
        personSystem.printHealth();
    }

    private void shootDealer() {
        Component nextBullet = ammoSystem.nextBullet();
        if (nextBullet instanceof BlankComponent) {
            shotgunSystem.respawnBarrel();
            System.out.println("BLANK BULLET");
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
            System.out.println("BLANK BULLET");
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
        System.out.println("TYPE INDEX TO USE CORRESPONDING PROPS");
        int choice = Integer.parseInt(input.nextLine()) - 1;
        propSystem.usePropByIndex(choice, turnSystem);
    }
}
