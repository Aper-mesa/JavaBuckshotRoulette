import Components.*;
import Systems.AmmoSystem;

import java.util.Random;
import java.util.Scanner;

public class JavaBuckshotRoulette {
    Random r = new Random();
    Scanner input = new Scanner(System.in);
    int initialHealth;
    String playerName;
    AmmoSystem ammoSystem;
    final String SHOOT_DEALER = "0";
    final String SHOOT_SELF = "9";
    final String USE_PROP = "8";

    public JavaBuckshotRoulette() {
//        setPlayerName();
        addRound();
        initialHealth = r.nextInt(3) + 2;
        System.out.println("INITIAL HEALTH: " + initialHealth);
        addDealer();
        addPlayer(playerName);
        addAmmoSystem();
    }

    private void setPlayerName() {
        System.out.println("ENTER YOUR NAME");
        playerName = input.nextLine().toUpperCase();
    }

    private void addDealer() {
        Entity dealer = new Entity();
        NameComponent name = new NameComponent("DEALER");
        dealer.addComponent(name);
        HealthComponent health = new HealthComponent(initialHealth);
        dealer.addComponent(health);
    }

    private void addPlayer(String playerName) {
        Entity player = new Entity();
        NameComponent name = new NameComponent(playerName);
        player.addComponent(name);
        HealthComponent health = new HealthComponent(initialHealth);
        player.addComponent(health);
    }

    private void addRound() {
        RoundComponent round = new RoundComponent();
        System.out.println("-----ROUND " + round.currentRound + "-----");
    }

    private void addAmmoSystem() {
        int initialAmmo = r.nextInt(7) + 2;
        ammoSystem = new AmmoSystem(initialAmmo);
        int blankAmmo = ammoSystem.getBlankAmount();
        int ballAmmo = ammoSystem.getBallAmount();
        System.out.println("BLANK " + blankAmmo);
        System.out.println("BALL " + ballAmmo);
    }

    private void play() {
        System.out.println("""
                TYPE 0 TO SHOOT THE DEALER
                TYPE 9 TO SHOOT YOURSELF
                TYPE 8 TO USE PROPS""");
        String command = input.nextLine();
        switch (command) {
            case "0" -> shootDealer();
            case "9" -> shootSelf();
            case "8" -> useProps();
            default -> System.out.println("INVALID COMMAND");
        }
    }

    private void shootDealer() {

    }

    private void shootSelf() {

    }

    private void useProps() {
    }
}
