import Components.*;
import Systems.AmmoSystem;
import Systems.PersonSystem;
import Systems.RoundSystem;
import Systems.TurnSystem;

import java.util.Random;
import java.util.Scanner;

public class JavaBuckshotRoulette {
    Random r = new Random();
    Scanner input = new Scanner(System.in);
    int initialHealth;
    String playerName;
    AmmoSystem ammoSystem;
    Entity dealer;
    Entity player;
    PersonSystem dealerSystem;
    PersonSystem playerSystem;
    TurnSystem turnSystem;
    RoundSystem roundSystem;

    public JavaBuckshotRoulette() {
//        setPlayerName();
        addRoundSystem();
        addDealer();
        addPlayer(playerName);
        setInitialHealth();
        printBothHealth();
        addAmmoSystem();
        addTurnSystem();
        play();
    }

    private void setInitialHealth() {
        initialHealth = r.nextInt(3) + 2;
        dealerSystem.setHealth(initialHealth);
        playerSystem.setHealth(initialHealth);
    }

    private void setPlayerName() {
        System.out.println("ENTER YOUR NAME");
        playerName = input.nextLine().toUpperCase();
    }

    private void addDealer() {
        dealer = new Entity();
        NameComponent name = new NameComponent("DEALER");
        dealer.addComponent(name);
        HealthComponent health = new HealthComponent(initialHealth);
        dealer.addComponent(health);
        dealerSystem = new PersonSystem(dealer);
    }

    private void addPlayer(String playerName) {
        player = new Entity();
        NameComponent name = new NameComponent(playerName);
        player.addComponent(name);
        HealthComponent health = new HealthComponent(initialHealth);
        player.addComponent(health);
        playerSystem = new PersonSystem(player);
    }

    private void addRoundSystem() {
        roundSystem = new RoundSystem();
        System.out.println("-----ROUND " + roundSystem.getRound() + "-----");
    }

    private void addAmmoSystem() {
        int initialAmmo = r.nextInt(7) + 2;
        ammoSystem = new AmmoSystem(initialAmmo);
        printChamber();
    }

    private void printChamber() {
        int blankAmmo = ammoSystem.getBlankAmount();
        int ballAmmo = ammoSystem.getBallAmount();
        System.out.println("\t\t\tBLANK " + blankAmmo + "\t" + "BALL " + ballAmmo);
    }

    private void addTurnSystem() {
        turnSystem = new TurnSystem();
    }

    private void play() {
        while (true) {
            if (roundSystem.noMoreRound()) {
                System.out.println("GAME OVER");
                System.exit(-1);
            }
            if (ammoSystem.noBullet()) {
                System.out.println("\t\t\t--------------");
                ammoSystem.reload(r.nextInt(7) + 2);
                turnSystem.playerTurn();
                printBothHealth();
                printChamber();
            }
            inTurn();
        }
    }

    private void inTurn() {
        if (turnSystem.isPlayerTurn()) {
            playerTurn();
        } else {
            dealerTurn();
        }
        if (playerSystem.isDead()) {
            System.out.println("YOU ARE DEAD, YOU LOSE");
            System.exit(-1);
        }
        if (dealerSystem.isDead()) {
            System.out.println("DEALER IS DEAD, YOU WIN THE ROUND");
            nextRound();
        }
    }

    private void nextRound() {
        roundSystem.nextRound();
        System.out.println("-----ROUND " + roundSystem.getRound() + "-----");
        ammoSystem.reload(r.nextInt(7) + 2);
        turnSystem.playerTurn();
        setInitialHealth();
        printBothHealth();
        printChamber();
    }

    private void playerTurn() {
        System.out.println("""
                \t\t\t⚠️⚠️⚠️YOUR TURN⚠️⚠️⚠️
                \t\t\tTYPE 0 TO SHOOT THE DEALER
                \t\t\tTYPE 9 TO SHOOT YOURSELF
                \t\t\tTYPE 8 TO USE PROPS (NOT WORKING)""");
        String command = input.nextLine();
        switch (command) {
            case "0" -> shootDealer();
            case "9" -> shootPlayer();
            case "8" -> useProps();
            default -> System.out.println("INVALID COMMAND");
        }
        ammoSystem.printChamber();
        printBothHealth();
    }

    private void dealerTurn() {
        System.out.println("\t\t\t⚠️⚠️⚠️️️DEALER TURN⚠️⚠️⚠️");
        System.out.println("THE DEALER SHOT YOU");
        shootPlayer();
        ammoSystem.printChamber();
        printBothHealth();
        System.out.println("\t\t\tTYPE ANYTHING TO CONTINUE");
        input.nextLine();
    }

    private void shootDealer() {
        Component nextBullet = ammoSystem.nextBullet();
        if (nextBullet instanceof BlankComponent) {
            printBlankBullet();
            if (turnSystem.isPlayerTurn()) {
                turnSystem.dealerTurn();
            }
            return;
        }
        System.out.println("BOOM!");
        turnSystem.dealerTurn();
        dealerSystem.decrementHealth();
    }

    private void shootPlayer() {
        Component nextBullet = ammoSystem.nextBullet();
        if (nextBullet instanceof BlankComponent) {
            printBlankBullet();
            if (!turnSystem.isPlayerTurn()) {
                turnSystem.playerTurn();
            }
            return;
        }
        System.out.println("BOOM!");
        turnSystem.playerTurn();
        playerSystem.decrementHealth();
    }

    private void useProps() {
    }

    private void printBlankBullet() {
        System.out.println("BLANK BULLET");
    }

    private void printBothHealth() {
        System.out.print("\t\t\tDEALER HEALTH:\t");
        for (int i = 0; i < dealerSystem.getHealth(); i++) {
            System.out.print("⬛");
        }
        System.out.println();
        System.out.print("\t\t\tYOUR HEALTH:\t");
        for (int i = 0; i < playerSystem.getHealth(); i++) {
            System.out.print("⬛");
        }
        System.out.println();
    }
}
