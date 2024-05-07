import Components.*;
import Systems.*;

import java.lang.reflect.InvocationTargetException;

public class JavaBuckshotRoulette {
    int initialHealth;
    String playerName;
    AmmoSystem ammoSystem;
    Entity dealer;
    Entity player;
    PersonSystem personSystem;
    TurnSystem turnSystem;
    RoundSystem roundSystem;
    Engine engine = new Engine();
    PropSystem propSystem = new PropSystem(engine);
    ShotgunSystem shotgunSystem = new ShotgunSystem();

    public JavaBuckshotRoulette() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
//        setPlayerName();
        addTurnSystem();
        addPeople();
        setInitialHealth();
        personSystem.printHealth();
        addEngine();
        play();
    }

    private void addEngine() {
        addRoundSystem();
        addAmmoSystem();
        engine.addSystem(shotgunSystem);
        engine.addSystem(roundSystem);
        engine.addSystem(personSystem);
        engine.addSystem(ammoSystem);
        engine.addSystem(turnSystem);
        engine.addSystem(propSystem);
    }

    private void setInitialHealth() {
        initialHealth = engine.rand.nextInt(3) + 2;
        personSystem.setHealth(initialHealth);
    }

    private void setPlayerName() {
        System.out.println("ENTER YOUR NAME");
        playerName = engine.input.nextLine().toUpperCase();
    }

    private void addPeople() {
        dealer = new Entity();
        NameComponent dealerName = new NameComponent("DEALER");
        dealer.addComponent(dealerName);
        HealthComponent dealerHealth = new HealthComponent(initialHealth);
        dealer.addComponent(dealerHealth);
        player = new Entity();
        NameComponent playerName = new NameComponent(this.playerName);
        player.addComponent(playerName);
        HealthComponent playerHealth = new HealthComponent(initialHealth);
        player.addComponent(playerHealth);
        personSystem = new PersonSystem(engine, dealer, player, turnSystem);
    }

    private void addRoundSystem() {
        roundSystem = new RoundSystem();
        System.out.println("-----ROUND " + roundSystem.getRound() + "-----");
    }

    private void addAmmoSystem() {
        int initialAmmo = engine.rand.nextInt(7) + 2;
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
                ammoSystem.reload(engine.rand.nextInt(7) + 2);
                turnSystem.playerTurn();
                personSystem.printHealth();
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
        if (personSystem.isPlayerDead()) {
            System.out.println("YOU ARE DEAD, YOU LOSE");
            System.exit(-1);
        } else if (personSystem.isDealerDead()) {
            System.out.println("DEALER IS DEAD, YOU WIN THE ROUND");
            engine.getSystem(PropSystem.class);
            nextRound();
        }
    }

    private void nextRound() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        roundSystem.nextRound();
        System.out.println("-----ROUND " + roundSystem.getRound() + "-----");
        ammoSystem.reload(engine.rand.nextInt(7) + 2);
        turnSystem.playerTurn();
        setInitialHealth();
        personSystem.printHealth();
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
        String command = engine.input.nextLine();
        switch (command) {
            case "1" -> shootDealer();
            case "2" -> shootPlayer();
            case "3" -> useProps();
            default -> System.out.println("INVALID COMMAND");
        }
        ammoSystem.printChamber();
        personSystem.printHealth();
    }

    private void dealerTurn() {
        System.out.println("\t\t\t⚠️⚠️⚠️️️DEALER TURN⚠️⚠️⚠️");
        System.out.println("THE DEALER SHOT YOU");
        shootPlayer();
        ammoSystem.printChamber();
        personSystem.printHealth();
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
        personSystem.harm();
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
        personSystem.harm();
    }

    private void useProps() {
        System.out.println("TYPE INDEX TO USE CORRESPONDING PROPS");
        int choice = Integer.parseInt(engine.input.nextLine()) - 1;
        propSystem.usePropByIndex(choice, turnSystem);
    }

    private void printBlankBullet() {
        System.out.println("BLANK BULLET");
    }
}
