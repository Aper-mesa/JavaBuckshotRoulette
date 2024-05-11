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
    TurnSystem turnSystem = new TurnSystem();
    RoundSystem roundSystem = new RoundSystem();
    Engine engine = new Engine();
    PropSystem propSystem;
    ShotgunSystem shotgunSystem = new ShotgunSystem();

    public JavaBuckshotRoulette()
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
//        setPlayerName();
        addPeople();
        setInitialHealth();
        personSystem.printHealth();
        addEngine();
        play();
    }

    private void addEngine() {
        System.out.println("-----ROUND " + roundSystem.getRound() + "-----");
        int initialAmmo = engine.rand.nextInt(7) + 2;
        ammoSystem = new AmmoSystem(engine, initialAmmo);
        printChamber();
        engine.addSystem(shotgunSystem);
        engine.addSystem(roundSystem);
        engine.addSystem(personSystem);
        engine.addSystem(ammoSystem);
        engine.addSystem(turnSystem);
        propSystem = new PropSystem(engine);
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
                ammoSystem.reload(engine.rand.nextInt(7) + 2);
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
        ammoSystem.reload(engine.rand.nextInt(7) + 2);
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
        String command = engine.input.nextLine();
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
        DealerAI ai = new DealerAI(engine);
        ai.useProp();
        if (ai.shootSelf()) {
            System.out.println("DEALER SHOT HIMSELF");
            shootDealer();
        } else {
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
            printBlankBullet();
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
            printBlankBullet();
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
        int choice = Integer.parseInt(engine.input.nextLine()) - 1;
        propSystem.usePropByIndex(choice, turnSystem);
    }

    private void printBlankBullet() {
        System.out.println("BLANK BULLET");
    }
}
