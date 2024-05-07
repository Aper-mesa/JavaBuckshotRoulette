import Components.*;
import Systems.*;

import java.lang.reflect.InvocationTargetException;
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
    PropSystem propSystem = new PropSystem();
    Engine engine = new Engine();

    public JavaBuckshotRoulette() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
//        setPlayerName();
        addDealer();
        addPlayer(playerName);
        setInitialHealth();
        printBothHealth();
        addEngine();
        play();
    }

    private void addEngine() {
        addRoundSystem();
        addAmmoSystem();
        addTurnSystem();
        engine.addSystem(roundSystem);
        engine.addSystem(dealerSystem);
        engine.addSystem(playerSystem);
        engine.addSystem(ammoSystem);
        engine.addSystem(turnSystem);
        engine.addSystem(propSystem);
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

    private void play() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        propSystem.spawnProps();
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

    private void inTurn() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
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
            engine.getSystem(PropSystem.class);
            nextRound();
        }
    }

    private void nextRound() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        roundSystem.nextRound();
        System.out.println("-----ROUND " + roundSystem.getRound() + "-----");
        ammoSystem.reload(r.nextInt(7) + 2);
        turnSystem.playerTurn();
        setInitialHealth();
        printBothHealth();
        printChamber();
        propSystem.spawnProps();
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
        ammoSystem.printChamber();
        printBothHealth();
    }

    private void dealerTurn() {
        System.out.println("\t\t\t⚠️⚠️⚠️️️DEALER TURN⚠️⚠️⚠️");
        System.out.println("THE DEALER SHOT YOU");
        shootPlayer();
        ammoSystem.printChamber();
        printBothHealth();
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
            if (turnSystem.isDealerTurn()) {
                turnSystem.playerTurn();
            }
            return;
        }
        System.out.println("BOOM!");
        turnSystem.playerTurn();
        playerSystem.decrementHealth();
    }

    private void useProps() {
        System.out.println("TYPE INDEX TO USE CORRESPONDING PROPS");
        int choice = Integer.parseInt(input.nextLine()) - 1;
        propSystem.usePropByIndex(choice, engine, turnSystem);
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
