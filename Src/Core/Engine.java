package Core;

import Systems.*;

import java.util.Random;
import java.util.Scanner;

public class Engine {
    public static Scanner input = new Scanner(System.in);
    public static Random rand = new Random();
    public static AmmoSystem ammoSystem = new AmmoSystem();
    public static PersonSystem personSystem = new PersonSystem();
    public static PropSystem propSystem = new PropSystem();
    public static RoundSystem roundSystem = new RoundSystem();
    public static ShotgunSystem shotgunSystem = new ShotgunSystem();
    public static TurnSystem turnSystem = new TurnSystem();
}
