package Systems;

import Components.BallComponent;
import Components.BlankComponent;
import Components.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

public class AmmoSystem extends EntitySystem {
    private int ballAmount;
    private int blankAmount;
    Stack<Component> chamber = new Stack<>();
    Stack<Component> chamberDuplication = new Stack<>();
    Random r = new Random();

    public AmmoSystem(int totalAmount) {
        reload(totalAmount);
    }

    public void reload(int totalAmount) {
        System.out.println("\t\t\tSHOTGUN RELOADED");
        ballAmount = r.nextInt(totalAmount - 1) + 1;
        blankAmount = totalAmount - ballAmount;
        chamber.clear();
        chamberDuplication.clear();
        for (int i = 0; i < ballAmount; i++) {
            chamber.push(new BallComponent());
            chamberDuplication.push(new BallComponent());
        }
        for (int i = 0; i < blankAmount; i++) {
            chamber.push(new BlankComponent());
            chamberDuplication.push(new BlankComponent());
        }
        Collections.shuffle(chamber);
        cheat();
    }

    //for testing only
    public void cheat() {
        System.out.println("\t\t\t" + Arrays.toString(chamber.toArray()));
    }

    public boolean noBullet() {
        return chamber.isEmpty();
    }

    public int getBallAmount() {
        return ballAmount;
    }

    public int getBlankAmount() {
        return blankAmount;
    }

    public Component nextBullet() {
        return chamber.pop();
    }

    public Component checkBullet() {
        return chamber.peek();
    }

    public void convertCurrentBullet() {
        Component bullet = chamber.pop();
        if (bullet instanceof BlankComponent) {
            chamber.push(new BallComponent());
        } else {
            chamber.push(new BlankComponent());
        }
    }

    public int getTotalAmount() {
        return blankAmount + ballAmount;
    }

    public Component checkBulletByPhone(int index) {
        return chamberDuplication.get(index);
    }

}
